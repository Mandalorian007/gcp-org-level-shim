package com.spotify.example;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.spotify.shim.OrgShim;
import com.spotify.shim.OrgShimImpl;
import com.spotify.shim.Project;
import java.util.ArrayList;
import java.util.List;

/*
  This example is to print all the projects I can issue BigQuery API calls safely against. 
  
  Because of issue https://enterprise.google.com/supportcenter/managecases#Case/0016000000sFY8w/U-18715624, 
  this isn't quite possible: the BigQuery API can be enabled while BigQuery itself is disabled.
  
 */

public class TestMain {

  public static void main(String[] args) throws Exception {
    OrgShim orgShim = new OrgShimImpl();

    List<Project> orgProjects = orgShim.getOrgProjects();


    List<String> failedProjects = new ArrayList<>();
    orgProjects.forEach(project -> {
      System.out.println(project.getProjectId() + " : "
                         + project.getProjectNumber());

      if(project.getEnabledApis().contains("bigquery-json")) {
        //The BigQuery API is enabled it is safe to make BigQuery API read operations
        BigQuery bigquery = BigQueryOptions.getDefaultInstance()
            .toBuilder()
            .setProjectId(project.getProjectId())
            .build()
            .getService();

        try {
          bigquery.listDatasets(BigQuery.DatasetListOption.pageSize(1));
          System.out.println(project.toString() + " has BigQuery enabled.");
        } catch (Exception e) {
          // This catch block exists because the BigQuery API can be enabled while BigQuery itself is disabled
          // https://enterprise.google.com/supportcenter/managecases#Case/0016000000sFY8w/U-18715624
          failedProjects.add(project.getProjectId() + " : " + project.getProjectNumber());
          e.printStackTrace();
        }

        if(project.isBillingEnabled()) {
          //Since Billing is enabled it is also safe to make write operations.
        }
      }
    });
    System.out.println("Here are all the failed projects: ");
    System.out.println(failedProjects);
  }

}
