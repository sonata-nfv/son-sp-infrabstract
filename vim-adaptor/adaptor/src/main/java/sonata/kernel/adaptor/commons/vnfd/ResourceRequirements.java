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

package sonata.kernel.adaptor.commons.vnfd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceRequirements {

  @JsonProperty("hypervisor_parameters")
  private HypervisorParameters hypervisorParameters;
  @JsonProperty("vswitch_capabilities")
  private VSwitchCapabilities vswitchCapabilities;
  private Cpu cpu;
  private Memory memory;
  private Storage storage;
  private Network network;
  private Pcie pcie;



  public void setHypervisorParameters(HypervisorParameters hypervisorParameters) {
    this.hypervisorParameters = hypervisorParameters;
  }

  public void setVswitchCapabilities(VSwitchCapabilities vswitchCapabilities) {
    this.vswitchCapabilities = vswitchCapabilities;
  }

  public void setCpu(Cpu cpu) {
    this.cpu = cpu;
  }

  public void setMemory(Memory memory) {
    this.memory = memory;
  }

  public void setStorage(Storage storage) {
    this.storage = storage;
  }

  public void setNetwork(Network network) {
    this.network = network;
  }

  public void setPcie(Pcie pcie) {
    this.pcie = pcie;
  }

  public HypervisorParameters getHypervisorParameters() {
    return hypervisorParameters;
  }

  public VSwitchCapabilities getVswitchCapabilities() {
    return vswitchCapabilities;
  }

  public Cpu getCpu() {
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

  public Pcie getPcie() {
    return pcie;
  }

}