package sonata.kernel.WimAdaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeconfigureWanPayload {

  @JsonProperty("service_instance_id")
  private String serviceInstanceId;

  public String getServiceInstanceId() {
    return serviceInstanceId;
  }

  public void setServiceInstanceId(String serviceInstanceId) {
    this.serviceInstanceId = serviceInstanceId;
  }
  
}
