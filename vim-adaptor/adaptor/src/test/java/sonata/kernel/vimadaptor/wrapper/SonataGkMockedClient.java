/*
 * Copyright (c) 2015 SONATA-NFV, UCL, OPT ALL RIGHTS RESERVED.
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
 * @author Xosé Ramón Sousa, OPT
 * @author Santiago Rodríguez, OPT
 * 
 */

package sonata.kernel.vimadaptor.wrapper;

import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sonata.kernel.vimadaptor.commons.SonataManifestMapper;
import sonata.kernel.vimadaptor.commons.VimResources;
import sonata.kernel.vimadaptor.wrapper.sp.client.model.VimRequestStatus;

public class SonataGkMockedClient {

	private ObjectMapper mapper;

	/**
	 * @return a List of VimResource object taken from file
	 * @throws IOException
	 *             for JSON parsing error
	 */
	public VimResources[] getVims() throws IOException {

		JSONParser parser = new JSONParser();
		Object object;
		try {
			object = parser.parse(new FileReader("./JSON/request-response.json"));
		} catch (ParseException e1) {
			throw new IOException(
					"Error parsing request response.");
		}

		// convert Object to JSONObject
		//JSONObject jsonObject = (JSONObject) object;

		this.mapper = SonataManifestMapper.getSonataMapper();

		VimRequestStatus requestStatus = mapper.readValue(object.toString(), VimRequestStatus.class);

		String requestUuid = "";
		try {
			requestUuid = requestStatus.getItems().getRequestUuid();
		} catch (NullPointerException e) {
			throw new IOException(
					"The Mocked GK sent back an request status with empty values or values are not parsed correctly.");
		}

		try {
			object = parser.parse(new FileReader("./JSON/vims-list.json"));
		} catch (ParseException e) {
			throw new IOException(
					"Error parsing vim list.");
		}
		//jsonObject = (JSONObject) object;
		VimResources[] list = mapper.readValue(object.toString(), VimResources[].class);

		
		return list;

	}
}
