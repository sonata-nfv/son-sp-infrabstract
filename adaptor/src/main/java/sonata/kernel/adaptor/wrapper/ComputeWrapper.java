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

import sonata.kernel.adaptor.DeployServiceCallProcessor;
import sonata.kernel.adaptor.commons.DeployServiceData;
import sonata.kernel.adaptor.commons.DeploymentResponse;

public abstract class ComputeWrapper extends AbstractWrapper implements Wrapper {

  /**
   * general constructor for wrappers of type compute.
   * */
  public ComputeWrapper() {
    
    this.setType("compute");

  }

  public abstract boolean deployService(DeployServiceData data,
      DeployServiceCallProcessor startServiceCallProcessor) throws Exception;

}
