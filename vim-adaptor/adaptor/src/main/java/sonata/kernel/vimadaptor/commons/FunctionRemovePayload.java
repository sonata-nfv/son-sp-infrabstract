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
 * @author Thomas Soenen, imec
 * 
 */

package sonata.kernel.vimadaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.vimadaptor.commons.vnfd.VnfDescriptor;

public class FunctionRemovePayload {

  @JsonProperty("service_instance_id")
  private String serviceInstanceId;
  @JsonProperty("vim_uuid")
  private String vimUuid;
  @JsonProperty("vnf_uuid")
  private String vnfUuid;
  // @JsonProperty("vnfd")
  // private VnfDescriptor vnfd;

  public String getServiceInstanceId() {
    return serviceInstanceId;
  }

  public String getVimUuid() {
    return vimUuid;
  }

  public String getVnfUuid() {
    return vnfUuid;
  }
  // public VnfDescriptor getVnfd() {
  //   return vnfd;
  // }

  public void setServiceInstanceId(String serviceInstanceId) {
    this.serviceInstanceId = serviceInstanceId;
  }

  public void setVimUuid(String vimUuid) {
    this.vimUuid = vimUuid;
  }

  public void setVnfUuid(String vnfUuid) {
    this.vnfUuid = vnfUuid;
  }

  // public void setVnfd(VnfDescriptor vnfd) {
  //   this.vnfd = vnfd;
  // }



}
