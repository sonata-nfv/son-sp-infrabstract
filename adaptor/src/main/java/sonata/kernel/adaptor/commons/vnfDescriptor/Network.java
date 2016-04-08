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
package sonata.kernel.adaptor.commons.vnfDescriptor;

import sonata.kernel.adaptor.commons.vnfDescriptor.Unit.BandwidthUnit;

public class Network {



  private double network_interface_bandwidth;
  private BandwidthUnit network_interface_bandwidth_unit;
  private NICCapabilities network_interface_card_capabilities;
  private String data_processing_acceleration_library;

  public double getNetwork_interface_bandwidth() {
    return network_interface_bandwidth;
  }

  public BandwidthUnit getNetwork_interface_bandwidth_unit() {
    return network_interface_bandwidth_unit;
  }

  public NICCapabilities getNetwork_interface_card_capabilities() {
    return network_interface_card_capabilities;
  }

  public String getData_processing_acceleration_library() {
    return data_processing_acceleration_library;
  }
}
