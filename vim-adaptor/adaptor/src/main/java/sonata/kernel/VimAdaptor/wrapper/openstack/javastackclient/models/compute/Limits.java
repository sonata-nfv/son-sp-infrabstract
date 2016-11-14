package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.compute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Limits {

  private Absolute absolute;

  public Absolute getAbsolute() {
    return absolute;
  }

  public void setAbsolute(Absolute absolute) {
    this.absolute = absolute;
  }


}
