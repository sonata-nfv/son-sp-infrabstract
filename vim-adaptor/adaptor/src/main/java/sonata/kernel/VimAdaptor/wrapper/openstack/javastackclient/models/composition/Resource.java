package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.composition;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource<T> {

  String resource_name;
  String resource_type;
  String physical_resource_id;

  T attributes;

  public String getPhysical_resource_id() {
    return physical_resource_id;
  }

  public void setPhysical_resource_id(String physical_resource_id) {
    this.physical_resource_id = physical_resource_id;
  }

  public T getAttributes() {
    return attributes;
  }

  public void setAttributes(T attributes) {
    this.attributes = attributes;
  }

  public String getResource_name() {
    return resource_name;
  }

  public void setResource_name(String resource_name) {
    this.resource_name = resource_name;
  }

  public String getResource_type() {
    return resource_type;
  }

  public void setResource_type(String resource_type) {
    this.resource_type = resource_type;
  }


}
