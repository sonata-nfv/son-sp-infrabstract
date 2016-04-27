package sonata.kernel.adaptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import sonata.kernel.adaptor.commons.DeployServiceData;
import sonata.kernel.adaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.adaptor.commons.vnfd.Unit;
import sonata.kernel.adaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.adaptor.commons.vnfd.VnfDescriptor;

/**
 * Unit test for simple App.
 */
public class ServiceDescriptorTest extends TestCase {

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public ServiceDescriptorTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(ServiceDescriptorTest.class);
  }

  
  
  /**
   * Test the Service Descriptor parsing it from file and doing some basic check on the parsed data.
   * 
   * @throws IOException
   */  
  public void testParsePayload() throws IOException{
    
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

    @SuppressWarnings("unused")
    ArrayList<VnfDescriptor> vnfds = data.getVnfdList();
    
  }
  
  /**
   * Test the Service Descriptor parsing it from file and doing some basic check on the parsed data.
   * 
   * @throws IOException
   */
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

    assertNotNull(sd.getDescriptor_version());
    assertNotNull(sd.getVendor());
    assertNotNull(sd.getName());
    assertNotNull(sd.getVersion());
    assertNotNull(sd.getAuthor());
    assertNotNull(sd.getDescription());
    assertTrue(sd.getNetwork_functions().size() > 0);
    assertTrue(sd.getConnection_points().size() > 0);
    assertTrue(sd.getVirtual_links().size() > 0);
    assertTrue(sd.getForwarding_graphs().size() > 0);
  }

  public void testParseVNFDescriptor() throws IOException {

    VnfDescriptor vd;
    StringBuilder bodyBuilder = new StringBuilder();
    BufferedReader in = new BufferedReader(new InputStreamReader(
        new FileInputStream(new File("./YAML/iperf-vnfd.yml")), Charset.forName("UTF-8")));
    String line;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line + "\n\r");
    in.close();
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    vd = mapper.readValue(bodyBuilder.toString(), VnfDescriptor.class);

    assertNotNull(vd.getDescriptor_version());
    assertNotNull(vd.getVendor());
    assertNotNull(vd.getName());
    assertNotNull(vd.getVersion());
    assertNotNull(vd.getAuthor());
    assertNotNull(vd.getDescription());
    assertTrue(vd.getVirtual_deployment_units().size() > 0);
    assertTrue(vd.getVirtual_links().size() > 0);
    assertTrue(vd.getConnection_points().size() > 0);
  }

}
