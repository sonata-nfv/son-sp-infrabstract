/**
 * @author Dario Valocchi (Ph.D.)
 * @mail d.valocchi@ucl.ac.uk
 * 
 *       Copyright 2016 [Dario Valocchi]
 * 
 *       Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *       except in compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *       Unless required by applicable law or agreed to in writing, software distributed under the
 *       License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *       either express or implied. See the License for the specific language governing permissions
 *       and limitations under the License.
 * 
 */

package sonata.kernel.adaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class DeployServiceResponse {

  private String instanceName;
  private String instanceVimUuid;
  @JsonProperty("request_status")
  private Status requestStatus;
  private ServiceRecord nsr;
  private ArrayList<VnfRecord> vnfrs;
  private String errorCode;

  public DeployServiceResponse() {
    this.vnfrs = new ArrayList<VnfRecord>();
  }

  public void setStatus(Status status) {
    this.requestStatus = status;
  }

  public void setNsr(ServiceRecord record) {
    this.nsr = record;
  }

  public void addVnfRecord(VnfRecord vnfr) {
    this.vnfrs.add(vnfr);
  }

  public ServiceRecord getNsr() {
    return nsr;
  }

  public Status getRequestStatus() {
    return requestStatus;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public String getInstanceVimUuid() {
    return instanceVimUuid;
  }

  public ArrayList<VnfRecord> getVnfrs() {
    return vnfrs;
  }

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }

  public void setInstanceVimUuid(String instanceVimUuid) {
    this.instanceVimUuid = instanceVimUuid;
  }

  public void setRequestStatus(Status requestStatus) {
    this.requestStatus = requestStatus;
  }

  public void setVnfrs(ArrayList<VnfRecord> vnfrs) {
    this.vnfrs = vnfrs;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

}
