package sonata.kernel.adaptor.commons;

import java.util.ArrayList;


public class VirtualLink {


  public enum ConnectivityType {
    E_LINE("E-Line"), E_TREE("E-Tree"), E_LAN("E-LAN");

    private final String name;

    ConnectivityType(String name) {
      this.name = name;
    }
    public String toString() {
      return this.name;
    }
  }


  // Virtual Link reference case
  private String vl_group;
  private String vl_name;
  private String vl_version;
  private String vl_description;

  // Virtual Link description case;

  private String id;
  private ConnectivityType connectivity_type;
  private ArrayList<String> connection_points_reference;
  private boolean access;
  private boolean external_access;
  private String root_requirement;
  private String leaf_requirement;
  private boolean dhcp;
  private String qos;

  public String getVl_group() {
    return vl_group;
  }

  public String getVl_name() {
    return vl_name;
  }

  public String getVl_version() {
    return vl_version;
  }

  public String getVl_description() {
    return vl_description;
  }

  public String getId() {
    return id;
  }

  public ConnectivityType getConnectivity_type() {
    return connectivity_type;
  }

  public ArrayList<String> getConnection_points_reference() {
    return connection_points_reference;
  }

  public boolean isAccess() {
    return access;
  }

  public boolean isExternal_access() {
    return external_access;
  }

  public String getRoot_requirement() {
    return root_requirement;
  }

  public String getLeaf_requirement() {
    return leaf_requirement;
  }

  public boolean isDhcp() {
    return dhcp;
  }

  public String getQos() {
    return qos;
  }


}
