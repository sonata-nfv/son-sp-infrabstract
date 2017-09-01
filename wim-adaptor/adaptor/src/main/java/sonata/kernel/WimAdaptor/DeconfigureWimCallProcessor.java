package sonata.kernel.WimAdaptor;

import java.util.ArrayList;
import java.util.Observable;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import sonata.kernel.WimAdaptor.commons.ConfigureWanPayload;
import sonata.kernel.WimAdaptor.commons.DeconfigureWanPayload;
import sonata.kernel.WimAdaptor.commons.SonataManifestMapper;
import sonata.kernel.WimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.WimAdaptor.wrapper.WimWrapper;
import sonata.kernel.WimAdaptor.wrapper.WrapperBay;
import sonata.kernel.WimAdaptor.wrapper.WrapperRecord;

public class DeconfigureWimCallProcessor extends AbstractCallProcessor {

  private static final org.slf4j.Logger Logger =
      LoggerFactory.getLogger(DeconfigureWimCallProcessor.class);

  public DeconfigureWimCallProcessor(ServicePlatformMessage message, String sid,
      WimAdaptorMux mux) {
    super(message, sid, mux);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean process(ServicePlatformMessage message) {
    DeconfigureWanPayload request = null;
    boolean out = true;
    ObjectMapper mapper = SonataManifestMapper.getSonataMapper();
    try {
      request = mapper.readValue(message.getBody(), DeconfigureWanPayload.class);
      Logger.info("payload parsed");
    } catch (Exception e) {
      Logger.error("Error parsing the wan configure payload: " + e.getMessage(), e);
      this.sendToMux(new ServicePlatformMessage(
          "{\"request_status\":\"fail\",\"message\":\"Payload parse error\"}", "application/json",
          message.getReplyTo(), message.getSid(), null));
      out = false;
      return out;
    }
    Logger.debug("Received request: ");
    Logger.debug(message.getBody());
    String instanceId = request.getServiceInstanceId();
    
    ArrayList<String> wimList = WrapperBay.getInstance().getWimList();
    
    for(String wimUuid: wimList){
      WimWrapper wim = (WimWrapper) WrapperBay.getInstance().getWimRecordFromWimUuid(wimUuid).getWimWrapper();
      wim.removeNetConfiguration(instanceId);
    }
    
    return false;
  }

}
