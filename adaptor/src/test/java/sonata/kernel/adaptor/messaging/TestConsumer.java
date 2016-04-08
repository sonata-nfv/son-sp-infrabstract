package sonata.kernel.adaptor.messaging;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

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
		System.out.println("[TestConsumer] Topic: " + message.getTopic() + " - Message:" + message.getBody());
		processMessage(message.getBody(), message.getTopic(), message.getSID());
	}

}
