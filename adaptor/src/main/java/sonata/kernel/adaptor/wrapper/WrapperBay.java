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

import java.util.Hashtable;



/**
 * 
 */
public class WrapperBay {
	
	private static WrapperBay myInstance=null;

	private Hashtable<String, WrapperRecord> storageRegistry;
	private Hashtable<String, WrapperRecord> computeRegistry;
	private Hashtable<String, WrapperRecord> networkingRegistry;
	
	private WrapperBay(){
		computeRegistry = new Hashtable<String, WrapperRecord>();
		storageRegistry = new Hashtable<String, WrapperRecord>();
		networkingRegistry = new Hashtable<String, WrapperRecord>();
	}
	
	public static WrapperBay getInstance(){
		if(myInstance==null)
			myInstance=new WrapperBay();
		return myInstance;
	}
	
	
	public boolean registerComputeWrapper(WrapperConfiguration configuration){
		boolean out=true;
		
		
		
		return out;
	}

	/**
	 * @param config The configuration object representing the Wrapper to register
	 */
	public String registerNewWrapper(WrapperConfiguration config) {
		Wrapper newWrapper = WrapperFactory.createWrapper(config);
		if(newWrapper.getType().equals("compute")){
			WrapperRecord record = new WrapperRecord(newWrapper, config, null);
			this.computeRegistry.put(config.getUUID(), record);
		}
		
		String output = "{\"status\":\"COMPLETED\",\"message:\"Vim Added succesfully\"}";
		return output;
	}
	

}
