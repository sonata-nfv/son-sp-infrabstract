package sonata.kernel.adaptor.wrapper;

import sonata.kernel.adaptor.StartServiceCallProcessor;
import sonata.kernel.adaptor.commons.ServiceDescriptor;

public class MockWrapper extends ComputeWrapper {

  public MockWrapper(WrapperConfiguration config) {
    super();
  }

  @Override
  public boolean deployService(ServiceDescriptor sd, StartServiceCallProcessor callProcessor) {
    this.addObserver(callProcessor);

    // TODO This is a mock compute wrapper.

    /*
     * Just use the SD to forge the response message for the SLM with a success. In general Wrappers
     * would need a complex set of actions to deploy the service, so this function should just check
     * if the request is acceptable, and if so start a new thread to deal with the perform the
     * needed actions.
     */

    String body = "";
    WrapperStatusUpdate update = new WrapperStatusUpdate(callProcessor.getSID(), "SUCCESS", body);
    this.notifyObservers(update);

    return true;
  }

}
