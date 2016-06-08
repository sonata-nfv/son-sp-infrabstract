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

import java.io.IOException;

/**
 * This is the interface to implement for any implementation of a Message bus producer.
 */
public interface MsgBusProducer {

  /**
   * Connect to the Message Bus.
   */
  public void connectToBus() throws IOException;

  /**
   * Start producing messages in the bus de-queuing them from the mux queue.
   */
  public boolean startProducing();

  /**
   * Stop producing messages.
   */
  public boolean stopProducing();

  /**
   * Send a message on the Message Bus.
   */
  public boolean sendMessage(ServicePlatformMessage message);

}
