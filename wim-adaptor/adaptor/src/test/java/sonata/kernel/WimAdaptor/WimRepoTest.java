package sonata.kernel.WimAdaptor;


import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import sonata.kernel.WimAdaptor.wrapper.WimRepo;
import sonata.kernel.WimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.WimAdaptor.wrapper.WrapperRecord;

/**
 * Unit test for simple App.
 */
public class WimRepoTest extends TestCase {

  private WimRepo repoInstance;

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public WimRepoTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(WimRepoTest.class);
  }

  /**
   * Create the Wim.
   * 
   * @throws IOException
   */
  public void testCreateWimRepo() {

    repoInstance = new WimRepo();
    ArrayList<String> vims = repoInstance.getComputeVim();
    assertNotNull("Unable to retrieve an empy list. SQL exception occurred", vims);
  }

  public void testAddVim() {


  }

  public void testListWims() {

  }

  public void testAddTenantNet() {

  }

  public void testGetTenantNet() {
  }


}
