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

package sonata.kernel.WimAdaptor;


import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import sonata.kernel.WimAdaptor.wrapper.WimRepo;

/**
 * Unit test for simple App.
 */
public class WimRepoTest {

  private WimRepo repoInstance;

  /**
   * Create the Wim.
   * 
   * @throws IOException
   */
  @Test public void testCreateWimRepo() {

    repoInstance = new WimRepo();
    ArrayList<String> vims = repoInstance.getComputeVim();
    Assert.assertNotNull("Unable to retrieve an empy list. SQL exception occurred", vims);
  }

  @Test public void testAddVim() {


  }

  @Test public void testListWims() {

  }

  @Test public void testAddTenantNet() {

  }

  @Test public void testGetTenantNet() {}


}
