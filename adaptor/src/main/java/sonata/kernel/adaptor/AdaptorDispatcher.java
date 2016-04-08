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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.json.JSONTokener;

import sonata.kernel.adaptor.messaging.ServicePlatformMessage;

/**
 * 
 */
public class AdaptorDispatcher implements Runnable {

  private BlockingQueue<ServicePlatformMessage> myQueue;
  private Executor myThreadPool;
  private boolean stop = false;
  private AdaptorMux mux;
  private AdaptorCore core;

  public AdaptorDispatcher(BlockingQueue<ServicePlatformMessage> queue, AdaptorMux mux,
      AdaptorCore core) {
    myQueue = queue;
    myThreadPool = Executors.newCachedThreadPool();
    this.mux = mux;
    this.core = core;
  }

  public void run() {
    ServicePlatformMessage message;
    do {
      try {
        message = myQueue.take();

        if (isRegistrationResponse(message)) this.core.handleRegistrationResponse(message);
        if (isDeregistrationResponse(message))
          this.core.handleDeregistrationResponse(message);
        else if (isManagementMsg(message)) {
          handleManagementMessage(message);
        } else if (isServiceMsg(message)) {
          this.handleServiceMsg(message);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } while (!stop);
  }

  private void handleServiceMsg(ServicePlatformMessage message) {
    if (message.getTopic().endsWith("deploy"))
      myThreadPool.execute(new StartServiceCallProcessor(message, message.getSID(), mux));
  }

  private void handleManagementMessage(ServicePlatformMessage message) {
    JSONTokener tokener = new JSONTokener(message.getBody());
    JSONObject json = (JSONObject) tokener.nextValue();
    String target = json.getString("target");
    String body = json.getJSONObject("body").toString();

    // TODO full API definition.
    if (target.equals("addVim")) myThreadPool.execute(new AddVimCallProcessor(
        new ServicePlatformMessage(body, message.getTopic(), message.getSID()), message.getSID(),
        mux));
  }

  private boolean isManagementMsg(ServicePlatformMessage message) {
    return message.getTopic().contains("infrastructure.management");
  }

  private boolean isServiceMsg(ServicePlatformMessage message) {
    return message.getTopic().contains("infrastructure.service");
  }

  private boolean isRegistrationResponse(ServicePlatformMessage message) {
    return message.getTopic().equals("platform.management.plugin.register")
        && message.getSID().equals(core.getRegistrationUUID());
  }

  private boolean isDeregistrationResponse(ServicePlatformMessage message) {
    return message.getTopic().equals("platform.management.plugin.deregister")
        && message.getSID().equals(core.getRegistrationUUID());
  }

  public void start() {
    Thread t = new Thread(this);
    t.start();
  }

  public void stop() {
    this.stop = true;
  }
}
