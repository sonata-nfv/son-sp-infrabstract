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
public abstract class AbstractMsgBusConsumer implements MsgBusConsumer {

  private BlockingQueue<ServicePlatformMessage> dispatcherQueue;

  public AbstractMsgBusConsumer(BlockingQueue<ServicePlatformMessage> dispatcherQueue) {
    this.dispatcherQueue = dispatcherQueue;
  }

  private void enqueue(ServicePlatformMessage message) {
    dispatcherQueue.add(message);
  }

  /**
   * 
   * @param A JSON formatted message from the SP and its Topic.
   */
  void processMessage(String message, String topic, String SID) {
    // TODO process the string (or not, leaving the pre-processing to the
    // dispatcher?)
    ServicePlatformMessage spMessage = new ServicePlatformMessage(message, topic, SID);
    this.enqueue(spMessage);
  }

}
