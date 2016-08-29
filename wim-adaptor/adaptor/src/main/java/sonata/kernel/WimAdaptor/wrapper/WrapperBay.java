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

package sonata.kernel.WimAdaptor.wrapper;

import java.util.ArrayList;

public class WrapperBay {

  private static WrapperBay myInstance = null;

  private WimRepo repository = null;

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
  public void setRepo(WimRepo repo) {
    this.repository = repo;
  }

  /**
   * Register a new WIM wrapper to the WIM adaptor.
   * 
   * @param config the WrapperConfiguration for the WIM wrapper to be created
   * @return a JSON formatted string with the result of the operation
   */
  public String registerWrapper(WrapperConfiguration config) {

    Wrapper newWrapper = WrapperFactory.createWrapper(config);
    String output = "";
    if (newWrapper == null) {
      output = "{\"status\":\"ERROR\",\"message:\"Cannot Attach To Wim\"}";
    } else {
      WrapperRecord record = new WrapperRecord(newWrapper, config);
      this.repository.writeWimEntry(config.getUuid(), record);
      output = "{\"status\":\"COMPLETED\",\"uuid\":\"" + config.getUuid() + "\"}";
    }

    return output;
  }

  public WrapperRecord getWimRecord(String vimUuid) {
    WrapperRecord out;
    out = this.repository.readWimEntryFromNetSegment(vimUuid);
    return out;
  }

  public String removeWimWrapper(String uuid) {
    repository.removeWimEntry(uuid);
    return "{\"status\":\"COMPLETED\"}";
  }

}
