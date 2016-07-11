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


import sonata.kernel.adaptor.wrapper.MockWrapper;
import sonata.kernel.adaptor.wrapper.VimRepo;
import sonata.kernel.adaptor.wrapper.WrapperConfiguration;
import sonata.kernel.adaptor.wrapper.WrapperRecord;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class VimRepoTest extends TestCase {

  private VimRepo repoInstance;

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public VimRepoTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(VimRepoTest.class);
  }

  /**
   * Register, send 4 heartbeat, deregister.
   * 
   * @throws IOException
   */
  public void testCreateVimRepo() {

    repoInstance = new VimRepo();
    ArrayList<String> vims = repoInstance.getComputeVims();
    assertNotNull("Unable to retrieve an empy list. SQL exception occurred", vims);
  }

  public void testAddVim() {

    repoInstance = new VimRepo();
    WrapperConfiguration config = new WrapperConfiguration();
    config.setVimEndpoint("x.x.x.x");
    config.setVimVendor("compute");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setTenantName("tenant");
    config.setUuid("12345");
    config.setWrapperType("mock");
    config.setTenantExtNet("ext-subnet");
    config.setTenantExtRouter("ext-router");
    WrapperRecord record = new WrapperRecord(new MockWrapper(config), config, null);
    boolean out = repoInstance.writeVimEntry(config.getUuid(), record);


    assertTrue("Unable to write a vim", out);

    out = repoInstance.removeVimEntry(config.getUuid());
    assertTrue("unable to remove vim", out);
  }

  public void testListVims() {

    repoInstance = new VimRepo();
    WrapperConfiguration config = new WrapperConfiguration();
    config.setVimEndpoint("x.x.x.x");
    config.setVimVendor("mock");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setTenantName("tenant");
    config.setUuid("1");
    config.setWrapperType("compute");
    config.setTenantExtNet("ext-subnet");
    config.setTenantExtRouter("ext-router");

    WrapperRecord record = new WrapperRecord(new MockWrapper(config), config, null);
    boolean out = repoInstance.writeVimEntry(config.getUuid(), record);
    assertTrue("Unable to write a vim", out);

    config.setUuid("2");
    record = new WrapperRecord(new MockWrapper(config), config, null);
    out = repoInstance.writeVimEntry(config.getUuid(), record);
    assertTrue("Unable to write a vim", out);

    config.setUuid("3");
    record = new WrapperRecord(new MockWrapper(config), config, null);
    out = repoInstance.writeVimEntry(config.getUuid(), record);
    assertTrue("Unable to write a vim", out);


    ArrayList<String> vims = repoInstance.getComputeVims();

    assertTrue("Db doesn't contain all the stored VIMs", vims.contains("1"));
    assertTrue("Db doesn't contain all the stored VIMs", vims.contains("2"));
    assertTrue("Db doesn't contain all the stored VIMs", vims.contains("3"));

    out = repoInstance.removeVimEntry("1");
    assertTrue("unable to remove vim", out);
    out = repoInstance.removeVimEntry("2");
    assertTrue("unable to remove vim", out);
    out = repoInstance.removeVimEntry("3");
    assertTrue("unable to remove vim", out);
  }

  public void testAddInstance() {

    repoInstance = new VimRepo();

    boolean out = repoInstance.writeInstanceEntry("1", "1-1", "stack1-1");

    assertTrue("Errors while writing the instance", out);

    out = repoInstance.removeInstanceEntry("1");

    assertTrue("Errors while removing the instance", out);

  }

  public void testGetInstanceVimUuid() {

    repoInstance = new VimRepo();

    boolean out = repoInstance.writeInstanceEntry("1", "1-1", "stack1-1");

    assertTrue("Errors while writing the instance", out);

    String vimUuid = repoInstance.getServiceVimUuid("1");

    assertTrue("Retrieved vim UUID different from the stored UUID", vimUuid.equals("1-1"));

    out = repoInstance.removeInstanceEntry("1");

    assertTrue("Errors while removing the instance", out);
  }

  public void testGetInstanceVimName() {

    repoInstance = new VimRepo();

    boolean out = repoInstance.writeInstanceEntry("1", "1-1", "stack1-1");

    assertTrue("Errors while writing the instance", out);

    String vimName = repoInstance.getServiceVimName("1");

    assertTrue("Retrieved vim Name different from the stored Name", vimName.equals("stack1-1"));

    out = repoInstance.removeInstanceEntry("1");

    assertTrue("Errors while removing the instance", out);

  }


}
