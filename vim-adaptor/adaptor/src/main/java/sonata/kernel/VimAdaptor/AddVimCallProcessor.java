/**
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the SONATA-NFV, UCL, NOKIA, NCSR Demokritos nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the SONATA project, funded by the European
 * Commission under Grant number 671517 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the SONATA partner consortium
 * (www.sonata-nfv.eu).
 *
 * @author Dario Valocchi (Ph.D.), UCL
 * 
 */

package sonata.kernel.VimAdaptor;

import org.json.JSONObject;
import org.json.JSONTokener;

import sonata.kernel.VimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.VimAdaptor.wrapper.WrapperBay;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;

import java.util.Observable;
import java.util.UUID;

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

    // process json message to extract the new Wrapper configurations
    // and ask the bay to create and register it

    JSONTokener tokener = new JSONTokener(message.getBody());

    WrapperConfiguration config = new WrapperConfiguration();

    JSONObject jsonObject = (JSONObject) tokener.nextValue();
    String wrapperType = jsonObject.getString("wr_type");
    String vimVendor = jsonObject.getString("vim_type");
    String vimEndpoint = jsonObject.getString("vim_address");
    String authUser = jsonObject.getString("username");
    String authPass = jsonObject.getString("pass");
    String tenantName = jsonObject.getString("tenant");

    String tenantExtNet = null;
    String tenantExtRouter = null;
    String computeVimRef = null;

    if (wrapperType.equals("compute")) {
      tenantExtNet = jsonObject.getString("tenant_ext_net");
      tenantExtRouter = jsonObject.getString("tenant_ext_router");
    } else if (wrapperType.equals("networking")) {
      computeVimRef = jsonObject.getString("compute_uuid");
    }
    config.setUuid(UUID.randomUUID().toString());
    config.setWrapperType(wrapperType);
    config.setVimVendor(vimVendor);
    config.setVimEndpoint(vimEndpoint);
    config.setAuthUserName(authUser);
    config.setAuthPass(authPass);
    config.setTenantName(tenantName);
    config.setTenantExtNet(tenantExtNet);
    config.setTenantExtRouter(tenantExtRouter);

    String output = null;
    boolean out = true;
    if (wrapperType.equals("compute")) {
      output = WrapperBay.getInstance().registerComputeWrapper(config);
    } else if (wrapperType.equals("storage")) {
      // TODO
      output = "";
    } else if (wrapperType.equals("networking")) {
      output = WrapperBay.getInstance().registerNetworkingWrapper(config, computeVimRef);
    }
    this.sendResponse(output);

    return out;
  }

  // private void sendError(String message) {
  //
  // String jsonError =
  // "{\"status\":\"ERROR\",\"message\":\"" + message + "\"}";
  // ServicePlatformMessage spMessage = new ServicePlatformMessage(jsonError, "application/json",
  // this.getMessage().getTopic(), this.getMessage().getSid(), null);
  // this.sendToMux(spMessage);
  // }

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
