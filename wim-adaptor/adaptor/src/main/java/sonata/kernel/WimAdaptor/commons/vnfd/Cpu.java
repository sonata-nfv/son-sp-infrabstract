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

public class Cpu {
  private int vcpus;
  @JsonProperty("cpu_support_accelerator")
  private String cpuSupportAccelerator;
  @JsonProperty("cpu_model")
  private String cpuModel;
  @JsonProperty("cpu_clock_speed")
  private String cpuClockSpeed;

  public int getVcpus() {
    return vcpus;
  }


  public void setVcpus(int vcpus) {
    this.vcpus = vcpus;
  }

  public void setCpuSupportAccelerator(String cpuSupportAccelerator) {
    this.cpuSupportAccelerator = cpuSupportAccelerator;
  }

  public void setCpuModel(String cpuModel) {
    this.cpuModel = cpuModel;
  }

  public void setCpuClockSpeed(String cpuClockSpeed) {
    this.cpuClockSpeed = cpuClockSpeed;
  }


  public String getCpuSupportAccelerator() {
    return cpuSupportAccelerator;
  }


  public String getCpuModel() {
    return cpuModel;
  }


  public String getCpuClockSpeed() {
    return cpuClockSpeed;
  }
}
