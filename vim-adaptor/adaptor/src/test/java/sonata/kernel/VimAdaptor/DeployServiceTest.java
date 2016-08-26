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

package sonata.kernel.VimAdaptor;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import sonata.kernel.VimAdaptor.AdaptorCore;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.DeployServiceResponse;
import sonata.kernel.VimAdaptor.commons.ResourceAvailabilityData;
import sonata.kernel.VimAdaptor.commons.Status;
import sonata.kernel.VimAdaptor.commons.VnfRecord;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit.MemoryUnit;
import sonata.kernel.VimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.VimAdaptor.messaging.TestConsumer;
import sonata.kernel.VimAdaptor.messaging.TestProducer;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.VimAdaptor.wrapper.odlWrapper.OdlWrapper;
import sonata.kernel.VimAdaptor.wrapper.openstack.OpenStackHeatClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Unit test for simple App.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(OpenStackHeatClient.class)

public class DeployServiceTest implements MessageReceiver {
  private String output = null;
  private Object mon = new Object();
  private TestConsumer consumer;
  private String lastHeartbeat;
  private DeployServiceData data;
  private DeployServiceData data1;
  private ObjectMapper mapper;

  /**
   * Set up the test environment
   *
   */
  @Before
  public void setUp() throws Exception {

    ServiceDescriptor sd;
    StringBuilder bodyBuilder = new StringBuilder();
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/sonata-demo.yml")), Charset.forName("UTF-8")));
    String line;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    this.mapper = new ObjectMapper(new YAMLFactory());
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Unit.class, new UnitDeserializer());
    mapper.registerModule(module);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.setSerializationInclusion(Include.NON_NULL);

    sd = mapper.readValue(bodyBuilder.toString(), ServiceDescriptor.class);

    VnfDescriptor vnfd1;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/vtc-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd1 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    VnfDescriptor vnfd2;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/fw-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd2 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);


    this.data = new DeployServiceData();

    data.setServiceDescriptor(sd);
    data.addVnfDescriptor(vnfd1);
    data.addVnfDescriptor(vnfd2);

    // Set a second data for the demo payload

    sd = null;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/sonata-demo1.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    sd = mapper.readValue(bodyBuilder.toString(), ServiceDescriptor.class);

    vnfd1 = null;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/vTC-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd1 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    this.data1 = new DeployServiceData();

    data1.setServiceDescriptor(sd);
    data1.addVnfDescriptor(vnfd1);
    
  }

  /**
   * Test the checkResource API with the mock wrapper.
   * 
   * @throws IOException
   * @throws InterruptedException
   */
  @Test
  public void testCheckResources() throws IOException, InterruptedException {

    BlockingQueue<ServicePlatformMessage> muxQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();

    TestProducer producer = new TestProducer(muxQueue, this);
    consumer = new TestConsumer(dispatcherQueue);
    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 0.1);

    core.start();
    int counter = 0;

    try {
      while (counter < 2) {
        synchronized (mon) {
          mon.wait();
          if (lastHeartbeat.contains("RUNNING")) counter++;
        }
      }
    } catch (Exception e) {
      Assert.assertTrue(false);
    }

    String message =
        "{\"wr_type\":\"compute\",\"tenant_ext_net\":\"ext-subnet\",\"tenant_ext_router\":\"ext-router\",\"vim_type\":\"Mock\",\"vim_address\":\"http://localhost:9999\",\"username\":\"Eve\",\"pass\":\"Operator\",\"tenant\":\"operator\"}";
    String topic = "infrastructure.management.compute.add";
    ServicePlatformMessage addVimMessage = new ServicePlatformMessage(message, "application/json",
        topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(addVimMessage);
    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }

    JSONTokener tokener = new JSONTokener(output);
    JSONObject jsonObject = (JSONObject) tokener.nextValue();
    String status = jsonObject.getString("status");
    String wrUuid = jsonObject.getString("uuid");
    Assert.assertTrue(status.equals("COMPLETED"));
    System.out.println("Mock Wrapper added, with uuid: " + wrUuid);

    ResourceAvailabilityData data = new ResourceAvailabilityData();

    data.setCpu(4);
    data.setMemory(10);
    data.setMemoryUnit(MemoryUnit.GB);
    data.setStorage(50);
    data.setStorageUnit(MemoryUnit.GB);
    topic = "infrastructure.management.compute.resourceAvailability";


    message = mapper.writeValueAsString(data);

    ServicePlatformMessage checkResourcesMessage = new ServicePlatformMessage(message,
        "application/x-yaml", topic, UUID.randomUUID().toString(), topic);

    output = null;
    consumer.injectMessage(checkResourcesMessage);
    Thread.sleep(2000);
    while (output == null) {
      synchronized (mon) {
        mon.wait(1000);
      }
    }
    Assert.assertTrue(output.contains("OK"));
    message = "{\"wr_type\":\"compute\",\"uuid\":\"" + wrUuid + "\"}";
    topic = "infrastructure.management.compute.remove";
    ServicePlatformMessage removeVimMessage = new ServicePlatformMessage(message,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(removeVimMessage);
    output = null;
    while (output == null) {
      synchronized (mon) {
        mon.wait(1000);
      }
    }


    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = jsonObject.getString("status");
    Assert.assertTrue(status.equals("COMPLETED"));
    core.stop();

  }

  /**
   * test the service deployment API call with the mockWrapper.
   * 
   * @throws IOException
   * @throws InterruptedException
   */
  @Test
  public void testDeployServiceMock() throws IOException, InterruptedException {


    BlockingQueue<ServicePlatformMessage> muxQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();

    TestProducer producer = new TestProducer(muxQueue, this);
    consumer = new TestConsumer(dispatcherQueue);
    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 0.1);

    core.start();
    int counter = 0;

    try {
      while (counter < 2) {
        synchronized (mon) {
          mon.wait();
          if (lastHeartbeat.contains("RUNNING")) counter++;
        }
      }
    } catch (Exception e) {
      Assert.assertTrue(false);
    }


    String message =
        "{\"wr_type\":\"compute\",\"tenant_ext_net\":\"ext-subnet\",\"tenant_ext_router\":\"ext-router\",\"vim_type\":\"Mock\",\"vim_address\":\"http://localhost:9999\",\"username\":\"Eve\",\"pass\":\"Operator\",\"tenant\":\"op_sonata\"}";
    String topic = "infrastructure.management.compute.add";
    ServicePlatformMessage addVimMessage = new ServicePlatformMessage(message, "application/json",
        topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(addVimMessage);
    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }

    JSONTokener tokener = new JSONTokener(output);
    JSONObject jsonObject = (JSONObject) tokener.nextValue();
    String status = jsonObject.getString("status");
    String wrUuid = jsonObject.getString("uuid");
    Assert.assertTrue(status.equals("COMPLETED"));
    System.out.println("Mock Wrapper added, with uuid: " + wrUuid);

    output = null;
    data.setVimUuid(wrUuid);

    String body = mapper.writeValueAsString(data);

    topic = "infrastructure.service.deploy";
    ServicePlatformMessage deployServiceMessage = new ServicePlatformMessage(body,
        "application/x-yaml", topic, UUID.randomUUID().toString(), topic);

    consumer.injectMessage(deployServiceMessage);

    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }
    Assert.assertNotNull(output);
    int retry = 0;
    int maxRetry = 60;
    while (output.contains("heartbeat") || output.contains("Vim Added") && retry < maxRetry)
      synchronized (mon) {
        mon.wait(1000);
        retry++;
      }

    Assert.assertTrue("No Deploy service response received", retry < maxRetry);

    DeployServiceResponse response = mapper.readValue(output, DeployServiceResponse.class);
    Assert.assertTrue(response.getRequestStatus().equals("DEPLOYED"));
    Assert.assertTrue(response.getNsr().getStatus() == Status.normal_operation);

    for (VnfRecord vnfr : response.getVnfrs())
      Assert.assertTrue(vnfr.getStatus() == Status.normal_operation);
    output = null;
    message = "{\"wr_type\":\"compute\",\"uuid\":\"" + wrUuid + "\"}";
    topic = "infrastructure.management.compute.remove";
    ServicePlatformMessage removeVimMessage = new ServicePlatformMessage(message,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(removeVimMessage);

    while (output == null) {
      synchronized (mon) {
        mon.wait(1000);
      }
    }

    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = jsonObject.getString("status");
    Assert.assertTrue(status.equals("COMPLETED"));
    core.stop();

  }

  /**
   * This test is de-activated, if you want to use it with your NFVi-PoP, please edit the addVimBody
   * and addNetVimBody String Member to match your OpenStack and ODL configuration and substitute
   * the @Ignore annotation with the @Test annotation
   * @throws Exception 
   */
  @Test
  public void testDeployServiceOpenStack() throws Exception {

    BlockingQueue<ServicePlatformMessage> muxQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();

    TestProducer producer = new TestProducer(muxQueue, this);
    consumer = new TestConsumer(dispatcherQueue);
    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 0.1);

    core.start();
    int counter = 0;

    try {
      while (counter < 2) {
        synchronized (mon) {
          mon.wait();
          if (lastHeartbeat.contains("RUNNING")) counter++;
        }
      }
    } catch (Exception e) {
      Assert.assertTrue(false);
    }


    String addVimBody = "{\"wr_type\":\"compute\",\"vim_type\":\"Heat\", "
        + "\"tenant_ext_router\":\"4ac2b52e-8f6b-4af3-ad28-38ede9d71c83\", "
        + "\"tenant_ext_net\":\"cbc5a4fa-59ed-4ec1-ad2d-adb270e21693\","
        + "\"vim_address\":\"10.100.32.200\",\"username\":\"admin\","
        + "\"pass\":\"ii70mseq\",\"tenant\":\"admin\"}";
    String topic = "infrastructure.management.compute.add";
    ServicePlatformMessage addVimMessage = new ServicePlatformMessage(addVimBody,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(addVimMessage);
    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }



    JSONTokener tokener = new JSONTokener(output);
    JSONObject jsonObject = (JSONObject) tokener.nextValue();
    String status = jsonObject.getString("status");
    String computeWrUuid = jsonObject.getString("uuid");
    Assert.assertTrue(status.equals("COMPLETED"));
    System.out.println("OpenStack Wrapper added, with uuid: " + computeWrUuid);


    output = null;
    String addNetVimBody = "{\"wr_type\":\"networking\",\"vim_type\":\"odl\", "
        + "\"vim_address\":\"10.100.32.200\",\"username\":\"operator\","
        + "\"pass\":\"apass\",\"tenant\":\"tenant\",\"compute_uuid\":\"" + computeWrUuid + "\"}";
    topic = "infrastructure.management.networking.add";
    ServicePlatformMessage addNetVimMessage = new ServicePlatformMessage(addNetVimBody,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(addNetVimMessage);
    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }

    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = null;
    status = jsonObject.getString("status");
    String netWrUuid = jsonObject.getString("uuid");
    Assert.assertTrue("Failed to add the Odl wrapper. Status " + status,
        status.equals("COMPLETED"));
    System.out.println("OpenDaylight Wrapper added, with uuid: " + netWrUuid);


    output = null;
    String baseInstanceUuid = data.getNsd().getInstanceUuid();
    data.setVimUuid(computeWrUuid);
    data.getNsd().setInstanceUuid(baseInstanceUuid + "-01");

    String body = mapper.writeValueAsString(data);

    topic = "infrastructure.service.deploy";
    ServicePlatformMessage deployServiceMessage = new ServicePlatformMessage(body,
        "application/x-yaml", topic, UUID.randomUUID().toString(), topic);

    consumer.injectMessage(deployServiceMessage);

    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }
    Assert.assertNotNull(output);
    int retry = 0;
    int maxRetry = 60;
    while (output.contains("heartbeat") || output.contains("Vim Added") && retry < maxRetry)
      synchronized (mon) {
        mon.wait(1000);
        retry++;
      }

    System.out.println("DeployServiceResponse: ");
    System.out.println(output);
    Assert.assertTrue("No Deploy service response received", retry < maxRetry);
    DeployServiceResponse response = mapper.readValue(output, DeployServiceResponse.class);
    Assert.assertTrue(response.getRequestStatus().equals("DEPLOYED"));
    Assert.assertTrue(response.getNsr().getStatus() == Status.offline);

    for (VnfRecord vnfr : response.getVnfrs())
      Assert.assertTrue(vnfr.getStatus() == Status.offline);

    // Service removal
    output = null;
    String instanceUuid = baseInstanceUuid + "-01";
    String message = "{\"instance_uuid\":\"" + instanceUuid + "\"}";
    topic = "infrastructure.service.remove";
    ServicePlatformMessage removeInstanceMessage = new ServicePlatformMessage(message,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(removeInstanceMessage);

    while (output == null) {
      synchronized (mon) {
        mon.wait(2000);
        System.out.println(output);
      }
    }
    System.out.println(output);
    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = jsonObject.getString("request_status");
    Assert.assertTrue("Adapter returned an unexpected status: " + status, status.equals("SUCCESS"));

    output = null;
    message = "{\"wr_type\":\"compute\",\"uuid\":\"" + computeWrUuid + "\"}";
    topic = "infrastructure.management.compute.remove";
    ServicePlatformMessage removeVimMessage = new ServicePlatformMessage(message,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(removeVimMessage);

    while (output == null) {
      synchronized (mon) {
        mon.wait(1000);
      }
    }
    System.out.println(output);
    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = jsonObject.getString("status");
    Assert.assertTrue(status.equals("COMPLETED"));
    core.stop();
    
    //clean the SFC engine
    WrapperConfiguration config = new WrapperConfiguration();

    config.setVimEndpoint("10.100.32.10");

    OdlWrapper wrapper = new OdlWrapper(config);
    wrapper.deconfigureNetworking(data.getNsd().getInstanceUuid());
    

  }

  /**
   * This test is de-activated, if you want to use it with your NFVi-PoP, please edit the addVimBody
   * String Member to match your OpenStack configuration and substitute the @ignore annotation with
   * the @test annotation
   * 
   * @throws IOException
   */
  @Ignore
  public void testDeployTwoServicesOpenStack() throws IOException, InterruptedException {


    BlockingQueue<ServicePlatformMessage> muxQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();

    TestProducer producer = new TestProducer(muxQueue, this);
    consumer = new TestConsumer(dispatcherQueue);
    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 0.1);

    core.start();
    int counter = 0;

    try {
      while (counter < 2) {
        synchronized (mon) {
          mon.wait();
          if (lastHeartbeat.contains("RUNNING")) counter++;
        }
      }
    } catch (Exception e) {
      Assert.assertTrue(false);
    }


    String addVimBody = "{\"wr_type\":\"compute\",\"vim_type\":\"Heat\", "
        + "\"tenant_ext_router\":\"20790da5-2dc1-4c7e-b9c3-a8d590517563\", "
        + "\"tenant_ext_net\":\"decd89e2-1681-427e-ac24-6e9f1abb1715\","
        + "\"vim_address\":\"openstack.sonata-nfv.eu\",\"username\":\"op_sonata\","
        + "\"pass\":\"op_s0n@t@\",\"tenant\":\"op_sonata\"}";
    String topic = "infrastructure.management.compute.add";
    ServicePlatformMessage addVimMessage = new ServicePlatformMessage(addVimBody,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(addVimMessage);
    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }

    JSONTokener tokener = new JSONTokener(output);
    JSONObject jsonObject = (JSONObject) tokener.nextValue();
    String status = jsonObject.getString("status");
    String wrUuid = jsonObject.getString("uuid");
    Assert.assertTrue(status.equals("COMPLETED"));
    System.out.println("OenStack Wrapper added, with uuid: " + wrUuid);

    output = null;
    String baseInstanceUuid = data.getNsd().getInstanceUuid();
    data.setVimUuid(wrUuid);
    data.getNsd().setInstanceUuid(baseInstanceUuid + "-01");

    String body = mapper.writeValueAsString(data);

    topic = "infrastructure.service.deploy";
    ServicePlatformMessage deployServiceMessage = new ServicePlatformMessage(body,
        "application/x-yaml", topic, UUID.randomUUID().toString(), topic);

    consumer.injectMessage(deployServiceMessage);

    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }
    Assert.assertNotNull(output);
    int retry = 0;
    int maxRetry = 60;
    while (output.contains("heartbeat") || output.contains("Vim Added") && retry < maxRetry)
      synchronized (mon) {
        mon.wait(1000);
        retry++;
      }

    System.out.println("DeployServiceResponse: ");
    System.out.println(output);
    Assert.assertTrue("No Deploy service response received", retry < maxRetry);
    DeployServiceResponse response = mapper.readValue(output, DeployServiceResponse.class);
    Assert.assertTrue(response.getRequestStatus().equals("DEPLOYED"));
    Assert.assertTrue(response.getNsr().getStatus() == Status.offline);

    for (VnfRecord vnfr : response.getVnfrs())
      Assert.assertTrue(vnfr.getStatus() == Status.offline);


    // Deploy a second instance of the same service

    data1.getNsd().setInstanceUuid(baseInstanceUuid + "-02");
    output = null;

    body = mapper.writeValueAsString(data1);

    topic = "infrastructure.service.deploy";
    deployServiceMessage = new ServicePlatformMessage(body, "application/x-yaml", topic,
        UUID.randomUUID().toString(), topic);

    consumer.injectMessage(deployServiceMessage);

    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }
    Assert.assertNotNull(output);
    retry = 0;
    while (output.contains("heartbeat") || output.contains("Vim Added") && retry < maxRetry)
      synchronized (mon) {
        mon.wait(1000);
        retry++;
      }

    System.out.println("DeployServiceResponse: ");
    System.out.println(output);
    Assert.assertTrue("No Deploy service response received", retry < maxRetry);
    response = mapper.readValue(output, DeployServiceResponse.class);
    Assert.assertTrue(response.getRequestStatus().equals("DEPLOYED"));
    Assert.assertTrue(response.getNsr().getStatus() == Status.offline);
    for (VnfRecord vnfr : response.getVnfrs())
      Assert.assertTrue(vnfr.getStatus() == Status.offline);


    // // Clean the OpenStack tenant from the stack
    // OpenStackHeatClient client =
    // new OpenStackHeatClient("143.233.127.3", "op_sonata", "op_s0n@t@", "op_sonata");
    // String stackName = response.getInstanceName();
    //
    // String deleteStatus = client.deleteStack(stackName, response.getInstanceVimUuid());
    // assertNotNull("Failed to delete stack", deleteStatus);
    //
    // if (deleteStatus != null) {
    // System.out.println("status of deleted stack " + stackName + " is " + deleteStatus);
    // assertEquals("DELETED", deleteStatus);
    // }


    // Service removal
    output = null;
    String instanceUuid = baseInstanceUuid + "-01";
    String message = "{\"instance_uuid\":\"" + instanceUuid + "\",\"vim_uuid\":\"" + wrUuid + "\"}";
    topic = "infrastructure.service.remove";
    ServicePlatformMessage removeInstanceMessage = new ServicePlatformMessage(message,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(removeInstanceMessage);

    while (output == null) {
      synchronized (mon) {
        mon.wait(2000);
        System.out.println(output);
      }
    }
    System.out.println(output);
    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = jsonObject.getString("request_status");
    Assert.assertTrue("Adapter returned an unexpected status: " + status, status.equals("SUCCESS"));

    output = null;
    instanceUuid = baseInstanceUuid + "-02";
    message = "{\"instance_uuid\":\"" + instanceUuid + "\",\"vim_uuid\":\"" + wrUuid + "\"}";
    topic = "infrastructure.service.remove";
    removeInstanceMessage = new ServicePlatformMessage(message, "application/json", topic,
        UUID.randomUUID().toString(), topic);
    consumer.injectMessage(removeInstanceMessage);

    while (output == null) {
      synchronized (mon) {
        mon.wait(2000);
        System.out.println(output);
      }
    }
    System.out.println(output);
    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = jsonObject.getString("request_status");
    Assert.assertTrue("Adapter returned an unexpected status: " + status, status.equals("SUCCESS"));



    output = null;
    message = "{\"wr_type\":\"compute\",\"uuid\":\"" + wrUuid + "\"}";
    topic = "infrastructure.management.compute.remove";
    ServicePlatformMessage removeVimMessage = new ServicePlatformMessage(message,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(removeVimMessage);

    while (output == null) {
      synchronized (mon) {
        mon.wait(1000);
      }
    }
    System.out.println(output);
    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = jsonObject.getString("status");
    Assert.assertTrue(status.equals("COMPLETED"));
    core.stop();

  }


  /**
   * 
   * This Module test try to deploy the demo service with the OpenStack wrapper. The actual
   * connection to OpenStack is mocked.
   * 
   * @throws Exception
   */
  /*
   * public void testDeployServiceMockStack() throws Exception {
   * 
   * OpenStackHeatClient client = Mockito.mock(OpenStackHeatClient.class);
   * Mockito.when(client.createStack(Matchers.anyString(),Matchers.anyString())).thenReturn(UUID.
   * randomUUID().toString());
   * 
   * Mockito.when(client.getStackStatus(Matchers.anyString(),
   * Matchers.anyString())).thenReturn("CREATE_COMPLETE");
   * 
   * Mockito.when(client.deleteStack(Matchers.anyString(),
   * Matchers.anyString())).thenReturn("DELETED");
   * 
   * StackComposition comp =
   * 
   * Mockito.when(client.getStackComposition(Matchers.anyString(),
   * Matchers.anyString())).thenReturn(comp);
   * 
   * PowerMockito.whenNew(OpenStackHeatClient.class).withAnyArguments().thenReturn(client);
   * 
   * 
   * 
   * BlockingQueue<ServicePlatformMessage> muxQueue = new
   * LinkedBlockingQueue<ServicePlatformMessage>(); BlockingQueue<ServicePlatformMessage>
   * dispatcherQueue = new LinkedBlockingQueue<ServicePlatformMessage>();
   * 
   * TestProducer producer = new TestProducer(muxQueue, this); consumer = new
   * TestConsumer(dispatcherQueue); AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue,
   * consumer, producer, 0.1);
   * 
   * core.start(); int counter = 0;
   * 
   * try { while (counter < 2) { synchronized (mon) { mon.wait(); if
   * (lastHeartbeat.contains("RUNNING")) counter++; } } } catch (Exception e) { assertTrue(false); }
   * 
   * 
   * String addVimBody=
   * "{\"wr_type\":\"compute\",\"vim_type\":\"Heat\", \"tenant_ext_router\":\"20790da5-2dc1-4c7e-b9c3-a8d590517563\", \"tenant_ext_net\":\"decd89e2-1681-427e-ac24-6e9f1abb1715\",\"vim_address\":\"openstack.sonata-nfv.eu\",\"username\":\"op_sonata\",\"pass\":\"op_s0n@t@\",\"tenant\":\"op_sonata\"}"
   * ; String topic = "infrastructure.management.compute.add"; ServicePlatformMessage addVimMessage
   * = new ServicePlatformMessage(addVimBody, "application/json", topic,
   * UUID.randomUUID().toString(), topic); consumer.injectMessage(addVimMessage);
   * Thread.sleep(2000); while (output == null) synchronized (mon) { mon.wait(1000); }
   * 
   * JSONTokener tokener = new JSONTokener(output); JSONObject jsonObject = (JSONObject)
   * tokener.nextValue(); String status = jsonObject.getString("status"); String wrUuid =
   * jsonObject.getString("uuid"); assertTrue(status.equals("COMPLETED"));
   * System.out.println("OenStack Wrapper added, with uuid: " + wrUuid);
   * 
   * output = null; String baseInstanceUuid = data.getNsd().getInstanceUuid();
   * data.setVimUuid(wrUuid); data.getNsd().setInstanceUuid(baseInstanceUuid + "-01");
   * 
   * String body = mapper.writeValueAsString(data);
   * 
   * topic = "infrastructure.service.deploy"; ServicePlatformMessage deployServiceMessage = new
   * ServicePlatformMessage(body, "application/x-yaml", topic, UUID.randomUUID().toString(), topic);
   * 
   * consumer.injectMessage(deployServiceMessage);
   * 
   * Thread.sleep(2000); while (output == null) synchronized (mon) { mon.wait(1000); }
   * assertNotNull(output); int retry = 0; int maxRetry = 60; while (output.contains("heartbeat") ||
   * output.contains("Vim Added") && retry < maxRetry) synchronized (mon) { mon.wait(1000); retry++;
   * }
   * 
   * System.out.println("DeployServiceResponse: "); System.out.println(output);
   * assertTrue("No Deploy service response received", retry < maxRetry); DeployServiceResponse
   * response = mapper.readValue(output, DeployServiceResponse.class);
   * assertTrue(response.getRequestStatus().equals("DEPLOYED"));
   * assertTrue(response.getNsr().getStatus() == Status.offline);
   * 
   * for (VnfRecord vnfr : response.getVnfrs()) assertTrue(vnfr.getStatus() == Status.offline);
   * 
   * 
   * // Deploy a second instance of the same service
   * 
   * data.getNsd().setInstanceUuid(baseInstanceUuid + "-02"); output = null;
   * 
   * body = mapper.writeValueAsString(data);
   * 
   * topic = "infrastructure.service.deploy"; deployServiceMessage = new
   * ServicePlatformMessage(body, "application/x-yaml", topic, UUID.randomUUID().toString(), topic);
   * 
   * consumer.injectMessage(deployServiceMessage);
   * 
   * Thread.sleep(2000); while (output == null) synchronized (mon) { mon.wait(1000); }
   * assertNotNull(output); retry = 0; while (output.contains("heartbeat") ||
   * output.contains("Vim Added") && retry < maxRetry) synchronized (mon) { mon.wait(1000); retry++;
   * }
   * 
   * System.out.println("DeployServiceResponse: "); System.out.println(output);
   * assertTrue("No Deploy service response received", retry < maxRetry); response =
   * mapper.readValue(output, DeployServiceResponse.class);
   * assertTrue(response.getRequestStatus().equals("DEPLOYED"));
   * assertTrue(response.getNsr().getStatus() == Status.offline); for (VnfRecord vnfr :
   * response.getVnfrs()) assertTrue(vnfr.getStatus() == Status.offline);
   * 
   * 
   * // // Clean the OpenStack tenant from the stack // OpenStackHeatClient client = // new
   * OpenStackHeatClient("143.233.127.3", "op_sonata", "op_s0n@t@", "op_sonata"); // String
   * stackName = response.getInstanceName(); // // String deleteStatus =
   * client.deleteStack(stackName, response.getInstanceVimUuid()); //
   * assertNotNull("Failed to delete stack", deleteStatus); // // if (deleteStatus != null) { //
   * System.out.println("status of deleted stack " + stackName + " is " + deleteStatus); //
   * assertEquals("DELETED", deleteStatus); // }
   * 
   * 
   * // Service removal output = null; String instanceUuid = baseInstanceUuid + "-01"; String
   * message = "{\"instance_uuid\":\"" + instanceUuid + "\",\"vim_uuid\":\"" + wrUuid + "\"}"; topic
   * = "infrastructure.service.remove"; ServicePlatformMessage removeInstanceMessage = new
   * ServicePlatformMessage(message, "application/json", topic, UUID.randomUUID().toString(),
   * topic); consumer.injectMessage(removeInstanceMessage);
   * 
   * while (output == null) { synchronized (mon) { mon.wait(2000); System.out.println(output); } }
   * System.out.println(output); tokener = new JSONTokener(output); jsonObject = (JSONObject)
   * tokener.nextValue(); status = jsonObject.getString("request_status");
   * assertTrue("Adapter returned an unexpected status: " + status, status.equals("SUCCESS"));
   * 
   * output = null; instanceUuid = baseInstanceUuid + "-02"; message = "{\"instance_uuid\":\"" +
   * instanceUuid + "\",\"vim_uuid\":\"" + wrUuid + "\"}"; topic = "infrastructure.service.remove";
   * removeInstanceMessage = new ServicePlatformMessage(message, "application/json", topic,
   * UUID.randomUUID().toString(), topic); consumer.injectMessage(removeInstanceMessage);
   * 
   * while (output == null) { synchronized (mon) { mon.wait(2000); System.out.println(output); } }
   * System.out.println(output); tokener = new JSONTokener(output); jsonObject = (JSONObject)
   * tokener.nextValue(); status = jsonObject.getString("request_status");
   * assertTrue("Adapter returned an unexpected status: " + status, status.equals("SUCCESS"));
   * 
   * 
   * 
   * output = null; message = "{\"wr_type\":\"compute\",\"uuid\":\"" + wrUuid + "\"}"; topic =
   * "infrastructure.management.compute.remove"; ServicePlatformMessage removeVimMessage = new
   * ServicePlatformMessage(message, "application/json", topic, UUID.randomUUID().toString(),
   * topic); consumer.injectMessage(removeVimMessage);
   * 
   * while (output == null) { synchronized (mon) { mon.wait(1000); } } System.out.println(output);
   * tokener = new JSONTokener(output); jsonObject = (JSONObject) tokener.nextValue(); status =
   * jsonObject.getString("status"); assertTrue(status.equals("COMPLETED")); core.stop();
   * 
   * }
   */

  public void receiveHeartbeat(ServicePlatformMessage message) {
    synchronized (mon) {
      this.lastHeartbeat = message.getBody();
      mon.notifyAll();
    }
  }

  public void receive(ServicePlatformMessage message) {
    synchronized (mon) {
      this.output = message.getBody();
      mon.notifyAll();
    }
  }

  public void forwardToConsumer(ServicePlatformMessage message) {
    consumer.injectMessage(message);
  }
}
