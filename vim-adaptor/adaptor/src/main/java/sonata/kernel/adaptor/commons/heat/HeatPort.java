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

public class HeatPort {

  @JsonProperty("name")
  private String portName;

  @JsonProperty("IP_address")
  private String ipAddress;

  @JsonProperty("MAC_address")
  private String macAddress;
  
  @JsonProperty("floating_IP")
  private String floatinIp;

  public String getPortName() {
    return portName;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public String getMacAddress() {
    return macAddress;
  }

  public void setPortName(String portName) {
    this.portName = portName;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public void setMacAddress(String macAddress) {
    this.macAddress = macAddress;
  }

  public String getFloatinIp() {
    return floatinIp;
  }

  public void setFloatinIp(String floatinIp) {
    this.floatinIp = floatinIp;
  }


}
