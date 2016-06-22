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


import sonata.kernel.adaptor.wrapper.openstack.Flavor;
import sonata.kernel.adaptor.wrapper.openstack.OpenStackNovaClient;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

public class OpenStackNovaClientTest extends TestCase {

  private OpenStackNovaClient novaClient;

  /**
   * Create the test case
   */
  public OpenStackNovaClientTest(String testName) {
    super(testName);
    initClient();
  }

  private void initClient() {

    // todo - this needs to be moved to configuration file
    this.novaClient =
        new OpenStackNovaClient("openstack.sonata-nfv.eu", "op_sonata", "op_s0n@t@", "op_sonata");
  }


  /**
   * Test a flavor get.
   *
   * @throws IOException
   */
  public void testFlavors() throws IOException {

    // System.out.println(novaClient);
    // list the flavors
    ArrayList<Flavor> vimFlavors = novaClient.getFlavors();
    System.out.println(vimFlavors);
    assertNotNull("Failed to retreive flavors", vimFlavors);

  }

  /**
   * Test a limits get.
   *
   * @throws IOException
   */
  public void testLimits() throws IOException {
    System.out.println(novaClient);
    ResourceUtilisation resources = novaClient.getResourceUtilizasion();
    System.out.println(resources);
    assertNotNull("Failed to retrieve limits", resources);
  }

}
