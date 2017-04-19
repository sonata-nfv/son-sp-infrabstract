package sonata.kernel.vimadaptor.wrapper.openstack.javastackclient.models.authenticationv3;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {

  ArrayList<CatalogItem> catalog = new ArrayList<>();
  Project project;

  public ArrayList<CatalogItem> getCatalog() {
    return catalog;
  }

  public void setCatalog(ArrayList<CatalogItem> catalog) {
    this.catalog = catalog;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }


}

