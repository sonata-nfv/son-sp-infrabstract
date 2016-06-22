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

import sonata.kernel.adaptor.commons.nsd.ConnectionPointRecord;

import java.util.ArrayList;

public class VnfcInstance {

  private String id;

  @JsonProperty("vim_id")
  private String vimId;

  @JsonProperty("vc_id")
  private String vcId;

  @JsonProperty("connection_points")
  private ArrayList<ConnectionPointRecord> connectionPoints;

  public String getId() {
    return id;
  }

  public String getVimId() {
    return vimId;
  }

  public String getVcId() {
    return vcId;
  }


  public void setId(String id) {
    this.id = id;
  }

  public void setVimId(String vimId) {
    this.vimId = vimId;
  }

  public void setVcId(String vcId) {
    this.vcId = vcId;
  }

  public ArrayList<ConnectionPointRecord> getConnectionPoints() {
    return connectionPoints;
  }

  public void setConnectionPoints(ArrayList<ConnectionPointRecord> connectionPoints) {
    this.connectionPoints = connectionPoints;
  }



}
