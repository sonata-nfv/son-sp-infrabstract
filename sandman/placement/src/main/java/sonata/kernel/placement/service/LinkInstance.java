package sonata.kernel.placement.service;

import sonata.kernel.VimAdaptor.commons.nsd.VirtualLink;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class LinkInstance {
	final static Logger logger = Logger.getLogger(LinkInstance.class);
    public final VirtualLink link;

    public List<FunctionInstance> nodeList;

    public LinkInstance(VirtualLink link){
    	logger.info("Link Instance");
        this.link = link;
        nodeList = new ArrayList<FunctionInstance>();
    }

}
