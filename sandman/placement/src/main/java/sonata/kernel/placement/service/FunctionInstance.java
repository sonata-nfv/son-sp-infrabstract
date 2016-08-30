package sonata.kernel.placement.service;

import sonata.kernel.VimAdaptor.commons.nsd.ConnectionPoint;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionInstance {

    public final VnfDescriptor descriptor;

    public final String name;

    public final Map<String,String> connectionPoints;

    public FunctionInstance(VnfDescriptor descriptor, String name){
        this.descriptor = descriptor;
        this.name = name;
        this.connectionPoints = new HashMap<String,String>();
        for(ConnectionPoint point : descriptor.getConnectionPoints()){
            connectionPoints.put(point.getId(), name+"_"+point.getId());
        }
    }
}
