package sonata.kernel.placement.service;

import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.wrapper.openstack.DeployServiceFsm;
import sonata.kernel.placement.config.PopResource;

import java.util.List;

import org.apache.log4j.Logger;

public interface PlacementPlugin {
	
    /*
     * SONATA D4.1 - 4.2.1.1 Future Plugins - Features
     * Service Scaling:
     * Input:
     *  - NSD
     *  - Service instance record
     *  - Trigger message specifying what needs to be scaled out/in (e.g., as calculated by an FSM)
     * Functionality:
     *  - Calculate the initial service graph based on the NSD
     *  - Check feasibility of the scaling operation
     * Output:
     *  - Scaling instructions to corresponding FSMs
     *  - Updated service instance record
     */

    public ServiceInstance initialScaling(DeployServiceData serviceData);

    public ServiceInstance updateScaling(DeployServiceData serviceData, ServiceInstance instance, ScaleMessage trigger);

    /*
     * SONATA D4.1 - 4.2.1.1 Future Plugins - Features
     * Service placement calculation:
     * Input:
     *   - (Modified) topology/resource information
     *   - NSD
     *   - Service instance record
     * Functionality:
     *   - Calculate the desired placement for the service without making any changes to the service graph
     * Output:
     *   - Mapping of each VNF in the NSD to a network node and mapping of the corresponding paths among them to network links
     */

    public PlacementMapping initialPlacement(DeployServiceData serviceData, ServiceInstance instance, List<PopResource> resources);

    public PlacementMapping updatePlacement(DeployServiceData serviceData, ServiceInstance instance, List<PopResource> resources, PlacementMapping mapping);

}
