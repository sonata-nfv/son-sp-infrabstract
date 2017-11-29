/*
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, THALES, NCSR Demokritos ALL RIGHTS RESERVED.
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

package sonata.kernel.vimadaptor.wrapper.sp;

import sonata.kernel.vimadaptor.commons.FunctionDeployPayload;
import sonata.kernel.vimadaptor.commons.FunctionScalePayload;
import sonata.kernel.vimadaptor.commons.ServiceDeployPayload;
import sonata.kernel.vimadaptor.commons.VimResources;
import sonata.kernel.vimadaptor.commons.VnfImage;
import sonata.kernel.vimadaptor.wrapper.ComputeWrapper;
import sonata.kernel.vimadaptor.wrapper.ResourceUtilisation;
import sonata.kernel.vimadaptor.wrapper.WrapperConfiguration;
import sonata.kernel.vimadaptor.wrapper.sp.client.SonataGkClient;

import java.io.IOException;

import javax.ws.rs.NotAuthorizedException;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.LoggerFactory;


public class ComputeSPWrapper extends ComputeWrapper {

  private static final org.slf4j.Logger Logger =
      LoggerFactory.getLogger(ComputeSPWrapper.class);

  public ComputeSPWrapper(WrapperConfiguration config) {
    super(config);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * sonata.kernel.vimadaptor.wrapper.ComputeWrapper#deployFunction(sonata.kernel.vimadaptor.commons
   * .FunctionDeployPayload, java.lang.String)
   */
  @Override
  public void deployFunction(FunctionDeployPayload data, String sid) {
    // TODO Implement this function by:
    // - Create a mock NSD with just the provided VNFD OR reference the original NSD ID.
    // - sending a REST call to the underlying SP Gatekeeper
    // - than poll the GK until the status is "READY" or "ERROR"

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * sonata.kernel.vimadaptor.wrapper.ComputeWrapper#deployService(sonata.kernel.vimadaptor.commons.
   * ServiceDeployPayload, java.lang.String)
   */
  @Deprecated
  @Override
  public boolean deployService(ServiceDeployPayload data, String callSid) throws Exception {

    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#getResourceUtilisation()
   */
  @Override
  public ResourceUtilisation getResourceUtilisation() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * sonata.kernel.vimadaptor.wrapper.ComputeWrapper#isImageStored(sonata.kernel.vimadaptor.commons.
   * VnfImage, java.lang.String)
   */
  @Override
  public boolean isImageStored(VnfImage image, String callSid) {
    // This Wrapper ignores this call
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#prepareService(java.lang.String)
   */
  @Override
  public boolean prepareService(String instanceId) throws Exception {
    // This Wrapper ignores this call
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#removeService(java.lang.String,
   * java.lang.String)
   */
  @Override
  public boolean removeService(String instanceUuid, String callSid) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * sonata.kernel.vimadaptor.wrapper.ComputeWrapper#scaleFunction(sonata.kernel.vimadaptor.commons.
   * FunctionScalePayload, java.lang.String)
   */
  @Override
  public void scaleFunction(FunctionScalePayload data, String sid) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * sonata.kernel.vimadaptor.wrapper.ComputeWrapper#uploadImage(sonata.kernel.vimadaptor.commons.
   * VnfImage)
   */
  @Override
  public void uploadImage(VnfImage image) {
    // This Wrapper ignores this call
  }

  public VimResources[] listPoPs()
      throws NotAuthorizedException, ClientProtocolException, IOException {

    Logger.info("[SpWrapper] Creating SONATA Rest Client");
    SonataGkClient client = new SonataGkClient(this.getConfig().getVimEndpoint(),
        this.getConfig().getAuthUserName(), this.getConfig().getAuthPass());

    Logger.info("[SpWrapper] Authenticating SONATA Rest Client");
    if (!client.authenticate()) throw new NotAuthorizedException("Client cannot login to the SP");

    Logger.info("[SpWrapper] Retrieving VIMs connected to slave SONATA SP");
    VimResources[] out = client.getVims();

    return out;
  }

  /* (non-Javadoc)
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#removeImage(sonata.kernel.vimadaptor.commons.VnfImage)
   */
  @Override
  public void removeImage(VnfImage image) {
    // TODO Auto-generated method stub
    
  }

}
