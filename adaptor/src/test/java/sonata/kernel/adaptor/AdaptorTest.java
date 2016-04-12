package sonata.kernel.adaptor;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import sonata.kernel.adaptor.messaging.ServicePlatformMessage;
import sonata.kernel.adaptor.messaging.TestConsumer;
import sonata.kernel.adaptor.messaging.TestProducer;
import sonata.kernel.adaptor.wrapper.WrapperBay;

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
    BlockingQueue<ServicePlatformMessage> muxQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();

    TestProducer producer = new TestProducer(muxQueue, this);
    consumer = new TestConsumer(dispatcherQueue);
    AdaptorCore core = new AdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 2);
    int counter = 0;

    core.start();
    assertNotNull(core.getUuid());

    try {
      while (counter < 4) {
        synchronized (mon) {
          mon.wait();
          if (lastHeartbeat.contains("RUNNING")) counter++;
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
   * Crete an empty VLSP wrapper
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
    while (output == null)
      synchronized (mon) {
        mon.wait();
      }


    assertTrue(output.contains("COMPLETED"));
    core.stop();
    WrapperBay.getInstance().clear();
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
    while (output == null)
      synchronized (mon) {
        mon.wait(1000);
      }


    assertTrue(output.contains("COMPLETED"));
    core.stop();
    WrapperBay.getInstance().clear();

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
