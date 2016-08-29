package sonata.kernel.placement.net;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV2;
import org.openstack4j.model.heat.Stack;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.OSFactory;

import java.util.List;
//import sun.plugin2.os.windows.OSVERSIONINFOA;

/**
 * Created by vrv on 8/22/2016.
 */
public class TranslatorNet {

    public static OSClientV2 authenticate_instance(String uri)
    {
        OSClientV2 os = OSFactory.builderV2()
                .endpoint(uri)
                .credentials("admin","sample")
                .tenantName("admin")
                .authenticate();
        return os;
    }

    public static void create_subnet( OSClientV2 os,
                                        String name,
                                        String network_id,
                                        String tenant_id,
                                        String start_ip,
                                        String end_ip)
    {
        List<? extends Network> networks = os.networking().network().list();
        for (Object o: networks
             ) {
            System.out.println(o);
        }
        /*
        Subnet subnet = os.networking().subnet().create(Builders.subnet()
                .name(name)
                .networkId(network_id)
                .tenantId(tenant_id)
                .addPool(start_ip, end_ip)
                .ipVersion(IPVersionType.V4)
                .cidr("192.168.0.0/24")
                .build());

        Stack stack = os.heat().stacks().create(Builders.stack()
                .name("Stack Name")
                .template("template")
                .timeoutMins(5L).build());
        return subnet;*/
    }

    public static boolean delete_subnet(OSClientV2 os,
                                        Subnet net)
    {
        os.networking().subnet().delete(net.getId());
        return true;
    }
}
