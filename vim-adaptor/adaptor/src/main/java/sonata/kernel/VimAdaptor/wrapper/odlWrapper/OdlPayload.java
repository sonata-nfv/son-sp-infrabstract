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
package sonata.kernel.VimAdaptor.wrapper.odlWrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class OdlPayload {

  /**
   * @param string
   * @param string2
   * @param odlList2
   */
  public OdlPayload(String action, String instanceId, String inSeg, String outSeg,
      ArrayList<OrderedMacAddress> odlList2) {
    this.inputSegment = inSeg;
    this.outputSegment = outSeg;
    if (odlList2 != null) {
      @SuppressWarnings("unchecked")
      ArrayList<OrderedMacAddress> clone = (ArrayList<OrderedMacAddress>) odlList2.clone();
      this.odlList = clone;
    }
    this.instanceId = instanceId;
    this.action = action;
  }

  @JsonProperty("action")
  String action;
  @JsonProperty("port_list")
  ArrayList<OrderedMacAddress> odlList;
  @JsonProperty("in_segment")
  String inputSegment;
  @JsonProperty("out_segment")
  String outputSegment;
  @JsonProperty("instance_id")
  String instanceId;

}
