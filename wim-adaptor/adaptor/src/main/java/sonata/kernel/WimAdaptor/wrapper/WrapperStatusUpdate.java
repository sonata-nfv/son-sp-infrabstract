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

package sonata.kernel.WimAdaptor.wrapper;

public class WrapperStatusUpdate {

  private String sid;
  private String status;
  private String body;

  /**
   * Standard constructor for a WrapperStatusUpdate object. To be used in the Observer pattern
   * between AbstractCallProcessor and Wrapper.
   * 
   * @param sid The session ID of the API call.
   * @param status a String representing the Wrapper status.
   * @param body a String with the detailed description of the status update.
   */
  public WrapperStatusUpdate(String sid, String status, String body) {
    this.sid = sid;
    this.status = status;
    this.body = body;
  }

  public String getSid() {
    return sid;
  }

  public String getStatus() {
    return status;
  }

  public String getBody() {
    return body;
  }

}
