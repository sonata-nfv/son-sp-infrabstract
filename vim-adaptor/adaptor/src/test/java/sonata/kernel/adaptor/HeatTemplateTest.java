package sonata.kernel.adaptor;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import sonata.kernel.adaptor.commons.DeployServiceData;
import sonata.kernel.adaptor.commons.heat.HeatResource;
import sonata.kernel.adaptor.commons.heat.HeatTemplate;
import sonata.kernel.adaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.adaptor.commons.vnfd.Unit;
import sonata.kernel.adaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.adaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.adaptor.wrapper.WrapperConfiguration;
import sonata.kernel.adaptor.wrapper.openstack.Flavor;
import sonata.kernel.adaptor.wrapper.openstack.OpenStackHeatWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class HeatTemplateTest extends TestCase {

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public HeatTemplateTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(HeatTemplateTest.class);
  }

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
        new FileInputStream(new File("./YAML/iperf-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd1 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    VnfDescriptor vnfd2;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/firewall-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd2 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);


    VnfDescriptor vnfd3;
    bodyBuilder = new StringBuilder();
    in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/tcpdump-vnfd.yml")), Charset.forName("UTF-8")));
    line = null;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    vnfd3 = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    DeployServiceData data = new DeployServiceData();
    data.setServiceDescriptor(sd);
    data.addVnfDescriptor(vnfd1);
    data.addVnfDescriptor(vnfd2);
    data.addVnfDescriptor(vnfd3);

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

      assertNotNull(body);
    } catch (Exception e) {
      System.out.println("Exception translating template.");
      e.printStackTrace();
    }
    return;
  }


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
    assertNotNull(body);

  }


}
