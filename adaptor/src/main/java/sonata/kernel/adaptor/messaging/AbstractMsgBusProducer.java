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
package sonata.kernel.adaptor.messaging;

import java.util.concurrent.BlockingQueue;

/**
 * 
 */
public abstract class AbstractMsgBusProducer implements MsgBusProducer, Runnable {

  private BlockingQueue<ServicePlatformMessage> muxQueue;
  private boolean stop = false;

  public AbstractMsgBusProducer(BlockingQueue<ServicePlatformMessage> muxQueue) {
    this.muxQueue = muxQueue;
  }

  public abstract boolean sendMessage(ServicePlatformMessage message);

  public boolean startProducing() {
    boolean out = true;
    Thread t = new Thread(this);
    try {
      t.start();
    } catch (Exception e) {
      e.printStackTrace();
      out = false;
    }
    return out;
  }

  public boolean stopProducing() {
    boolean out = true;
    while (!muxQueue.isEmpty()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    this.stop = true;
    return out;
  }

  public void run() {
    do {
      try {
        this.sendMessage(muxQueue.take());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } while (!stop);
  }

}
