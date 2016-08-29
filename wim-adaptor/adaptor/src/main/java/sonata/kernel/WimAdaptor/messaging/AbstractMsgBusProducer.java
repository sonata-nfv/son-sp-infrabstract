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

import java.util.concurrent.BlockingQueue;

import org.slf4j.LoggerFactory;


public abstract class AbstractMsgBusProducer implements MsgBusProducer, Runnable {

  private static final org.slf4j.Logger Logger =
      LoggerFactory.getLogger(AbstractMsgBusProducer.class);

  private BlockingQueue<ServicePlatformMessage> muxQueue;
  private boolean stop = false;

  public AbstractMsgBusProducer(BlockingQueue<ServicePlatformMessage> muxQueue) {
    this.muxQueue = muxQueue;
  }

  /**
   * Send a message in the MsgBus.
   * 
   * @param the SP message to send
   */
  public abstract boolean sendMessage(ServicePlatformMessage message);


  /**
   * Start consuming SP messages from the mux queue.
   */
  public boolean startProducing() {
    boolean out = true;
    Thread thread = new Thread(this);
    try {
      thread.start();
    } catch (Exception e) {
      Logger.error(e.getMessage(), e);
      out = false;
    }
    return out;
  }

  /**
   * Stop consuming SP messages from the mux queue.
   */
  public boolean stopProducing() {
    boolean out = true;
    while (!muxQueue.isEmpty()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Logger.error(e.getMessage(), e);
      }
    }
    this.stop = true;
    return out;
  }

  @Override
  public void run() {
    do {
      try {
        this.sendMessage(muxQueue.take());
      } catch (InterruptedException e) {
        Logger.error(e.getMessage(), e);
      }
    } while (!stop);
  }

}
