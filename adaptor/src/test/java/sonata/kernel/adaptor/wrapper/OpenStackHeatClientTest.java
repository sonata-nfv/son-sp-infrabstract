package sonata.kernel.adaptor.wrapper;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by smendel on 4/20/16.
 */
public class OpenStackHeatClientTest {


    OpenStackHeatClient client = new OpenStackHeatClient();


    @Test
    public void testCreateStack() throws Exception {

     boolean stackCreated= client.createStack("test", "/workspace/sonata/son-sp-infrabstract/adaptor/YAML/simpe-heat-example");
     Assert.assertTrue("failed to create stack", stackCreated);

    }
}