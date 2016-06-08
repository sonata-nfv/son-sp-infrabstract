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

import sonata.kernel.adaptor.messaging.ServicePlatformMessage;

import java.util.concurrent.BlockingQueue;

public class AdaptorMux {

  private BlockingQueue<ServicePlatformMessage> muxQueue;

  /**
   * Create a multiplexer for the outgoing messages bind to the provided queue.
   * 
   * @param the queue to en-queue outgoing messages
   */
  public AdaptorMux(BlockingQueue<ServicePlatformMessage> muxQueue) {
    this.muxQueue = muxQueue;
  }

  /**
   * enqueue the message in the queue.
   * 
   * @param message the message to enqueue
   * @return true if the message is correctly enqueued, false otherwise.
   */
  public boolean enqueue(ServicePlatformMessage message) {
    return this.muxQueue.add(message);
  }
}
