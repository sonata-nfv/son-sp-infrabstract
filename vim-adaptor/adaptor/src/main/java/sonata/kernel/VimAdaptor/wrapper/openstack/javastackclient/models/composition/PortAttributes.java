package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.composition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortAttributes {

    private String name;
    private ArrayList<HashMap<String, String>> fixed_ips;
    private String mac_address;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public ArrayList<HashMap<String, String>> getFixed_ips() {
        return fixed_ips;
    }

    public void setFixed_ips(ArrayList<HashMap<String, String>> fixed_ips) {
        this.fixed_ips = fixed_ips;
    }
}
