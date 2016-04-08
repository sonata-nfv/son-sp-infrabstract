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

/**
 * 
 */
public class WrapperFactory {

  /**
   * @param config
   * @return the brand new wrapper
   */
  public static Wrapper createWrapper(WrapperConfiguration config) {
    Wrapper output = null;
    System.out.println("WrapperFactory - createWrapper");
    System.out.println("config:\n\r" + config);
    if (config.getWrapperType().equals("compute")) output = createComputeWrapper(config);
    if (config.getWrapperType().equals("networking")) output = createNetworkingWrapper(config);
    if (config.getWrapperType().equals("storage")) output = createStorageWrapper(config);
    return output;
  }

  private static ComputeWrapper createComputeWrapper(WrapperConfiguration config) {
    ComputeWrapper output = null;

    if (config.getVimType().equals(ComputeVimType.VLSP.getName()))
      output = new VLSPWrapper(config);
    else if (config.getVimType().equals(ComputeVimType.MOCK.getName()))
      output = new MockWrapper(config);
    else if (config.getVimType().equals(ComputeVimType.OPENSTACKHEAT.getName()))
      output = new OpenStackHeatWrapper(config);

    // TODO Extends with all wrappers or refactor with a more OO type
    // generation

    return output;
  }

  private static NetworkingWrapper createNetworkingWrapper(WrapperConfiguration config) {
    return null;
  }

  private static StorageWrapper createStorageWrapper(WrapperConfiguration config) {
    return null;
  }
}
