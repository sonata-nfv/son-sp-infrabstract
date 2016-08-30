package sonata.kernel.placement.config;

import java.util.ArrayList;

public class PopResource {

    String popName;
    ArrayList<NodeResource> nodes;

    public String getPopName() {
        return popName;
    }

    public void setPopName(String popName) {
        this.popName = popName;
    }

    public ArrayList<NodeResource> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<NodeResource> nodes) {
        this.nodes = nodes;
    }
}
