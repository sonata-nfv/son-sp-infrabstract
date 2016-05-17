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

public class Events {

  private VnfEvent start;
  private VnfEvent stop;
  private VnfEvent restart;
  @JsonProperty("scale-in")
  private VnfEvent scaleIn;
  @JsonProperty("scale-out")
  private VnfEvent scaleOut;

  public VnfEvent getStart() {
    return start;
  }

  public VnfEvent getStop() {
    return stop;
  }

  public VnfEvent getRestart() {
    return restart;
  }
  
  public VnfEvent getScaleIn() {
    return scaleIn;
  }

  public VnfEvent getScaleOut() {
    return scaleOut;
  }

  public void setStart(VnfEvent start) {
    this.start = start;
  }

  public void setStop(VnfEvent stop) {
    this.stop = stop;
  }

  public void setRestart(VnfEvent restart) {
    this.restart = restart;
  }

  public void setScaleIn(VnfEvent scaleIn) {
    this.scaleIn = scaleIn;
  }

  public void setScaleOut(VnfEvent scaleOut) {
    this.scaleOut = scaleOut;
  }

}
