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

import java.util.Hashtable;

public class WrapperBay {

  private static WrapperBay myInstance = null;

  @SuppressWarnings("unused")
  private Hashtable<String, WrapperRecord> storageRegistry;
  private Hashtable<String, WrapperRecord> computeRegistry;
  @SuppressWarnings("unused")
  private Hashtable<String, WrapperRecord> networkingRegistry;

  private WrapperBay() {
    computeRegistry = new Hashtable<String, WrapperRecord>();
    storageRegistry = new Hashtable<String, WrapperRecord>();
    networkingRegistry = new Hashtable<String, WrapperRecord>();
  }

  public static WrapperBay getInstance() {
    if (myInstance == null) myInstance = new WrapperBay();
    return myInstance;
  }


  /**
   * Register a new compute wrapper.
   * 
   * @param config The configuration object representing the Wrapper to register
   * @return a JSON representing the output of the API call
   */
  public String registerComputeWrapper(WrapperConfiguration config) {
    Wrapper newWrapper = WrapperFactory.createWrapper(config);
    String output = "";
    if (newWrapper == null) {
      output = "{\"status\":\"ERROR\",\"message:\"Cannot Attach To Vim\"}";
    } else if (newWrapper.getType().equals("compute")) {
      WrapperRecord record = new WrapperRecord(newWrapper, config, null);
      this.computeRegistry.put(config.getUuid(), record);
      output = "{\"status\":\"COMPLETED\",\"uuid\":\"" + config.getUuid() + "\"}";
    }

    return output;
  }

  /**
   * Order the list of available compute wrapper to find the best basing on an OptimizationStrategy.
   *
   * @return A ComputeWrapper object, the best according to the OptimizationStrategy.
   */
  public ComputeWrapper getBestComputeWrapper() {
    ComputeWrapper bestWrapper = null;
    if (this.computeRegistry.size() != 0) {
      bestWrapper =
          (ComputeWrapper) this.computeRegistry.values().iterator().next().getVimWrapper();
    }
    return bestWrapper;
  }

  /**
   * Utility methods to clear registry tables.
   */
  public void clear() {
    computeRegistry = new Hashtable<String, WrapperRecord>();
    storageRegistry = new Hashtable<String, WrapperRecord>();
    networkingRegistry = new Hashtable<String, WrapperRecord>();
  }

  /**
   * Remove a registered compute wrapper from the IA.
   * 
   * @param uuid the uuid of the wrapper to remove
   * @return a JSON representing the output of the API call
   */
  public String removeComputeWrapper(String uuid) {
    computeRegistry.remove(uuid);
    return "{\"status\":\"COMPLETED\"}";
  }

}
