/**
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the SONATA-NFV, UCL, NOKIA, NCSR Demokritos nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the SONATA project, funded by the European
 * Commission under Grant number 671517 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the SONATA partner consortium
 * (www.sonata-nfv.eu).
 *
 * @author Dario Valocchi (Ph.D.), UCL
 * 
 */

package sonata.kernel.VimAdaptor.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.slf4j.LoggerFactory;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.DeployServiceResponse;
import sonata.kernel.VimAdaptor.commons.ServiceRecord;
import sonata.kernel.VimAdaptor.commons.Status;
import sonata.kernel.VimAdaptor.commons.VduRecord;
import sonata.kernel.VimAdaptor.commons.VnfRecord;
import sonata.kernel.VimAdaptor.commons.vnfd.VirtualDeploymentUnit;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;


public class MockWrapper extends ComputeWrapper implements Runnable {

  /*
   * Utility fields to implement the mock response creation. A real wrapper should instantiate a
   * suitable object with these fields, able to handle the API call asynchronously, generate a
   * response and update the observer
   */
  private DeployServiceData data;
  private String sid;

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(MockWrapper.class);
  private static final long THREAD_SLEEP = 1000;

  public MockWrapper(WrapperConfiguration config) {
    super();
  }

  @Override
  public String toString() {
    return "MockWrapper";
  }

  @Override
  public boolean deployService(DeployServiceData data, String callSid) {
    this.data = data;
    this.sid = callSid;
    // This is a mock compute wrapper.

    /*
     * Just use the SD to forge the response message for the SLM with a success. In general Wrappers
     * would need a complex set of actions to deploy the service, so this function should just check
     * if the request is acceptable, and if so start a new thread to deal with the perform the
     * needed actions.
     */
    Thread thread = new Thread(this);
    thread.start();
    return true;
  }

  @Override
  public void run() {
    Logger.info("Deploying Service...");
    try {
      Thread.sleep(THREAD_SLEEP);
    } catch (InterruptedException e) {
      Logger.error(e.getMessage(), e);
    }
    Logger.info("Service DEPLOYED. Creating response");
    DeployServiceResponse response = new DeployServiceResponse();
    response.setRequestStatus("DEPLOYED");;
    ServiceRecord sr = new ServiceRecord();
    sr.setStatus(Status.normal_operation);
    sr.setId(data.getNsd().getInstanceUuid());
    sr.setDescriptorReference(data.getNsd().getUuid());
    for (VnfDescriptor vnf : data.getVnfdList()) {
      VnfRecord vnfr = new VnfRecord();
      vnfr.setDescriptorVersion("vnfr-schema-01");
      vnfr.setStatus(Status.normal_operation);
      vnfr.setDescriptorReference(vnf.getUuid());
      // vnfr.setDescriptorReferenceName(vnf.getName());
      // vnfr.setDescriptorReferenceVendor(vnf.getVendor());
      // vnfr.setDescriptorReferenceVersion(vnf.getVersion());

      vnfr.setId(vnf.getInstanceUuid());
      for (VirtualDeploymentUnit vdu : vnf.getVirtualDeploymentUnits()) {
        VduRecord vdur = new VduRecord();
        vdur.setId(vdu.getId());
        vdur.setNumberOfInstances(1);
        vdur.setVduReference(vnf.getName() + ":" + vdu.getId());
        vdur.setVmImage(vdu.getVmImage());
        vnfr.addVdu(vdur);
      }
      response.addVnfRecord(vnfr);
    }
    response.setNsr(sr);

    Logger.info("Response created. Serializing...");

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.setSerializationInclusion(Include.NON_NULL);
    String body;
    try {
      body = mapper.writeValueAsString(response);
      this.setChanged();
      Logger.info("Serialized. notifying call processor");
      WrapperStatusUpdate update = new WrapperStatusUpdate(this.sid, "SUCCESS", body);
      this.notifyObservers(update);
    } catch (JsonProcessingException e) {
      Logger.error(e.getMessage(), e);
    }
  }

  @Override
  public boolean removeService(String instanceUuid, String callSid) {
    boolean out = true;

    this.setChanged();
    String body = "{\"status\":\"SUCCESS\"}";
    WrapperStatusUpdate update = new WrapperStatusUpdate(this.sid, "SUCCESS", body);
    this.notifyObservers(update);

    return out;
  }

  @Override
  public ResourceUtilisation getResourceUtilisation() {

    ResourceUtilisation resources = new ResourceUtilisation();
    resources.setTotCores(10);
    resources.setUsedCores(0);
    resources.setTotMemory(10000);
    resources.setUsedMemory(0);

    return resources;
  }

}
