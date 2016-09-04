package sonata.kernel.placement.service;

import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ServiceInstance {
	final static Logger logger = Logger.getLogger(ServiceInstance.class);

    public ServiceDescriptor service;

    // Maps unique vnf id from service descriptor to the vnf instance
    public Map<String,FunctionInstance> nodes;

    public List<Object> links;

    public ServiceInstance(){
    	logger.info("Service Instance");
        nodes = new HashMap<String,FunctionInstance>();
        links = new ArrayList<Object>();
    }

}
