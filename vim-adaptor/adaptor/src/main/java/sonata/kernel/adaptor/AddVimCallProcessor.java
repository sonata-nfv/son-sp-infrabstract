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
import sonata.kernel.adaptor.wrapper.WrapperBay;
import sonata.kernel.adaptor.wrapper.WrapperConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;

public class AddVimCallProcessor extends AbstractCallProcessor {

  /**
   * Generate a CallProcessor to process an API call to create a new VIM wrapper
   * 
   * @param message the API call message
   * @param sid the session ID of thi API call
   * @param mux the Adaptor Mux to which send responses.
   */
  public AddVimCallProcessor(ServicePlatformMessage message, String sid, AdaptorMux mux) {
    super(message, sid, mux);

  }

  @Override
  public boolean process(ServicePlatformMessage message) {
    boolean out = true;
    // process json message to extract the new Wrapper configurations
    // and ask the bay to create and register it

    JSONTokener tokener = new JSONTokener(message.getBody());

    WrapperConfiguration config = new WrapperConfiguration();

    JSONObject jsonObject = (JSONObject) tokener.nextValue();
    String wrapperType = jsonObject.getString("wr_type");
    String vimType = jsonObject.getString("vim_type");
    String vimEndpoint = jsonObject.getString("vim_address");
    String authUser = jsonObject.getString("username");
    String authPass = jsonObject.getString("pass");
    String tenantName = jsonObject.getString("tenant");

    config.setUuid(this.getSid());
    config.setWrapperType(wrapperType);
    config.setVimType(vimType);
    config.setVimEndpoint(vimEndpoint);
    config.setAuthUserName(authUser);
    config.setAuthPass(authPass);
    config.setTenantName(tenantName);
    String output = null;
    if (wrapperType.equals("compute")) {
      output = WrapperBay.getInstance().registerComputeWrapper(config);
    } else if (wrapperType.equals("storage")) {
      // TODO
      output = "";
    } else if (wrapperType.equals("network")) {
      // TODO
      output = "";
    }
    this.sendResponse(output);

    return out;
  }

  private void sendError(String message) {

    String jsonError =
        "{\"status\":\"error,\"sid\":\"" + this.getSid() + "\",\"message\":\"" + message + "\"}";
    ServicePlatformMessage spMessage = new ServicePlatformMessage(jsonError, "application/json",
        this.getMessage().getTopic(), this.getMessage().getSid(), null);
    this.sendToMux(spMessage);
  }

  private void sendResponse(String message) {
    ServicePlatformMessage spMessage = new ServicePlatformMessage(message, "application/json",
        this.getMessage().getTopic(), this.getMessage().getSid(), null);
    this.sendToMux(spMessage);
  }

  @Override
  public void update(Observable observable, Object arg) {
    // This call does not need to be updated by any observable (wrapper).
  }
}
