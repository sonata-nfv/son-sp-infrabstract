package sonata.kernel.WimAdaptor;

import java.util.Observable;

import org.json.JSONObject;
import org.json.JSONTokener;

import sonata.kernel.WimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.WimAdaptor.wrapper.WrapperBay;

public class AttachVimCallProcessor extends AbstractCallProcessor {

  public AttachVimCallProcessor(ServicePlatformMessage message, String sid, WimAdaptorMux mux) {
    super(message, sid, mux);
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    //Nothing to do here
  }

  @Override
  public boolean process(ServicePlatformMessage message) {
    JSONTokener tokener = new JSONTokener(message.getBody());
    
    JSONObject jsonObject = (JSONObject) tokener.nextValue();
    //String wrapperType = jsonObject.getString("WIM");
    String wimUuid = jsonObject.getString("wim_uuid");
    String vimUuid = jsonObject.getString("vim_uuid");
    
    String result = WrapperBay.getInstance().attachVim(wimUuid, vimUuid);
    this.sendResponse(result);
    return true;
  }
  
  private void sendResponse(String message) {
    ServicePlatformMessage spMessage = new ServicePlatformMessage(message, "application/json",
        this.getMessage().getTopic(), this.getMessage().getSid(), null);
    this.sendToMux(spMessage);
  }

}
