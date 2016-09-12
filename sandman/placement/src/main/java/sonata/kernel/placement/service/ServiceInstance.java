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

    /**
     * Maps connection point ids to virtual link names
     */
    public final Map<String, String> connectionPoints;

    // Maps unique vnf id from service descriptor to the vnf instance
    public final Map<String, FunctionInstance> functions;

    public final List<UnitInstance> units;

    // Maps virtual link id to LinkInstance
    // LinkInstances connect only units
    public final Map<String, LinkInstance> outerLinks;

    // Maps virtual link id to LinkInstance
    public final Map<String, LinkInstance> innerLinks;

    public ServiceInstance(){
    	logger.info("Service Instance");
        functions = new HashMap<String,FunctionInstance>();
        units = new ArrayList<UnitInstance>();
        outerLinks = new HashMap<String, LinkInstance>();
        innerLinks = new HashMap<String, LinkInstance>();
        this.connectionPoints = new HashMap<String, String>();
    }

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
    }
}
