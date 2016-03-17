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
package sonata.kernel.adaptor.messaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 
 */
public class RabbitMQProducer extends AbstractMsgBusProducer {

	public RabbitMQProducer(BlockingQueue<ServicePlatformMessage> muxQueue) {
		super(muxQueue);
	}

	private final String configFilePath="/etc/son-mano/broker.config";

	private Connection connection;
	private Properties brokerConfig;


	public void connectToBus() {
		brokerConfig = parseConfigFile(); 

		ConnectionFactory cf = new ConnectionFactory();		
		if(!brokerConfig.containsKey("broker_url")||!brokerConfig.containsKey("exchange")){
			System.err.println("Missing broker url configuration.");
			System.exit(1);
		}
		
		try {
			cf.setUri(brokerConfig.getProperty("broker_url"));
		} catch (KeyManagementException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			connection = cf.newConnection();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean sendMessage(ServicePlatformMessage message) {
		boolean out=true;
		
		//TODO maps the specific Adaptor message to the proper SP topic
		
		try{
			Channel channel = connection.createChannel();
			String exchangeName = brokerConfig.getProperty("exchange");
			channel.exchangeDeclare(exchangeName, "topic");
			BasicProperties properties = new BasicProperties()
					.builder()
					.appId("sonata.kernel.infrastructure_adaptor")
					.replyTo(message.getTopic())
					.correlationId(message.getUUID())
					.build();
			channel.basicPublish(exchangeName, message.getTopic(), properties, message.getBody().getBytes("UTF-8"));
			System.out.println("[northbound] - message: "+message+"\n\r - Properties:"+properties);
		}catch(Exception e){e.printStackTrace();out=false;}
		return out;
	}

	/** Utility function to parse the broker configuration file
	 *
	 * @return a Java Properties object representing the json config as a Key-Value map
	 */
	private Properties parseConfigFile() {
		Properties p = new Properties();
		try{
			FileReader in= new FileReader(new File(configFilePath));

			JSONTokener tokener = new JSONTokener(in);

			JSONObject jsonObject = (JSONObject) tokener.nextValue();

			String brokerURL = jsonObject.getString("broker_url");
			String exchange = jsonObject.getString("exchange");
			p.put("broker_url", brokerURL);
			p.put("exchange", exchange);
		}catch(FileNotFoundException e){
			System.err.println("Unable to load Broker Config file");
			System.exit(1);
		}

		return p;
	}
	

}
