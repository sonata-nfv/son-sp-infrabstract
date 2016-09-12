package sonata.kernel.placement.service;

import com.google.common.collect.Lists;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.nsd.ConnectionPoint;
import sonata.kernel.VimAdaptor.commons.nsd.NetworkFunction;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.nsd.VirtualLink;
import sonata.kernel.VimAdaptor.commons.vnfd.ConnectionPointReference;
import sonata.kernel.VimAdaptor.commons.vnfd.VirtualDeploymentUnit;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfVirtualLink;
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
        //Map<String, FunctionInstance> functionInstances = new HashMap<String, FunctionInstance>();
        for (NetworkFunction function : functions) {
            VnfDescriptor descriptor = functionMap.get(function.getVnfName());
            assert descriptor!=null : "Virtual Network Function "+function.getVnfName()+" not found";
            //assert function.getVnfVersion().equals(descriptor.getVersion())==false;
            //assert function.getVnfVendor().equals(descriptor.getVendor())==false;

            FunctionInstance functionInstance = initVnf(function, descriptor);

            //functionInstances.put(function.getVnfId(),functionInstance);

            instance.functions.put(function.getVnfId(),functionInstance);
        }


        // Add virtual links to connect functions
        for (VirtualLink link : links) {
            LinkInstance linkInstance = new LinkInstance(link, "nslink:"+link.getId());

            int nsConnectionPointCount = 0;
            String nsConnectionPointName = null;

            // Search for nodes with connection points that are involved in this link
            for(String conPoint : link.getConnectionPointsReference()) {

                String[] conPointParts = conPoint.split(":");
                assert conPointParts != null && conPointParts.length == 2 : "Virtual Link " + link.getId() + " uses odd vnf reference " + conPoint;
                String vnfid = conPointParts[0];
                String connectionPointName = conPointParts[1];

                if ("ns".equals(vnfid)) {
                    nsConnectionPointCount++;
                    nsConnectionPointName = connectionPointName;
                    // No units to add for ns outer connection point
                    continue;
                }

                // Get vnf instance created before
                FunctionInstance functionInstance = instance.functions.get(vnfid);
                assert functionInstance != null : "In Service " + service.getName() + " Virtual Link " + link.getId() + " references unknown vnf with id " + vnfid;

                // Add units for vnf virtual link to new LinkInstance that connects vnfs
                LinkInstance vnfLinkInstance = functionInstance.outerLinks.get(connectionPointName);
                assert vnfLinkInstance != null : "In Service " + service.getName() + " Virtual Link " + link.getId() + " connects to function " + functionInstance.name + " that does not contain link for connection point " + connectionPointName;

                for (UnitInstance unit: vnfLinkInstance.nodeList.keySet()) {
                    linkInstance.nodeList.put(unit, conPoint);
                    unit.aliasConnectionPoints.put(conPoint, vnfLinkInstance.nodeList.get(unit));
                    unit.links.put(conPoint, linkInstance);
                }
            }
            if(nsConnectionPointCount>0){
                instance.connectionPoints.put(nsConnectionPointName, link.getId());
                instance.outerLinks.put(link.getId(), linkInstance);
            } else
                instance.innerLinks.put(link.getId(), linkInstance);
        }

        // Add LinkInstances for inner Vnf VirtualLinks and units
        for(FunctionInstance functionInstance: instance.functions.values()) {
            for(Map.Entry<String, LinkInstance> linkEntry: functionInstance.innerLinks.entrySet()) {
                instance.innerLinks.put(linkEntry.getKey(), linkEntry.getValue());
            }
            instance.units.addAll(functionInstance.units.values());
        }

        return instance;
    }

    /**
     * Creates instances of a function's units and virtual links
     * @param function
     * @param vnfd
     */
    protected FunctionInstance initVnf(NetworkFunction function, VnfDescriptor vnfd){

        FunctionInstance instance = new FunctionInstance(function, vnfd, function.getVnfId());

        // Create UnitInstances for VirtualDeploymentUnits
        for(VirtualDeploymentUnit unit : vnfd.getVirtualDeploymentUnits()) {
            instance.units.put(unit.getId(),new UnitInstance(instance, function, vnfd, unit, function.getVnfId()+":unit:"+unit.getId()));
        }

        // Create list of Vnf outer connection point names
        List<String> vnfConPoints = new ArrayList<String>();
        for(ConnectionPoint p:vnfd.getConnectionPoints()){
            vnfConPoints.add(p.getId());
        }

        // Create LinkInstances for VirtualLinks
        for(VnfVirtualLink link: vnfd.getVirtualLinks()){
            // Create new VirtualLink
            LinkInstance linkInstance = new LinkInstance(link, "vnflink:"+instance.name+":"+link.getId());
            // Variables to check for Vnf outer connection point
            int outerVnfConnection = 0;
            String outVnfConnectionName = null;
            // Add UnitInstances to LinkInstance
            for(String ref: link.getConnectionPointsReference()){
                String[] conPointParts = ref.split(":");
                if("vnf".equals(conPointParts[0])) {
                    outerVnfConnection++;
                    outVnfConnectionName = conPointParts[1];
                    // No UnitInstance for Vnf outer connection point
                    continue;
                }
                UnitInstance unit = instance.units.get(conPointParts[0]);
                assert unit != null : "In Vnfd "+vnfd.getName()+" virtual link "+link.getId()+" references an unknown connection point "+ref;
                linkInstance.nodeList.put(unit, ref);
                unit.links.put(ref, linkInstance);
            }
            assert outerVnfConnection<=1 : "In Vnfd "+vnfd.getName()+" virtual link "+link.getId()+" connects to more than one vnf outer connection";
            if(outerVnfConnection>0) {
                instance.connectionPoints.put(outVnfConnectionName, link.getId());
                instance.outerLinks.put(link.getId(), linkInstance);
            }
            else
                instance.innerLinks.put(link.getId(), linkInstance);

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
        List<String> unitNodeNames = new ArrayList<String>();
        for(UnitInstance unitInstance: instance.units) {
            unitNodeNames.add(unitInstance.name);
        }

        assert availableNodeCounter >= unitNodeNames.size() : "Datacenter do not have enough nodes." ;
        int currentDatacenterIndex = 0;
        for(int i=0; i<unitNodeNames.size(); i++) {

            List<String> availableNodes;

            // Search for datacenter with available nodes
            do {
                availableNodes = availableResourceNodes.get(currentDatacenterIndex);
                if(availableNodes.isEmpty())
                    currentDatacenterIndex++;
            } while(availableNodes.isEmpty());

            mapping.mapping.put(unitNodeNames.get(i), availableNodes.remove(0));
            mapping.popMapping.put(unitNodeNames.get(i), resources.get(currentDatacenterIndex));
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
