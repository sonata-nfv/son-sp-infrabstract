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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.slf4j.LoggerFactory;

import sonata.kernel.VimAdaptor.commons.FunctionDeployPayload;
import sonata.kernel.VimAdaptor.commons.FunctionDeployResponse;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.VimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.VimAdaptor.wrapper.ComputeWrapper;
import sonata.kernel.VimAdaptor.wrapper.WrapperBay;
import sonata.kernel.VimAdaptor.wrapper.WrapperStatusUpdate;

import java.util.Observable;

public class DeployFunctionCallProcessor extends AbstractCallProcessor {

  private FunctionDeployPayload data;
  private static final org.slf4j.Logger Logger =
      LoggerFactory.getLogger(DeployFunctionCallProcessor.class);

  /**
   * Basic constructor for the call processor.
   * 
   * @param message
   * @param sid
   * @param mux
   */
  public DeployFunctionCallProcessor(ServicePlatformMessage message, String sid, AdaptorMux mux) {
    super(message, sid, mux);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  @Override
  public void update(Observable o, Object arg) {
    WrapperStatusUpdate update = (WrapperStatusUpdate) arg;
    if (update.getSid().equals(this.getSid())) {
      Logger.info("Received an update from the wrapper...");
      if (update.getStatus().equals("SUCCESS")) {
        Logger.info("Deploy " + this.getSid() + " succeed");

        // Sending the response to the FLM
        Logger.info("Sending partial response to FLM...");
        ServicePlatformMessage response = new ServicePlatformMessage(update.getBody(),
            "application/x-yaml", this.getMessage().getReplyTo(), this.getSid(), null);
        this.sendToMux(response);
      } else if (update.getStatus().equals("ERROR")) {
        Logger.warn("Deploy " + this.getSid() + " error");
        Logger.warn("Pushing back error...");
        ServicePlatformMessage response = new ServicePlatformMessage(
            "{\"request_status\":\"fail\",\"message\":\"" + update.getBody() + "\"}",
            "application/x-yaml", this.getMessage().getReplyTo(), this.getSid(), null);
        this.sendToMux(response);
      } else {
        Logger.info("Deploy " + this.getSid() + " - " + update.getStatus());
        Logger.info("Message " + update.getBody());
      }

      // TODO handle other update from the compute wrapper;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.VimAdaptor.AbstractCallProcessor#process(sonata.kernel.VimAdaptor.messaging.
   * ServicePlatformMessage)
   */
  @Override
  public boolean process(ServicePlatformMessage message) {
    boolean out = true;
    Logger.info("Deploy function call received by call processor.");
    // parse the payload to get Wrapper UUID and NSD/VNFD from the request body
    Logger.info("Parsing payload...");
    data = null;
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Unit.class, new UnitDeserializer());
    mapper.registerModule(module);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    try {
      data = mapper.readValue(message.getBody(), FunctionDeployPayload.class);
      Logger.info("payload parsed");
      ComputeWrapper wr = WrapperBay.getInstance().getComputeWrapper(data.getVimUuid());
      Logger.info("Wrapper retrieved");

      if (wr == null) {
        Logger.warn("Error retrieving the wrapper");

        this.sendToMux(new ServicePlatformMessage(
            "{\"request_status\":\"fail\",\"message\":\"VIM not found\"}", "application/json",
            message.getReplyTo(), message.getSid(), null));
        out = false;
      } else {
        // use wrapper interface to send the NSD/VNFD, along with meta-data
        // to the wrapper, triggering the service instantiation.
        Logger.info("Calling wrapper: " + wr);
        wr.addObserver(this);
        wr.deployFunction(data, this.getSid());
      }
    } catch (Exception e) {
      Logger.error("Error deploying the system: " + e.getMessage(), e);
      this.sendToMux(new ServicePlatformMessage(
          "{\"request_status\":\"fail\",\"message\":\"Deployment Error\"}", "application/json",
          message.getReplyTo(), message.getSid(), null));
      out = false;
    }
    return out;
  }

}
