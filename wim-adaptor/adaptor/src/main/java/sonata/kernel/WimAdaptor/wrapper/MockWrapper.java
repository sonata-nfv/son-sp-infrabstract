package sonata.kernel.WimAdaptor.wrapper;

public class MockWrapper extends WimWrapper {

  public MockWrapper(WrapperConfiguration config) {
    super(config);
  }

  @Override
  public boolean configureNetwork(String instanceId) {

    return true;
  }

  @Override
  public boolean removeNetConfiguration(String instanceId) {
    return true;
  }

}
