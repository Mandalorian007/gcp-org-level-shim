package com.spotify.shim;

import java.util.List;

public class Project {

  private final String projectId;
  private final long projectNumber;
  private final boolean billingEnabled;
  private final List<String> enabledApis;

  //Builder, Constructors, Getters, Setters, ect. Boilerplate below this
  @java.beans.ConstructorProperties({"projectId", "projectNumber", "billingEnabled", "enabledApis"})
  Project(String projectId, long projectNumber, boolean billingEnabled,
          List<String> enabledApis) {
    this.projectId = projectId;
    this.projectNumber = projectNumber;
    this.billingEnabled = billingEnabled;
    this.enabledApis = enabledApis;
  }

  public static ProjectBuilder builder() {
    return new ProjectBuilder();
  }

  public String getProjectId() {
    return this.projectId;
  }

  public long getProjectNumber() {
    return this.projectNumber;
  }

  public boolean isBillingEnabled() {
    return this.billingEnabled;
  }

  public List<String> getEnabledApis() {
    return this.enabledApis;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Project)) {
      return false;
    }
    final Project other = (Project) o;
    if (!other.canEqual((Object) this)) {
      return false;
    }
    final Object this$projectId = this.getProjectId();
    final Object other$projectId = other.getProjectId();
    if (this$projectId == null ? other$projectId != null
                               : !this$projectId.equals(other$projectId)) {
      return false;
    }
    if (this.getProjectNumber() != other.getProjectNumber()) {
      return false;
    }
    if (this.isBillingEnabled() != other.isBillingEnabled()) {
      return false;
    }
    final Object this$enabledApis = this.getEnabledApis();
    final Object other$enabledApis = other.getEnabledApis();
    if (this$enabledApis == null ? other$enabledApis != null
                                 : !this$enabledApis.equals(other$enabledApis)) {
      return false;
    }
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Project;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $projectId = this.getProjectId();
    result = result * PRIME + ($projectId == null ? 43 : $projectId.hashCode());
    final long $projectNumber = this.getProjectNumber();
    result = result * PRIME + (int) ($projectNumber >>> 32 ^ $projectNumber);
    result = result * PRIME + (this.isBillingEnabled() ? 79 : 97);
    final Object $enabledApis = this.getEnabledApis();
    result = result * PRIME + ($enabledApis == null ? 43 : $enabledApis.hashCode());
    return result;
  }

  public String toString() {
    return "Project(projectId=" + this.getProjectId() + ", projectNumber=" + this.getProjectNumber()
           + ", billingEnabled=" + this.isBillingEnabled() + ", enabledApis=" + this
               .getEnabledApis() + ")";
  }

  public static class ProjectBuilder {

    private String projectId;
    private long projectNumber;
    private boolean billingEnabled;
    private List<String> enabledApis;

    ProjectBuilder() {
    }

    public ProjectBuilder projectId(String projectId) {
      this.projectId = projectId;
      return this;
    }

    public ProjectBuilder projectNumber(long projectNumber) {
      this.projectNumber = projectNumber;
      return this;
    }

    public ProjectBuilder billingEnabled(boolean billingEnabled) {
      this.billingEnabled = billingEnabled;
      return this;
    }

    public ProjectBuilder enabledApis(List<String> enabledApis) {
      this.enabledApis = enabledApis;
      return this;
    }

    public Project build() {
      return new Project(projectId, projectNumber, billingEnabled, enabledApis);
    }

    public String toString() {
      return "Project.ProjectBuilder(projectId=" + this.projectId + ", projectNumber="
             + this.projectNumber + ", billingEnabled=" + this.billingEnabled + ", enabledApis="
             + this.enabledApis + ")";
    }
  }
}
