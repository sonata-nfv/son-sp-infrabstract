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

package sonata.kernel.VimAdaptor.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;

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

public class RabbitMqConsumer extends AbstractMsgBusConsumer implements MsgBusConsumer, Runnable {

  private static final String configFilePath = "/etc/son-mano/broker.config";
  DefaultConsumer consumer;
  private Connection connection;
  private Channel channel;
  private String queueName;

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(RabbitMqConsumer.class);

  public RabbitMqConsumer(BlockingQueue<ServicePlatformMessage> dispatcherQueue) {
    super(dispatcherQueue);
  }

  @Override
  public void connectToBus() {
    Properties brokerConfig = parseConfigFile();
    Logger.info("Connecting to broker...");
    ConnectionFactory cf = new ConnectionFactory();
    if (!brokerConfig.containsKey("broker_url") || !brokerConfig.containsKey("exchange")) {
      Logger.error("Missing broker url configuration.");
      System.exit(1);
    }
    try {

      Logger.info("Connecting to: " + brokerConfig.getProperty("broker_url"));
      cf.setUri(brokerConfig.getProperty("broker_url"));
      connection = cf.newConnection();
      channel = connection.createChannel();
      String exchangeName = brokerConfig.getProperty("exchange");
      channel.exchangeDeclare(exchangeName, "topic");
      queueName = exchangeName + "." + "InfraAbstract";
      channel.queueDeclare(queueName, true, false, false, null);
      Logger.info("Binding queue to topics...");

      channel.queueBind(queueName, exchangeName, "platform.management.plugin.register");
      Logger.info("Bound to topic \"platform.platform.management.plugin.register\"");

      channel.queueBind(queueName, exchangeName, "platform.management.plugin.deregister");
      Logger.info("Bound to topic \"platform.platform.management.plugin.deregister\"");

      channel.queueBind(queueName, exchangeName, "infrastructure.#");
      Logger.info("[northbound] RabbitMqConsumer - bound to topic \"infrastructure.#\"");

      consumer = new AdaptorDefaultConsumer(channel, this);
    } catch (TimeoutException e) {
      Logger.error(e.getMessage(), e);
    } catch (KeyManagementException e) {
      Logger.error(e.getMessage(), e);
    } catch (NoSuchAlgorithmException e) {
      Logger.error(e.getMessage(), e);
    } catch (URISyntaxException e) {
      Logger.error(e.getMessage(), e);
    } catch (IOException e) {
      Logger.error(e.getMessage(), e);
    }

  }

  @Override
  public boolean startConsuming() {
    boolean out = true;
    Thread thread;
    try {
      thread = new Thread(this);
      thread.start();
    } catch (Exception e) {
      Logger.error(e.getMessage(), e);
      out = false;
    }
    return out;
  }

  @Override
  public boolean stopConsuming() {
    boolean out = true;
    try {
      channel.close();
      connection.close();
    } catch (IOException e) {
      Logger.error(e.getMessage(), e);
      out = false;
    } catch (TimeoutException e) {
      Logger.error(e.getMessage(), e);
      out = false;
    }

    return out;
  }

  @Override
  public void run() {
    try {
      Logger.info("Starting consumer thread");
      channel.basicConsume(queueName, true, consumer);
    } catch (IOException e) {
      Logger.error(e.getMessage(), e);
    }
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
