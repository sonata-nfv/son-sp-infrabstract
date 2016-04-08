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

public class ResourceRequirements {

  private HypervisorParameters hyperviso_parameters;
  private VSwitchCapabilities vswitch_capabilities;
  private CPU cpu;
  private Memory memory;
  private Storage storage;
  private Network network;
  private PCIE pcie;

  public HypervisorParameters getHyperviso_parameters() {
    return hyperviso_parameters;
  }

  public VSwitchCapabilities getVswitch_capabilities() {
    return vswitch_capabilities;
  }

  public CPU getCpu() {
    return cpu;
  }

  public Memory getMemory() {
    return memory;
  }

  public Storage getStorage() {
    return storage;
  }

  public Network getNetwork() {
    return network;
  }

  public PCIE getPcie() {
    return pcie;
  }

}
