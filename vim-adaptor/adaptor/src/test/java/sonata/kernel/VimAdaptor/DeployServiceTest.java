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
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.junit.Test;

import sonata.kernel.VimAdaptor.commons.ServiceDeployPayload;
import sonata.kernel.VimAdaptor.commons.ServiceDeployResponse;
import sonata.kernel.VimAdaptor.commons.ResourceAvailabilityData;
import sonata.kernel.VimAdaptor.commons.ServicePreparePayload;
import sonata.kernel.VimAdaptor.commons.Status;
import sonata.kernel.VimAdaptor.commons.VimPreDeploymentList;
import sonata.kernel.VimAdaptor.commons.VnfImage;
import sonata.kernel.VimAdaptor.commons.FunctionDeployPayload;
import sonata.kernel.VimAdaptor.commons.FunctionDeployResponse;
import sonata.kernel.VimAdaptor.commons.NetworkConfigurePayload;
import sonata.kernel.VimAdaptor.commons.VnfRecord;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit.MemoryUnit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.VimAdaptor.messaging.TestConsumer;
import sonata.kernel.VimAdaptor.messaging.TestProducer;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.VimAdaptor.wrapper.ovsWrapper.OvsWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Unit test for simple App.
 */

public class DeployServiceTest implements MessageReceiver {
  private String output = null;
  private Object mon = new Object();
  private TestConsumer consumer;
  private String lastHeartbeat;
  private ServiceDeployPayload data;
  private ServiceDeployPayload data1;
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


    this.data = new ServiceDeployPayload();

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
        new FileInputStream(new File("./YAML/vtc-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd1 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    this.data1 = new ServiceDeployPayload();

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
        "{\"tenant_ext_net\":\"ext-subnet\",\"tenant_ext_router\":\"ext-router\",\"vim_type\":\"Mock\",\"vim_address\":\"http://localhost:9999\",\"username\":\"Eve\",\"pass\":\"Operator\",\"tenant\":\"operator\"}";
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
    message = "{\"uuid\":\"" + wrUuid + "\"}";
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
        "{\"tenant_ext_net\":\"ext-subnet\",\"tenant_ext_router\":\"ext-router\",\"vim_type\":\"Mock\",\"vim_address\":\"http://localhost:9999\",\"username\":\"Eve\",\"pass\":\"Operator\",\"tenant\":\"op_sonata\"}";
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

    ServiceDeployResponse response = mapper.readValue(output, ServiceDeployResponse.class);
    Assert.assertTrue(response.getRequestStatus().equals("DEPLOYED"));
    Assert.assertTrue(response.getNsr().getStatus() == Status.normal_operation);

    for (VnfRecord vnfr : response.getVnfrs())
      Assert.assertTrue(vnfr.getStatus() == Status.normal_operation);
    output = null;
    message = "{\"uuid\":\"" + wrUuid + "\"}";
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
   * and addNetVimBody String Member to match your OpenStack and ovs configuration and substitute
   * the @Ignore annotation with the @Test annotation
   *
   * @throws Exception
   */
  @Ignore
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


    String addVimBody = "{\"vim_type\":\"Heat\", "
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
    String addNetVimBody = "{\"vim_type\":\"ovs\", "
        + "\"vim_address\":\"10.100.32.200\",\"username\":\"operator\","
        + "\"pass\":\"apass\",\"tenant\":\"tenant\",\"compute_uuid\":\"" + computeWrUuid + "\"}";
    topic = "infrastructure.management.network.add";
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
    Assert.assertTrue("Failed to add the ovs wrapper. Status " + status,
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

    System.out.println("ServiceDeployResponse: ");
    System.out.println(output);
    Assert.assertTrue("No Deploy service response received", retry < maxRetry);
    ServiceDeployResponse response = mapper.readValue(output, ServiceDeployResponse.class);
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

    // VIM removal
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
    output = null;
    message = "{\"wr_type\":\"network\",\"uuid\":\"" + netWrUuid + "\"}";
    topic = "infrastructure.management.compute.remove";
    ServicePlatformMessage removeNetVimMessage = new ServicePlatformMessage(message,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(removeNetVimMessage);

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


    // clean the SFC engine
    System.out.println("Cleaning the SFC environment...");
    WrapperConfiguration config = new WrapperConfiguration();

    config.setVimEndpoint("10.100.32.200");

    OvsWrapper wrapper = new OvsWrapper(config);
    wrapper.deconfigureNetworking(data.getNsd().getInstanceUuid());


  }

  /**
   * This test is de-activated, if you want to use it with your NFVi-PoP, please edit the addVimBody
   * String Member to match your OpenStack configuration and substitute the @ignore annotation with
   * the @test annotation
   * 
   * @throws Exception
   */
  @Ignore
  public void testDeployTwoServicesOpenStack() throws Exception {


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


    String addVimBody = "{\"vim_type\":\"Heat\", "
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
    String addNetVimBody = "{\"vim_type\":\"ovs\", "
        + "\"vim_address\":\"10.100.32.200\",\"username\":\"operator\","
        + "\"pass\":\"apass\",\"tenant\":\"tenant\",\"compute_uuid\":\"" + computeWrUuid + "\"}";
    topic = "infrastructure.management.network.add";
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
    Assert.assertTrue("Failed to add the ovs wrapper. Status " + status,
        status.equals("COMPLETED"));
    System.out.println("Openvswitch Wrapper added, with uuid: " + netWrUuid);


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

    System.out.println("ServiceDeployResponse: ");
    System.out.println(output);
    Assert.assertTrue("No Deploy service response received", retry < maxRetry);
    ServiceDeployResponse response = mapper.readValue(output, ServiceDeployResponse.class);
    Assert.assertTrue(response.getRequestStatus().equals("DEPLOYED"));
    Assert.assertTrue(response.getNsr().getStatus() == Status.offline);

    for (VnfRecord vnfr : response.getVnfrs())
      Assert.assertTrue(vnfr.getStatus() == Status.offline);


    // Deploy a second instance of the same service

    data1.getNsd().setInstanceUuid(baseInstanceUuid + "-02");
    data1.setVimUuid(computeWrUuid);
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

    System.out.println("ServiceDeployResponse: ");
    System.out.println(output);
    Assert.assertTrue("No Deploy service response received", retry < maxRetry);
    response = mapper.readValue(output, ServiceDeployResponse.class);
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
    String message =
        "{\"instance_uuid\":\"" + instanceUuid + "\",\"vim_uuid\":\"" + computeWrUuid + "\"}";
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
    message = "{\"instance_uuid\":\"" + instanceUuid + "\",\"vim_uuid\":\"" + computeWrUuid + "\"}";
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
    output = null;
    message = "{\"wr_type\":\"network\",\"uuid\":\"" + netWrUuid + "\"}";
    topic = "infrastructure.management.compute.remove";
    ServicePlatformMessage removeNetVimMessage = new ServicePlatformMessage(message,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(removeNetVimMessage);

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


    // clean the SFC engine
    System.out.println("Cleaning the SFC environment...");
    WrapperConfiguration config = new WrapperConfiguration();

    config.setVimEndpoint("10.100.32.200");

    OvsWrapper wrapper = new OvsWrapper(config);
    wrapper.deconfigureNetworking(data.getNsd().getInstanceUuid());
  }

  /**
   * This test is de-activated, if you want to use it with your NFVi-PoP, please edit the addVimBody
   * String Member to match your OpenStack configuration and substitute the @ignore annotation with
   * the @test annotation
   * 
   * @throws Exception
   */
  @Ignore
  public void testDeployServiceIncremental() throws Exception {
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
    String addNetVimBody = "{\"vim_type\":\"ovs\", "
        + "\"vim_address\":\"10.100.32.200\",\"username\":\"operator\","
        + "\"pass\":\"apass\",\"tenant\":\"tenant\",\"compute_uuid\":\"" + computeWrUuid + "\"}";
    topic = "infrastructure.management.network.add";
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
    Assert.assertTrue("Failed to add the ovs wrapper. Status " + status,
        status.equals("COMPLETED"));
    System.out.println("OVS Wrapper added, with uuid: " + netWrUuid);


    output = null;

    // Prepare the system for a service deployment

    ServicePreparePayload payload = new ServicePreparePayload();

    payload.setInstanceId(data.getNsd().getInstanceUuid());
    ArrayList<VimPreDeploymentList> vims = new ArrayList<VimPreDeploymentList>();
    VimPreDeploymentList vimDepList = new VimPreDeploymentList();
    vimDepList.setUuid(computeWrUuid);
    ArrayList<VnfImage> vnfImages = new ArrayList<VnfImage>();
    VnfImage vtcImgade =
        new VnfImage("eu.sonata-nfv:vtc-vnf:0.1:1", "file:///test_images/sonata-vtc");
    vnfImages.add(vtcImgade);
    VnfImage vfwImgade =
        new VnfImage("eu.sonata-nfv:vfw-vnf:0.1:1", "file:///test_images/sonata-vfw");
    vnfImages.add(vfwImgade);
    vimDepList.setImages(vnfImages);
    vims.add(vimDepList);

    payload.setVimList(vims);

    String body = mapper.writeValueAsString(payload);

    topic = "infrastructure.service.prepare";
    ServicePlatformMessage servicePrepareMessage = new ServicePlatformMessage(body,
        "application/x-yaml", topic, UUID.randomUUID().toString(), topic);

    consumer.injectMessage(servicePrepareMessage);

    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }

    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = null;
    status = jsonObject.getString("status");
    String message = jsonObject.getString("message");
    Assert.assertTrue("Failed to prepare the environment for the service deployment: " + status
        + " - message: " + message, status.equals("COMPLETED"));
    System.out.println("Service " + payload.getInstanceId() + " ready for deployment");


    // Send a VNF instantiation request for each VNFD linked by the NSD
    ArrayList<VnfRecord> records = new ArrayList<VnfRecord>();
    for (VnfDescriptor vnfd : data.getVnfdList()) {

      output = null;

      FunctionDeployPayload vnfPayload = new FunctionDeployPayload();
      vnfPayload.setVnfd(vnfd);
      vnfPayload.setVimUuid(computeWrUuid);
      vnfPayload.setServiceInstanceId(data.getNsd().getInstanceUuid());
      body = mapper.writeValueAsString(vnfPayload);

      topic = "infrastructure.function.deploy";
      ServicePlatformMessage functionDeployMessage = new ServicePlatformMessage(body,
          "application/x-yaml", topic, UUID.randomUUID().toString(), topic);

      consumer.injectMessage(functionDeployMessage);

      Thread.sleep(2000);
      while (output == null)
        synchronized (mon) {
          mon.wait(1000);
        }
      Assert.assertNotNull(output);
      int retry = 0;
      int maxRetry = 60;
      while (output.contains("heartbeat") || output.contains("Vim Added") && retry < maxRetry) {
        synchronized (mon) {
          mon.wait(1000);
          retry++;
        }
      }

      System.out.println("FunctionDeployResponse: ");
      System.out.println(output);
      Assert.assertTrue("No response received after function deployment", retry < maxRetry);
      FunctionDeployResponse response = mapper.readValue(output, FunctionDeployResponse.class);
      Assert.assertTrue(response.getRequestStatus().equals("DEPLOYED"));
      Assert.assertTrue(response.getVnfr().getStatus() == Status.offline);
      records.add(response.getVnfr());
    }

    // Finally configure Networking in each NFVi-PoP (VIMs)

    output = null;

    NetworkConfigurePayload netPayload = new NetworkConfigurePayload();
    netPayload.setNsd(data.getNsd());
    netPayload.setVnfds(data.getVnfdList());
    netPayload.setVnfrs(records);
    netPayload.setServiceInstanceId(data.getNsd().getInstanceUuid());


    body = mapper.writeValueAsString(netPayload);

    topic = "infrastructure.network.configure";
    ServicePlatformMessage networkConfigureMessage = new ServicePlatformMessage(body,
        "application/x-yaml", topic, UUID.randomUUID().toString(), topic);

    consumer.injectMessage(networkConfigureMessage);

    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }

    System.out.println(output);
    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = null;
    status = jsonObject.getString("status");
    Assert.assertTrue("Failed to configure inter-PoP SFC. status:" + status,
        status.equals("COMPLETED"));
    System.out.println(
        "Service " + payload.getInstanceId() + " deployed and configured in selected VIM(s)");

    // Clean everything:
    // 1. De-configure SFC
    // 2. Remove Service
    // Service removal (Still the old way)
    output = null;
    String instanceUuid = data.getNsd().getInstanceUuid();
    message = "{\"instance_uuid\":\"" + instanceUuid + "\"}";
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

    // 3. De-register VIMs.

    output = null;
    message = "{\"uuid\":\"" + computeWrUuid + "\"}";
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

    output = null;
    message = "{\"uuid\":\"" + netWrUuid + "\"}";
    topic = "infrastructure.management.network.remove";
    ServicePlatformMessage removeNetVimMessage = new ServicePlatformMessage(message,
        "application/json", topic, UUID.randomUUID().toString(), topic);
    consumer.injectMessage(removeNetVimMessage);

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


  @Ignore
  public void testDeployServiceIncrementalMultiPoP() throws Exception {
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


    // Add first PoP
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
    String computeWrUuid1 = jsonObject.getString("uuid");
    Assert.assertTrue(status.equals("COMPLETED"));
    System.out.println("OpenStack Wrapper added, with uuid: " + computeWrUuid1);


    output = null;
    String addNetVimBody = "{\"vim_type\":\"ovs\", "
        + "\"vim_address\":\"10.100.32.200\",\"username\":\"operator\","
        + "\"pass\":\"apass\",\"tenant\":\"tenant\",\"compute_uuid\":\"" + computeWrUuid1 + "\"}";
    topic = "infrastructure.management.network.add";
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
    String netWrUuid1 = jsonObject.getString("uuid");
    Assert.assertTrue("Failed to add the ovs wrapper. Status " + status,
        status.equals("COMPLETED"));
    System.out.println("OVS Wrapper added, with uuid: " + netWrUuid1);


    output = null;

    // Add second PoP
    addVimBody = "{\"wr_type\":\"compute\",\"vim_type\":\"Heat\", "
        + "\"tenant_ext_router\":\"4ac2b52e-8f6b-4af3-ad28-38ede9d71c83\", "
        + "\"tenant_ext_net\":\"cbc5a4fa-59ed-4ec1-ad2d-adb270e21693\","
        + "\"vim_address\":\"10.100.32.200\",\"username\":\"admin\","
        + "\"pass\":\"ii70mseq\",\"tenant\":\"admin\"}";
    topic = "infrastructure.management.compute.add";
    addVimMessage = new ServicePlatformMessage(addVimBody, "application/json", topic,
        UUID.randomUUID().toString(), topic);
    consumer.injectMessage(addVimMessage);
    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }



    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = jsonObject.getString("status");
    String computeWrUuid2 = jsonObject.getString("uuid");
    Assert.assertTrue(status.equals("COMPLETED"));
    System.out.println("OpenStack Wrapper added, with uuid: " + computeWrUuid2);


    output = null;
    addNetVimBody = "{\"vim_type\":\"ovs\", "
        + "\"vim_address\":\"10.100.32.200\",\"username\":\"operator\","
        + "\"pass\":\"apass\",\"tenant\":\"tenant\",\"compute_uuid\":\"" + computeWrUuid2 + "\"}";
    topic = "infrastructure.management.network.add";
    addNetVimMessage = new ServicePlatformMessage(addNetVimBody, "application/json", topic,
        UUID.randomUUID().toString(), topic);
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
    String netWrUuid2 = jsonObject.getString("uuid");
    Assert.assertTrue("Failed to add the ovs wrapper. Status " + status,
        status.equals("COMPLETED"));
    System.out.println("OVS Wrapper added, with uuid: " + netWrUuid2);


    output = null;

    // Prepare the system for a service deployment

    ServicePreparePayload payload = new ServicePreparePayload();

    payload.setInstanceId(data.getNsd().getInstanceUuid());
    ArrayList<VimPreDeploymentList> vims = new ArrayList<VimPreDeploymentList>();
    VimPreDeploymentList vimDepList = new VimPreDeploymentList();
    vimDepList.setUuid(computeWrUuid1);
    ArrayList<VnfImage> vnfImages = new ArrayList<VnfImage>();
    VnfImage vtcImgade =
        new VnfImage("eu.sonata-nfv:vtc-vnf:0.1:1", "file:///test_images/sonata-vtc");
    vnfImages.add(vtcImgade);
    vimDepList.setImages(vnfImages);
    vims.add(vimDepList);



    vimDepList = new VimPreDeploymentList();
    vimDepList.setUuid(computeWrUuid2);
    vnfImages = new ArrayList<VnfImage>();
    VnfImage vfwImgade =
        new VnfImage("eu.sonata-nfv:fw-vnf:0.1:1", "file:///test_images/sonata-vfw");
    vnfImages.add(vfwImgade);
    vimDepList.setImages(vnfImages);
    vims.add(vimDepList);

    payload.setVimList(vims);

    String body = mapper.writeValueAsString(payload);

    topic = "infrastructure.service.prepare";
    ServicePlatformMessage servicePrepareMessage = new ServicePlatformMessage(body,
        "application/x-yaml", topic, UUID.randomUUID().toString(), topic);

    consumer.injectMessage(servicePrepareMessage);

    Thread.sleep(2000);
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }

    tokener = new JSONTokener(output);
    jsonObject = (JSONObject) tokener.nextValue();
    status = null;
    status = jsonObject.getString("status");
    String message = jsonObject.getString("message");
    Assert.assertTrue("Failed to prepare the environment for the service deployment: " + status
        + " - message: " + message, status.equals("COMPLETED"));
    System.out.println("Service " + payload.getInstanceId() + " ready for deployment");



  }



  @Test
  public void testPrepareServicePayload() throws JsonProcessingException {

    ServicePreparePayload payload = new ServicePreparePayload();

    payload.setInstanceId(data.getNsd().getInstanceUuid());
    ArrayList<VimPreDeploymentList> vims = new ArrayList<VimPreDeploymentList>();
    VimPreDeploymentList vimDepList = new VimPreDeploymentList();
    vimDepList.setUuid("aaaa-aaaaaaaaaaaaa-aaaaaaaaaaaaa-aaaaaaaa");
    ArrayList<VnfImage> vnfImages = new ArrayList<VnfImage>();
    VnfImage Image1 = new VnfImage("eu.sonata-nfv:1-vnf:0.1:1", "file:///test_images/sonata-1");
    VnfImage Image2 = new VnfImage("eu.sonata-nfv:2-vnf:0.1:1", "file:///test_images/sonata-2");
    VnfImage Image3 = new VnfImage("eu.sonata-nfv:3-vnf:0.1:1", "file:///test_images/sonata-3");
    VnfImage Image4 = new VnfImage("eu.sonata-nfv:4-vnf:0.1:1", "file:///test_images/sonata-4");
    vnfImages.add(Image1);
    vnfImages.add(Image2);
    vnfImages.add(Image3);
    vnfImages.add(Image4);
    vimDepList.setImages(vnfImages);
    vims.add(vimDepList);


    vimDepList = new VimPreDeploymentList();
    vimDepList.setUuid("bbbb-bbbbbbbbbbbb-bbbbbbbbbbbb-bbbbbbbbb");
    vnfImages = new ArrayList<VnfImage>();
    VnfImage Image5 = new VnfImage("eu.sonata-nfv:5-vnf:0.1:1", "file:///test_images/sonata-5");
    VnfImage Image6 = new VnfImage("eu.sonata-nfv:6-vnf:0.1:1", "file:///test_images/sonata-6");
    VnfImage Image7 = new VnfImage("eu.sonata-nfv:7-vnf:0.1:1", "file:///test_images/sonata-7");
    vnfImages.add(Image5);
    vnfImages.add(Image6);
    vnfImages.add(Image7);
    vimDepList.setImages(vnfImages);
    vims.add(vimDepList);

    payload.setVimList(vims);

    System.out.println(mapper.writeValueAsString(payload));
  }

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
