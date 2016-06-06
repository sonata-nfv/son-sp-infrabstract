package sonata.kernel.adaptor.wrapper.openstack;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import sonata.kernel.adaptor.commons.DeployServiceData;
import sonata.kernel.adaptor.commons.DeployServiceResponse;
import sonata.kernel.adaptor.commons.DeploymentResponse;
import sonata.kernel.adaptor.commons.ServiceRecord;
import sonata.kernel.adaptor.commons.Status;
import sonata.kernel.adaptor.commons.VduRecord;
import sonata.kernel.adaptor.commons.VnfRecord;
import sonata.kernel.adaptor.commons.VnfcInstance;
import sonata.kernel.adaptor.commons.heat.HeatModel;
import sonata.kernel.adaptor.commons.heat.HeatServer;
import sonata.kernel.adaptor.commons.heat.StackComposition;
import sonata.kernel.adaptor.commons.vnfd.VirtualDeploymentUnit;
import sonata.kernel.adaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.adaptor.wrapper.WrapperStatusUpdate;

import java.util.Hashtable;
import java.util.UUID;

public class DeployServiceFsm implements Runnable {

  private String sid;
  private DeployServiceData data;
  private OpenStackHeatWrapper wrapper;
  private OpenStackHeatClient client;
  private HeatModel stack;
  private static final int maxCounter = 5;


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
      DeployServiceData data, HeatModel stack) {

    this.wrapper = wrapper;
    this.client = client;
    this.sid = sid;
    this.data = data;
    this.stack = stack;
  }

  @Override
  public void run() {
    DeployServiceResponse response = new DeployServiceResponse();
    try {
      System.out.println("[OS-Deploy-FSM] Deploying new stack");
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
      mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
      mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
      mapper.setSerializationInclusion(Include.NON_NULL);
      System.out.println("[OS-Deploy-FSM]   Serializing stack...");

      String stackString = mapper.writeValueAsString(stack);

      String stackName = data.getNsd().getName() + data.getNsd().getInstanceUuid();
      System.out.println("[OS-Deploy-FSM]   Pushing stack to Heat...");
      String instanceUuid = client.createStack(stackName, stackString);

      int counter = 0;
      int wait = 1000;
      String status = null;
      while (counter < DeployServiceFsm.maxCounter
          && (status != null && !status.equals("COMPLETE"))) {
        status = client.getStackStatus(stackName, instanceUuid);
        System.out.println("[OS-Deploy-FSM]   Status of stack " + instanceUuid + ": " + status);
        try {
          Thread.sleep(wait);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        counter++;
        wait *= 2;
      }


      counter = 0;
      wait = 1000;
      StackComposition composition = null;
      while (counter < DeployServiceFsm.maxCounter && composition != null) {
        composition = client.getStackComposition(stackName, instanceUuid);
        System.out.println("[OS-Deploy-FSM]   composition of stack " + instanceUuid + ": "
            + composition.toString());
        try {
          Thread.sleep(wait);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        counter++;
        wait *= 2;
      }


      // Aux data structures for efficent search

      Hashtable<String, VnfDescriptor> vnfTable = new Hashtable<String, VnfDescriptor>();
      Hashtable<String, VirtualDeploymentUnit> vduTable =
          new Hashtable<String, VirtualDeploymentUnit>();
      Hashtable<String, VduRecord> vdurTable = new Hashtable<String, VduRecord>();

      // Create the response
      
      ServiceRecord sr = new ServiceRecord();
      sr.setUuid(data.getNsd().getUuid());
      sr.setStatus(Status.offline);
      sr.setInstanceUuid(data.getNsd().getInstanceUuid());
      for (VnfDescriptor vnf : data.getVnfdList()) {
        vnfTable.put(vnf.getName(), vnf);
        VnfRecord vnfr = new VnfRecord();
        vnfr.setDescriptorVersion("vnfr-schema-01");
        vnfr.setDescriptorReferenceName(vnf.getName());
        vnfr.setDescriptorReferenceVendor(vnf.getVendor());
        vnfr.setDescriptorReferenceVersion(vnf.getVersion());
        vnfr.setStatus(Status.offline);
        // TODO addresses are added next step
        // vnfr.setVnfAddress("0.0.0.0");
        vnfr.setUuid(vnf.getUuid());
        vnfr.setInstanceUuid(vnf.getInstanceUuid());
        for (VirtualDeploymentUnit vdu : vnf.getVirtualDeploymentUnits()) {
          vduTable.put(vnf.getName() + ":" + vdu.getId(), vdu);
          VduRecord vdur = new VduRecord();
          vdur.setId(UUID.randomUUID().toString());
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
        vnfc.setConnectionPoints(referenceVdu.getConnectionPoints());
        VduRecord referenceVdur =
            vdurTable.get(referenceVnf.getName() + ":" + referenceVdu.getId());
        referenceVdur.addVnfcInstance(vnfc);
      }

      // TODO add each composition.ports information in the response. The IP (and maybe MAC address) fields in the NSR are still to be defined



      response.setInstanceName(stackName);
      response.setInstanceVimUuid(instanceUuid);
      
      String body = mapper.writeValueAsString(response);

      WrapperStatusUpdate update =
          new WrapperStatusUpdate(this.sid, "SUCCESS", response.toString());
      wrapper.notifyObservers(update);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      response.setErrorCode("TranslationError");
    }
  }

}
