package sonata.kernel.WimAdaptor.vtn;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.sun.tools.classfile.Opcode.Set;

import sonata.kernel.WimAdaptor.wrapper.WimVendor;
import sonata.kernel.WimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.WimAdaptor.wrapper.vtn.VTNCreateRequest;
import sonata.kernel.WimAdaptor.wrapper.vtn.VtnWrapper;

public class VtnWrapperTest {

  private VtnWrapper wrapper;

  private String instanceId;
  private String inputSegment;
  private String outputSegment;
  private String[] segmentList;

  @Before
  public void setUp() {
    WrapperConfiguration config = new WrapperConfiguration();
    config.setUuid(UUID.randomUUID().toString());
    config.setWrapperType("WIM");
    config.setWimVendor(WimVendor.getByName("VTN"));
    config.setWimEndpoint("10.30.0.13");
    config.setAuthUserName("stavros");
    config.setAuthPass("st@vr0s");
    config.setName("localTestWim");
    wrapper = new VtnWrapper(config);
    // System.out.println("Wrapper info:");
    // System.out.println(wrapper.getConfig());
    UUID uuid = UUID.randomUUID();
    instanceId = uuid.toString();
    inputSegment = "10.100.0.1/24";
    outputSegment = "10.100.0.40/32";
    segmentList = new String[2];
    segmentList[0] = "10.100.0.2/24";
    segmentList[1] = "10.100.0.5/24";
  }

  @Test
  public void testVtnWrapperConfigure() {

    System.out.println();
    System.out.println("Configure VTN rules test for instanceId "+instanceId);
    boolean out = wrapper.configureNetwork(instanceId, inputSegment, outputSegment, segmentList);
    Assert.assertTrue("Configuration call returned failed and returned \"false\" value",out);
    System.out.println("Delete VTN rules test for instanceId "+instanceId);
    out = wrapper.removeNetConfiguration(instanceId);
    Assert.assertTrue("Configuration call returned failed and returned \"false\" value", out);
  }


  @Test
  public void testVtnWrapperConfigureTwice() {

    System.out.println();
    System.out.println("Configure two VTN rules test for instanceId "+ instanceId);
    boolean out = wrapper.configureNetwork(instanceId, inputSegment, outputSegment, segmentList);
    Assert.assertTrue("First configuration call returned failed and returned \"false\" value",out);
    out = wrapper.configureNetwork(instanceId, inputSegment, outputSegment, segmentList);
    Assert.assertTrue("Second configuration call returned failed and returned \"false\" value",out);
    System.out.println("Delete VTN rules test for instanceId "+instanceId);
    out = wrapper.removeNetConfiguration(instanceId);
    Assert.assertTrue("Configuration call returned failed and returned \"false\" value", out);
  }
  
  @Test
  public void testVtnWrapperList() {
    System.out.println();
    System.out.println("List VTN rules test");
    VTNCreateRequest[] out = wrapper.listVTNRuleset();
    Assert.assertNotNull(out);
    System.out.println("Returned list of rules");
    System.out.println(out);
  }
}
