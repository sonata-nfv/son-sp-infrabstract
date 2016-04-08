package sonata.kernel.adaptor.commons;

import java.util.ArrayList;

public class LifeCycleEvent {

  private ArrayList<Event> start;
  private ArrayList<Event> stop;
  private ArrayList<Event> scale_out;

  public ArrayList<Event> getStart() {
    return start;
  }

  public ArrayList<Event> getStop() {
    return stop;
  }

  public ArrayList<Event> getScale_out() {
    return scale_out;
  }

}
