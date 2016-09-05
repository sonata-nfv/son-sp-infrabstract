package sonata.kernel.placement.service;

import org.junit.Test;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.heat.HeatTemplate;
import sonata.kernel.placement.PackageLoader;
import sonata.kernel.placement.PlacementConfigLoader;
import sonata.kernel.placement.config.PlacementConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DefaultPlacementPluginTest {

    @Test
    public void testOne() {

        System.out.println(new File("").getAbsolutePath());

        PlacementConfig config = PlacementConfigLoader.loadPlacementConfig();

        DeployServiceData data = PackageLoader.loadPackageFromDisk(Paths.get("YAML", "test.son").toString());

        PlacementPlugin plugin = new DefaultPlacementPlugin();

        ServiceInstance instance = plugin.initialScaling(data);

        PlacementMapping mapping = plugin.initialPlacement(data, instance, config.getResources());

        List<HeatTemplate> templates = ServiceHeatTranslator.translatePlacementMappingToHeat(instance, config.getResources(), mapping);

        assert templates.size()==1;

        List<Object> nodeList = new ArrayList<Object>();
        // add first node as example
        nodeList.add(mapping.mapping.keySet().iterator().next());

        ScaleMessage trigger = new ScaleMessage(ScaleMessage.SCALE_TYPE.SCALE_OUT, nodeList);

        ServiceInstance updatedInstance = plugin.updateScaling(data, instance, trigger);

        PlacementMapping updatedMapping = plugin.updatePlacement(data, updatedInstance, config.getResources(), mapping);


    }
}