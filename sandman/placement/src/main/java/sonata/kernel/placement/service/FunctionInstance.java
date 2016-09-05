package sonata.kernel.placement.service;

import sonata.kernel.VimAdaptor.commons.nsd.ConnectionPoint;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.placement.TranslatorCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class FunctionInstance {
	final static Logger logger = Logger.getLogger(FunctionInstance.class);
    public final VnfDescriptor descriptor;

    public final String name;

    public final Map<String,String> connectionPoints;

    public FunctionInstance(VnfDescriptor descriptor, String name){
    	logger.info("Function Instance Name: "+ name);
        this.descriptor = descriptor;
        this.name = name;
        this.connectionPoints = new HashMap<String,String>();
        for(ConnectionPoint point : descriptor.getConnectionPoints()){
            connectionPoints.put(point.getId(), name+"_"+point.getId());
            logger.info("Connection point: "+ point.getId() );
        }
    }
}
