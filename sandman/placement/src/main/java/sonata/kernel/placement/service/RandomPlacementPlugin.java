package sonata.kernel.placement.service;

import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.placement.config.PopResource;
import sonata.kernel.placement.service.PlacementPlugin;

import java.util.List;

public class RandomPlacementPlugin implements PlacementPlugin {

    @Override
    public ServiceInstance initialScaling(DeployServiceData serviceData) {
        return null;
    }

    @Override
    public ServiceInstance updateScaling(DeployServiceData serviceData, ServiceInstance instance, ScaleMessage trigger) {
        return null;
    }

    @Override
    public PlacementMapping initialPlacement(DeployServiceData serviceData, ServiceInstance instance, List<PopResource> ressources) {
        return null;
    }

    @Override
    public PlacementMapping updatePlacement(DeployServiceData serviceData, ServiceInstance instance, List<PopResource> ressources, PlacementMapping mapping) {
        return null;
    }

}
