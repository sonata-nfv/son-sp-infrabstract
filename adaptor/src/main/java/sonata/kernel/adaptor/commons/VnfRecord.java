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

public class VnfRecord {

  @JsonProperty("descriptor_version")
  private String descriptorVersion;
  private String id;
  private Status status;
  
  @JsonProperty("vnf_address")
  private String vnfAddress;
  
  @JsonProperty("virtual_deployment_units")
  private ArrayList<VduRecord> virtualDeploymentUnits;


  public VnfRecord() {
    this.virtualDeploymentUnits = new ArrayList<VduRecord>();
  }

  public void addVdu(VduRecord unit) {
    this.virtualDeploymentUnits.add(unit);
  }

  public String getId() {
    return id;
  }

  public Status getStatus() {
    return status;
  }

  public String getVnf_address() {
    return vnfAddress;
  }

  public String getDescriptor_version() {
    return descriptorVersion;
  }

  public void setDescriptor_version(String descriptorVersion) {
    this.descriptorVersion = descriptorVersion;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void setVnf_address(String address) {
    this.vnfAddress = address;
  }

  public ArrayList<VduRecord> getVirtual_deployment_units() {
    return virtualDeploymentUnits;
  }



}
