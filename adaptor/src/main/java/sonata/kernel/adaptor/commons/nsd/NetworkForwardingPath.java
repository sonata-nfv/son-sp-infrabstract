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

import sonata.kernel.adaptor.commons.vnfd.ConnectionPointReference;

import java.util.ArrayList;


public class NetworkForwardingPath {

  @JsonProperty("fp_id")
  private String fpId;
  private String policy;
  @JsonProperty("connection_points")
  private ArrayList<ConnectionPointReference> connectionPoints;

  public String getFp_id() {
    return fpId;
  }

  public String getPolicy() {
    return policy;
  }

  public ArrayList<ConnectionPointReference> getConnection_points() {
    return connectionPoints;
  }

  public String getFpId() {
    return fpId;
  }

  public ArrayList<ConnectionPointReference> getConnectionPoints() {
    return connectionPoints;
  }

  public void setFpId(String fpId) {
    this.fpId = fpId;
  }

  public void setPolicy(String policy) {
    this.policy = policy;
  }

  public void setConnectionPoints(ArrayList<ConnectionPointReference> connectionPoints) {
    this.connectionPoints = connectionPoints;
  }

}
