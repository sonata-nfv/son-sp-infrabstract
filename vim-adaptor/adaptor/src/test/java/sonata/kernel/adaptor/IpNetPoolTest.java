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

package sonata.kernel.adaptor;


import sonata.kernel.adaptor.commons.IpNetPool;

import java.util.ArrayList;
import java.util.UUID;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class IpNetPoolTest extends TestCase {
  private IpNetPool pool;

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public IpNetPoolTest(String testName) {
    super(testName);
    IpNetPool.resetInstance();
    pool = IpNetPool.getInstance();
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(IpNetPoolTest.class);
  }



  /**
   * Allocate and de-allocate a subnet range.
   * 
   * 
   */
  public void testReserveSubnetRange() throws Exception {


    int totSubnet = pool.getFreeSubnetsNumber();
    int neededSubnet = 100;
    String instanceUuid = UUID.randomUUID().toString();
    ArrayList<String> myPool = pool.reserveSubnets(instanceUuid, neededSubnet);
    int availableSubnet = pool.getFreeSubnetsNumber();
    assertNotNull("Null pool returned from allocation", myPool);

    assertTrue("Subnets have not been reserved", totSubnet == (availableSubnet + neededSubnet));

    pool.freeSubnets(instanceUuid);

    availableSubnet = pool.getFreeSubnetsNumber();

    assertTrue("Subnets have not been freed", totSubnet == availableSubnet);
  }

  /**
   * Try to allocate too many subnets.
   * 
   * 
   */
  public void testReserveSubnetRangeTooMany() {

    pool = IpNetPool.getInstance();
    int totSubnet = pool.getFreeSubnetsNumber();
    String instanceUuid = UUID.randomUUID().toString();
    ArrayList<String> myPool = pool.reserveSubnets(instanceUuid, totSubnet + 1);

    assertNull("More reserved subnets than available subnets, result should be null and it's not.",
        myPool);

  }

  /**
   * Try a double allocation. Get the same
   * 
   * 
   */
  public void testReserveSubnetRangeTwice() {

    pool = IpNetPool.getInstance();
    int numOfSubnet = 100;
    String instanceUuid1 = UUID.randomUUID().toString();
    ArrayList<String> myPool = pool.reserveSubnets(instanceUuid1, numOfSubnet);

    assertNotNull("Reservation gave unexpected null result.", myPool);

    ArrayList<String> mySecondPool = pool.reserveSubnets(instanceUuid1, numOfSubnet);

    assertNotNull("Second reservation gave unexpected null result.", mySecondPool);

    assertTrue("The two reservation should be equals. They are not.", myPool.equals(mySecondPool));
  }

  /**
   * Get the gateway of a network.
   * 
   * 
   */
  public void testGetGateway() {

    pool = IpNetPool.getInstance();
    String gateway = pool.getGateway("192.168.0.0/24");
    assertTrue("Unexpected gateway.", gateway.equals("192.168.0.1"));
    gateway = pool.getGateway("192.168.0.8/29");
    assertTrue("Unexpected gateway.", gateway.equals("192.168.0.9"));
    gateway = pool.getGateway("172.0.0.0/29");
    assertTrue("Unexpected gateway.", gateway.equals("172.0.0.1"));


  }
}
