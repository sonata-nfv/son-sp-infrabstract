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
package sonata.kernel.VimAdaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;

public class FunctionDeployPayload {

  @JsonProperty("vim_uuid")
  private String vimUuid;
  @JsonProperty("service_instance_id")
  private String serviceInstanceId;
  @JsonProperty("vnfd")
  private VnfDescriptor vnfd;

  public String getVimUuid() {
    return vimUuid;
  }

  public VnfDescriptor getVnfd() {
    return vnfd;
  }

  public void setVimUuid(String vimUuid) {
    this.vimUuid = vimUuid;
  }

  public void setVnfd(VnfDescriptor vnfd) {
    this.vnfd = vnfd;
  }

  public String getServiceInstanceId() {
    return serviceInstanceId;
  }

  public void setServiceInstanceId(String serviceInstanceId) {
    this.serviceInstanceId = serviceInstanceId;
  }



}
