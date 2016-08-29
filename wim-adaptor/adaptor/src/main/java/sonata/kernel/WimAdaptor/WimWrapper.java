package sonata.kernel.WimAdaptor;

import sonata.kernel.WimAdaptor.wrapper.AbstractWrapper;
import sonata.kernel.WimAdaptor.wrapper.Wrapper;

public abstract class WimWrapper extends AbstractWrapper implements Wrapper {

  /**
   * general constructor for wrappers of type compute.
   */
  public WimWrapper() {

    this.setType("wim");

  }

  /**
   * Configure the WAN for a service instance.
   * 
   * @param instanceId the ID of the service instance
   * 
   * @return true if the WAN has been configured correctly.
   */
  public abstract boolean configureNetwork(String instanceId);

  /**
   * Remove the WAN configuration for a given service instance.
   * 
   * @param instanceId the ID of the service instance to de-configure
   * @return true if the WAN has been de-configured correctly.
   */
  public abstract boolean removeNetConfiguration(String instanceId);

}
