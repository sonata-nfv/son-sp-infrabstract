package sonata.kernel.VimAdaptor.wrapper.mock;

import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.heat.StackComposition;
import sonata.kernel.VimAdaptor.wrapper.NetworkingWrapper;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;

/**
 * Created by nle5220 on 17.10.2016.
 */
public class NetworkMockWrapper extends NetworkingWrapper {
  private WrapperConfiguration config;

  public NetworkMockWrapper(WrapperConfiguration config) {
    this.config = config;
  }

  @Override
  public void configureNetworking(DeployServiceData data, StackComposition composition)
      throws Exception {
    return;
  }
}
