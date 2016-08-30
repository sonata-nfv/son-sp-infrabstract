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
import sonata.kernel.WimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.WimAdaptor.wrapper.WrapperRecord;
import sonata.kernel.WimAdaptor.wrapper.vtn.VtnWrapper;


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
  @Test
  public void testCreateWimRepo() {

    repoInstance = new WimRepo();

    ArrayList<String> wims = repoInstance.listWims();
    Assert.assertNotNull("Unable to retrieve an empy list. SQL exception occurred", wims);

  }

  @Test
  public void testAddWim() {

    repoInstance = new WimRepo();
    WrapperConfiguration config = new WrapperConfiguration();
    config.setWimEndpoint("x.x.x.x");
    config.setWimVendor("compute");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setUuid("12345");
    config.setWrapperType("mock");
    ArrayList<String> servicedSegments = new ArrayList<String>();
    servicedSegments.add("1234-1234567890-1234567890-1234");
    config.setServicedSegments(servicedSegments);
    WrapperRecord record = new WrapperRecord(new VtnWrapper(config), config);
    boolean out = repoInstance.writeWimEntry(config.getUuid(), record);


    Assert.assertTrue("Unable to write a wim", out);

    out = repoInstance.removeWimEntry(config.getUuid());
    Assert.assertTrue("unable to remove wim", out);

  }

  @Test
  public void testServiceSegmentRetrival() {
    repoInstance = new WimRepo();
    WrapperConfiguration config = new WrapperConfiguration();
    config.setWimEndpoint("x.x.x.x");
    config.setWimVendor("compute");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setUuid("1");
    config.setWrapperType("mock");
    ArrayList<String> servicedSegments = new ArrayList<String>();
    servicedSegments.add("A");
    config.setServicedSegments(servicedSegments);
    WrapperRecord record = new WrapperRecord(new VtnWrapper(config), config);
    boolean out = repoInstance.writeWimEntry(config.getUuid(), record);

    config.setUuid("2");
    config.setWrapperType("mock");
    servicedSegments = new ArrayList<String>();
    servicedSegments.add("B");
    config.setServicedSegments(servicedSegments);
    out = repoInstance.writeWimEntry(config.getUuid(), record);

    WrapperRecord recordA = repoInstance.readWimEntryFromNetSegment("A");
    Assert.assertTrue("Unable to retrieve the correct WIM for segment A",
        recordA.getConfig().getUuid().equals("1"));
    WrapperRecord recordB = repoInstance.readWimEntryFromNetSegment("B");
    Assert.assertTrue("Unable to retrieve the correct WIM for segment B",
        recordB.getConfig().getUuid().equals("2"));
    out = repoInstance.removeWimEntry("1");
    Assert.assertTrue("unable to remove wim 1", out);
    out = repoInstance.removeWimEntry("2");
    Assert.assertTrue("unable to remove wim 2", out);
  }

  @Test
  public void testListWims() {
    repoInstance = new WimRepo();
    WrapperConfiguration config = new WrapperConfiguration();
    config.setWimEndpoint("x.x.x.x");
    config.setWimVendor("mock");
    config.setAuthUserName("operator");
    config.setAuthPass("apass");
    config.setUuid("1");
    config.setWrapperType("compute");
    ArrayList<String> servicedSegments = new ArrayList<String>();
    servicedSegments.add("1");

    config.setServicedSegments(servicedSegments);
    WrapperRecord record = new WrapperRecord(new VtnWrapper(config), config);
    boolean out = repoInstance.writeWimEntry(config.getUuid(), record);
    Assert.assertTrue("Unable to write a wim", out);

    config.setUuid("2");
    servicedSegments = new ArrayList<String>();
    servicedSegments.add("2");
    config.setServicedSegments(servicedSegments);

    record = new WrapperRecord(new VtnWrapper(config), config);
    out = repoInstance.writeWimEntry(config.getUuid(), record);
    Assert.assertTrue("Unable to write a wim", out);

    config.setUuid("3");
    servicedSegments = new ArrayList<String>();
    servicedSegments.add("3");
    config.setServicedSegments(servicedSegments);
    record = new WrapperRecord(new VtnWrapper(config), config);
    out = repoInstance.writeWimEntry(config.getUuid(), record);
    Assert.assertTrue("Unable to write a wim", out);


    ArrayList<String> vims = repoInstance.listWims();

    Assert.assertTrue("Db doesn't contain all the stored VIMs", vims.contains("1"));
    Assert.assertTrue("Db doesn't contain all the stored VIMs", vims.contains("2"));
    Assert.assertTrue("Db doesn't contain all the stored VIMs", vims.contains("3"));

    out = repoInstance.removeWimEntry("1");
    Assert.assertTrue("unable to remove vim", out);
    out = repoInstance.removeWimEntry("2");
    Assert.assertTrue("unable to remove vim", out);
    out = repoInstance.removeWimEntry("3");
    Assert.assertTrue("unable to remove vim", out);
  }

}
