package sonata.kernel.WimAdaptor.commons;

import java.util.ArrayList;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigureWanPayload {

  @JsonProperty("instance_id")
  private String instanceId;
  @JsonProperty("vim_list")
  private ArrayList<ComparableUuid> vimList;
  @JsonProperty("nap")
  private NetworkAttachmentPoints nap;

  public String getInstanceId() {
    return instanceId;
  }

  public ArrayList<ComparableUuid> getVimList() {
    return vimList;
  }

  public NetworkAttachmentPoints getNap() {
    return nap;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  public void setVimList(ArrayList<ComparableUuid> vimList) {
    this.vimList = vimList;
  }

  public void setNap(NetworkAttachmentPoints nap) {
    this.nap = nap;
  }



}
