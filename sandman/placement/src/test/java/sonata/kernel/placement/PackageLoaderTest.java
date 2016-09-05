package sonata.kernel.placement;

import org.junit.Assert;
import org.junit.Test;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.placement.PackageLoader;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PackageLoaderTest {
    @Test
    public void loadPackageFromDisk() throws Exception {
        DeployServiceData data = PackageLoader.loadPackageFromDisk(Paths.get("YAML","test.son").toString());
        Assert.assertNotNull(data);
    }

}