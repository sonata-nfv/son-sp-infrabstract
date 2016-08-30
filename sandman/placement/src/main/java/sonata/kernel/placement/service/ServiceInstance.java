package sonata.kernel.placement.service;

import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceInstance {

    public ServiceDescriptor service;

    // Maps unique vnf id from service descriptor to the vnf instance
    public Map<String,FunctionInstance> nodes;

    public List<Object> links;

    public ServiceInstance(){
        nodes = new HashMap<String,FunctionInstance>();
        links = new ArrayList<Object>();
    }

}
