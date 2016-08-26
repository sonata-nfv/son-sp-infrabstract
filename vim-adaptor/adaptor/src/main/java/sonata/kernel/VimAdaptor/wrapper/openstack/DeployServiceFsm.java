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

package sonata.kernel.VimAdaptor.wrapper.openstack;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
import sonata.kernel.VimAdaptor.commons.VnfcInstance;
import sonata.kernel.VimAdaptor.commons.heat.HeatPort;
import sonata.kernel.VimAdaptor.commons.heat.HeatServer;
import sonata.kernel.VimAdaptor.commons.heat.HeatTemplate;
import sonata.kernel.VimAdaptor.commons.heat.StackComposition;
import sonata.kernel.VimAdaptor.commons.nsd.ConnectionPoint;
import sonata.kernel.VimAdaptor.commons.nsd.ConnectionPointRecord;
import sonata.kernel.VimAdaptor.commons.nsd.InterfaceRecord;
import sonata.kernel.VimAdaptor.commons.vnfd.VirtualDeploymentUnit;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.wrapper.NetworkingWrapper;
import sonata.kernel.VimAdaptor.wrapper.WrapperBay;
import sonata.kernel.VimAdaptor.wrapper.WrapperStatusUpdate;

import java.util.ArrayList;
import java.util.Hashtable;

public class DeployServiceFsm implements Runnable {

  private String sid;
  private DeployServiceData data;
  private OpenStackHeatWrapper wrapper;
  private OpenStackHeatClient client;
  private HeatTemplate stack;
  private static final int maxCounter = 10;
  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DeployServiceFsm.class);


  /**
   * Return an object that handles the FSM needed to deploy a service in OpenStackHeat.
   * 
   * @param wrapper the Compute wrapper issuing this FSM
   * @param client the OpenStack client to use for the deployment
   * @param sid the session ID of the service platform call
   * @param data the payload of the service platform call
   * @param stack the HeatStack result of the translation
   */
  public DeployServiceFsm(OpenStackHeatWrapper wrapper, OpenStackHeatClient client, String sid,
      DeployServiceData data, HeatTemplate stack) {

    this.wrapper = wrapper;
    this.client = client;
    this.sid = sid;
    this.data = data;
    this.stack = stack;
  }

  @Override
  public void run() {
    DeployServiceResponse response = new DeployServiceResponse();

    Logger.info("Deploying new stack");
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.setSerializationInclusion(Include.NON_NULL);
    Logger.info("Serializing stack...");
    try {
      String stackString = mapper.writeValueAsString(stack);
      Logger.info(stackString);
      String stackName = data.getNsd().getName() + data.getNsd().getInstanceUuid();
      Logger.info("Pushing stack to Heat...");
      String stackUuid = client.createStack(stackName, stackString);

      if (stackUuid == null) {
        WrapperStatusUpdate update = new WrapperStatusUpdate(this.sid, "FAIL",
            "{\"message\":\"unable to contact the VIM to instantiate the service\"}");
        wrapper.markAsChanged();
        wrapper.notifyObservers(update);
        return;
      }
      int counter = 0;
      int wait = 1000;
      String status = null;
      while ((status == null || !status.equals("CREATE_COMPLETE")
          || !status.equals("CREATE_FAILED")) && counter < DeployServiceFsm.maxCounter) {
        status = client.getStackStatus(stackName, stackUuid);
        Logger.info("Status of stack " + stackUuid + ": " + status);
        if (status != null
            && (status.equals("CREATE_COMPLETE") || status.equals("CREATE_FAILED"))) {
          break;
        }
        try {
          Thread.sleep(wait);
        } catch (InterruptedException e) {
          Logger.error(e.getMessage(), e);
        }
        counter++;
        wait *= 2;
      }

      if (status == null) {
        WrapperStatusUpdate update = new WrapperStatusUpdate(this.sid, "ERROR",
            "unable to contact the VIM to check the instantiation status");
        wrapper.markAsChanged();
        wrapper.notifyObservers(update);
        return;
      }
      if (status.equals("CREATE_FAILED")) {
        // client.deleteStack(stackName, instanceUuid);
        WrapperStatusUpdate update =
            new WrapperStatusUpdate(this.sid, "ERROR", "Stack Creation Failed.");
        wrapper.markAsChanged();
        wrapper.notifyObservers(update);
        return;
      }

      counter = 0;
      wait = 1000;
      StackComposition composition = null;
      while (composition == null && counter < DeployServiceFsm.maxCounter) {
        Logger.info("Getting composition of stack " + stackUuid);
        composition = client.getStackComposition(stackName, stackUuid);
        try {
          Thread.sleep(wait);
        } catch (InterruptedException e) {
          Logger.error(e.getMessage(), e);
        }
        counter++;
        wait *= 2;
      }

      if (composition == null) {
        WrapperStatusUpdate update = new WrapperStatusUpdate(this.sid, "ERROR",
            "unable to contact the VIM to check the instantiation status");
        wrapper.markAsChanged();
        wrapper.notifyObservers(update);
        return;
      }

      // Aux data structures for efficient mapping
      Hashtable<String, VnfDescriptor> vnfTable = new Hashtable<String, VnfDescriptor>();
      Hashtable<String, VirtualDeploymentUnit> vduTable =
          new Hashtable<String, VirtualDeploymentUnit>();
      Hashtable<String, VduRecord> vdurTable = new Hashtable<String, VduRecord>();

      // Create the response
      Logger.info("Creating deploy response");
      ServiceRecord sr = new ServiceRecord();
      sr.setStatus(Status.offline);
      sr.setDescriptorVersion("nsr-schema-01");
      sr.setId(data.getNsd().getInstanceUuid());
      sr.setDescriptorReference(data.getNsd().getUuid());
      for (VnfDescriptor vnf : data.getVnfdList()) {
        vnfTable.put(vnf.getName(), vnf);
        VnfRecord vnfr = new VnfRecord();
        vnfr.setDescriptorVersion("vnfr-schema-01");
        vnfr.setDescriptorReference(vnf.getUuid());
        // vnfr.setDescriptorReferenceName(vnf.getName());
        // vnfr.setDescriptorReferenceVendor(vnf.getVendor());
        // vnfr.setDescriptorReferenceVersion(vnf.getVersion());
        vnfr.setStatus(Status.offline);
        // TODO addresses are added next step
        // vnfr.setVnfAddress("0.0.0.0");

        vnfr.setId(vnf.getInstanceUuid());
        for (VirtualDeploymentUnit vdu : vnf.getVirtualDeploymentUnits()) {
          vduTable.put(vnf.getName() + ":" + vdu.getId(), vdu);
          VduRecord vdur = new VduRecord();
          vdur.setId(vdu.getId());
          vdur.setNumberOfInstances(1);
          vdur.setVduReference(vnf.getName() + ":" + vdu.getId());
          vdur.setVmImage(vdu.getVmImage());
          vdurTable.put(vdur.getVduReference(), vdur);
          vnfr.addVdu(vdur);
        }
        response.addVnfRecord(vnfr);
      }
      response.setNsr(sr);

      // Put each composition.server data in a VNFC instance in the relevant VDU

      for (HeatServer server : composition.getServers()) {
        String[] identifiers = server.getServerName().split(":");
        String vnfName = identifiers[0];
        String vduName = identifiers[1];
        String instanceId = identifiers[2];

        VnfcInstance vnfc = new VnfcInstance();
        vnfc.setId(instanceId);
        vnfc.setVimId(data.getVimUuid());
        vnfc.setVcId(server.getServerId());
        VnfDescriptor referenceVnf = vnfTable.get(vnfName);
        VirtualDeploymentUnit referenceVdu = vduTable.get(vnfName + ":" + vduName);

        ArrayList<ConnectionPointRecord> cpRecords = new ArrayList<ConnectionPointRecord>();

        for (ConnectionPoint cp : referenceVdu.getConnectionPoints()) {
          ConnectionPointRecord cpr = new ConnectionPointRecord();
          cpr.setId(cp.getId());

          // add each composition.ports information in the response. The IP, the netmask (and maybe
          // MAC address)
          for (HeatPort port : composition.getPorts()) {
            if (port.getPortName().equals(referenceVnf.getName() + ":" + cp.getId() + ":"
                + data.getNsd().getInstanceUuid())) {
              InterfaceRecord ip = new InterfaceRecord();
              if (port.getFloatinIp() != null) {
                ip.setAddress(port.getFloatinIp());
                // Logger.info("Port:" + port.getPortName() + "- Addr: " +
                // port.getFloatinIp());
              } else {
                ip.setAddress(port.getIpAddress());
                // Logger.info("Port:" + port.getPortName() + "- Addr: " +
                // port.getFloatinIp());
                ip.setNetmask("255.255.255.248");

              }
              cpr.setType(ip);
              break;
            }
          }

          cpRecords.add(cpr);
        }
        vnfc.setConnectionPoints(cpRecords);
        VduRecord referenceVdur =
            vdurTable.get(referenceVnf.getName() + ":" + referenceVdu.getId());
        referenceVdur.addVnfcInstance(vnfc);
      }

      NetworkingWrapper netVim = (NetworkingWrapper) WrapperBay.getInstance().getVimRepo()
          .getNetworkVim(this.data.getVimUuid()).getVimWrapper();

      netVim.configureNetworking(data, composition);

      response.setVimUuid(data.getVimUuid());
      response.setInstanceName(stackName);
      response.setInstanceVimUuid(stackUuid);
      response.setRequestStatus("DEPLOYED");
      String body = mapper.writeValueAsString(response);
      Logger.info("Response created");
      // Logger.info("body");

      WrapperBay.getInstance().getVimRepo().writeInstanceEntry(response.getNsr().getId(),
          response.getInstanceVimUuid(), response.getInstanceName(), data.getVimUuid());

      WrapperStatusUpdate update = new WrapperStatusUpdate(this.sid, "SUCCESS", body);
      wrapper.markAsChanged();
      wrapper.notifyObservers(update);
    } catch (Exception e) {
      Logger.error(e.getMessage(), e);
      response.setRequestStatus("FAIL");
      response.setErrorCode("TranslationError");
      try {
        String body = mapper.writeValueAsString(response);
        Logger.info("Error response created");
        // Logger.info("body");

        // WrapperBay.getInstance().getVimRepo().writeInstanceEntry(response.getNsr().getId(),
        // response.getInstanceVimUuid(), response.getInstanceVimUuid());

        WrapperStatusUpdate update = new WrapperStatusUpdate(this.sid, "ERROR", body);
        wrapper.markAsChanged();
        wrapper.notifyObservers(update);
      } catch (Exception f) {
        Logger.error("Error while handling Error!", e);
      }
    }
  }

}
