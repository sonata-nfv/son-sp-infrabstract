/*
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the SONATA-NFV, UCL, NOKIA, NCSR Demokritos nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the SONATA project, funded by the European
 * Commission under Grant number 671517 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the SONATA partner consortium
 * (www.sonata-nfv.eu).
 *
 * @author Dario Valocchi (Ph.D.), UCL
 * 
 */

package sonata.kernel.vimadaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;


public class FunctionDeployResponse {

  private String instanceName;
  private String instanceVimUuid;
  private String message;
  @JsonProperty("request_status")
  private String requestStatus;
  private String vimUuid;

  @JsonProperty("ip_mapping")
  private ArrayList<IpMapping> ipMappingList;
  private VnfRecord vnfr;

  public FunctionDeployResponse() {
    this.ipMappingList = new ArrayList<IpMapping>();
  }

  public void addIp(IpMapping unit) {
    this.ipMappingList.add(unit);
  }


  public String getInstanceName() {
    return instanceName;
  }

  public String getInstanceVimUuid() {
    return instanceVimUuid;
  }

  public String getMessage() {
    return message;
  }

  public String getRequestStatus() {
    return requestStatus;
  }

  public String getVimUuid() {
    return vimUuid;
  }

  public ArrayList<IpMapping> getipMappingList() {
    return ipMappingList;
  }

  public VnfRecord getVnfr() {
    return vnfr;
  }

  public void setInstanceName(String instanceName) {
    this.instanceName = instanceName;
  }


  public void setInstanceVimUuid(String instanceVimUuid) {
    this.instanceVimUuid = instanceVimUuid;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setRequestStatus(String requestStatus) {
    this.requestStatus = requestStatus;
  }

  public void setVimUuid(String vimUuid) {
    this.vimUuid = vimUuid;
  }

  public void setIpMappingList(ArrayList<IpMapping> ipMappingList) {
    this.ipMappingList = ipMappingList;
  }

  public void setVnfr(VnfRecord vnfr) {
    this.vnfr = vnfr;
  }



}
