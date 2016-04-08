package sonata.kernel.adaptor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import sonata.kernel.adaptor.commons.ServiceDescriptor;
import sonata.kernel.adaptor.commons.VirtualLink.ConnectivityType;

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
  public void testParseDescriptor() throws IOException {

    ServiceDescriptor sd;
    StringBuilder bodyBuilder = new StringBuilder();
    BufferedReader in =
        new BufferedReader(new InputStreamReader(new FileInputStream(new File("./sonata-demo.yml")),
          Charset.forName("UTF-8")));
    String line;
    while ((line = in.readLine()) != null)
      bodyBuilder.append(line+"\n\r");
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    sd = mapper.readValue(bodyBuilder.toString(), ServiceDescriptor.class);

    assertNotNull(sd.getDescriptor_version());
    assertNotNull(sd.getVendor());
    assertNotNull(sd.getName());
    assertNotNull(sd.getVersion());
    assertNotNull(sd.getAuthor());
    assertNotNull(sd.getDescription());
    assertTrue(sd.getNetwork_functions().size()>0);
    assertTrue(sd.getConnection_points().size()>0);
    assertTrue(sd.getVirtual_links().size()>0);
    assertTrue(sd.getForwarding_graphs().size()>0);
  }
}
