/**
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

package sonata.kernel.WimAdaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceRecord {

  @JsonProperty("descriptor_version")
  private String descriptorVersion;
  private Status status;
  @JsonProperty("id")
  private String id;
  @JsonProperty("descriptor_reference")
  private String descriptorReference;



  public Status getStatus() {
    return status;
  }


  public void setStatus(Status status) {
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getDescriptorVersion() {
    return descriptorVersion;
  }


  public void setDescriptorVersion(String descriptorVersion) {
    this.descriptorVersion = descriptorVersion;
  }


  public String getDescriptorReference() {
    return descriptorReference;
  }


  public void setDescriptorReference(String descriptorReference) {
    this.descriptorReference = descriptorReference;
  }



}
