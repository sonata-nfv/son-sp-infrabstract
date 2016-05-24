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

public class WrapperRecord {

  private WrapperConfiguration config;
  private Wrapper vimWrapper;
  private ResourceDescriptor resources;

  public WrapperConfiguration getConfig() {
    return config;
  }

  public Wrapper getVimWrapper() {
    return vimWrapper;
  }

  public ResourceDescriptor getResources() {
    return resources;
  }

  /**
   * Create a WrapperRecord to be store in the WrapperBay
   * 
   * @param wrapper The Wrapper object to be recorded.
   * @param config The WrapperConfiguration object describing the wrapper to be recorded.
   * @param resources The ResourceDescription object representing the resource availability of the
   *        wrapper to be recorded.
   */
  public WrapperRecord(Wrapper wrapper, WrapperConfiguration config, ResourceDescriptor resources) {
    this.config = config;
    this.vimWrapper = wrapper;
    this.resources = resources;
  }
}
