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

package sonata.kernel.adaptor.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceUtilisation {

  @JsonProperty("CPU_used")
  public int usedCores;
  @JsonProperty("CPU_total")
  public int totCores;
  @JsonProperty("memory_used")
  public int usedMemory;
  @JsonProperty("memory_total")
  public int totMemory;

  public int getUsedCores() {
    return usedCores;
  }

  public int getTotCores() {
    return totCores;
  }

  public int getUsedMemory() {
    return usedMemory;
  }

  public int getTotMemory() {
    return totMemory;
  }

  public void setUsedCores(int usedCores) {
    this.usedCores = usedCores;
  }

  public void setTotCores(int totCores) {
    this.totCores = totCores;
  }

  public void setUsedMemory(int usedMemory) {
    this.usedMemory = usedMemory;
  }

  public void setTotMemory(int totMemory) {
    this.totMemory = totMemory;
  }

  @Override
  public String toString() {
    String out = "totMem: " + totMemory + "/usedMem: " + usedMemory + "\n";
    out += "totCore: " + totCores + "/usedMem: " + usedCores;
    return out;
  }
}
