package com.spotify.shim;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.cloudbilling.Cloudbilling;
import com.google.api.services.cloudbilling.model.ProjectBillingInfo;
import com.google.api.services.serviceusage.v1beta1.ServiceUsage;
import com.google.api.services.serviceusage.v1beta1.model.ListServicesResponse;
import com.google.api.services.serviceusage.v1beta1.model.Service;
import com.google.cloud.resourcemanager.ResourceManager;
import com.google.cloud.resourcemanager.ResourceManagerOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OrgShimImpl implements OrgShim {

  private final ResourceManager resourceManager;
  private final Cloudbilling billing;
  private final ServiceUsage serviceUsage;

  public OrgShimImpl() throws Exception {
    resourceManager = ResourceManagerOptions.getDefaultInstance().getService();

    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    GoogleCredential credential = GoogleCredential.getApplicationDefault();
    credential = credential
        .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

    billing = new Cloudbilling.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(ResourceManagerOptions.getDefaultInstance().getApplicationName())
        .build();

    serviceUsage = new ServiceUsage.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(ResourceManagerOptions.getDefaultInstance().getApplicationName())
        .build();
  }

  @Override
  public List<Project> getOrgProjects() {
    List<Project> orgProjects = new ArrayList<>();

    //Filter out projects that have a non active state (example: Delete requested)
    List<com.google.cloud.resourcemanager.Project> projects =
        StreamSupport.stream(
            resourceManager.list().iterateAll().spliterator(),
            false)
            .filter(project -> project.getState().name().equals("ACTIVE"))
            .collect(Collectors.toList());

    projects.forEach(gcpProject -> {

      Project.ProjectBuilder projectBuilder = Project.builder();
      projectBuilder.projectId(gcpProject.getProjectId());
      projectBuilder.projectNumber(gcpProject.getProjectNumber());

      List<String> enabledServices = new ArrayList<>();
      try {
        ListServicesResponse listServicesResponse =
            serviceUsage.services().list("projects/" + gcpProject.getProjectNumber())
                .setFilter("state:ENABLED")
                .execute();

        List<Service> services = listServicesResponse.getServices();
        if (services == null) {
          services = new ArrayList<>();
        }
        enabledServices = services.stream()
            .map(Service::getName)
            // remove: projects/{project_number}/services/
            .map(name -> name.substring(name.lastIndexOf("/") + 1))
            // remove: .googleapis.com
            .map(name -> name.substring(0, name.indexOf('.')))
            .collect(Collectors.toList());

      } catch (Exception e) {
        System.err.println("error with services api: " + e.getMessage());
        e.printStackTrace();
        projectBuilder.enabledApis(new ArrayList<>());
      }
      projectBuilder.enabledApis(enabledServices);

      boolean billingIsEnabled = false;
      try {
        Cloudbilling.Projects.GetBillingInfo request =
            billing.projects().getBillingInfo("projects/" + gcpProject.getProjectId());
        ProjectBillingInfo response = request.execute();
        Boolean billingEnabled = response.getBillingEnabled();
        billingIsEnabled = (billingEnabled == null ? false : billingEnabled);
      } catch (Exception e) {
        System.err.println("error with billing api: " + e.getMessage());
        e.printStackTrace();
      }
      projectBuilder.billingEnabled(billingIsEnabled);

      orgProjects.add(projectBuilder.build());
    });
    return orgProjects;
  }
}
