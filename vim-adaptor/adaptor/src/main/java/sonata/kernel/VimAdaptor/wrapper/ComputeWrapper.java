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

package sonata.kernel.VimAdaptor.wrapper;

import sonata.kernel.VimAdaptor.commons.DeployServiceData;

public abstract class ComputeWrapper extends AbstractWrapper implements Wrapper {

  /**
   * general constructor for wrappers of type compute.
   */
  public ComputeWrapper() {

    this.setType("compute");

  }

  /**
   * Remove a service instance from this VIM.
   * 
   * @param data the payload containing the service descriptors and the metadata for this service
   *        deployment
   * @param callSid the call processor to notify on completion
   * 
   * @return true if the remove process has started correctly, false otherwise
   */
  public abstract boolean deployService(DeployServiceData data, String callSid) throws Exception;

  /**
   * Remove a service instance from this VIM.
   * 
   * @param instanceUuid the identifier of the instance in the VIM scope
   * 
   * @return true if the remove process has started correctly, false otherwise
   */
  public abstract boolean removeService(String instanceUuid, String callSid);


  /**
   * Get the resource utilisation status of this compute VIM.
   * 
   * @return the ResourceUtilisation object representing the status of this VIM
   */
  public abstract ResourceUtilisation getResourceUtilisation();

}
