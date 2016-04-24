package sonata.kernel.adaptor;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;
import org.json.JSONTokener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import sonata.kernel.adaptor.messaging.ServicePlatformMessage;
import sonata.kernel.adaptor.messaging.TestConsumer;
import sonata.kernel.adaptor.messaging.TestProducer;

/**
 * Unit test for simple App.
 */
public class AdaptorTest extends TestCase implements MessageReceiver {
  private String output = null;
  private Object mon = new Object();
  private TestConsumer consumer;
  private String lastHeartbeat;

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
//    BlockingQueue<ServicePlatformMessage> muxQueue =
//        new LinkedBlockingQueue<ServicePlatformMessage>();
//  BlockingQueue<ServicePlatformMessage> dispatcherQueue =
//        new LinkedBlockingQueue<ServicePlatformMessage>();
//
//    TestProducer producer = new TestProducer(muxQueue, this);
//    consumer = new TestConsumer(dispatcherQueue);
//    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 2);
//    int counter = 0;

//    core.start();
//    assertNotNull(core.getUuid());

//    try {
//      while (counter < 4) {
//        synchronized (mon) {
//          mon.wait();
//          if (lastHeartbeat.contains("RUNNING")) counter++;
//        }
//      }
//    } catch (Exception e) {
//      assertTrue(false);
//    }

//    System.out.println("Heartbeats received");
//    assertTrue(true);

//    core.stop();
//    assertTrue(core.getState().equals("STOPPED"));
  }

  /**
   * Crete an empty VLSP wrapper
   * 
   * @throws IOException
   */
  public void testCreateVLSPWrapper() throws InterruptedException, IOException {
//    String message =
//        "{\"wr_type\":\"compute\",\"vim_type\":\"VLSP\",\"vim_address\":\"http://localhost:9999\",\"username\":\"Eve\",\"pass\":\"Operator\"}}";
//    String topic = "infrastructure.management.compute.add";
//    BlockingQueue<ServicePlatformMessage> muxQueue =
//        new LinkedBlockingQueue<ServicePlatformMessage>();
//    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
//        new LinkedBlockingQueue<ServicePlatformMessage>();

//    TestProducer producer = new TestProducer(muxQueue, this);
//    ServicePlatformMessage addVimMessage =
//        new ServicePlatformMessage(message, topic, UUID.randomUUID().toString());
//    consumer = new TestConsumer(dispatcherQueue);
//    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 0.05);

//    core.start();

//    consumer.injectMessage(addVimMessage);
//    Thread.sleep(2000);
//    while (output == null)
//      synchronized (mon) {
//        mon.wait();
//      }

//    JSONTokener tokener = new JSONTokener(output);
//    JSONObject jsonObject = (JSONObject) tokener.nextValue();
//    String uuid = jsonObject.getString("uuid");
//    String status = jsonObject.getString("status");
//    assertTrue(status.equals("COMPLETED"));

//    output = null;
//    message = "{\"wr_type\":\"compute\",\"uuid\":\"" + uuid + "\"}";
//    topic = "infrastructure.management.compute.remove";
//    ServicePlatformMessage removeVimMessage =
//        new ServicePlatformMessage(message, topic, UUID.randomUUID().toString());
//    consumer.injectMessage(removeVimMessage);

//    while (output == null) {
//      synchronized (mon) {
//        mon.wait(1000);
//      }
//    }

//    tokener = new JSONTokener(output);
//    jsonObject = (JSONObject) tokener.nextValue();
//    status = jsonObject.getString("status");
//    assertTrue(status.equals("COMPLETED"));
//    core.stop();
  }

  /**
   * Create a Mock wrapper
   * 
   * @throws IOException
   */
  public void testCreateMOCKWrapper() throws InterruptedException, IOException {
//    String message =
//        "{\"wr_type\":\"compute\",\"vim_type\":\"Mock\",\"vim_address\":\"http://localhost:9999\",\"username\":\"Eve\",\"pass\":\"Operator\"}}";
//    String topic = "infrastructure.management.compute.add";
//    BlockingQueue<ServicePlatformMessage> muxQueue =
//        new LinkedBlockingQueue<ServicePlatformMessage>();
//    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
//        new LinkedBlockingQueue<ServicePlatformMessage>();

//    TestProducer producer = new TestProducer(muxQueue, this);
//    ServicePlatformMessage addVimMessage =
//        new ServicePlatformMessage(message, topic, UUID.randomUUID().toString());
//    consumer = new TestConsumer(dispatcherQueue);
//    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 0.05);

//    core.start();

//    consumer.injectMessage(addVimMessage);
//    Thread.sleep(2000);
//    while (output == null) {
//      synchronized (mon) {
//        mon.wait(1000);
//      }
//    }

//    JSONTokener tokener = new JSONTokener(output);
//    JSONObject jsonObject = (JSONObject) tokener.nextValue();
//    String uuid = jsonObject.getString("uuid");
//    String status = jsonObject.getString("status");
//    assertTrue(status.equals("COMPLETED"));

//    output = null;
//    message = "{\"wr_type\":\"compute\",\"uuid\":\"" + uuid + "\"}";
//    topic = "infrastructure.management.compute.remove";
//    ServicePlatformMessage removeVimMessage =
//        new ServicePlatformMessage(message, topic, UUID.randomUUID().toString());
//    consumer.injectMessage(removeVimMessage);

//    while (output == null) {
//      synchronized (mon) {
//        mon.wait(1000);
//      }
//    }

//    tokener = new JSONTokener(output);
//    jsonObject = (JSONObject) tokener.nextValue();
//    status = jsonObject.getString("status");
//    assertTrue(status.equals("COMPLETED"));

//    core.stop();

  }

  public void receiveHeartbeat(ServicePlatformMessage message) {
//    synchronized (mon) {
//      this.lastHeartbeat = message.getBody();
//      mon.notifyAll();
//    }
  }

  public void receive(ServicePlatformMessage message) {
//    synchronized (mon) {
//      this.output = message.getBody();
//      mon.notifyAll();
//    }
  }

  public void forwardToConsumer(ServicePlatformMessage message) {
//    consumer.injectMessage(message);
  }
}
