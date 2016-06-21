package sonata.kernel.adaptor.wrapper;

import org.apache.commons.io.IOUtils;

import sonata.kernel.adaptor.wrapper.openstack.OpenStackHeatClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import junit.framework.TestCase;

public class OpenStackHeatClientTest extends TestCase {

  private OpenStackHeatClient heatClient;

  /**
   * Create the test case
   */
  public OpenStackHeatClientTest(String testName) {
    super(testName);
    initClient();
  }

  private void initClient() {

    // todo - this needs to be moved to configuration file
    this.heatClient = new OpenStackHeatClient("openstack.sonata-nfv.eu", "op_sonata", "op_s0n@t@", "op_sonata");
  }


  /**
   * Test a full flow of: 1-create stack 2-get stack status 3-delete stack
   *
   * @throws IOException
   */
  public void testStackCreateAndStatusAndDelete() throws IOException {

    final String stackName = "testStack" + UUID.randomUUID().toString().replaceAll("-", "");

    // Heat template to be created - convert from file to string
    String template = convertTemplateFileToString("./YAML/single-vm-heat-example");

    // creat the stack, the output of a successful create process is the uuid of the new stack
    String stackUUID = heatClient.createStack(stackName, template);
    assertNotNull("Failed to create stack", stackUUID);

    // check the status of the new stack
    if (stackUUID != null) {
      // status after create
      String status = heatClient.getStackStatus(stackName, stackUUID);
      assertNotNull("Failed to get stack status", status);
      if (status != null) {
        System.out.println("status of stack " + stackName + " is " + status);
        assertTrue(status.contains("CREATE"));
      }

      // delete the stack, the output of a successful delete process is the String DELETED
      String isDeleted = heatClient.deleteStack(stackName, stackUUID);
      assertNotNull("Failed to delete stack", isDeleted);
      if (isDeleted != null) {
        System.out.println("status of deleted stack " + stackName + " is " + isDeleted);
        assertEquals("DELETED", isDeleted);
      }

    }

  }


  /**
   * Checks that when a status of a non existing stack is requested the returned status is null
   *
   * @throws Exception
   */
  public void testStatusOfNonValidUUID() throws Exception {

    // generate random stack name and random stack uuid
    final String stackName = "testStack" + UUID.randomUUID().toString().replaceAll("-", "");
    final String stackUUID = UUID.randomUUID().toString().replaceAll("-", "");

    // try to get the status of this random uuid and verify it is null
    String isDeleted = heatClient.deleteStack(stackName, stackUUID);
    assertNull("Non valid delete operation - recieved DELETED of a non existing stack", isDeleted);

  }

  public void testDeleteNonValidUUID() throws Exception {

    final String stackName = "testStack" + UUID.randomUUID().toString().replaceAll("-", "");
    final String stackUUID = UUID.randomUUID().toString().replaceAll("-", "");

    // try to delete the random uuid and verify it is null
    String deleted = heatClient.getStackStatus(stackName, stackUUID);
    assertNull("Non valid status - recieved status of a non existing stack", deleted);

  }

  private String convertTemplateFileToString(String templatePath) {

    String template = null;

    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(new File(templatePath));
    } catch (FileNotFoundException e) {
      System.out.println("Failed to get template from file" + e.getMessage());
    }


    if (inputStream != null) {
      try {
        template = IOUtils.toString(inputStream);
      } catch (IOException e) {
        System.out.println("Failed to get template from file" + e.getMessage());
      }


    }
    return template;

  }


}
