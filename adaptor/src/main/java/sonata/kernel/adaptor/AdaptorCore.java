/**
 * @author Dario Valocchi (Ph.D.)
 * @mail d.valocchi@ucl.ac.uk
 * 
 *       Copyright 2016 [Dario Valocchi]
 * 
 *       Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *       except in compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *       Unless required by applicable law or agreed to in writing, software distributed under the
 *       License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *       either express or implied. See the License for the specific language governing permissions
 *       and limitations under the License.
 * 
 */

package sonata.kernel.adaptor;

import org.json.JSONObject;
import org.json.JSONTokener;

import sonata.kernel.adaptor.messaging.AbstractMsgBusConsumer;
import sonata.kernel.adaptor.messaging.AbstractMsgBusProducer;
import sonata.kernel.adaptor.messaging.MsgBusConsumer;
import sonata.kernel.adaptor.messaging.MsgBusProducer;
import sonata.kernel.adaptor.messaging.RabbitMQConsumer;
import sonata.kernel.adaptor.messaging.RabbitMQProducer;
import sonata.kernel.adaptor.messaging.ServicePlatformMessage;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;



public class AdaptorCore {

  public static final String APP_ID = "sonata.kernel.InfrAdaptor";
  private MsgBusConsumer northConsumer;
  private MsgBusProducer northProducer;
  private AdaptorDispatcher dispatcher;
  private AdaptorMux mux;
  private String status;
  private HeartBeat heartbeat;
  private double rate;
  private Object writeLock = new Object();
  private String uuid;
  private String registrationSid;

  private static final String version = "0.0.1";
  private static final String description = "Service Platform Infrastructure Adaptor";


  /**
   * utility constructor for Tests. Allows attaching mock MsgBus to the adaptor plug-in Manager.
   * 
   * @param muxQueue A Java BlockingQueue for the AdaptorMux
   * @param dispatcherQueue A Java BlockingQueue for the AdaptorDispatcher
   * @param consumer The consumer queuing messages in the dispatcher queue
   * @param producer The producer de-queuing messages from the mux queue
   * @param rate of the heart-beat in beat/s
   */
  public AdaptorCore(BlockingQueue<ServicePlatformMessage> muxQueue,
      BlockingQueue<ServicePlatformMessage> dispatcherQueue, AbstractMsgBusConsumer consumer,
      AbstractMsgBusProducer producer, double rate) {
    mux = new AdaptorMux(muxQueue);
    dispatcher = new AdaptorDispatcher(dispatcherQueue, mux, this);
    northConsumer = consumer;
    northProducer = producer;
    status = "READY";
    this.rate = rate;
  }

  /**
   * Create an AdaptorCore ready to use. No services are started.
   * 
   * @param rate of the heart-beat in beat/s
   */
  public AdaptorCore(double rate) {
    this.rate = rate;
    // instantiate the Adaptor:
    // - Mux and queue
    BlockingQueue<ServicePlatformMessage> muxQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    mux = new AdaptorMux(muxQueue);

    // - Dispatcher and queue
    BlockingQueue<ServicePlatformMessage> dispatcherQueue =
        new LinkedBlockingQueue<ServicePlatformMessage>();
    dispatcher = new AdaptorDispatcher(dispatcherQueue, mux, this);

    // - Northbound interface

    northConsumer = new RabbitMQConsumer(dispatcherQueue);
    northProducer = new RabbitMQProducer(muxQueue);

    status = "READY";

  }

  /**
   * Start the adaptor engines. Starts reading messages from the MsgBus
   * 
   * @throws IOException when something goes wrong in the MsgBus plug-in
   */
  public void start() throws IOException {
    // Start the message plug-in
    northProducer.connectToBus();
    northConsumer.connectToBus();
    northProducer.startProducing();
    northConsumer.startConsuming();

    dispatcher.start();

    register();
    status = "RUNNING";
    // - Start pumping blood
    this.heartbeat = new HeartBeat(mux, rate, this);
    new Thread(this.heartbeat).start();
  }

  private void register() {
    String body = "{\"name\":\"" + AdaptorCore.APP_ID + "\",\"version\":\"" + AdaptorCore.version
        + "\",\"description\":\"" + AdaptorCore.description + "\"}";
    String topic = "platform.management.plugin.register";
    ServicePlatformMessage message =
        new ServicePlatformMessage(body, topic, java.util.UUID.randomUUID().toString());
    synchronized (writeLock) {
      try {
        this.registrationSid = message.getSid();
        mux.enqueue(message);
        writeLock.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void deregister() {
    String body = "{\"uuid\":\"" + this.uuid + "\"}";
    String topic = "platform.management.plugin.deregister";
    ServicePlatformMessage message =
        new ServicePlatformMessage(body, topic, java.util.UUID.randomUUID().toString());
    synchronized (writeLock) {
      try {
        this.registrationSid = message.getSid();
        mux.enqueue(message);
        writeLock.wait(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    this.status = "STOPPED";
  }

  /**
   * Stop the engines: Message production and consumption, heart-beat.
   */
  public void stop() {
    this.deregister();
    this.heartbeat.stop();
    northProducer.stopProducing();
    northConsumer.stopConsuming();
    dispatcher.stop();
  }



  private static AdaptorCore core;

  /**
   * Main method. param args the adaptor take no args.
   */
  public static void main(String[] args) throws IOException {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        core.stop();
      }
    });
    core = new AdaptorCore(0.1);
    core.start();

  }

  /**
   * @return this plug-in UUID.
   */
  public String getUuid() {
    return this.uuid;
  }


  /**
   * @return The status of this plug-in.
   */
  public String getState() {
    return this.status;
  }

  /**
   * Handle the RegistrationResponse message from the MANO Plugin Manager.
   * 
   * @param message the response message
   */
  public void handleRegistrationResponse(ServicePlatformMessage message) {
    System.out.println("[AdaptorCore] Received the registration response from the pluginmanager");
    JSONTokener tokener = new JSONTokener(message.getBody());
    JSONObject object = (JSONObject) tokener.nextValue();
    String status = object.getString("status");
    String pid = object.getString("uuid");
    if (status.equals("OK")) {
      synchronized (writeLock) {
        uuid = pid;
        writeLock.notifyAll();
      }
    } else {
      String error = object.getString("error");
      System.err.println("Failed to register to the plugin manager");
      System.err.println("Message: " + error);
    }

  }

  /**
   * Handle the DeregistrationResponse message from the MANO Plugin Manager.
   * 
   * @param message the response message
   */
  public void handleDeregistrationResponse(ServicePlatformMessage message) {
    System.out.println("[AdaptorCore] Received the deregistration response from the pluginmanager");
    JSONTokener tokener = new JSONTokener(message.getBody());
    JSONObject object = (JSONObject) tokener.nextValue();
    String status = object.getString("status");
    if (status.equals("OK")) {
      synchronized (writeLock) {
        writeLock.notifyAll();
      }
    } else {
      System.err.println("Failed to deregister to the plugin manager");
      this.status = "FAILED";
    }

  }

  /**
   * return the session ID of the registration message used to register this plugin to the
   * plugin-manager.
   * 
   * @return the session ID
   */
  public String getRegistrationSid() {
    return registrationSid;
  }
}
