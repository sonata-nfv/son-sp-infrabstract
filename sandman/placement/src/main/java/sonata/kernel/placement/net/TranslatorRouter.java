package sonata.kernel.placement.net;

import org.apache.log4j.Logger;
import org.openstack4j.api.OSClient;
import org.openstack4j.openstack.OSFactory;
import sonata.kernel.placement.config.PopResource;
import org.openstack4j.api.OSClient.OSClientV2;

/**
 * Created by vrv on 9/17/2016.
 */
public class TranslatorRouter {
    final static Logger logger = Logger.getLogger(TranslatorNetwork.class);
    public static OSClient.OSClientV2 authenticate_instance(PopResource popResource)
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

    public static void create_router(OSClientV2 os,
                                     String router_name,
                                     boolean state,
                                     String network_id
                                     )
    {

    }

    public static void delete_router(){}
}
