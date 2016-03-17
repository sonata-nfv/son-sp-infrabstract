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
package sonata.kernel.adaptor.wrapper;

import java.net.InetAddress;
import java.net.URL;

/**
 * 
 */
public class WrapperConfiguration {

	private URL vimEndpoint;
	private String vimType;
	private String wrapperType;
	private String authUserName;
	private String authPass;
	private String authKey;
	private String UUID;
	
	
	
	public String getWrapperType() {
		return wrapperType;
	}
	public void setWrapperType(String wrapperType) {
		this.wrapperType = wrapperType;
	}
	public URL getVimEndpoint() {
		return vimEndpoint;
	}
	public void setVimEndpoint(URL vimEndpoint) {
		this.vimEndpoint = vimEndpoint;
	}
	public String getVimType() {
		return vimType;
	}
	public void setVimType(String vimType) {
		this.vimType = vimType;
	}
	public String getAuthUserName() {
		return authUserName;
	}
	public void setAuthUserName(String authUserName) {
		this.authUserName = authUserName;
	}
	public String getAuthPass() {
		return authPass;
	}
	public void setAuthPass(String authPass) {
		this.authPass = authPass;
	}
	public String getAuthKey() {
		return authKey;
	}
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
	public String getUUID() {
		return this.UUID;
	}
	
	public void setUUID(String UUID){
		this.UUID=UUID;
	}
	
	
	public String toString(){
		String out="";
		
		out+="SID: "+UUID+"\n\r";
		out+="WrapperType: "+wrapperType+"\n\r";
		out+="VimType: "+vimType+"\n\r";
		out+="VimEndpount: "+vimEndpoint+"\n\r";
		out+="User: "+authUserName+"\n\r";
		out+="pass: "+authPass+"\n\r";
		return out;
	}
	
}
