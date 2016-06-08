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

import sonata.kernel.adaptor.commons.vnfd.Unit.MemoryUnit;

public class Flavor {

  @JsonProperty("cpu_core")
  private int cpu;

  private int memory;
  @JsonProperty("memory_unit")
  private MemoryUnit memoryUnit;

  private int storage;
  @JsonProperty("storage_unit")
  private MemoryUnit storageUnit;

  public int getCpu() {
    return cpu;
  }

  public int getMemory() {
    return memory;
  }

  public MemoryUnit getMemoryUnit() {
    return memoryUnit;
  }

  public int getStorage() {
    return storage;
  }

  public MemoryUnit getStorageUnit() {
    return storageUnit;
  }

  public void setCpu(int cpu) {
    this.cpu = cpu;
  }

  public void setMemory(int memory) {
    this.memory = memory;
  }

  public void setMemoryUnit(MemoryUnit memoryUnit) {
    this.memoryUnit = memoryUnit;
  }

  public void setStorage(int storage) {
    this.storage = storage;
  }

  public void setStorageUnit(MemoryUnit storageUnit) {
    this.storageUnit = storageUnit;
  }



}
