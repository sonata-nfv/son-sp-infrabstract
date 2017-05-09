package sonata.kernel.WimAdaptor.commons;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WimRecord {

  private String uuid;
  private String name;
  @JsonProperty("attached_vims")
  private ArrayList<String> attachedVims;
  
  public String getUuid() {
    return uuid;
  }
  public String getName() {
    return name;
  }
  public ArrayList<String> getAttachedVims() {
    return attachedVims;
  }
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setAttachedVims(ArrayList<String> attachedVims) {
    this.attachedVims = attachedVims;
  }
  
  @Override
  public String toString(){
    String out = "uuid: "+this.uuid+"\n"+
        "name: "+this.name+"\n"+
        "attachedVim: "+this.attachedVims.toString();
        
    return out;
  }
  
  
}
