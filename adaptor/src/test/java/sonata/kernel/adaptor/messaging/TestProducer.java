package sonata.kernel.adaptor.messaging;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import sonata.kernel.adaptor.SimpleTest;

public class TestProducer extends AbstractMsgBusProducer{

	private SimpleTest output;
	public TestProducer(BlockingQueue<ServicePlatformMessage> muxQueue, SimpleTest output) {
		super(muxQueue);
		this.output=output;
	}

	@Override
	public void connectToBus() throws IOException {
		//do nothing
	}

	@Override
	public boolean sendMessage(ServicePlatformMessage message) {
		System.out.println("[TestProducer] Topic: "+ message.getTopic()+" - Message:"+message.getBody());
		if(message.getTopic().equals("infrastructure.management.compute")){
			output.receive(message);
		}
		if(message.getTopic().equals("platform.management.plugin.register")){
			String registrationResponse= "{\"status\":\"OK\",\"uuid\":\""+UUID.randomUUID().toString()+"\",\"error\":\"none\"}";
			ServicePlatformMessage response = new ServicePlatformMessage(registrationResponse, "platform.management.plugin.register",message.getSID());
			output.forwardToConsumer(response);
		}
		if(message.getTopic().equals("platform.management.plugin.deregister")){
			String registrationResponse= "{\"status\":\"OK\"}";
			ServicePlatformMessage response = new ServicePlatformMessage(registrationResponse, "platform.management.plugin.deregister",message.getSID());
			output.forwardToConsumer(response);
		}
		if(message.getTopic().contains("heartbeat")){
			output.receive(message);
		}	
		return true;
	}

}
