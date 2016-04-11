package sonata.kernel.adaptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import sonata.kernel.adaptor.commons.DeployServiceData;
import sonata.kernel.adaptor.commons.DeployServiceResponse;
import sonata.kernel.adaptor.commons.ServiceRecord;
import sonata.kernel.adaptor.commons.Status;
import sonata.kernel.adaptor.commons.VNFRecord;
import sonata.kernel.adaptor.commons.serviceDescriptor.ServiceDescriptor;
import sonata.kernel.adaptor.commons.vnfDescriptor.Unit;
import sonata.kernel.adaptor.commons.vnfDescriptor.UnitDeserializer;
import sonata.kernel.adaptor.commons.vnfDescriptor.VNFDescriptor;
import sonata.kernel.adaptor.messaging.ServicePlatformMessage;
import sonata.kernel.adaptor.messaging.TestConsumer;
import sonata.kernel.adaptor.messaging.TestProducer;

/**
 * Unit test for simple App.
 */
public class AdaptorTest extends TestCase {
  private String output = null;
  private Object mon = new Object();
  private TestConsumer consumer;

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public AdaptorTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(AdaptorTest.class);
  }

  /**
   * Register, send 4 heartbeat, deregister.
   * 
   * @throws IOException
   */
  public void testHeartbeating() throws IOException {
    BlockingQueue<ServicePlatformMessage> muxQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();

    TestProducer producer = new TestProducer(muxQueue, this);
    consumer = new TestConsumer(dispatcherQueue);
    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 2);
    int counter = 0;

    core.start();
    assertNotNull(core.getUUID());

    try {
      while (counter < 4) {
        synchronized (mon) {
          mon.wait();
          if (output.contains("RUNNING")) counter++;
        }
      }
    } catch (Exception e) {
      assertTrue(false);
    }

    System.out.println("Heartbeats received");
    assertTrue(true);

    core.stop();
    assertTrue(core.getState().equals("STOPPED"));
  }

  /**
   * Crete an empy VLSP wrapper
   * 
   * @throws IOException
   */
  public void testCreateVLSPWrapper() throws InterruptedException, IOException {
    String message =
        "{\"target\":\"addVim\",\"body\":{\"wr_type\":\"compute\",\"vim_type\":\"VLSP\",\"vim_address\":\"http://localhost:9999\",\"username\":\"Eve\",\"pass\":\"Operator\"}}";
    String topic = "infrastructure.management.compute";
    BlockingQueue<ServicePlatformMessage> muxQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();

    TestProducer producer = new TestProducer(muxQueue, this);
    ServicePlatformMessage addVimMessage =
        new ServicePlatformMessage(message, topic, UUID.randomUUID().toString());
    consumer = new TestConsumer(dispatcherQueue);
    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 0.05);

    core.start();

    consumer.injectMessage(addVimMessage);
    Thread.sleep(2000);
    if (output != null) {
      while (output.contains("heartbeat"))
        synchronized (mon) {
          mon.wait();
        }
    }

    assertTrue(output.contains("COMPLETED"));
    core.stop();
  }

  /**
   * Create a Mock wrapper
   * 
   * @throws IOException
   */
  public void testCreateMOCKWrapper() throws InterruptedException, IOException {
    String message =
        "{\"target\":\"addVim\",\"body\":{\"wr_type\":\"compute\",\"vim_type\":\"Mock\",\"vim_address\":\"http://localhost:9999\",\"username\":\"Eve\",\"pass\":\"Operator\"}}";
    String topic = "infrastructure.management.compute";
    BlockingQueue<ServicePlatformMessage> muxQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();

    TestProducer producer = new TestProducer(muxQueue, this);
    ServicePlatformMessage addVimMessage =
        new ServicePlatformMessage(message, topic, UUID.randomUUID().toString());
    consumer = new TestConsumer(dispatcherQueue);
    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 0.05);

    core.start();

    consumer.injectMessage(addVimMessage);
    Thread.sleep(2000);
    if (output != null) {
      while (output.contains("heartbeat"))
        synchronized (mon) {
          mon.wait();
        }
    }

    assertTrue(output.contains("COMPLETED"));
    core.stop();
  }



  public void testDeployService() throws IOException, InterruptedException {


    BlockingQueue<ServicePlatformMessage> muxQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();

    TestProducer producer = new TestProducer(muxQueue, this);
    consumer = new TestConsumer(dispatcherQueue);
    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 0.05);

    core.start();

    String message =
        "{\"target\":\"addVim\",\"body\":{\"wr_type\":\"compute\",\"vim_type\":\"Mock\",\"vim_address\":\"http://localhost:9999\",\"username\":\"Eve\",\"pass\":\"Operator\"}}";
    String topic = "infrastructure.management.compute";
    ServicePlatformMessage addVimMessage =
        new ServicePlatformMessage(message, topic, UUID.randomUUID().toString());
    consumer.injectMessage(addVimMessage);
    Thread.sleep(2000);
    if (output != null) {
      while (output.contains("heartbeat"))
        synchronized (mon) {
          mon.wait();
        }
    }

    assertTrue(output.contains("COMPLETED"));

    output=null;
    ServiceDescriptor sd;
    StringBuilder bodyBuilder = new StringBuilder();
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/sonata-demo.yml")), Charset.forName("UTF-8")));
    String line;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Unit.class, new UnitDeserializer());
    mapper.registerModule(module);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    sd = mapper.readValue(bodyBuilder.toString(), ServiceDescriptor.class);

    VNFDescriptor vnfd1;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/iperf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd1 = mapper.readValue(bodyBuilder.toString(), VNFDescriptor.class);

    VNFDescriptor vnfd2;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/firewall-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd2 = mapper.readValue(bodyBuilder.toString(), VNFDescriptor.class);


    VNFDescriptor vnfd3;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/tcpdump-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd3 = mapper.readValue(bodyBuilder.toString(), VNFDescriptor.class);

    DeployServiceData data = new DeployServiceData();
    data.setServiceDescriptor(sd);
    data.addVNFDescriptor(vnfd1);
    data.addVNFDescriptor(vnfd2);
    data.addVNFDescriptor(vnfd3);
    
    mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.setSerializationInclusion(Include.NON_NULL);
    String body = mapper.writeValueAsString(data);
    
    topic = "infrastructure.service.deploy";
    ServicePlatformMessage deployServiceMessage =
        new ServicePlatformMessage(body, topic, UUID.randomUUID().toString());
    
    consumer.injectMessage(deployServiceMessage);
    
    Thread.sleep(2000);
    if (output != null) {
      while (output.contains("heartbeat"))
        synchronized (mon) {
          mon.wait(1000);
        }
    }
    
    DeployServiceResponse response = mapper.readValue(output, DeployServiceResponse.class);
    assertTrue(response.getRequest_status()==Status.normal_operation);
    assertTrue(response.getNSR().getStatus()==Status.normal_operation);
    
    for(VNFRecord vnfr : response.getVNFRs())
      assertTrue(vnfr.getStatus()==Status.normal_operation);
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
