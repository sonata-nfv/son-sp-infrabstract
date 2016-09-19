package sonata.kernel.placement.net;

import org.junit.Test;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.network.Subnet;

import sonata.kernel.placement.config.PopResource;

import static org.junit.Assert.*;

public class TranslatorNetworkTest {

	@Test
	public void authenticate_Instance() throws Exception {
		PopResource res = new PopResource();
		res.setEndpoint("http://131.234.31.45:5001/v2.0");
		res.setUserName("bla");
		res.setPassword("bla");
		res.setTenantName("fc394f2ab2df4114bde39905f800dc57");
		res.setPopName("Datacenter1");
		OSClientV2 os = TranslatorNetwork.authenticate_instance(res);
		System.out.println(os.getEndpoint());
		
		TranslatorNetwork.create_subnet(os, "Test", "bd3711ee-fc31-4d65-802d-df6f9b071ab0", "fc394f2ab2df4114bde39905f800dc57", "10.0.1.0", "10.0.1.7", "10.0.1.0/29");
		//TranslatorNetwork.delete_subnet(os, "");
	}
}
