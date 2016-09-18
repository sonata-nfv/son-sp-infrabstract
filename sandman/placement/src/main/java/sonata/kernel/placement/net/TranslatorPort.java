package sonata.kernel.placement.net;

import org.apache.log4j.Logger;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.Port;
import org.openstack4j.openstack.OSFactory;
import sonata.kernel.placement.config.PopResource;
import org.openstack4j.api.OSClient.OSClientV2;

import java.util.List;

/**
 * Created by vrv on 9/17/2016.
 */
public class TranslatorPort {
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

    public static void create_port(OSClientV2 os,
                                   String port_name,
                                   String network_id,
                                   String ip_address,
                                   String subnet_id)
    {
        logger.info("Create port :" + port_name + " on " + os.getEndpoint());
        logger.debug("Port name :" + port_name
                        + " Network Id :" + network_id
                        + " IP address :" + ip_address
                        + " Subnet Id :" + subnet_id);

        Port port = os.networking().port().create(Builders.port()
                .name(port_name)
                .networkId(network_id)
                .fixedIp(ip_address, subnet_id)
                .build());
        /*TODO - Check the return status of the port*/

        List<? extends Port> ports = os.networking().port().list();
        for (Object p: ports) {
            logger.debug("Ports :"+ p);
        }

        return;
    }

    public static void delete_port(OSClientV2 os,
                                   String port_id)
    {
        logger.info("Delete port :" + port_id + " on " + os.getEndpoint());
        ActionResponse response = os.networking().port().delete(port_id);

        if(!response.isSuccess())
        {
            logger.error("Port deletion failed: Port Id :" + port_id
                    + " on " + os.getEndpoint() + " Error cause :" + response.getFault());
        }

        List<? extends Port> ports = os.networking().port().list();
        for (Object p: ports) {
            logger.debug("Ports :"+ p);
        }
        return;
    }

}
