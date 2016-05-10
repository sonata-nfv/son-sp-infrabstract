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

public class Pcie {

  @JsonProperty("SR-IOV")
  private boolean srIov;

  @JsonProperty("device_pass_through")
  private boolean devicePassThrough;

  public boolean isSrIov() {
    return srIov;
  }

  public boolean isDevicePassThrough() {
    return devicePassThrough;
  }

  public void setSrIov(boolean srIov) {
    this.srIov = srIov;
  }

  public void setDevicePassThrough(boolean devicePassThrough) {
    this.devicePassThrough = devicePassThrough;
  }

}
