package sonata.kernel.WimAdaptor.wrapper;

public abstract class WimWrapper extends AbstractWrapper implements Wrapper {

  protected WrapperConfiguration config;

  
  
  /**
   * general constructor for wrappers of type compute.
   */
  public WimWrapper(WrapperConfiguration config) {
    this.config=config;
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

  public WrapperConfiguration getConfig() {
    return config;
  }

}
