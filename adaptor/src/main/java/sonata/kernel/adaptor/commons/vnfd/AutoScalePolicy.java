package sonata.kernel.adaptor.commons.vnfd;

import java.util.ArrayList;

public class AutoScalePolicy {

  private ArrayList<Criterion> critaria;
  private String action;

  public ArrayList<Criterion> getCritaria() {
    return critaria;
  }

  public String getAction() {
    return action;
  }

}
