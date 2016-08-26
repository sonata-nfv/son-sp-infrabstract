/**
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos ALL RIGHTS RESERVED.
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
 * @author Dario Valocchi (Ph.D.), UCL
 * 
 */

package sonata.kernel.VimAdaptor;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.junit.Assert;
import org.junit.Test;

import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


/**
 * Unit test for simple App.
 */
public class ServiceDescriptorTest {



  /**
   * Test the whole DeployService payload parsing it from file and doing some basic check on the
   * parsed data.
   * 
   * @throws IOException
   */
  @Test
  public void testParsePayload() throws IOException {

    ServiceDescriptor sd;
    StringBuilder bodyBuilder = new StringBuilder();
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/sonata-demo.yml")), Charset.forName("UTF-8")));
    String line;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Unit.class, new UnitDeserializer());
    mapper.registerModule(module);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    sd = mapper.readValue(bodyBuilder.toString(), ServiceDescriptor.class);

    VnfDescriptor vnfd1;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/vtc-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd1 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    VnfDescriptor vnfd2;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/fw-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd2 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    DeployServiceData data = new DeployServiceData();
    data.setServiceDescriptor(sd);
    data.addVnfDescriptor(vnfd1);
    data.addVnfDescriptor(vnfd2);

    mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.setSerializationInclusion(Include.NON_NULL);
    // System.out.println(mapper.writeValueAsString(data));

  }

  /**
   * Test the Service Descriptor parsing it from file and doing some basic check on the parsed data.
   * 
   * @throws IOException
   */
  @Test
  public void testParseServiceDescriptor() throws IOException {

    ServiceDescriptor sd;
    StringBuilder bodyBuilder = new StringBuilder();
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/sonata-demo.yml")), Charset.forName("UTF-8")));
    String line;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    in.close();
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    sd = mapper.readValue(bodyBuilder.toString(), ServiceDescriptor.class);

    Assert.assertNotNull(sd.getDescriptorVersion());
    Assert.assertNotNull(sd.getVendor());
    Assert.assertNotNull(sd.getName());
    Assert.assertNotNull(sd.getVersion());
    Assert.assertNotNull(sd.getAuthor());
    Assert.assertNotNull(sd.getDescription());
    Assert.assertTrue(sd.getNetworkFunctions().size() > 0);
    Assert.assertTrue(sd.getConnectionPoints().size() > 0);
    Assert.assertTrue(sd.getVirtualLinks().size() > 0);
    Assert.assertTrue(sd.getForwardingGraphs().size() > 0);

    sd = null;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/sonata-demo1.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    in.close();
    mapper = new ObjectMapper(new YAMLFactory());
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    sd = mapper.readValue(bodyBuilder.toString(), ServiceDescriptor.class);

    Assert.assertNotNull(sd.getDescriptorVersion());
    Assert.assertNotNull(sd.getVendor());
    Assert.assertNotNull(sd.getName());
    Assert.assertNotNull(sd.getVersion());
    Assert.assertNotNull(sd.getAuthor());
    Assert.assertNotNull(sd.getDescription());
    Assert.assertTrue(sd.getNetworkFunctions().size() > 0);
    Assert.assertTrue(sd.getConnectionPoints().size() > 0);
    Assert.assertTrue(sd.getVirtualLinks().size() > 0);
    Assert.assertTrue(sd.getForwardingGraphs().size() > 0);

  }

  /**
   * Test the firewall example VNF Descriptor parsing it from file and doing some basic check on the
   * parsed data.
   * 
   * @throws IOException
   */
  @Test
  public void testParseFirewallVNFDescriptor() throws IOException {

    VnfDescriptor vd;
    StringBuilder bodyBuilder = new StringBuilder();
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/fw-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    String line;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    in.close();
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    vd = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    Assert.assertNotNull(vd.getDescriptorVersion());
    Assert.assertNotNull(vd.getVendor());
    Assert.assertNotNull(vd.getName());
    Assert.assertNotNull(vd.getVersion());
    Assert.assertNotNull(vd.getAuthor());
    Assert.assertNotNull(vd.getDescription());
    Assert.assertTrue(vd.getVirtualDeploymentUnits().size() > 0);
    Assert.assertTrue(vd.getVirtualLinks().size() > 0);
    Assert.assertTrue(vd.getConnectionPoints().size() > 0);

  }

  /**
   * Test the vTC example VNF Descriptor parsing it from file and doing some basic check on the
   * parsed data.
   * 
   * @throws IOException
   */
  @Test
  public void testParseVtcVNFDescriptor() throws IOException {

    VnfDescriptor vd;
    StringBuilder bodyBuilder = new StringBuilder();
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/vtc-vnf-vnfd.yml")), Charset.forName("UTF-8")));
    String line;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    in.close();
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Unit.class, new UnitDeserializer());
    mapper.registerModule(module);
    vd = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    Assert.assertNotNull(vd.getDescriptorVersion());
    Assert.assertNotNull(vd.getVendor());
    Assert.assertNotNull(vd.getName());
    Assert.assertNotNull(vd.getVersion());
    Assert.assertNotNull(vd.getAuthor());
    Assert.assertNotNull(vd.getDescription());
    Assert.assertTrue(vd.getVirtualDeploymentUnits().size() > 0);
    Assert.assertTrue(vd.getVirtualLinks().size() > 0);
    Assert.assertTrue(vd.getConnectionPoints().size() > 0);

  }

}
