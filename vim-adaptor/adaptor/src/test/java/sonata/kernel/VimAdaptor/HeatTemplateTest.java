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
 * @author Sharon Mandel Brin(Ph.D.), Nokia
 * @author Akis Kourtis, Nokia
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
import sonata.kernel.VimAdaptor.commons.heat.HeatResource;
import sonata.kernel.VimAdaptor.commons.heat.HeatTemplate;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.VimAdaptor.wrapper.openstack.Flavor;
import sonata.kernel.VimAdaptor.wrapper.openstack.OpenStackHeatWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Unit test for simple App.
 */
public class HeatTemplateTest {


  @Test
  public void testHeatTranslate() throws IOException {

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

    WrapperConfiguration config = new WrapperConfiguration();

    config.setTenantExtNet("decd89e2-1681-427e-ac24-6e9f1abb1715");
    config.setTenantExtRouter("20790da5-2dc1-4c7e-b9c3-a8d590517563");

    OpenStackHeatWrapper wrapper = new OpenStackHeatWrapper(config);

    ArrayList<Flavor> vimFlavors = new ArrayList<Flavor>();
    vimFlavors.add(new Flavor("m1.small", 2, 2048, 20));

    HeatTemplate template;
    try {
      template = wrapper.getHeatTemplateFromSonataDescriptor(data, vimFlavors);



      mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
      mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
      mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
      mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
      mapper.setSerializationInclusion(Include.NON_NULL);
      String body = mapper.writeValueAsString(template);
      System.out.println(body);

      Assert.assertNotNull(body);
    } catch (Exception e) {
      System.out.println("Exception translating template.");
      e.printStackTrace();
    }
    return;
  }


  @Test
  public void testHeatSerialize() throws IOException {

    HeatTemplate template = new HeatTemplate();

    HeatResource server = new HeatResource();
    server.setType("OS::Nova::Server");
    server.putProperty("name", "testServer");
    server.putProperty("flavor", "m1.small");
    server.putProperty("image", "snappy");
    server.putProperty("flavor", "m1.small");

    ArrayList<HashMap<String, Object>> net = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> n1 = new HashMap<String, Object>();
    HashMap<String, Object> portMap = new HashMap<String, Object>();
    portMap.put("get_resource", "server_port");
    n1.put("port", portMap);
    net.add(n1);
    server.putProperty("networks", net);

    HeatResource port = new HeatResource();
    port.setType("OS::Neutron::Port");
    port.putProperty("network_id", "12345");


    template.putResource("server1_hot", server);
    template.putResource("server_port", port);


    YAMLFactory ff = new YAMLFactory();
    ObjectMapper mapper = new ObjectMapper(ff);
    mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.setSerializationInclusion(Include.NON_NULL);
    String body = mapper.writeValueAsString(template);
    // System.out.println(body);
    Assert.assertNotNull(body);

  }


}
