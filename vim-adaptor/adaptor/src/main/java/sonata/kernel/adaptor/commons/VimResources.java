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

package sonata.kernel.adaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VimResources {

  @JsonProperty("vim_uuid")
  private String vimUuid;
  @JsonProperty("memory_used")
  private int memoryUsed;
  @JsonProperty("memory_total")
  private int memoryTotal;
  @JsonProperty("core_used")
  private int coreUsed;
  @JsonProperty("core_total")
  private int coreTotal;

  public String getVimUuid() {
    return vimUuid;
  }

  public int getMemoryUsed() {
    return memoryUsed;
  }

  public int getMemoryTotal() {
    return memoryTotal;
  }

  public int getCoreUsed() {
    return coreUsed;
  }

  public int getCoreTotal() {
    return coreTotal;
  }

  public void setVimUuid(String vimUuid) {
    this.vimUuid = vimUuid;
  }

  public void setMemoryUsed(int memoryUsed) {
    this.memoryUsed = memoryUsed;
  }

  public void setMemoryTotal(int memoryTotal) {
    this.memoryTotal = memoryTotal;
  }

  public void setCoreUsed(int coreUsed) {
    this.coreUsed = coreUsed;
  }

  public void setCoreTotal(int coreTotal) {
    this.coreTotal = coreTotal;
  }


}
