/**
 * @author Dario Valocchi (Ph.D.)
 * @mail d.valocchi@ucl.ac.uk
 * 
 * Copyright 2016 [Dario Valocchi]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 */
package sonata.kernel.adaptor.messaging;

/**
 * 
 */
public class ServicePlatformMessage {

	String jsonMessage;
	String topic;
	String SID;
	
	/**
	 * @param a JSON formatted String to wrap in the SP Message
	 */
	public ServicePlatformMessage(String message, String topic, String SID) {
		jsonMessage=message;
		this.topic=topic;
		this.SID=SID;
	}

	/**
	 * @return
	 */
	public String getBody() {
		return jsonMessage;
	}

	public void setTopic(String topic) {
		this.topic=topic;
	}

	public String getTopic() {
		return topic;
	}
	
	public String getSID(){
		return this.SID;
	}
	
	public String toString(){
		return "SID: "+SID+ " - message: "+jsonMessage+" - topic: " + topic;
	}

}
 