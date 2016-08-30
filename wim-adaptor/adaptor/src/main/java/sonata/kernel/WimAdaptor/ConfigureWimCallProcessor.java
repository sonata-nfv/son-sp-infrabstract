package sonata.kernel.WimAdaptor;

import java.util.Observable;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import sonata.kernel.WimAdaptor.commons.DeployServiceResponse;
import sonata.kernel.WimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.WimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.WimAdaptor.wrapper.WrapperBay;

public class ConfigureWimCallProcessor extends AbstractCallProcessor {

  private static final org.slf4j.Logger Logger =
      LoggerFactory.getLogger(ConfigureWimCallProcessor.class);

  public ConfigureWimCallProcessor(ServicePlatformMessage message, String sid, WimAdaptorMux mux) {
    super(message, sid, mux);
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    // No update async update mechanism for this call
  }

  @Override
  public boolean process(ServicePlatformMessage message) {

    DeployServiceResponse response = null;
    boolean out;
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    SimpleModule module = new SimpleModule();
    module.addDeserializer(sonata.kernel.WimAdaptor.commons.vnfd.Unit.class,
        new UnitDeserializer());
    mapper.registerModule(module);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.setSerializationInclusion(Include.NON_NULL);
    try {
      response = mapper.readValue(message.getBody(), DeployServiceResponse.class);
      Logger.info("payload parsed");
    } catch (Exception e) {
      Logger.error("Error deploying the system: " + e.getMessage(), e);
      this.sendToMux(new ServicePlatformMessage(
          "{\"request_status\":\"fail\",\"message\":\"Deployment Error\"}", "application/json",
          message.getReplyTo(), message.getSid(), null));
      out = false;
    }

    String instanceId = response.getNsr().getId();
    String vimId = response.getVimUuid();

    WimWrapper wim = (WimWrapper) WrapperBay.getInstance().getWimRecord(vimId).getWimWrapper();
    wim.addObserver(this);

    ServicePlatformMessage responseMessage = null;
    if (wim.configureNetwork(instanceId)) {
      response.setVimUuid(null);
      String body;
      try {
        body = mapper.writeValueAsString(response);
        responseMessage = new ServicePlatformMessage(body, "application/x-yaml",
            this.getMessage().getReplyTo(), this.getSid(), null);
        this.sendToMux(responseMessage);
      } catch (JsonProcessingException e) {
        Logger.error("Unable to serialize YAML response", e);
        sendResponse("{\"request_status\":\"ERROR\",\"module\":\"WimAdaptor\",\"message\":\""
            + e.getMessage() + "\"}");
      }

    } else {
      sendResponse(
          "{\"status\":\"ERROR\",\"module\":\"WimAdaptor\",\"message\":\"Unable to configure WAN\"}");
    }

    return true;
  }

  private void sendResponse(String message) {
    ServicePlatformMessage spMessage = new ServicePlatformMessage(message, "application/json",
        this.getMessage().getTopic(), this.getMessage().getSid(), null);
    this.sendToMux(spMessage);
  }
}
