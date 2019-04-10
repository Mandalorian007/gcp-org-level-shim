package com.spotify.example;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.DatasetInfo;
import com.spotify.shim.OrgShim;
import com.spotify.shim.OrgShimImpl;
import com.spotify.shim.Project;
import java.util.List;

public class TestMain {

  public static void main(String[] args) throws Exception {
    OrgShim orgShim = new OrgShimImpl();

    List<Project> orgProjects = orgShim.getOrgProjects();

    orgProjects.forEach(project -> {
      System.out.println(project.getProjectId() + " : "
                         + project.getProjectNumber());

      if (project.getEnabledApis().contains("bigquery-json")) {
        //The BigQuery API is enabled it is safe to make BigQuery API read operations
        BigQuery bigquery = BigQueryOptions.getDefaultInstance()
            .toBuilder()
            .setProjectId(project.getProjectId())
            .build()
            .getService();

        bigquery.listDatasets(BigQuery.DatasetListOption.pageSize(1));

        if (project.isBillingEnabled()) {
          //Since Billing is enabled it is also safe to make write operations.
          bigquery.create(DatasetInfo.newBuilder("temp-54321")
              .setLocation("EU")
              .build());
          bigquery.delete("temp-54321");
        }
      }
    });
  }

}