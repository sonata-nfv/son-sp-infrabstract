package sonata.kernel.placement.service;

import com.google.common.collect.Lists;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.nsd.NetworkFunction;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.nsd.VirtualLink;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.placement.TranslatorCore;
import sonata.kernel.placement.config.NodeResource;
import sonata.kernel.placement.config.PopResource;
import sonata.kernel.placement.service.PlacementPlugin;

import java.util.*;

import org.apache.log4j.Logger;

public class DefaultPlacementPlugin implements PlacementPlugin {
	final static Logger logger = Logger.getLogger(DefaultPlacementPlugin.class);
    @Override
    public ServiceInstance initialScaling(DeployServiceData serviceData) {
    	logger.info("Initial Scaling");
        ServiceInstance instance = new ServiceInstance();
        int nodeCounter = 0;

        // Assumption: one service descriptor, containing a graph of functions
        ServiceDescriptor service = serviceData.getNsd();
        instance.service = service;

        // Create function map for name -> function resolution
        Map<String,VnfDescriptor> functionMap = new HashMap<String,VnfDescriptor>();
        for(VnfDescriptor descriptor : serviceData.getVnfdList()) {
            functionMap.put(descriptor.getName(),descriptor);
            logger.debug("VNF Descriptor "+ descriptor);
        }

        // Create one network service instance as direct mirror of the descriptor definition

        // Functions used in network service
        ArrayList<NetworkFunction> functions = Lists.newArrayList(service.getNetworkFunctions());

        // Links used in network service
        ArrayList<VirtualLink> links = Lists.newArrayList(service.getVirtualLinks());


        // Add functions
        for (NetworkFunction function : functions) {
            VnfDescriptor descriptor = functionMap.get(function.getVnfName());
            assert descriptor!=null : "Virtual Network Function "+function.getVnfName()+" not found";
            FunctionInstance functionInstance = new FunctionInstance(descriptor, "node_"+(++nodeCounter)+"_"+function.getVnfId());
            // TODO: Maybe add additional version/ vendor check
            instance.nodes.put(function.getVnfId(),functionInstance);
        }


        // Add virtual links
        for (VirtualLink link : links) {
            LinkInstance linkInstance = new LinkInstance(link);

            System.out.println("->> " + link.getId());
            // Search for nodes with connection points that are involved in this link
            for(String conPoint : link.getConnectionPointsReference()) {

                String[] conPointParts = conPoint.split(":");
                assert conPointParts!=null && conPointParts.length == 2 : "Virtual Link "+link.getId()+" uses odd vnf reference "+conPoint;
                String vnfid = conPointParts[0];
                String connectionPointName = conPointParts[1];
                logger.debug("VNF Id "+ vnfid);
                logger.debug("Connection Point Name "+ connectionPointName);
                System.out.println(vnfid+" --- "+connectionPointName);
                if("ns".equals(vnfid)) {
                    // TODO: Maybe add virtual links that connect the service (!) connection points to an additional list
                    continue;
                }

                FunctionInstance node = instance.nodes.get(vnfid);
                logger.debug("Nodes "+ instance.nodes.get(vnfid));
                assert node!=null : "Virtual Link "+link.getId()+" references unknown vnf with id "+vnfid;
                linkInstance.nodeList.add(node);
            }

            instance.links.add(linkInstance);
        }


        return instance;
    }

    @Override
    public ServiceInstance updateScaling(DeployServiceData serviceData, ServiceInstance instance, ScaleMessage trigger) {
    	logger.info("Update Scaling");
        // TODO: implement update for scale out/in
        return null;
    }

    @Override
    public PlacementMapping initialPlacement(DeployServiceData serviceData, ServiceInstance instance, List<PopResource> resources) {
    	logger.info("Initial Placement");
    	PlacementMapping mapping = new PlacementMapping();
        mapping.resources.addAll(resources);

        // For every pop there is a list of available nodes
        List<List<String>> availableResourceNodes = new ArrayList<List<String>>();
        int availableNodeCounter = 0;

        // TODO: actually need uniquely identifiable resource node ids

        for(PopResource pop : resources) {
            List<String> popNodes = new ArrayList<String>();
            for(NodeResource nodeSet : pop.getNodes()) {
                for(int i=1; i<=nodeSet.getQuantity(); i++) {
                    popNodes.add(pop.getPopName()+"_"+nodeSet.getName()+"_"+i);
                }
                availableNodeCounter += nodeSet.getQuantity();
            }
            availableResourceNodes.add(popNodes);
        }
        // Warning: this lists will get consumed in the following steps!

        // Simply map the list of instance nodes to the lists of resource nodes
        List<String> functionNodeNames = Lists.newArrayList(instance.nodes.keySet());

        assert availableNodeCounter >= functionNodeNames.size() : "Datacenter do not have enough nodes." ;
        int currentDatacenterIndex = 0;
        for(int i=0; i<functionNodeNames.size(); i++) {

            List<String> availableNodes;

            // Search for datacenter with available nodes
            do {
                availableNodes = availableResourceNodes.get(currentDatacenterIndex);
                if(availableNodes.isEmpty())
                    currentDatacenterIndex++;
            } while(availableNodes.isEmpty());

            mapping.mapping.put(functionNodeNames.get(i), availableNodes.remove(0));
            mapping.popMapping.put(functionNodeNames.get(i), resources.get(currentDatacenterIndex));
        }

        // TODO: Ignoring network resources for now

        return mapping;
    }

    @Override
    public PlacementMapping updatePlacement(DeployServiceData serviceData, ServiceInstance instance, List<PopResource> ressources, PlacementMapping mapping) {

        // TODO: implement placement update for scale out/in
        return null;
    }
}
