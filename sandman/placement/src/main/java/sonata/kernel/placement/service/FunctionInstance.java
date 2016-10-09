package sonata.kernel.placement.service;

import sonata.kernel.VimAdaptor.commons.nsd.ConnectionPoint;
import sonata.kernel.VimAdaptor.commons.nsd.NetworkFunction;
import sonata.kernel.VimAdaptor.commons.vnfd.VirtualDeploymentUnit;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfVirtualLink;
import sonata.kernel.placement.TranslatorCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class FunctionInstance {
	final static Logger logger = Logger.getLogger(FunctionInstance.class);

    public final NetworkFunction function;

    public final VnfDescriptor descriptor;

    public final List<VirtualDeploymentUnit> deploymentUnits;

    public final String name;

    public final Map<String,LinkInstance> links;

    /**
     * Maps connection point id to virtual link name
     */
    public final Map<String,String> connectionPoints;

    /**
     * Maps VirtualDeployUnitName to UnitInstance
     */
    public final Map<String,UnitInstance> units;

    /**
     * Maps virtual link name to the virtual link
     * Contains links that connect to vnf connection points
     * e.g. name: "mgmt", not "vnf:mgmt"
     */
    public final Map<String,LinkInstance> outerLinks;

    /**
     * Maps virtual link name to the virtual link
     * Contains links that connect units only
     */
    public final Map<String, LinkInstance> innerLinks;

    public FunctionInstance(NetworkFunction function, VnfDescriptor descriptor, String name){
    	logger.info("Function Instance Name: "+ name);
        this.function = function;
        this.descriptor = descriptor;
        this.name = name;
        this.connectionPoints = new HashMap<String,String>();
        this.units = new HashMap<String, UnitInstance>();
        this.outerLinks = new HashMap<String, LinkInstance>();
        this.innerLinks = new HashMap<String, LinkInstance>();
        this.links = new HashMap<String, LinkInstance>();
        this.deploymentUnits = descriptor.getVirtualDeploymentUnits();
    }

    public UnitInstance searchUnitInstanceByConnectionPointId(String conPointId){
        for(UnitInstance unit: units.values()){
            for(ConnectionPoint conPoint: unit.descriptor.getConnectionPoints()) {
                if(conPoint.getId().equals(conPointId))
                    return unit;
            }
        }
        return null;
    }
}
