package sonata.kernel.placement.service;

import sonata.kernel.VimAdaptor.commons.nsd.VirtualLink;

import java.util.ArrayList;
import java.util.List;

public class LinkInstance {

    public final VirtualLink link;

    public List<FunctionInstance> nodeList;

    public LinkInstance(VirtualLink link){
        this.link = link;
        nodeList = new ArrayList<FunctionInstance>();
    }

}
