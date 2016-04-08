package sonata.kernel.adaptor;

import java.util.Observable;

import sonata.kernel.adaptor.commons.ServiceDescriptor;
import sonata.kernel.adaptor.messaging.ServicePlatformMessage;
import sonata.kernel.adaptor.wrapper.ComputeWrapper;
import sonata.kernel.adaptor.wrapper.WrapperBay;
import sonata.kernel.adaptor.wrapper.WrapperStatusUpdate;

public class StartServiceCallProcessor extends AbstractCallProcessor {

  public StartServiceCallProcessor(ServicePlatformMessage message, String SID, AdaptorMux mux) {
    super(message, SID, mux);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean process(ServicePlatformMessage message) {

    // TODO implement wrapper selection based on request body
    ComputeWrapper wr = WrapperBay.getInstance().getBestComputeWrapper();
    if (wr == null) {
      this.getMux()
          .enqueue(new ServicePlatformMessage("{\"status\":\"ERROR\",\"message\":\"no_wrapper\"}",
              message.getTopic(), message.getSID()));
      return false;
    }
    // TODO parse the NSD/VNFD from the request body
    ServiceDescriptor sd = new ServiceDescriptor(message.getBody());
    // TODO use wrapper interface to send the NSD/VNFD, along with meta-data
    // to the wrapper, triggering the service instantiation.
    try {
      wr.deployService(sd, this);
    } catch (Exception e) {
      ; // TODO handle possible exception from the wrapper and send report to the SLM;
    } 
    return true;
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    WrapperStatusUpdate update = (WrapperStatusUpdate) arg1;

    if (update.getSID().equals(this.getSID())) {
      if (update.getStatus().equals("SUCCESS")) {
        ServicePlatformMessage response = new ServicePlatformMessage(update.getBody(),
            "infrastructure.service.deploy", this.getSID());
        this.getMux().enqueue(response);
      }
      // TODO handle other update from the compute wrapper;
    }
  }

}
