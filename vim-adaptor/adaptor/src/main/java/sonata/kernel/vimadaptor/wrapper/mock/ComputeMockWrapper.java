/*
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

package sonata.kernel.vimadaptor.wrapper.mock;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.slf4j.LoggerFactory;

import sonata.kernel.vimadaptor.commons.FunctionDeployPayload;
import sonata.kernel.vimadaptor.commons.ServiceDeployPayload;
import sonata.kernel.vimadaptor.commons.ServiceDeployResponse;
import sonata.kernel.vimadaptor.commons.ServiceRecord;
import sonata.kernel.vimadaptor.commons.Status;
import sonata.kernel.vimadaptor.commons.VduRecord;
import sonata.kernel.vimadaptor.commons.VnfImage;
import sonata.kernel.vimadaptor.commons.VnfRecord;
import sonata.kernel.vimadaptor.commons.vnfd.VirtualDeploymentUnit;
import sonata.kernel.vimadaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.vimadaptor.wrapper.ComputeWrapper;
import sonata.kernel.vimadaptor.wrapper.ResourceUtilisation;
import sonata.kernel.vimadaptor.wrapper.WrapperConfiguration;
import sonata.kernel.vimadaptor.wrapper.WrapperStatusUpdate;

import java.io.IOException;


public class ComputeMockWrapper extends ComputeWrapper implements Runnable {

  /*
   * Utility fields to implement the mock response creation. A real wrapper should instantiate a
   * suitable object with these fields, able to handle the API call asynchronously, generate a
   * response and update the observer
   */
  private ServiceDeployPayload data;
  private String sid;

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ComputeMockWrapper.class);
  private static final long THREAD_SLEEP = 1000;

  public ComputeMockWrapper(WrapperConfiguration config) {
    super(config);
  }

  @Override
  public String toString() {
    return "MockWrapper";
  }

  @Override
  public boolean deployService(ServiceDeployPayload data, String callSid) {
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
    ServiceDeployResponse response = new ServiceDeployResponse();
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

    try {
      Thread.sleep(1900);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }   
    
    ResourceUtilisation resources = new ResourceUtilisation();
    resources.setTotCores(10);
    resources.setUsedCores(0);
    resources.setTotMemory(10000);
    resources.setUsedMemory(0);

    return resources;
  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#prepareService(java.lang.String)
   */
  @Override
  public boolean prepareService(String instanceId) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * sonata.kernel.vimadaptor.wrapper.ComputeWrapper#deployFunction(sonata.kernel.vimadaptor.commons
   * .FunctionDeployPayload, java.lang.String)
   */
  @Override
  public void deployFunction(FunctionDeployPayload data, String sid) {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#isImageStored(java.lang.String)
   */
  @Override
  public boolean isImageStored(VnfImage image, String callSid) {
    boolean out = true;
    return out;
  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#removeImage(java.lang.String)
   */
  @Override
  public void removeImage(VnfImage image) {
    this.setChanged();
    String body = "{\"status\":\"SUCCESS\"}";
    WrapperStatusUpdate update = new WrapperStatusUpdate(this.sid, "SUCCESS", body);
    this.notifyObservers(update);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * sonata.kernel.vimadaptor.wrapper.ComputeWrapper#uploadImage(sonata.kernel.vimadaptor.commons.
   * VnfImage)
   */
  @Override
  public void uploadImage(VnfImage image) throws IOException {
    this.setChanged();
    String body = "{\"status\":\"SUCCESS\"}";
    WrapperStatusUpdate update = new WrapperStatusUpdate(this.sid, "SUCCESS", body);
    this.notifyObservers(update);

    return;
  }

}
