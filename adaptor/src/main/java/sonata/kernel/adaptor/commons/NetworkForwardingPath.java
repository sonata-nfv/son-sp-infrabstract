package sonata.kernel.adaptor.commons;

import java.util.ArrayList;

public class NetworkForwardingPath {

  private String fp_id;
  private String policy;
  private ArrayList<ConnectionPointReference> connection_points;

  public String getFp_id() {
    return fp_id;
  }

  public String getPolicy() {
    return policy;
  }

  public ArrayList<ConnectionPointReference> getConnection_points() {
    return connection_points;
  }

}
