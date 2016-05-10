package sonata.kernel.adaptor.commons;

import java.util.ArrayList;

public class DeploymentResponse {

  private String instanceName;
  private String instanceVimUuid;
  private ArrayList<VimInstanceInfo> instanceInfo;
  private String errorCode=null;

  public String getInstanceName() {
    return instanceName;
  }

  public String getInstanceVimUuid() {
    return instanceVimUuid;
  }

  public ArrayList<VimInstanceInfo> getInstanceInfo() {
    return instanceInfo;
  }

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }

  public void setInstanceVimUuid(String instanceVimUuid) {
    this.instanceVimUuid = instanceVimUuid;
  }

  public void setInstanceInfo(ArrayList<VimInstanceInfo> instanceInfo) {
    this.instanceInfo = instanceInfo;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

}
