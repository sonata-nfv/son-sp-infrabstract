package sonata.kernel.adaptor.messaging;

import sonata.kernel.adaptor.MessageReceiver;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class TestProducer extends AbstractMsgBusProducer {

  private MessageReceiver output;

  public TestProducer(BlockingQueue<ServicePlatformMessage> muxQueue, MessageReceiver output) {
    super(muxQueue);
    this.output = output;
  }

  @Override
  public void connectToBus() throws IOException {
    // do nothing
  }

  @Override
  public boolean sendMessage(ServicePlatformMessage message) {
    System.out
        .println("[TestProducer] Topic: " + message.getTopic() + " - Message:" + message.getBody());
    if (message.getTopic().contains("infrastructure.management.compute")) {
      output.receive(message);
    }
    if (message.getTopic().equals("infrastructure.service.deploy")) {
      output.receive(message);
    }
    if (message.getTopic().equals("platform.management.plugin.register")) {
      String registrationResponse = "{\"status\":\"OK\",\"uuid\":\"" + UUID.randomUUID().toString()
          + "\",\"error\":\"none\"}";
      ServicePlatformMessage response = new ServicePlatformMessage(registrationResponse,
          "application/json", "platform.management.plugin.register", message.getSid(), null);
      output.forwardToConsumer(response);
    }
    if (message.getTopic().equals("platform.management.plugin.deregister")) {
      String registrationResponse = "{\"status\":\"OK\"}";
      ServicePlatformMessage response = new ServicePlatformMessage(registrationResponse,
          "application/json", "platform.management.plugin.deregister", message.getSid(), null);
      output.forwardToConsumer(response);
    }
    if (message.getTopic().contains("heartbeat")) {
      output.receiveHeartbeat(message);
    }
    return true;
  }

}
