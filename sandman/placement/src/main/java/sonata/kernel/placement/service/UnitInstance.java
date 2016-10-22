package sonata.kernel.placement.service;

import sonata.kernel.VimAdaptor.commons.nsd.ConnectionPoint;
import sonata.kernel.VimAdaptor.commons.nsd.NetworkFunction;
import sonata.kernel.VimAdaptor.commons.vnfd.VirtualDeploymentUnit;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UnitInstance {

    public FunctionInstance parentInstance;

    public NetworkFunction parentFunction;

    public VnfDescriptor parentVnfd;

    public VirtualDeploymentUnit descriptor;

    public String name;

    // connection point id maps to unique connection point id
    public final Map<String,String> connectionPoints;

    // External connection point id maps to internal connection point id
    public final Map<String, String> aliasConnectionPoints;

    // Connection point name maps to link instance
    // Several links can include this unit using in the end the same port
    public final Map<String, LinkInstance> links;

    public UnitInstance(FunctionInstance parentInstance, NetworkFunction parentFunction, VnfDescriptor parentVnfd, VirtualDeploymentUnit descriptor, String name){
        this.parentInstance = parentInstance;
        this.parentFunction = parentFunction;
        this.parentVnfd = parentVnfd;
        this.descriptor = descriptor;
        this.name = name;
        connectionPoints = new HashMap<String,String>();
        for(ConnectionPoint point:descriptor.getConnectionPoints()) {
            connectionPoints.put(point.getId(), name+"_"+point.getId());
        }
        aliasConnectionPoints = new HashMap<String, String>();
        links = new HashMap<String, LinkInstance>();
    }

//    public boolean linkConnectsToUnit(LinkInstance link, String portname){
//        // direct connection
//        if(links.get(portname)==link)
//            return true;
//        // connection uses alias, but actually the given portname
//        // link --> alias --> portname --> unit
//        String linkPortname = link.nodeList.get(this);
//        String alias = aliasConnectionPoints.get(linkPortname);
//        for(Map.Entry<String, String> entryAlias: this.aliasConnectionPoints.entrySet())
//            if(entryAlias.getValue().equals(portname)) {
//                alias = entryAlias.getKey();
//                break;
//            }
//        if(linkPortname!=null && alias!=null && links.get(alias)==link)
//            return true;
//        return false;
//    }




}
