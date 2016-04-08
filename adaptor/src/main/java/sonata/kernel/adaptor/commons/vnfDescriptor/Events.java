package sonata.kernel.adaptor.commons.vnfDescriptor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Events {

  private VNFEvent start;
  private VNFEvent stop;
  private VNFEvent restart;
  @JsonProperty("scale-in")
  private VNFEvent scale_in;
  @JsonProperty("scale-out")
  private VNFEvent scale_out;

  public VNFEvent getStart() {
    return start;
  }

  public VNFEvent getStop() {
    return stop;
  }

  public VNFEvent getRestart() {
    return restart;
  }

  public VNFEvent getScale_in() {
    return scale_in;
  }

  public VNFEvent getScale_out() {
    return scale_out;
  }

}
