package sonata.kernel.WimAdaptor;

import java.util.Observable;

import sonata.kernel.WimAdaptor.messaging.ServicePlatformMessage;

public class AttachVimCallProcessor extends AbstractCallProcessor {

  public AttachVimCallProcessor(ServicePlatformMessage message, String sid, WimAdaptorMux mux) {
    super(message, sid, mux);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean process(ServicePlatformMessage message) {
    // TODO Auto-generated method stub
    return false;
  }

}
