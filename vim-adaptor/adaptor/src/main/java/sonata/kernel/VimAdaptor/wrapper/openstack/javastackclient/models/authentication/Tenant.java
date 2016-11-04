package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.authentication;

/**
 * Created by nle5220 on 20.10.2016.
 */
public class Tenant {
  private String id;
  private String description;
  private String enabled;
  private String name;

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getEnabled() {
    return enabled;
  }

  public void setEnabled(String enabled) {
    this.enabled = enabled;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
