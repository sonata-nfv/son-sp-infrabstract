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
 * @author Michael Bredel (Ph.D.), NEC
 */

package sonata.kernel.VimAdaptor;

import org.slf4j.LoggerFactory;
import sonata.kernel.VimAdaptor.messaging.ServicePlatformMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AdaptorDispatcher implements Runnable {

  private BlockingQueue<ServicePlatformMessage> myQueue;
  private Executor myThreadPool;
  private boolean stop = false;
  private AdaptorMux mux;
  private AdaptorCore core;

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(AdaptorDispatcher.class);

  /**
   * Create an AdaptorDispatcher attached to the queue. CallProcessor will be bind to the provided
   * mux.
   * 
   * @param queue the queue the dispatcher is attached to
   * 
   * @param mux the AdaptorMux the CallProcessors will be attached to
   */
  public AdaptorDispatcher(BlockingQueue<ServicePlatformMessage> queue, AdaptorMux mux,
      AdaptorCore core) {
    myQueue = queue;
    myThreadPool = Executors.newCachedThreadPool();
    this.mux = mux;
    this.core = core;
  }

  @Override
  public void run() {
    ServicePlatformMessage message;
    do {
      try {
        message = myQueue.take();

        if (isRegistrationResponse(message)) {
          this.core.handleRegistrationResponse(message);
        } else if (isDeregistrationResponse(message)) {
          this.core.handleDeregistrationResponse(message);
        } else if (isManagementMsg(message)) {
          handleManagementMessage(message);
        } else if (isServiceMsg(message)) {
          this.handleServiceMsg(message);
        } else if (isMonitoringMessage(message)) {
          this.handleMonitoringMessage(message);
        }
      } catch (InterruptedException e) {
        Logger.error(e.getMessage(), e);
      }
    } while (!stop);
  }

  private void handleMonitoringMessage(ServicePlatformMessage message) {
    if (message.getTopic().contains("compute")) {
      Logger.info("Received a monitoring message on topic: " + message.getTopic());
    } else if (message.getTopic().contains("storage")) {
      Logger.info("Received a monitoring message on topic: " + message.getTopic());
    } else if (message.getTopic().contains("network")) {
      Logger.info("Received a monitoring message on topic: " + message.getTopic());
    }
  }

  private void handleServiceMsg(ServicePlatformMessage message) {
    if (message.getTopic().endsWith("deploy")) {
      myThreadPool.execute(new DeployServiceCallProcessor(message, message.getSid(), mux));
    } else if (message.getTopic().endsWith("remove")) {
      Logger.info("Received a remove-service message on topic: " + message.getTopic());
      myThreadPool.execute(new RemoveServiceCallProcessor(message, message.getSid(), mux));
    }
  }

  private void handleManagementMessage(ServicePlatformMessage message) {

    if (message.getTopic().contains("compute")) { // compute menagement API
      if (message.getTopic().endsWith("add")) {
        myThreadPool.execute(new AddVimCallProcessor(message, message.getSid(), mux));
      } else if (message.getTopic().endsWith("remove")) {
        myThreadPool.execute(new RemoveVimCallProcessor(message, message.getSid(), mux));
      } else if (message.getTopic().endsWith("resourceAvailability")) {
        myThreadPool.execute(new ResourceAvailabilityCallProcessor(message, message.getSid(), mux));
      } else if (message.getTopic().endsWith("list")) {
        myThreadPool.execute(new ListVimCallProcessor(message, message.getSid(), mux));
      }
    } else if (message.getTopic().contains("storage")) {
      // TODO Storage Management API
    } else if (message.getTopic().contains("network")) {
      if (message.getTopic().endsWith("add")) {
        myThreadPool.execute(new AddVimCallProcessor(message, message.getSid(), mux));
      }
    } else {
      Logger.info("Received an unknown menagement message on topic: " + message.getTopic());
    }

  }

  private boolean isManagementMsg(ServicePlatformMessage message) {
    return message.getTopic().contains("infrastructure.management");
  }

  private boolean isServiceMsg(ServicePlatformMessage message) {
    return message.getTopic().contains("infrastructure.service");
  }

  private boolean isMonitoringMessage(ServicePlatformMessage message) {
    return message.getTopic().contains("infrastructure.monitoring");
  }

  private boolean isRegistrationResponse(ServicePlatformMessage message) {
    return message.getTopic().equals("platform.management.plugin.register")
        && message.getSid().equals(core.getRegistrationSid());
  }

  private boolean isDeregistrationResponse(ServicePlatformMessage message) {
    return message.getTopic().equals("platform.management.plugin.deregister")
        && message.getSid().equals(core.getRegistrationSid());
  }

  public void start() {
    Thread thread = new Thread(this);
    thread.start();
  }

  public void stop() {
    this.stop = true;
  }
}
