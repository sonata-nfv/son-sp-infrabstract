/**
 * @author Dario Valocchi (Ph.D.)
 * @mail d.valocchi@ucl.ac.uk
 * 
 * Copyright 2016 [Dario Valocchi]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 */
package sonata.kernel.adaptor;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;
import org.json.JSONTokener;

import sonata.kernel.adaptor.messaging.AbstractMsgBusConsumer;
import sonata.kernel.adaptor.messaging.AbstractMsgBusProducer;
import sonata.kernel.adaptor.messaging.MsgBusConsumer;
import sonata.kernel.adaptor.messaging.MsgBusProducer;
import sonata.kernel.adaptor.messaging.RabbitMQConsumer;
import sonata.kernel.adaptor.messaging.RabbitMQProducer;
import sonata.kernel.adaptor.messaging.ServicePlatformMessage;
import sonata.kernel.adaptor.wrapper.WrapperBay;

public class AdaptorCore {

	public static final String APP_ID = "sonata.kernel.InfrAdaptor";
	private MsgBusConsumer northConsumer;
	private MsgBusProducer northProducer;
	private AdaptorDispatcher dispatcher;
	private AdaptorMux mux;
	private WrapperBay wrapperBay;
	private String status;
	private HeartBeat heartbeat;
	private double rate;
	private Object writeLock = new Object();
	private String UUID;
	private String registrationUUID;


	private final String version="0.0.1";
	private final String description="Service Platform Infrastructure Adaptor";


	public AdaptorCore(BlockingQueue<ServicePlatformMessage> muxQueue, BlockingQueue<ServicePlatformMessage> dispatcherQueue, AbstractMsgBusConsumer consumer, AbstractMsgBusProducer producer, double rate){
		mux = new AdaptorMux(muxQueue);
		dispatcher = new AdaptorDispatcher(dispatcherQueue, mux, this);
		northConsumer=consumer;
		northProducer=producer;
		status="INIT";
		this.rate=rate;
	}

	public AdaptorCore(double rate){
		this.rate=rate;
		//	instantiate the Adaptor:
		//	- Mux and queue
		BlockingQueue<ServicePlatformMessage> muxQueue = new LinkedBlockingQueue<ServicePlatformMessage>();
		mux = new AdaptorMux(muxQueue);

		//	- Dispatcher and queue
		BlockingQueue<ServicePlatformMessage> dispatcherQueue = new LinkedBlockingQueue<ServicePlatformMessage>();
		dispatcher = new AdaptorDispatcher(dispatcherQueue,mux,this);

		//	- Northbound interface

		northConsumer = new RabbitMQConsumer(dispatcherQueue);
		northProducer = new RabbitMQProducer(muxQueue);

		//	- Southbound interface
		wrapperBay = WrapperBay.getInstance();
		status="READY";		

	}


	public void start() throws IOException{
		// Start the message plugins 
		northProducer.connectToBus();
		northConsumer.connectToBus();
		northProducer.startProducing();
		northConsumer.startConsuming();
		
		dispatcher.start();

		register();
		status="RUNNING";
		//  - Start pumping blood
		this.heartbeat = new HeartBeat(mux, rate, this);
		new Thread(this.heartbeat).start();
	}

	private void register() {
		String body = "{\"name\":\""+ AdaptorCore.APP_ID+"\",\"version\":\""+this.version+"\",\"description\":\""+this.description+"\"}";
		String topic = "platform.management.plugin.register";
		ServicePlatformMessage message= new ServicePlatformMessage(body,topic,java.util.UUID.randomUUID().toString());
		mux.enqueue(message);
		this.registrationUUID=message.getUUID();
		synchronized (writeLock) {
			try {
				writeLock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void deregister() {
		String body = "{\"uuid\":\""+ this.UUID+"\"}";
		String topic = "platform.management.plugin.deregister";
		ServicePlatformMessage message= new ServicePlatformMessage(body,topic, java.util.UUID.randomUUID().toString());
		mux.enqueue(message);
		this.registrationUUID=message.getUUID();
		synchronized (writeLock) {
			try {
				writeLock.wait(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.status="STOPPED";
	}

	public void stop(){
		northProducer.stopProducing();
		northConsumer.stopConsuming();
		dispatcher.stop();
		this.heartbeat.stop();
		this.deregister();
	}

	MsgBusProducer getNorthProducer() {
		return northProducer;
	}


	private static AdaptorCore core;

	public static void main(String[] args) throws IOException{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				core.stop();
			}   
		}); 
		core = new AdaptorCore(0.1);
		core.start();

	}

	public String getUUID() {
		return this.UUID;
	}

	public String getState() {
		return this.status;
	}

	public void handleRegistrationResponse(ServicePlatformMessage message) {
		JSONTokener tokener = new JSONTokener(message.getBody());
		JSONObject object = (JSONObject) tokener.nextValue();
		String status = object.getString("status");
		String pid = object.getString("uuid");
		if(status.equals("OK")){
			synchronized (writeLock) {
				UUID = pid;
				writeLock.notifyAll();
			}
		}else{
			String error = object.getString("error");
			System.err.println("Failed to register to the plugin manager");
			System.err.println("Message: "+error);
		}

	}
	
	public void handleDeregistrationResponse(ServicePlatformMessage message) {
		JSONTokener tokener = new JSONTokener(message.getBody());
		JSONObject object = (JSONObject) tokener.nextValue();
		String status = object.getString("status");
		if(status.equals("OK")){
			synchronized (writeLock) {
				writeLock.notifyAll();
			}
		}else{
			System.err.println("Failed to deregister to the plugin manager");
			this.status="FAILED";
		}

	}

	public String getRegistrationUUID() {
		return registrationUUID;
	}
}
