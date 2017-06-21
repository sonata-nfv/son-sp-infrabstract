package sonata.kernel.WimAdaptor.wrapper.vtn;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VTNCreateRequest {

  @JsonProperty("instance_id")
  private String instanceId;
  @JsonProperty("in_seg")
  private String inSeg;
  @JsonProperty("out_seg")
  private String outSeg;
  
  private OrderedSegment[] ports;

  public String getInstanceId() {
    return instanceId;
  }

  public String getInSeg() {
    return inSeg;
  }

  public String getOutSeg() {
    return outSeg;
  }

  public OrderedSegment[] getPorts() {
    return ports;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public void setInSeg(String inSeg) {
    this.inSeg = inSeg;
  }

  public void setOutSeg(String outSeg) {
    this.outSeg = outSeg;
  }

  public void setPorts(OrderedSegment[] ports) {
    this.ports = ports;
  }
  
  
  
  
}
