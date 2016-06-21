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

import org.json.JSONObject;
import org.json.JSONTokener;

import sonata.kernel.adaptor.messaging.ServicePlatformMessage;
import sonata.kernel.adaptor.wrapper.ComputeWrapper;
import sonata.kernel.adaptor.wrapper.WrapperBay;
import sonata.kernel.adaptor.wrapper.WrapperStatusUpdate;

import java.util.Observable;

public class RemoveServiceCallProcessor extends AbstractCallProcessor {

  /**
   * Generate a CallProcessor to process an API call to create a new VIM wrapper
   * 
   * @param message the API call message
   * @param sid the session ID of thi API call
   * @param mux the Adaptor Mux to which send responses.
   */
  public RemoveServiceCallProcessor(ServicePlatformMessage message, String sid, AdaptorMux mux) {
    super(message, sid, mux);

  }

  @Override
  public boolean process(ServicePlatformMessage message) {
    // process json message to get the wrapper type and UUID
    // and de-register it
    JSONTokener tokener = new JSONTokener(message.getBody());
    JSONObject jsonObject = (JSONObject) tokener.nextValue();
    String instanceUuid = jsonObject.getString("instance_uuid");
    String vimUuid = jsonObject.getString("vim_uuid");
    ComputeWrapper wr = WrapperBay.getInstance().getComputeWrapper(vimUuid);
    wr.addObserver(this);
    wr.removeService(instanceUuid);

    boolean out = true;
    return out;
  }

  private void sendResponse(String message) {
    ServicePlatformMessage spMessage = new ServicePlatformMessage(message, "application/json",
        this.getMessage().getTopic(), this.getMessage().getSid(), this.getMessage().getReplyTo());
    this.sendToMux(spMessage);
  }

  @Override
  public void update(Observable observable, Object arg) {
   
    WrapperStatusUpdate update = (WrapperStatusUpdate) arg;  
    System.out.println("[RemoveService] Received an update:");
    System.out.println(update.getBody());
    
    sendResponse(update.getBody());
    return;
  }
}
