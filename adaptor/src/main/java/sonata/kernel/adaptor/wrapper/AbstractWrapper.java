package sonata.kernel.adaptor.wrapper;

import java.util.Observable;

public abstract class AbstractWrapper extends Observable {

  private String type;

  protected void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
