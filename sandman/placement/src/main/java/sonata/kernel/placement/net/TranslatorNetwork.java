package sonata.kernel.placement.net;

import org.apache.log4j.Logger;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.State;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.OSFactory;

import sonata.kernel.placement.config.PopResource;

import java.util.List;
//import sun.plugin2.os.windows.OSVERSIONINFOA;

/**
 * Created by vrv on 8/22/2016.
 */
public class TranslatorNetwork {
	final static Logger logger = Logger.getLogger(TranslatorNetwork.class);
    public static OSClientV2 authenticate_instance(PopResource popResource)
    {
    	logger.info("Authenticating Instance");
    	logger.debug("URI "+ popResource.getEndpoint());
        OSClientV2 os = OSFactory.builderV2()
                .endpoint(popResource.getEndpoint())
                .credentials(popResource.getUserName(),popResource.getPassword())
                .tenantName(popResource.getTenantName())
                .authenticate();
        return os;
    }

    public static void create_network(OSClientV2 os,
                                      String name,
                                      String network_id,
                                      String tenant_id)
    {
        logger.info("Creating network");
        logger.debug("Network name :"+ name);

        Network network = os.networking().network()
                .create(Builders.network().name(name).tenantId(tenant_id).build());

        if(network.getStatus() == State.ERROR)
            logger.error("Network creation failed: Network name: " + name + " endpoint: " + os.getEndpoint());

        return;

    }

    public static void delete_network(OSClientV2 os,
                                      String network_id)
    {
        Network network = os.networking().network().get(network_id);

        if(network.getStatus() == State.ACTIVE)
            os.networking().network().delete(network_id);
        else
            logger.error("Network deletion failed: Network Id: " + network_id + " endpoint: " + os.getEndpoint());

        return;
    }
    
    public static void create_subnet( OSClientV2 os,
                                        String name,
                                        String network_id,
                                        String tenant_id,
                                        String start_ip,
                                        String end_ip,
                                        String baseaddress_cidr)
    {
    	logger.info("Creating subnet");
    	logger.debug("Subnet name :"+ name
                + " Subnet network id :" + network_id
                + " Subnet tenent id :"+ tenant_id
                + " Subnet start ip :"+ start_ip
                + " Subnet end ip :"+ end_ip);

        logger.debug("Listing networks on " + os.getEndpoint());
        List<? extends Network> networks = os.networking().network().list();
        for (Object net: networks) {
            logger.debug("Networks"+ net);
        }

        logger.debug("Listing subnets on " + os.getEndpoint());
        List<? extends Subnet> ss = os.networking().subnet().list();
        for (Object o: ss) {
            System.out.println(o);
            logger.debug("Subnets"+ ss);
        }

        Subnet subnet = os.networking().subnet().create(Builders.subnet()
                .name(name)
                .networkId(network_id)
                .tenantId(tenant_id)
                .addPool(start_ip, end_ip)
                .ipVersion(IPVersionType.V4)
                .cidr(baseaddress_cidr)
                .build());

        /*
        Stack stack = os.heat().stacks().create(Builders.stack()
                .name("Stack Name")
                .template("template")
                .timeoutMins(5L).build()); */
        return;
    }

    public static void delete_subnet(OSClientV2 os,
                                        Subnet subnet)
    {
        logger.info("Deleting subnet :" + subnet.getId() + " on " + os.getEndpoint());
        logger.debug("Subnet name :"+ subnet.getName()
                + " Subnet network id :" + subnet.getNetworkId()
                + " Subnet tenant id :"+ subnet.getTenantId());

        ActionResponse response = os.networking().subnet().delete(subnet.getId());

        if(!response.isSuccess())
            logger.error("Subnet deletion failed with error cause :" + response.getFault());

        logger.debug("Listing networks on " + os.getEndpoint());
        List<? extends Network> networks = os.networking().network().list();
        for (Object net : networks) {
            logger.debug("Networks"+ net);
        }

        logger.debug("Listing subnets on " + os.getEndpoint());
        List<? extends Subnet> ss = os.networking().subnet().list();
        for (Object o: ss) {
            System.out.println(o);
            logger.debug("Subnets"+ ss);
        }

        return;
    }
}
