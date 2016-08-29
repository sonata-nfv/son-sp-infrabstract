/**
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the SONATA-NFV, UCL, NOKIA, NCSR Demokritos nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the SONATA project, funded by the European
 * Commission under Grant number 671517 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the SONATA partner consortium
 * (www.sonata-nfv.eu).
 *
 * @author Dario Valocchi (Ph.D.), UCL
 * 
 */

package sonata.kernel.WimAdaptor.messaging;

import com.rabbitmq.client.AMQP.BasicProperties;

import sonata.kernel.WimAdaptor.WimAdaptorCore;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMqProducer extends AbstractMsgBusProducer {

  public RabbitMqProducer(BlockingQueue<ServicePlatformMessage> muxQueue) {
    super(muxQueue);
  }

  private final static String configFilePath = "/etc/son-mano/broker.config";
  private final static org.slf4j.Logger Logger = LoggerFactory.getLogger(RabbitMqProducer.class);

  private Connection connection;
  private Properties brokerConfig;

  @Override
  public void connectToBus() {
    brokerConfig = parseConfigFile();

    ConnectionFactory cf = new ConnectionFactory();
    if (!brokerConfig.containsKey("broker_url") || !brokerConfig.containsKey("exchange")) {
      Logger.error("Missing broker url configuration.");
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

  @Override
  public boolean sendMessage(ServicePlatformMessage message) {
    boolean out = true;

    // TODO maps the specific Adaptor message to the proper SP topic

    try {
      Channel channel = connection.createChannel();
      String exchangeName = brokerConfig.getProperty("exchange");
      channel.exchangeDeclare(exchangeName, "topic");
      BasicProperties properties = new BasicProperties().builder().appId(WimAdaptorCore.APP_ID)
          .contentType(message.getContentType()).replyTo(message.getReplyTo())
          .correlationId(message.getSid()).build();
      channel.basicPublish(exchangeName, message.getTopic(), properties,
          message.getBody().getBytes("UTF-8"));
      // System.out.println(
      // "[northbound] - sending message: " + message + "\n\r - Properties:" + properties);
    } catch (Exception e) {
      e.printStackTrace();
      out = false;
    }
    return out;
  }

  /**
   * Utility function to parse the broker configuration file.
   *
   * @return a Java Properties object representing the json config as a Key-Value map
   */
  private Properties parseConfigFile() {
    Properties prop = new Properties();
    try {
      InputStreamReader in =
          new InputStreamReader(new FileInputStream(configFilePath), Charset.forName("UTF-8"));

      JSONTokener tokener = new JSONTokener(in);

      JSONObject jsonObject = (JSONObject) tokener.nextValue();

      String brokerUrl = jsonObject.getString("broker_url");
      String exchange = jsonObject.getString("exchange");
      prop.put("broker_url", brokerUrl);
      prop.put("exchange", exchange);
    } catch (FileNotFoundException e) {
      Logger.error("Unable to load Broker Config file", e);
      System.exit(1);
    }

    return prop;
  }

}
