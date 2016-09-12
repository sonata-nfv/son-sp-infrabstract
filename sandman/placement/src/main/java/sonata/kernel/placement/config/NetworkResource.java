package sonata.kernel.placement.config;

import java.util.ArrayList;

public class NetworkResource {

    String name;
    String subnet;
    String prefer;
    ArrayList<String> available;

    public String getPrefer() {
        return prefer;
    }

    public void setPrefer(String prefer) {
        this.prefer = prefer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubnet() {
        return subnet;
    }

    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }

    public ArrayList<String> getAvailable() {
        return available;
    }

    public void setAvailable(ArrayList<String> available) {
        this.available = available;
    }
}
