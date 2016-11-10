package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.composition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FloatingIpAttributes {

  private String floating_ip_address;
  private String port_id;

  public String getFloating_ip_address() {
    return floating_ip_address;
  }

  public void setFloating_ip_address(String floating_ip_address) {
    this.floating_ip_address = floating_ip_address;
  }

  public String getPort_id() {
    return port_id;
  }

  public void setPort_id(String port_id) {
    this.port_id = port_id;
  }

  @Override
  public String toString() {
    return "FloatingIpAttributes{" + "floating_ip_address='" + floating_ip_address + '\''
        + ", port_id='" + port_id + '\'' + '}';
  }
}
