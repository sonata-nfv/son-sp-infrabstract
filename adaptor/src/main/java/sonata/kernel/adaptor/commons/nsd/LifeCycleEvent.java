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

package sonata.kernel.adaptor.commons.nsd;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class LifeCycleEvent {

  private ArrayList<Event> start;
  private ArrayList<Event> stop;
  @JsonProperty("scale_out")
  private ArrayList<Event> scaleOut;


  public void setStart(ArrayList<Event> start) {
    this.start = start;
  }

  public void setStop(ArrayList<Event> stop) {
    this.stop = stop;
  }

  public void setScaleOut(ArrayList<Event> scaleOut) {
    this.scaleOut = scaleOut;
  }

  public ArrayList<Event> getStart() {
    return start;
  }

  public ArrayList<Event> getStop() {
    return stop;
  }

  public ArrayList<Event> getScaleOut() {
    return scaleOut;
  }

}
