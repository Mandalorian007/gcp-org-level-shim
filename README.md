# gcp-org-level-shim

This project is a sample project for creating a prototype GCP API client that could be used to assist projects that need to operate across a GCP org instead of a GCP project.


Requirements to run:
* Organization level GCP permissions on a service account:
    * resourcemanager.projects.list ([Project List Function](https://cloud.google.com/resource-manager/reference/rest/v1beta1/projects/list#google.cloudresourcemanager.projects.v1beta1.DeveloperProjects.ListProjects))
    * serviceusage.services.list ([Service List Function](https://cloud.google.com/service-usage/docs/reference/rest/v1beta1/services/list))
    * resourcemanager.projects.get ([Get Billing Info Function](https://cloud.google.com/billing/reference/rest/v1/projects/getBillingInfo))
    * bigquery.datasets.get ([BigQuery Datasets List Function](https://cloud.google.com/bigquery/docs/reference/rest/v2/datasets/list))
* This application must be run using the above service account.

## "Flow" of the Application
* Using the ResourceManager list all GCP projects in the Spotify organization.
* Filter out any projects that are not in the active state. (Example project being deleted)
* Use the ServiceList API to get all enabled services (note these are in DNS style names not an easy enum value and I can't seem to find a list of possiblities)
* Look up the Billing Information for the project to see if billing is enabled. (Note: Billing does not need to be enabled for Read API calls, but does need to be enabled for write API calls)
* Once all this metadata is present an application should be able to iterate all projects in an org looking for projects that use a particular service and then issue calls as needed.
