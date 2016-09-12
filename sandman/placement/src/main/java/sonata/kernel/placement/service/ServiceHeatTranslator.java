package sonata.kernel.placement.service;

import org.apache.commons.net.util.SubnetUtils;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.heat.HeatModel;
import sonata.kernel.VimAdaptor.commons.heat.HeatResource;
import sonata.kernel.VimAdaptor.commons.heat.HeatTemplate;
import sonata.kernel.VimAdaptor.commons.nsd.ConnectionPoint;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfVirtualLink;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.VimAdaptor.wrapper.openstack.Flavor;
import sonata.kernel.VimAdaptor.wrapper.openstack.OpenStackHeatWrapper;
import sonata.kernel.placement.config.NetworkResource;
import sonata.kernel.placement.config.PopResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ServiceHeatTranslator {

    final static Logger logger = Logger.getLogger(ServiceHeatTranslator.class);

    public static List<HeatTemplate> translatePlacementMappingToHeat(ServiceInstance instance, List<PopResource> resources, PlacementMapping mapping) {
    	logger.info("Transalating Placement Mapping to Heat");
        List<HeatTemplate> templates = new ArrayList<HeatTemplate>();
        List<HeatModel> models = new ArrayList<HeatModel>();
        
        ServiceDescriptor service = instance.service;
        
        for(PopResource datacenter : resources) {

            // TODO: do something more intelligent, also how to connect multiple datacenters?
            WrapperConfiguration config = new WrapperConfiguration();

            config.setTenantExtNet("decd89e2-1681-427e-ac24-6e9f1abb1715");
            config.setTenantExtRouter("20790da5-2dc1-4c7e-b9c3-a8d590517563");

            OpenStackHeatWrapper wrapper = new OpenStackHeatWrapper(config);

            ArrayList<Flavor> vimFlavors = new ArrayList<Flavor>();
            vimFlavors.add(new Flavor("m1.small", 2, 2048, 20));

            // Create list of network resources
            List<String> networkResources = new ArrayList<String>();
            List<String> mgmtNetworkResources = new ArrayList<String>();
            for(NetworkResource nr: datacenter.getNetworks()){
                List<String> nrlist;
                if ("mgmt".equals(nr.getPrefer()))
                    nrlist = mgmtNetworkResources;
                else
                    nrlist = networkResources;
                if(nr.getAvailable()!=null)
                    nrlist.addAll(nr.getAvailable());
                else {
                    SubnetUtilsV6 subnet = SubnetUtilsV6.createSubnet(nr.getSubnet());
                    String[] aa;
                    if(subnet.isV6())
                        aa = subnet.getInfo().getAllAddresses(126);
                    else
                        aa = subnet.getInfo().getAllAddresses();
                    int subnetmaskcidr = subnet.getInfo().getCidrBits();
                    for(String s: aa)
                        nrlist.add(s+"/"+subnetmaskcidr);
                }
            }
            int subnetIndex = 0;
            HeatTemplate template = new HeatTemplate();
            HeatModel model = new HeatModel();
            List<String> mgmtPortNames = new ArrayList<String>();

            // Add Servers for all units
            for(UnitInstance unit:instance.units){
                model.addResource(createServer(instance, unit, vimFlavors, model, mgmtPortNames));
            }

            // Add Routers / RouterInterfaces for all virtual links -- inner
            for(LinkInstance link: instance.innerLinks.values()) {

                // Create router
                HeatResource router = new HeatResource();
                router.setName(instance.service.getName() + ":" + link.getLinkId() + ":" + instance.service.getInstanceUuid());
                router.setType("OS::Neutron::Router");
                router.putProperty("name",
                        instance.service.getName() + ":" + link.getLinkId() + ":" + instance.service.getInstanceUuid());
                model.addResource(router);
                logger.debug("Neutron::Router \t\t\t"+router.getResourceName());
                // For each port create net,subnet,routerInterface
                for(Map.Entry<UnitInstance, String> unitPort: link.nodeList.entrySet()) {

                    UnitInstance unit = unitPort.getKey();
                    String portName = unitPort.getValue();

                    HeatResource network = new HeatResource();
                    network.setType("OS::Neutron::Net");
                    network.setName(unit.parentVnfd.getName() + ":" + link.getLinkId() + ":net:" + instance.service.getInstanceUuid());
                    network.putProperty("name", network.getResourceName());
                    model.addResource(network);

                    HeatResource subnet = new HeatResource();
                    subnet.setType("OS::Neutron::Subnet");
                    subnet.setName(unit.parentVnfd.getName() + ":" + link.getLinkId() + ":subnet:" + instance.service.getInstanceUuid());
                    subnet.putProperty("name", subnet.getResourceName());
                    String cidr = networkResources.get(subnetIndex);
                    subnet.putProperty("cidr", cidr);
                    // subnet.putProperty("gateway_ip", myPool.getGateway(cidr));
                    // subnet.putProperty("cidr", "192.168." + subnetIndex + ".0/24");
                    // subnet.putProperty("gateway_ip", "192.168." + subnetIndex + ".1");
                    subnetIndex++;
                    //net.getInfo().
                    HashMap<String, Object> netMap = new HashMap<String, Object>();
                    netMap.put("get_resource", network.getResourceName());
                    subnet.putProperty("network", netMap);
                    model.addResource(subnet);


                    // Create RouterInterface
                    HeatResource routerInterface = new HeatResource();
                    routerInterface.setType("OS::Neutron::RouterInterface");
                    routerInterface.setName(unit.parentVnfd.getName() + ":" + portName + ":" + instance.service.getInstanceUuid());

                        HashMap<String, Object> subnetMap = new HashMap<String, Object>();
                        subnetMap.put("get_resource", subnet.getResourceName());
                    routerInterface.putProperty("subnet", subnetMap);

                    // Attach to the virtual router
                        HashMap<String, Object> routerMap = new HashMap<String, Object>();
                        routerMap.put("get_resource", router.getResourceName());
                    routerInterface.putProperty("router", routerMap);

                    model.addResource(routerInterface);
                    logger.debug("Neutron::Net \t\t\t\t"+network.getResourceName());
                    logger.debug("Neutron::Subnet \t\t\t"+subnet.getResourceName());
                    logger.debug("Neutron::RouterInterface \t"+routerInterface.getResourceName());
                }
            }

            // Add network resources for virtual links -- outer
            for(LinkInstance link: instance.outerLinks.values()) {
                // Non mgmt link
                boolean mgmtLink = link.isMgmtLink();
                if(mgmtLink)
                    continue;
                // For each port add net,subnet
                for(Map.Entry<UnitInstance, String> unitPort: link.nodeList.entrySet()) {
                    UnitInstance unit = unitPort.getKey();
                    String portName = unitPort.getValue();

                    HeatResource network = new HeatResource();
                    network.setType("OS::Neutron::Net");
                    network.setName(unit.parentVnfd.getName() + ":" + link.getLinkId() + ":net:" + instance.service.getInstanceUuid());
                    network.putProperty("name", network.getResourceName());
                    model.addResource(network);

                    HeatResource subnet = new HeatResource();
                    subnet.setType("OS::Neutron::Subnet");
                    subnet.setName(unit.parentVnfd.getName() + ":" + link.getLinkId() + ":subnet:" + instance.service.getInstanceUuid());
                    subnet.putProperty("name", subnet.getResourceName());
                    String cidr = networkResources.get(subnetIndex);
                    subnet.putProperty("cidr", cidr);
                    // subnet.putProperty("gateway_ip", myPool.getGateway(cidr));
                    // subnet.putProperty("cidr", "192.168." + subnetIndex + ".0/24");
                    // subnet.putProperty("gateway_ip", "192.168." + subnetIndex + ".1");
                    subnetIndex++;
                    HashMap<String, Object> netMap = new HashMap<String, Object>();
                    netMap.put("get_resource", network.getResourceName());
                    subnet.putProperty("network", netMap);
                    model.addResource(subnet);

                    logger.debug("Neutron::Net \t\t\t\t"+network.getResourceName());
                    logger.debug("Neutron::Subnet \t\t\t"+subnet.getResourceName());
                }
            }


            for (String portName : mgmtPortNames) {
                // allocate floating IP
                HeatResource floatingIp = new HeatResource();
                floatingIp.setType("OS::Neutron::FloatingIP");
                floatingIp.setName("floating:" + portName);
                logger.debug("Neutron::FloatingIP \t\t"+floatingIp.getResourceName());

                floatingIp.putProperty("floating_network_id", config.getTenantExtNet());

                HashMap<String, Object> floatMapPort = new HashMap<String, Object>();
                floatMapPort.put("get_resource", portName);
                floatingIp.putProperty("port_id", floatMapPort);

                model.addResource(floatingIp);
            }

            // Add Mgmt stuff
            HeatResource mgmtNetwork = new HeatResource();
            mgmtNetwork.setType("OS::Neutron::Net");
            mgmtNetwork.setName(instance.service.getName() + ":mgmt:net:" + instance.service.getInstanceUuid());
            mgmtNetwork.putProperty("name", mgmtNetwork.getResourceName());
            model.addResource(mgmtNetwork);

            HeatResource mgmtSubnet = new HeatResource();
            mgmtSubnet.setType("OS::Neutron::Subnet");
            mgmtSubnet.setName(instance.service.getName() + ":mgmt:subnet:" + instance.service.getInstanceUuid());
            mgmtSubnet.putProperty("name", mgmtSubnet.getResourceName());
            String cidr = networkResources.get(subnetIndex);
            subnetIndex++;
            mgmtSubnet.putProperty("cidr", cidr);
            //mgmtSubnet.putProperty("gateway_ip", myPool.getGateway(cidr));
            HashMap<String, Object> mgmtNetMap = new HashMap<String, Object>();
            mgmtNetMap.put("get_resource", mgmtNetwork.getResourceName());
            mgmtSubnet.putProperty("network", mgmtNetMap);
            model.addResource(mgmtSubnet);

            // Internal mgmt router interface
            HeatResource mgmtRouterInterface = new HeatResource();
            mgmtRouterInterface.setType("OS::Neutron::RouterInterface");
            mgmtRouterInterface.setName(instance.service.getName() + ":mgmt:internal:" + instance.service.getInstanceUuid());
            HashMap<String, Object> mgmtSubnetMapInt = new HashMap<String, Object>();
            mgmtSubnetMapInt.put("get_resource", mgmtSubnet.getResourceName());
            mgmtRouterInterface.putProperty("subnet", mgmtSubnetMapInt);
            mgmtRouterInterface.putProperty("router", config.getTenantExtRouter());
            model.addResource(mgmtRouterInterface);

            logger.debug("Neutron::Net \t\t\t\t"+mgmtNetwork.getResourceName());
            logger.debug("Neutron::Subnet \t\t\t"+mgmtSubnet.getResourceName());
            logger.debug("Neutron::RouterInterface \t"+mgmtRouterInterface.getResourceName());

            model.prepare();

            for (HeatResource resource : model.getResources()) {
                template.putResource(resource.getResourceName(), resource);
            }
            templates.add(template);
        }
        logger.info("Returning templates: "+ templates.size());
        return templates;
    }

    protected static HeatResource createServer(ServiceInstance instance, UnitInstance unit, ArrayList<Flavor> vimFlavors, HeatModel model, List<String> mgmtPortNames){
        HeatResource server = new HeatResource();

        // Add basic properties
        server.setType("OS::Nova::Server");
        server.setName(unit.parentFunction.getVnfId()+":"+unit.name + ":" +instance.service.getInstanceUuid());
        server.putProperty("name", server.getResourceName());
        server.putProperty("flavor", vimFlavors.get(0)); // TODO: Flavor selection method
        server.putProperty("image", unit.descriptor.getVmImage());
        logger.debug("Nova::Server \t\t\t\t"+server.getResourceName());

        // Add ports for the connection points
        List<HashMap<String, Object>> net = new ArrayList<HashMap<String, Object>>();
        for(ConnectionPoint connectionPoint: unit.descriptor.getConnectionPoints()){

            LinkInstance link = instance.findLinkInstanceByUnit(unit, connectionPoint.getId());
            assert link!=null;

            boolean mgmtPort = "mgmt".equals(link.getLinkId());

            // Create port for connection point
            HeatResource port = new HeatResource();
            port.setType("OS::Neutron::Port");
            port.setName(unit.parentVnfd.getName() + ":" + connectionPoint.getId() + ":" + instance.service.getInstanceUuid());
            port.putProperty("name", port.getResourceName());
            logger.debug("Neutron::Port \t\t\t\t"+port.getResourceName());

            HashMap<String, Object> netMap = new HashMap<String, Object>();
            if(mgmtPort)
                netMap.put("get_resource", instance.service.getName() + ":mgmt:net:" + instance.service.getInstanceUuid());
            else
                netMap.put("get_resource",
                    unit.parentVnfd.getName() + ":" + link.getLinkId() + ":net:" + instance.service.getInstanceUuid());
            port.putProperty("network", netMap);
            model.addResource(port);

            // Add the port to the server
            HashMap<String, Object> n1 = new HashMap<String, Object>();
            HashMap<String, Object> portMap = new HashMap<String, Object>();
            portMap.put("get_resource",
                    unit.parentVnfd.getName() + ":" + connectionPoint.getId() + ":" + instance.service.getInstanceUuid());
            n1.put("port", portMap);
            net.add(n1);

            if(mgmtPort)
                mgmtPortNames.add(port.getResourceName());
        }
        server.putProperty("networks", net);
        return server;
    }

}
