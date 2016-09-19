package sonata.kernel.placement.net;

import org.junit.Test;
import org.openstack4j.api.OSClient.OSClientV2;

import sonata.kernel.placement.config.PopResource;

import static org.junit.Assert.*;

public class TransalatorPortTest {
	
	@Test
	public void authenticate_Instance_cerate_port() throws Exception {
		PopResource res = new PopResource();
		res.setEndpoint("http://131.234.31.45:5001/v2.0");
		res.setUserName("bla");
		res.setPassword("bla");
		res.setTenantName("fc394f2ab2df4114bde39905f800dc57");
		res.setPopName("Datacenter1");
		OSClientV2 os = TranslatorNetwork.authenticate_instance(res);
		TranslatorPort.create_port(os, "PortTest", "bd3711ee-fc31-4d65-802d-df6f9b071ab0", "192.0.1.1", "afc8013b-89ca-462b-909f-13f582dcd52e","fc394f2ab2df4114bde39905f800dc57");
		//wait(100000);
		//TranslatorPort.delete_port(os, "1");
	}
	
	@Test
	public void authenticate_Instance_delete_port() throws Exception {
		PopResource res = new PopResource();
		res.setEndpoint("http://131.234.31.45:5001/v2.0");
		res.setUserName("bla");
		res.setPassword("bla");
		res.setTenantName("fc394f2ab2df4114bde39905f800dc57");
		res.setPopName("Datacenter1");
		OSClientV2 os = TranslatorNetwork.authenticate_instance(res);
		TranslatorPort.delete_port(os, "2dc93c01-80fd-4fc0-8b67-b3bc16ffcfc6");
	}

}
