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

package sonata.kernel.WimAdaptor.commons.vnfd;

import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.WimAdaptor.commons.vnfd.Unit.BandwidthUnit;

public class Network {


  @JsonProperty("network_interface_bandwidth")
  private double networkInterfaceBandwidth;
  @JsonProperty("network_interface_bandwidth_unit")
  private BandwidthUnit networkInterfaceBandwidthUnit;
  @JsonProperty("network_interface_card_capabilities")
  private NicCapabilities networkInterfaceCardCapabilities;
  @JsonProperty("data_processing_acceleration_library")
  private String dataProcessingAccelerationLibrary;


  public void setNetworkInterfaceBandwidth(double networkInterfaceBandwidth) {
    this.networkInterfaceBandwidth = networkInterfaceBandwidth;
  }

  public void setNetworkInterfaceBandwidthUnit(BandwidthUnit networkInterfaceBandwidthUnit) {
    this.networkInterfaceBandwidthUnit = networkInterfaceBandwidthUnit;
  }

  public void setNetworkInterfaceCardCapabilities(
      NicCapabilities networkInterfaceCardCapabilities) {
    this.networkInterfaceCardCapabilities = networkInterfaceCardCapabilities;
  }

  public void setDataProcessingAccelerationLibrary(String dataProcessingAccelerationLibrary) {
    this.dataProcessingAccelerationLibrary = dataProcessingAccelerationLibrary;
  }

  public double getNetworkInterfaceBandwidth() {
    return networkInterfaceBandwidth;
  }

  public BandwidthUnit getNetworkInterfaceBandwidthUnit() {
    return networkInterfaceBandwidthUnit;
  }

  public NicCapabilities getNetworkInterfaceCardCapabilities() {
    return networkInterfaceCardCapabilities;
  }

  public String getDataProcessingAccelerationLibrary() {
    return dataProcessingAccelerationLibrary;
  }
}
