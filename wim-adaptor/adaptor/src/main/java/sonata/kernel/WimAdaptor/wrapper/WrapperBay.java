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

import java.util.ArrayList;


public class WrapperBay {

  private static WrapperBay myInstance = null;

  private VimRepo repository = null;

  private WrapperBay() {}

  /**
   * Singleton method to get the instance of the wrapperbay.
   * 
   * @return the instance of the wrapperbay
   */
  public static WrapperBay getInstance() {
    if (myInstance == null) {
      myInstance = new WrapperBay();
    }
    return myInstance;
  }


  /**
   * Set the Database reader/writer to use as a repository for VIMs.
   * 
   * @param repo the Database reader/writer to store the wrappers
   */
  public void setRepo(VimRepo repo) {
    this.repository = repo;
  }

  public String registerWrapper(WrapperConfiguration config) {
    
    Wrapper newWrapper = WrapperFactory.createWrapper(config);
    String output = "";
    if (newWrapper == null) {
      output = "{\"status\":\"ERROR\",\"message:\"Cannot Attach To Vim\"}";
    } else if (newWrapper.getType().equals("compute")) {
      WrapperRecord record = new WrapperRecord(newWrapper, config);
      this.repository.writeWimEntry(config.getUuid(), record);
      output = "{\"status\":\"COMPLETED\",\"uuid\":\"" + config.getUuid() + "\"}";
    }

    return output;
  }


  
}
