package sonata.kernel.WimAdaptor.commons;

public class ComparableUuid implements Comparable<ComparableUuid> {

  private int order;
  private String uuid;
  public int getOrder() {
    return order;
  }
  public String getUuid() {
    return uuid;
  }
  public void setOrder(int order) {
    this.order = order;
  }
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
  @Override
  public int compareTo(ComparableUuid other) {
    return this.order-other.order;
  }
  
  @Override
  public boolean equals(Object obj){
    if (!(obj instanceof ComparableUuid))
      return false;
    else{
      ComparableUuid other = (ComparableUuid) obj;
      return this.uuid.equals(other.getUuid());
    }
          
  }
  
  
}
