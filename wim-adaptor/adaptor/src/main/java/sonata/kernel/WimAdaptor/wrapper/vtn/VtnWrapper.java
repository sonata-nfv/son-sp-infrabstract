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

package sonata.kernel.WimAdaptor.wrapper.vtn;

import org.slf4j.LoggerFactory;

import sonata.kernel.WimAdaptor.ConfigureWimCallProcessor;
import sonata.kernel.WimAdaptor.wrapper.WimWrapper;
import sonata.kernel.WimAdaptor.wrapper.WrapperConfiguration;

public class VtnWrapper extends WimWrapper {

  private static final org.slf4j.Logger Logger =
      LoggerFactory.getLogger(ConfigureWimCallProcessor.class);

  public VtnWrapper(WrapperConfiguration config) {
    super(config);
  }

  @Override
  public String getType() {
    return null;
  }


  @Override
  public boolean configureNetwork(String instanceId) {
    boolean out = true;
    VtnClient client = new VtnClient(this.config.getWimEndpoint(), this.config.getAuthUserName(),
        this.config.getAuthPass());
    /**
     * Logger.info("Setting up the VTN for the service"); out = out && client.setupVtn(instanceId);
     * if (out){ Logger.info("VTN created"); } else { Logger.error("Unable to create VTN"); }
     * Comment it out, as for the moment, new vtn will not be created
     */
    Logger.info("Setting up the flow rules in the VTN");
    out = out && client.setupFlow("vtn7", "green");
    if (out) {
      Logger.info("Flow rules created");
    } else {
      Logger.error("Unable to create flow rules. GOING ON NONETHELESS");
    }
    // FIXME This is a DEBUG edit! ! Remove me ASAP!
    // return out;
    return true;
  }

  @Override
  public boolean removeNetConfiguration(String instanceId) {
    String condition = "green";
    VtnClient client = new VtnClient(this.config.getWimEndpoint(), this.config.getAuthUserName(),
        this.config.getAuthPass());
    // return client.deleteVtn(instanceId); commenting for clarity
    return client.modifyFlow(condition);
  }


}
