package sonata.kernel.adaptor.wrapper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Unit test for OpenStack Heat client
 */
public class OpenStackClientTest extends TestCase {

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
    public void testStackCreateStatus() throws IOException {

        final String stackName = "testStack" + UUID.randomUUID().toString().replaceAll("-", "");

        String stackUUID = heatClient.createStack(stackName, "./YAML/simpe-heat-example");
        stackUuid = stackUUID;

        assertNotNull("Failed to create stack" , stackUUID);

        if (stackUuid != null) {
            String status = heatClient.getCreateStackStatus("test", stackUuid);
            assertNotNull("Failed to get stack status", status);

            if (status!=null){
                System.out.println("status of stack " + stackName + " is "+ status);
            }
        }


    }


//    /**
//     * @throws IOException
//     */
//    public void testGetDelete() throws IOException {
//
//        if (stackUuid != null) {
//            String status = heatClient.deleteStack("test", stackUuid);
//            assertNotNull("Failed to delete stack", status);
//            assertNull("status of deletied stack: " + status, status);
//        }
//
//    }
}
