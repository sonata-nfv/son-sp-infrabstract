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

package sonata.kernel.vimadaptor;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.NotAuthorizedException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import sonata.kernel.vimadaptor.commons.VimResources;
import sonata.kernel.vimadaptor.wrapper.SonataGkMockedClient;

public class SPWrapperTest {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(SPWrapperTest.class);

	@Before
	public void setUp() {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "false");
		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "warn");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "warn");
	}

	@Test
	public void listPoPs() throws NotAuthorizedException, ClientProtocolException, IOException {

		String[] vim_cities = {"Athens", "Aveiro", "London", "Paderborn", "Tel Aviv"};
		
		Logger.info("[SpWrapperTest] Creating SONATA Rest Client");
		SonataGkMockedClient client = new SonataGkMockedClient();

		Logger.info("[SpWrapperTest] Retrieving VIMs connected to slave SONATA SP");
		VimResources[] out = client.getVims();

		Logger.info("[SpWrapperTest] VIMs list size: " + out.length);
		
		//mocked vim list must contains 5 elements
		Assert.assertTrue(out.length == 5);
		
		//mocked vim list contains the vim_cities
		for (int i=0;i<out.length;i++){
			Assert.assertTrue(Arrays.asList(vim_cities).contains(out[i].getVimCity()));
		}
	}

}
