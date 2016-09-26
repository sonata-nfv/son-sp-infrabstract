package sonata.kernel.placement.net;

import org.junit.Test;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;

import sonata.kernel.placement.config.PopResource;

import static org.junit.Assert.*;

/**
 * Created by Manoj.
 */
public class TranslatorNetworkTest {
	
	
	
	@Test
	public void authenticate_Instance_create_network() throws Exception {
		PopResource res = new PopResource();
		res.setEndpoint("http://131.234.31.45:5001/v2.0");
		res.setUserName("bla");
		res.setPassword("bla");
		res.setTenantName("fc394f2ab2df4114bde39905f800dc57");
		res.setPopName("Datacenter1");
		OSClientV2 os = TranslatorNetwork.authenticate_instance(res);
		System.out.println(os.getEndpoint());
		
		Network network = TranslatorNetwork.create_network(os, "Test6", "fc394f2ab2df4114bde39905f800dc57");
		String netId = network.getId();
		Thread.sleep(10000);
		Subnet subnet = TranslatorNetwork.create_subnet(os, "Test2", netId, "fc394f2ab2df4114bde39905f800dc57", "10.0.1.0", "10.0.1.7", "10.0.1.0/29");
		String subId = subnet.getId();
		Thread.sleep(30000);
		TranslatorNetwork.delete_subnet(os, subId);
		Thread.sleep(30000);
		TranslatorNetwork.delete_network(os, netId);
	}
	
	@Test
	public void authenticate_Instance_delete_network() throws Exception {
		
		PopResource res = new PopResource();
		res.setEndpoint("http://131.234.31.45:5001/v2.0");
		res.setUserName("bla");
		res.setPassword("bla");
		res.setTenantName("fc394f2ab2df4114bde39905f800dc57");
		res.setPopName("Datacenter1");
		OSClientV2 os = TranslatorNetwork.authenticate_instance(res);
		System.out.println(os.getEndpoint());
		
		//TranslatorNetwork.delete_network(os, "59ea2163-a86c-460c-bf10-b28d8c0a97bc");
	}
	
	
	
	@Test
	public void authenticate_Instance_create_subnet() throws Exception {
		PopResource res = new PopResource();
		res.setEndpoint("http://131.234.31.45:5001/v2.0");
		res.setUserName("bla");
		res.setPassword("bla");
		res.setTenantName("fc394f2ab2df4114bde39905f800dc57");
		res.setPopName("Datacenter1");
		OSClientV2 os = TranslatorNetwork.authenticate_instance(res);
		System.out.println(os.getEndpoint());
		
		//TranslatorNetwork.create_subnet(os, "Test2", "374f8ece-8830-4412-ac84-2901c3158125", "fc394f2ab2df4114bde39905f800dc57", "10.0.1.0", "10.0.1.7", "10.0.1.0/29");
		//TranslatorNetwork.delete_subnet(os, "");
	}
	
	@Test
	public void authenticate_Instance_delete_subnet() throws Exception {
		PopResource res = new PopResource();
		res.setEndpoint("http://131.234.31.45:5001/v2.0");
		res.setUserName("bla");
		res.setPassword("bla");
		res.setTenantName("fc394f2ab2df4114bde39905f800dc57");
		res.setPopName("Datacenter1");
		OSClientV2 os = TranslatorNetwork.authenticate_instance(res);
		System.out.println(os.getEndpoint());
		
		//TranslatorNetwork.delete_subnet(os, "7cb96914-b311-4b4a-81ae-fa0605320b63");
	}
}
	
