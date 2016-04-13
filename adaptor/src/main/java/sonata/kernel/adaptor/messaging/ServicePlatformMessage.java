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

public class ServicePlatformMessage {

  String messageBody;
  String topic;
  String sid;

  /**
   * Create the Service Platform Message.
   * 
   * @param message a JSON or YAML formatted String to wrap in the SP Message
   * @param topic the topic on which the message has been received
   * @param sid the session ID of this message
   */
  public ServicePlatformMessage(String message, String topic, String sid) {
    messageBody = message;
    this.topic = topic;
    this.sid = sid;
  }

  /**
   * @return a String representing the message wrapped in this object.
   */
  public String getBody() {
    return messageBody;
  }

  /**
   * set the topic of this message.
   * 
   * @param topic a String representing the Topic to set
   */
  public void setTopic(String topic) {
    this.topic = topic;
  }

  /**
   * @return a String representing the topic of this message.
   */
  public String getTopic() {
    return topic;
  }

  /**
   * @return a String representing the session ID of this message.
   */
  public String getSid() {
    return this.sid;
  }

  @Override
  public String toString() {
    return "sid: " + sid + " - message: " + messageBody + " - topic: " + topic;
  }

}
