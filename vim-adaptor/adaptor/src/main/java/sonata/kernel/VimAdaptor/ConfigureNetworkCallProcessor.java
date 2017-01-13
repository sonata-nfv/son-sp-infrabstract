/**
 * @author Dario Valocchi (Ph.D.)
 * @mail d.valocchi@ucl.ac.uk
 * 
 *       Copyright 2016 [Dario Valocchi]
 * 
 *       Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *       except in compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *       Unless required by applicable law or agreed to in writing, software distributed under the
 *       License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *       either express or implied. See the License for the specific language governing permissions
 *       and limitations under the License.
 * 
 */
package sonata.kernel.VimAdaptor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.slf4j.LoggerFactory;

import sonata.kernel.VimAdaptor.commons.FunctionDeployPayload;
import sonata.kernel.VimAdaptor.commons.NetworkConfigurePayload;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.VimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.VimAdaptor.wrapper.ComputeWrapper;
import sonata.kernel.VimAdaptor.wrapper.WrapperBay;

import java.io.IOException;
import java.util.Observable;

public class ConfigureNetworkCallProcessor extends AbstractCallProcessor {

  NetworkConfigurePayload data = null;

  private static final org.slf4j.Logger Logger =
      LoggerFactory.getLogger(DeployFunctionCallProcessor.class);

  /**
   * @param message
   * @param sid
   * @param mux
   */
  public ConfigureNetworkCallProcessor(ServicePlatformMessage message, String sid,
      AdaptorMux mux) {
    super(message, sid, mux);
  }

  @Override
  public void run() {

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  @Override
  public void update(Observable arg0, Object arg1) {

  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.VimAdaptor.AbstractCallProcessor#process(sonata.kernel.VimAdaptor.messaging.
   * ServicePlatformMessage)
   */
  @Override
  public boolean process(ServicePlatformMessage message) {

    data = null;
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Unit.class, new UnitDeserializer());
    mapper.registerModule(module);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    try {
      data = mapper.readValue(message.getBody(), NetworkConfigurePayload.class);
      Logger.info("payload parsed");
    } catch (IOException e) {
      Logger.error("Unable to parse the payload received");
    }
    String responseJson = "{\"status\":\"COMPLETED\",\"message\":\"\"}";
    Logger.info("Received networking.configure call for service instance " + data.getServiceInstanceId());
    this.sendToMux(new ServicePlatformMessage(responseJson, "application/json",
        message.getReplyTo(), message.getSid(), null));
    return false;
  }

}
