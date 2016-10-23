package sonata.kernel.placement.service;

import com.google.common.collect.Lists;
import org.jaxen.Function;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.nsd.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfVirtualLink;

/*
ServiceInstanceManager enables addition/deletion/updation of resources
for a given the ServiceInstance pertaining to the SONATA descriptor.
 */

public class ServiceInstanceManager {

    final static Logger logger = Logger.getLogger(ServiceInstanceManager.class);

    public enum ACTION_TYPE {
        ADD_INSTANCE,
        DELETE_INSTANCE
    }

    public void initialize_service_instance(DeployServiceData service_data)
    {
        ServiceDescriptor service = service_data.getNsd();

        ServiceInstance instance = new ServiceInstance();
        instance.service = service;


        ArrayList<VirtualLink> virtual_links = Lists.newArrayList(service.getVirtualLinks());
        String uuid = service.getUuid();
        String instance_uuid = service.getInstanceUuid();
        String service_name = service.getName();
        ArrayList<ConnectionPoint> connection_points = Lists.newArrayList(service.getConnectionPoints());
        ArrayList<ForwardingGraph> forwarding_graph = Lists.newArrayList(service.getForwardingGraphs());

        initialize_function_instance(instance, service_data);

    }

    protected void initialize_function_instance(ServiceInstance instance, DeployServiceData service_data)
    {
        Map<String, VnfDescriptor> nw_function_desc_map = new HashMap<String,VnfDescriptor>();
        ArrayList<NetworkFunction> network_functions = Lists.newArrayList(instance.service.getNetworkFunctions());

        for(VnfDescriptor descriptor : service_data.getVnfdList()) {
            nw_function_desc_map.put(descriptor.getName(),descriptor);
            logger.debug("VNF Descriptor "+ descriptor);
        }

        for (NetworkFunction function : network_functions) {
            VnfDescriptor descriptor = nw_function_desc_map.get(function.getVnfName());
            assert descriptor!=null : "Virtual Network Function "+function.getVnfName()+" not found";

            FunctionInstance function_instance = new FunctionInstance(function, descriptor, function.getVnfId());

            for(VnfVirtualLink link: descriptor.getVirtualLinks()){

                LinkInstance linkInstance = new LinkInstance(link, "vnflink:"+function_instance.name+":"+link.getId());
                boolean is_outerlink = false;

                for(String ref: link.getConnectionPointsReference()){
                    String[] conPointParts = ref.split(":");
                    if("vnf".equals(conPointParts[0])) {
                        is_outerlink = true;
                        continue;
                    }
                    linkInstance.interfaceList.put(function_instance, ref);
                }
                if(is_outerlink)
                    instance.outerLinks.put(link.getId(), linkInstance);
                else
                    instance.innerLinks.put(link.getId(), linkInstance);
            }

            instance.functions.put(function.getVnfId(),function_instance);
        }
    }

    public void update_functions_list(FunctionInstance instance, ACTION_TYPE action)
    {

    }

    public void update_vlink_list(LinkInstance instance, ACTION_TYPE action)
    {

    }


}
