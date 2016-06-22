package sonata.kernel.WimAdaptor.messaging;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import sonata.kernel.WimAdaptor.messaging.AbstractMsgBusConsumer;
import sonata.kernel.WimAdaptor.messaging.ServicePlatformMessage;

public class TestConsumer extends AbstractMsgBusConsumer {

  public TestConsumer(BlockingQueue<ServicePlatformMessage> dispatcherQueue) {
    super(dispatcherQueue);
  }

  @Override
  public void connectToBus() throws IOException {
    // Do nothing
  }

  @Override
  public boolean startConsuming() {
    // Do nothing
    return true;
  }

  @Override
  public boolean stopConsuming() {
    // TODO Auto-generated method stub
    return false;
  }

  public void injectMessage(ServicePlatformMessage message) {
    int maxChars = 500;
    if (message.getBody() != null) {
      if (message.getBody().length() <= maxChars) {
        System.out.println(
            "[TestConsumer] Topic: " + message.getTopic() + " - Message:" + message.getBody());
      } else {
        System.out.println("[TestConsumer] Topic: " + message.getTopic() + " - Message:"
            + message.getBody().substring(0, maxChars));
        System.out.println("...TRUNCATED");
      }
    } else {
      System.out.println("[TestConsumer] Topic: " + message.getTopic() + " - Message empty");
    }
    processMessage(message.getBody(), message.getContentType(), message.getTopic(),
        message.getSid(), message.getReplyTo());
  }

}
