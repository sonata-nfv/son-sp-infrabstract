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

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 
 */
public class AdaptorDefaultConsumer extends DefaultConsumer {

	private RabbitMQConsumer msgBusConsumer;

	/**
	 * @param channel the RabbitMQ channel for this consumer
	 * @param msgBusConsumer the Adaptor consumer, responsible for msg processing and queuing. 
	 */
	public AdaptorDefaultConsumer(Channel channel, RabbitMQConsumer msgBusConsumer) {
		super(channel);
		this.msgBusConsumer=msgBusConsumer;
	}


	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException {
		String message = new String(body, "UTF-8");
		System.out.println(" [nortbounf] Received " + message +" on "+envelope.getRoutingKey());
		if(!properties.getAppId().equals("sonata.kernel.infrastructure_adaptor"))
			this.msgBusConsumer.processMessage(message,envelope.getRoutingKey(),properties.getCorrelationId());
	}

}
