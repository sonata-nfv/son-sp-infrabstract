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

package sonata.kernel.WimAdaptor.wrapper;

import sonata.kernel.WimAdaptor.wrapper.vtn.VtnWrapper;

public class WrapperFactory {

  /**
   * Uses the parser configuration to create the relevant Wrapper.
   * 
   * @param config the WrapperConfiguration object describing the wrapper to create.
   * @return the brand new wrapper
   */
  public static Wrapper createWrapper(WrapperConfiguration config) {
    Wrapper output = null;
    System.out.println("  [WrapperFactory] - creating wrapper...");

    if (config.getWimVendor().equals("VTN")) {
      output = new VtnWrapper(config);
    }

    System.out.println("  [WrapperFactory] - Wrapper created...");
    return output;
  }


}
