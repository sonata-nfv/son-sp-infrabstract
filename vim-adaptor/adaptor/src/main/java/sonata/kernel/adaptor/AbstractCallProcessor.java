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

package sonata.kernel.adaptor;


import sonata.kernel.adaptor.messaging.ServicePlatformMessage;

import java.util.Observer;

public abstract class AbstractCallProcessor implements Runnable, Observer {

  public ServicePlatformMessage getMessage() {
    return message;
  }

  public String getSid() {
    return sid;
  }

  public AdaptorMux getMux() {
    return mux;
  }

  private ServicePlatformMessage message;
  private String sid;
  private AdaptorMux mux;

  /**
   * Abtract class for an API call processor. The processo runs on a thread an processes a
   * ServicePlatformMessage.
   * 
   * @param message The ServicePlatformMessage to process
   * @param sid the Session Identifier for this API call
   * @param mux the AdaptorMux where response messages are to be sent.
   */
  public AbstractCallProcessor(ServicePlatformMessage message, String sid, AdaptorMux mux) {
    this.message = message;
    this.sid = sid;
    this.mux = mux;
  }

  protected void sendToMux(ServicePlatformMessage message) {
    mux.enqueue(message);
  }

  @Override
  public void run() {

    this.process(message);

  }

  public abstract boolean process(ServicePlatformMessage message);

}
