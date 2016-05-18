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

package sonata.kernel.adaptor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import sonata.kernel.adaptor.commons.ResourceAvailabilityData;
import sonata.kernel.adaptor.commons.vnfd.Unit;
import sonata.kernel.adaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.adaptor.messaging.ServicePlatformMessage;

import java.io.IOException;
import java.util.Observable;


public class ResourceAvailabilityCallProcessor extends AbstractCallProcessor {

  /**
   * Generate a CallProcessor to process an API call to create a new VIM wrapper
   * 
   * @param message the API call message
   * @param sid the session ID of thi API call
   * @param mux the Adaptor Mux to which send responses.
   */
  public ResourceAvailabilityCallProcessor(ServicePlatformMessage message, String sid,
      AdaptorMux mux) {
    super(message, sid, mux);

  }

  @Override
  public boolean process(ServicePlatformMessage message) {
    boolean out = true;
    System.out.println("[ResourceAvailabilityCallProcessor] - Call received...");


    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Unit.class, new UnitDeserializer());
    mapper.registerModule(module);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    try {
      ResourceAvailabilityData data = null;
      data = mapper.readValue(message.getBody(), ResourceAvailabilityData.class);
      
      System.out.println("[ResourceAvailabilityCallProcessor] - Checking availability of resource. Minimum:");
      System.out.println(mapper.writeValueAsString(data));
      // TODO get resource availability

      // By now we just answer OK, for resource available.
      String responseMessage = "status: \"OK\"";
      ServicePlatformMessage response =
          new ServicePlatformMessage(responseMessage,"application/x-yaml", message.getTopic(), message.getSid(),null);

      this.sendToMux(response);
    } catch (IOException e1) {
      e1.printStackTrace();
      // TODO report deserialization error to the SLM (malformed requests)
    }

    return out;
  }


  @Override
  public void update(Observable observable, Object arg) {
    // This call does not need to be updated by any observable (wrapper).
  }
}
