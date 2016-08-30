package sonata.kernel.placement.service;

import jdk.nashorn.internal.objects.annotations.Function;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.heat.HeatTemplate;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.VimAdaptor.wrapper.openstack.Flavor;
import sonata.kernel.VimAdaptor.wrapper.openstack.OpenStackHeatWrapper;
import sonata.kernel.placement.config.PopResource;

import java.util.ArrayList;
import java.util.List;

public class ServiceHeatTranslator {

    public static List<HeatTemplate> translatePlacementMappingToHeat(ServiceInstance instance, List<PopResource> resources, PlacementMapping mapping) {

        List<HeatTemplate> templates = new ArrayList<HeatTemplate>();

        ServiceDescriptor service = instance.service;

        for(PopResource datacenter : resources) {

            HeatTemplate template = null;

            // TODO: do something more intelligent, also how to connect multiple datacenters?

            // Get all functions, that are mapped to the current datacenter
            List<FunctionInstance> popFunctions = new ArrayList<FunctionInstance>();
            DeployServiceData deployData = new DeployServiceData();
            deployData.setServiceDescriptor(service);

            for(String functionName : mapping.popMapping.keySet()) {
                if(mapping.popMapping.get(functionName).equals(datacenter)) {
                    popFunctions.add(instance.nodes.get(functionName));
                    deployData.addVnfDescriptor(instance.nodes.get(functionName).descriptor);
                }
            }

            WrapperConfiguration config = new WrapperConfiguration();

            config.setTenantExtNet("decd89e2-1681-427e-ac24-6e9f1abb1715");
            config.setTenantExtRouter("20790da5-2dc1-4c7e-b9c3-a8d590517563");

            OpenStackHeatWrapper wrapper = new OpenStackHeatWrapper(config);

            ArrayList<Flavor> vimFlavors = new ArrayList<Flavor>();
            vimFlavors.add(new Flavor("m1.small", 2, 2048, 20));

            try {
                template = wrapper.getHeatTemplateFromSonataDescriptor(deployData, vimFlavors);
                templates.add(template);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return templates;
    }

}
