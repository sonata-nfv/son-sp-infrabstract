package sonata.kernel.adaptor.wrapper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Unit test for OpenStack Heat client
 */
public class OpenStackClientTest extends TestCase  {

    private OpenStackHeatClient heatClient = new OpenStackHeatClient();

    private String stackUuid = null;

    /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public OpenStackClientTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(OpenStackClientTest.class);
  }

  /**
   * Crate stack
   * 
   * @throws IOException
   */
  public void testCreatStack() throws IOException {
        
        String uuid = heatClient.createStack("test", "/YAML/simpe-heat-example");
        stackUuid = uuid;
        assertNotNull("Failed to create stack", uuid);
        assertNull("uuid of created stack: "+ uuid, uuid);

  }

    /**
     *
     * @throws IOException
     */
    public void testGetCreateStackStatus() throws IOException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: "+ s);
        if (stackUuid!=null) {
            String status = heatClient.getCreateStackStatus("test", stackUuid);
            assertNotNull("Failed to get stack status", status);
            assertNull("status of created stack: " + status, status);
        }

    }
}
