package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.Image;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {

  private String name;
  private String container_format;
  private String id;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContainer_format() {
    return container_format;
  }

  public void setContainer_format(String container_format) {
    this.container_format = container_format;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
