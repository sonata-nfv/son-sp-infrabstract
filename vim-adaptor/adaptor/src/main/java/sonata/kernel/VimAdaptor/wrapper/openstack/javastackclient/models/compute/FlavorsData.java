package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.compute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlavorsData {

    private ArrayList<FlavorProperties> flavors;

    public ArrayList<FlavorProperties> getFlavors() {
        return flavors;
    }

    public void setFlavors(ArrayList<FlavorProperties> flavors) {
        this.flavors = flavors;
    }
}
