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
package sonata.kernel.adaptor.wrapper;

import sonata.kernel.adaptor.StartServiceCallProcessor;
import sonata.kernel.adaptor.commons.DeployServiceData;
import sonata.kernel.adaptor.commons.serviceDescriptor.ServiceDescriptor;

public class MockWrapper extends ComputeWrapper {

  public MockWrapper(WrapperConfiguration config) {
    super();
  }

  @Override
  public boolean deployService(DeployServiceData data, StartServiceCallProcessor callProcessor) {
    this.addObserver(callProcessor);

    // TODO This is a mock compute wrapper.

    /*
     * Just use the SD to forge the response message for the SLM with a success. In general Wrappers
     * would need a complex set of actions to deploy the service, so this function should just check
     * if the request is acceptable, and if so start a new thread to deal with the perform the
     * needed actions.
     */

    String body = "";
    WrapperStatusUpdate update = new WrapperStatusUpdate(callProcessor.getSID(), "SUCCESS", body);
    this.notifyObservers(update);

    return true;
  }

}
