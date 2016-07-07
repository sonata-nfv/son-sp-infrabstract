package sonata.kernel.WimAdaptor;


import sonata.kernel.WimAdaptor.WimAdaptorCore;
import sonata.kernel.WimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.WimAdaptor.messaging.TestConsumer;
import sonata.kernel.WimAdaptor.messaging.TestProducer;

import java.io.IOException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
    WimAdaptorCore core = new WimAdaptorCore(muxQueue, dispatcherQueue, consumer, producer, 2);
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
