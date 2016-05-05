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

package sonata.kernel.adaptor.commons.nsd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkFunction {

  @JsonProperty("vnf_id")
  private String vnfId;
  @JsonProperty("vnf_vendor")
  private String vnfVendor;
  @JsonProperty("vnf_name")
  private String vnfName;
  @JsonProperty("vnf_version")
  private String vnfVersion;
  private String description;

  public String getVnf_id() {
    return vnfId;
  }

  public String getVnf_vendor() {
    return vnfVendor;
  }

  public String getVnf_name() {
    return vnfName;
  }

  public String getVnf_version() {
    return vnfVersion;
  }

  public String getDescription() {
    return description;
  }

  public String getVnfId() {
    return vnfId;
  }

  public String getVnfVendor() {
    return vnfVendor;
  }

  public String getVnfName() {
    return vnfName;
  }

  public String getVnfVersion() {
    return vnfVersion;
  }

  public void setVnfId(String vnfId) {
    this.vnfId = vnfId;
  }

  public void setVnfVendor(String vnfVendor) {
    this.vnfVendor = vnfVendor;
  }

  public void setVnfName(String vnfName) {
    this.vnfName = vnfName;
  }

  public void setVnfVersion(String vnfVersion) {
    this.vnfVersion = vnfVersion;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
