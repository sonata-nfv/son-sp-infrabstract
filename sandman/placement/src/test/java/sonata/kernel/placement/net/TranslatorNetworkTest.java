package sonata.kernel.placement.net;

import org.junit.Test;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Subnet;

import sonata.kernel.placement.PlacementConfigLoader;
import sonata.kernel.placement.config.PlacementConfig;
import sonata.kernel.placement.config.PopResource;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Manoj.
 */
public class TranslatorNetworkTest {
	
	
	
	@Test
	public void create_network() throws Exception {

		PlacementConfig config = PlacementConfigLoader.loadPlacementConfig();
		List<PopResource> resources = config.getResources();

		for(PopResource pop : resources) {
            OSClientV2 os = TranslatorNetwork.authenticate_instance(pop);
            System.out.println(os.getEndpoint());

            Network network = TranslatorNetwork.create_network(os, "Test6", "fc394f2ab2df4114bde39905f800dc57");
            String netId = network.getId();
            Thread.sleep(10000);
            Subnet subnet = TranslatorNetwork.create_subnet(os, "Test2", netId, "fc394f2ab2df4114bde39905f800dc57", "10.0.1.0", "10.0.1.7", "10.0.1.0/29");
            String subId = subnet.getId();
            Thread.sleep(30000);
            Port port = TranslatorPort.create_port(os, "PortTest", netId, "192.0.1.1", subId);
            String portId = port.getId();
            Thread.sleep(30000);
            TranslatorPort.delete_port(os, portId);
            Thread.sleep(30000);
            TranslatorNetwork.delete_subnet(os, subId);
            Thread.sleep(30000);
            TranslatorNetwork.delete_network(os, netId);
		}
	}

}
	
