package sonata.kernel.placement.service;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Service;
import org.apache.commons.chain.web.MapEntry;
import org.jaxen.Function;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.nsd.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import sonata.kernel.VimAdaptor.commons.vnfd.Network;
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

    private ServiceInstance instance;
    private Map<String, VnfDescriptor> nw_function_desc_map;
    private Map<String, NetworkFunction> network_functions_db;

    public ServiceInstance initialize_service_instance(DeployServiceData service_data) {
        ServiceDescriptor service = service_data.getNsd();

        instance = new ServiceInstance();
        instance.service = service;


        String uuid = service.getUuid();
        String instance_uuid = service.getInstanceUuid();
        String service_name = service.getName();
        ArrayList<ConnectionPoint> connection_points = Lists.newArrayList(service.getConnectionPoints());
        ArrayList<ForwardingGraph> forwarding_graph = Lists.newArrayList(service.getForwardingGraphs());

        initialize_function_instance(service_data);
        initialize_vlinks_list(service_data);

        return instance;


    }

    protected void initialize_function_instance(DeployServiceData service_data) {
        nw_function_desc_map = new HashMap<String, VnfDescriptor>();
        network_functions_db = new HashMap<String, NetworkFunction>();
        ArrayList<NetworkFunction> network_functions = Lists.newArrayList(instance.service.getNetworkFunctions());

        for (VnfDescriptor descriptor : service_data.getVnfdList()) {
            nw_function_desc_map.put(descriptor.getName(), descriptor);
            logger.debug("VNF Descriptor " + descriptor);
        }

        for (NetworkFunction function : network_functions) {


            network_functions_db.put(function.getVnfName(), function);
            VnfDescriptor descriptor = nw_function_desc_map.get(function.getVnfName());
            assert descriptor != null : "Virtual Network Function " + function.getVnfName() + " not found";

            FunctionInstance function_instance = new FunctionInstance(function, descriptor, function.getVnfId());

            int id;

            if (null == instance.function_list.get(function.getVnfId())) {
                AtomicInteger vnf_uid = new AtomicInteger(0);
                id = vnf_uid.addAndGet(1);
                vnf_uid.set(id);
                instance.vnf_uid.put(function.getVnfId(), vnf_uid);
                Map<String, FunctionInstance> map = new HashMap<String, FunctionInstance>();
                map.put(function.getVnfId() + id, function_instance);
                instance.function_list.put(function.getVnfId(), map);


            } else {
                id = instance.vnf_uid.get(function.getVnfId()).addAndGet(1);
                instance.vnf_uid.get(function.getVnfId()).set(id);
                instance.function_list.get(function.getVnfId()).put(function.getVnfId() +
                        id, function_instance);
            }

            function_instance.setName(function.getVnfName().split("-")[0] + id);

            initialize_vnfvlink_list(function_instance, descriptor);

        }
    }

    protected void initialize_vnfvlink_list(FunctionInstance f_instance, VnfDescriptor descriptor) {
        for (VnfVirtualLink link : descriptor.getVirtualLinks()) {

            LinkInstance linkInstance = new LinkInstance(link, "vnflink:" + f_instance.name + ":" + link.getId());
            boolean is_outerlink = false;

            for (String ref : link.getConnectionPointsReference()) {
                String[] conPointParts = ref.split(":");
                if ("vnf".equals(conPointParts[0])) {
                    is_outerlink = true;
                    continue;
                }
                linkInstance.interfaceList.put(f_instance, ref);
            }
            if (is_outerlink)
                f_instance.links.put(link.getId(), linkInstance);

        }
    }

    protected void initialize_vlinks_list(DeployServiceData service_data) {
        ArrayList<VirtualLink> virtual_links = Lists.newArrayList(instance.service.getVirtualLinks());

        for (VirtualLink link : virtual_links) {
            LinkInstance linkInstance = new LinkInstance(link, "nslink:" + link.getId());

            boolean is_nslink = false;

            for (String cp_ref : link.getConnectionPointsReference()) {

                String[] cp_ref_str = cp_ref.split(":");
                assert cp_ref_str != null && cp_ref_str.length == 2 : "Virtual Link " + link.getId() + " uses odd vnf reference " + cp_ref;
                String vnfid = cp_ref_str[0];
                String connectionPointName = cp_ref_str[1];

                if ("ns".equals(vnfid)) {
                    is_nslink = true;
                    continue;
                }

                Map<String, FunctionInstance> vnf_instances = instance.function_list.get(vnfid);
                assert vnf_instances.size() != 0 : "In Service " + instance.service.getName() + " Virtual Link " + link.getId() + " references unknown vnf with id " + vnfid;

                for (Map.Entry<String, FunctionInstance> finst : vnf_instances.entrySet()) {
                    LinkInstance vnfLinkInstance = finst.getValue().links.get(connectionPointName);
                    assert vnfLinkInstance != null : "In Service " + instance.service.getName() + " Virtual Link "
                            + link.getId() + " connects to function " + finst.getValue().name
                            + " that does not contain link for connection point " + connectionPointName;

                    linkInstance.interfaceList.put(finst.getValue(), cp_ref);
                }

            }
            if (is_nslink) {
                instance.outerLinks.put(link.getId(), linkInstance);
            } else
                instance.innerLinks.put(link.getId(), linkInstance);
        }

    }

    /*
       Handle addition of function instance as part of scale out
       Handle deletion of function instance as part of scale in

     */

    public ServiceInstance update_functions_list(String vnf_id, ACTION_TYPE action) {
        if (instance.function_list.get(vnf_id) != null && 0 == instance.function_list.get(vnf_id).size()) {

            /*
            Also update the service_data with the new entry for the vnf_descriptor.
             */
            /*
            Map<String, FunctionInstance> map = new HashMap<String, FunctionInstance>();
            map.put(function.getVnfId() + System.currentTimeMillis(), function_instance);
            instance.function_list.put(function.getVnfId(), map);
            */

        } else {
            VnfDescriptor descriptor = nw_function_desc_map.get(vnf_id);
            assert descriptor != null : "Virtual Network Function " + vnf_id + " not found";

            NetworkFunction n_function = network_functions_db.get(vnf_id);


            int id = instance.vnf_uid.get(n_function.getVnfId()).addAndGet(1);
            instance.vnf_uid.get(n_function.getVnfId()).set(id);
            FunctionInstance function_instance = new FunctionInstance(n_function, descriptor, n_function.getVnfName().split("-")[0] + id);

            initialize_vnfvlink_list(function_instance, descriptor);

            instance.function_list.get(n_function.getVnfId()).put(n_function.getVnfId() +
                    id, function_instance);


        }


        return instance;

    }

    /*
    Handle addition of link between function instance.
    Handle deletion of link between function instance.
    Handle update of link as delete followed by add.
     */
    public void update_vlink_list(String endpoint_src, String endpoint_target, ACTION_TYPE action) {
        //Find the link instance on the service instance.
        //perform the necessary action on it.
    }


}
