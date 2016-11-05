package sonata.kernel.placement.service;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class ServiceInstance {
	final static Logger logger = Logger.getLogger(ServiceInstance.class);

    public ServiceDescriptor service;

    /**
     * Maps connection point ids to virtual link names
     */
    public final Map<String, String> connectionPoints;

    // Maps unique vnf id from service descriptor to the vnf instance
    //public final Map<String, FunctionInstance> functions;

    public final Map<String, Map<String, FunctionInstance>> function_list;
    public final Map<String, AtomicInteger> vnf_uid;


    //public final List<UnitInstance> units;

    // Maps virtual link id to LinkInstance
    // LinkInstances connect only units
    public final Map<String, LinkInstance> outerLinks;

    // Maps virtual link id to LinkInstance
    public final Map<String, LinkInstance> innerLinks;

    public ServiceInstance(){
    	logger.info("Service Instance");
        //functions = new HashMap<String,FunctionInstance>();
        function_list = new HashMap<String, Map<String,FunctionInstance>>();
        vnf_uid = new HashMap<String, AtomicInteger>();
        //units = new ArrayList<UnitInstance>();
        outerLinks = new HashMap<String, LinkInstance>();
        innerLinks = new HashMap<String, LinkInstance>();
        this.connectionPoints = new HashMap<String, String>();

    }

    /*
    public LinkInstance findLinkInstanceByUnit(UnitInstance unit, String conPoint){

        LinkInstance link = null;
        for(LinkInstance linkInstance: outerLinks.values()) {
            for(Map.Entry<UnitInstance, String> entry: linkInstance.nodeList.entrySet()){
                UnitInstance entryUnit = entry.getKey();
                String portname = entry.getValue();
                if(unit.linkConnectsToUnit(linkInstance, conPoint))
                //if(entryUnit==unit && (entryUnit.links.get(portname)==linkInstance || entryUnit.links.get(entryUnit.aliasConnectionPoints.get(portname))==linkInstance))
                //if(entryUnit.connectionPoints.keySet().contains(portName) || entryUnit.aliasConnectionPoints.keySet().contains(portName))
                    return linkInstance;
            }
        }
        for(LinkInstance linkInstance: innerLinks.values()) {
            for(Map.Entry<UnitInstance, String> entry: linkInstance.nodeList.entrySet()) {
                UnitInstance entryUnit = entry.getKey();
                String portname = entry.getValue();
                if(unit.linkConnectsToUnit(linkInstance, conPoint))
                //if(entryUnit==unit && (entryUnit.links.get(portname)==linkInstance || entryUnit.links.get(entryUnit.aliasConnectionPoints.get(portname))==linkInstance))
                //if(entryUnit.connectionPoints.keySet().contains(portName) || entryUnit.aliasConnectionPoints.keySet().contains(portName))
                    return linkInstance;
            }
        }
        return link;
    }*/
}
