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

import java.util.UUID;

import sonata.kernel.adaptor.messaging.ServicePlatformMessage;

public class HeartBeat implements Runnable {

  private AdaptorMux mux;
  private AdaptorCore core;
  private double rate; // measured in beat/s
  private boolean stop;

  public HeartBeat(AdaptorMux mux, double rate, AdaptorCore core) {
    this.mux = mux;
    this.rate = rate;
    this.core = core;
  }

  @Override
  public void run() {
    String uuid = core.getUUID();
    while (!stop) {
      try {
        Thread.sleep((int) ((1 / rate) * 1000));
        String body = "{\"uuid\":\"" + uuid + "\",\"state\":\"" + core.getState() + "\"}";
        ServicePlatformMessage message = new ServicePlatformMessage(body,
            "platform.management.plugin." + uuid + ".heartbeat", UUID.randomUUID().toString());
        mux.enqueue(message);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  public void stop() {
    this.stop = true;
  }

}
