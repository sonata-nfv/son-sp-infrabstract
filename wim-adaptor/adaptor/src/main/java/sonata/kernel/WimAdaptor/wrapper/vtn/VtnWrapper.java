package sonata.kernel.WimAdaptor.wrapper.vtn;

import sonata.kernel.WimAdaptor.wrapper.Wrapper;
import sonata.kernel.WimAdaptor.wrapper.WrapperConfiguration;

public class VtnWrapper implements Wrapper {

  private WrapperConfiguration config;

  public VtnWrapper(WrapperConfiguration config) {
    super();
    this.config = config;
  }

  @Override
  public String getType() {
    return null;
  }


  public boolean configureNetwork() {
    boolean out = true;
    
    
    return out;
  }


}
