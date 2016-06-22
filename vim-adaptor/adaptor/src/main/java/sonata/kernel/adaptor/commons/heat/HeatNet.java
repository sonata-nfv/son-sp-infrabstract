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

package sonata.kernel.adaptor.commons.heat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HeatNet {


  @JsonProperty("net_name")
  private String netName;

  @JsonProperty("net_id")
  private String netId;

  @JsonProperty("subnet_name")
  private String subnetName;

  @JsonProperty("subnet_id")
  private String subnetId;

  @JsonProperty("segmentation_id")
  private int segmentationId;

  private String cidr;

  public String getNetName() {
    return netName;
  }

  public String getNetId() {
    return netId;
  }

  public String getSubnetName() {
    return subnetName;
  }

  public String getSubnetId() {
    return subnetId;
  }

  public int getSegmentationId() {
    return segmentationId;
  }

  public String getCidr() {
    return cidr;
  }

  public void setNetName(String netName) {
    this.netName = netName;
  }

  public void setNetId(String netId) {
    this.netId = netId;
  }

  public void setSubnetName(String subnetName) {
    this.subnetName = subnetName;
  }

  public void setSubnetId(String subnetId) {
    this.subnetId = subnetId;
  }

  public void setSegmentationId(int segmentationId) {
    this.segmentationId = segmentationId;
  }

  public void setCidr(String cidr) {
    this.cidr = cidr;
  }

}
