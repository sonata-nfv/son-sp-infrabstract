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

public class NicCapabilities {

  @JsonProperty("SR-IOV")
  private boolean srIov;
  private boolean mirroring;

  public boolean isMirroring() {
    return mirroring;
  }

  public boolean isSrIov() {
    return srIov;
  }

  public void setSrIov(boolean srIov) {
    this.srIov = srIov;
  }

  public void setMirroring(boolean mirroring) {
    this.mirroring = mirroring;
  }

}
