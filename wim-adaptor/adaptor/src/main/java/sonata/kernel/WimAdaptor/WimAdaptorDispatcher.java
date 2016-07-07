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

package sonata.kernel.WimAdaptor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import sonata.kernel.WimAdaptor.messaging.ServicePlatformMessage;


public class WimAdaptorDispatcher implements Runnable {

  private BlockingQueue<ServicePlatformMessage> myQueue;
  private Executor myThreadPool;
  private boolean stop = false;
  private WimAdaptorMux mux;
  private WimAdaptorCore core;

  /**
   * Create an WimAdaptorDispatcher attached to the queue. CallProcessor will be bind to the
   * provided mux.
   * 
   * @param queue the queue the dispatcher is attached to
   * 
   * @param mux the WimAdaptorMux the CallProcessors will be attached to
   */
  public WimAdaptorDispatcher(BlockingQueue<ServicePlatformMessage> queue, WimAdaptorMux mux,
      WimAdaptorCore core) {
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
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } while (!stop);
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
