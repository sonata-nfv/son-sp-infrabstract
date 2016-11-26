package sonata.kernel.placement.pd;


import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;

import java.util.ArrayList;
import java.util.List;

public class SonataPackage {

    public final PackageDescriptor descriptor;
    public final List<ServiceDescriptor> services;
    public final List<VnfDescriptor> functions;

    public SonataPackage(PackageDescriptor descriptor){
        this.descriptor = descriptor;
        this.services = new ArrayList<ServiceDescriptor>();
        this.functions = new ArrayList<VnfDescriptor>();
    }

}
