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

import java.io.IOException;
import java.util.Observable;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import sonata.kernel.adaptor.commons.DeployServiceData;
import sonata.kernel.adaptor.commons.serviceDescriptor.ServiceDescriptor;
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
    DeployServiceData data = new DeployServiceData();
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    try {
      data = mapper.readValue(message.getBody(), DeployServiceData.class);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    // TODO use wrapper interface to send the NSD/VNFD, along with meta-data
    // to the wrapper, triggering the service instantiation.
    try {
      wr.deployService(data, this);
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
