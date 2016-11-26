package sonata.kernel.placement.config;

import java.util.ArrayList;

public class PopResource {

    String popName;
    String endpoint;
    String chainingEndpoint;
    String tenantName;
    String userName;
    String password;

    ArrayList<NodeResource> nodes;
    ArrayList<NetworkResource> networks;

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

    public ArrayList<NetworkResource> getNetworks() {
        return networks;
    }

    public void setNetworks(ArrayList<NetworkResource> networks) {
        this.networks = networks;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChainingEndpoint() {
        return chainingEndpoint;
    }

    public void setChainingEndpoint(String chainingEndpoint) {
        this.chainingEndpoint = chainingEndpoint;
    }
}
