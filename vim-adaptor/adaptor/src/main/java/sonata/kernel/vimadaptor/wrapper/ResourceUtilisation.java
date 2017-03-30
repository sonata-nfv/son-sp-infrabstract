/*
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the SONATA-NFV, UCL, NOKIA, NCSR Demokritos nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the SONATA project, funded by the European
 * Commission under Grant number 671517 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the SONATA partner consortium
 * (www.sonata-nfv.eu).
 *
 * @author Dario Valocchi (Ph.D.), UCL
 * 
 */

package sonata.kernel.vimadaptor.wrapper;

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
