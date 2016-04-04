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
package sonata.kernel.adaptor;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;

import sonata.kernel.adaptor.messaging.ServicePlatformMessage;
import sonata.kernel.adaptor.wrapper.WrapperBay;
import sonata.kernel.adaptor.wrapper.WrapperConfiguration;

/**
 * 
 */
public class AddVimCallProcessor extends AbstractCallProcessor {

	/**
	 * @param message
	 * @param SID
	 * @param mux
	 */
	public AddVimCallProcessor(ServicePlatformMessage message, String UUID, AdaptorMux mux) {
		super(message, UUID, mux);

	}

	@Override
	public boolean process(ServicePlatformMessage message) {
		boolean out = true;
		//TODO process json message to extract the new Wrapper configurations and ask the bay to create and register it

		JSONTokener tokener = new JSONTokener(message.getBody());

		WrapperConfiguration config = new WrapperConfiguration();

		JSONObject jsonObject = (JSONObject) tokener.nextValue();	
		String wrapperType = jsonObject.getString("wr_type");
		String vimType = jsonObject.getString("vim_type");
		String vimEndpoint = jsonObject.getString("vim_address");
		String authUser = jsonObject.getString("username");
		String authPass = jsonObject.getString("pass");
		try{
			URL vimURL=new URL(vimEndpoint);
			config.setUUID(this.getUUID());
			config.setWrapperType(wrapperType);
			config.setVimType(vimType);
			config.setVimEndpoint(vimURL);
			config.setAuthUserName(authUser);
			config.setAuthPass(authPass);
				
			String output = WrapperBay.getInstance().registerNewWrapper(config);
			this.sendMessage(output);
		}catch(MalformedURLException e){
			e.printStackTrace();
			//TODO Call mux to send a "malformed request" error
			this.sendError("Malformed Request");
			out=false;
		}
		
		return out;
	}

	/**
	 * @param The error message to send back to the platform
	 */
	private void sendError(String message) {
		
		String jsonError = "{\"url\":\"son://sonata-sp/adaptor/registerVim/error?id="+this.getUUID()+"\";\"message\":\""+message+"\"}";
		
		ServicePlatformMessage spMessage = new ServicePlatformMessage(jsonError, this.getMessage().getTopic(),this.getMessage().getSID());
		this.sendToMux(spMessage);
	}

	private void sendMessage(String message) {
		ServicePlatformMessage spMessage = new ServicePlatformMessage(message,this.getMessage().getTopic(),this.getMessage().getSID());
		this.sendToMux(spMessage);
	}
}
