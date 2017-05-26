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
 * @author Dario Valocchi(Ph.D.), UCL
 * 
 * @author Guy Paz, Nokia
 * 
 */

package sonata.kernel.vimadaptor.wrapper.openstack;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.LoggerFactory;

import sonata.kernel.vimadaptor.commons.FunctionDeployPayload;
import sonata.kernel.vimadaptor.commons.FunctionDeployResponse;
import sonata.kernel.vimadaptor.commons.IpNetPool;
import sonata.kernel.vimadaptor.commons.ServiceDeployPayload;
import sonata.kernel.vimadaptor.commons.SonataManifestMapper;
import sonata.kernel.vimadaptor.commons.Status;
import sonata.kernel.vimadaptor.commons.VduRecord;
import sonata.kernel.vimadaptor.commons.VimNetTable;
import sonata.kernel.vimadaptor.commons.VnfImage;
import sonata.kernel.vimadaptor.commons.VnfRecord;
import sonata.kernel.vimadaptor.commons.VnfcInstance;
import sonata.kernel.vimadaptor.commons.heat.HeatModel;
import sonata.kernel.vimadaptor.commons.heat.HeatPort;
import sonata.kernel.vimadaptor.commons.heat.HeatResource;
import sonata.kernel.vimadaptor.commons.heat.HeatServer;
import sonata.kernel.vimadaptor.commons.heat.HeatTemplate;
import sonata.kernel.vimadaptor.commons.heat.StackComposition;
import sonata.kernel.vimadaptor.commons.nsd.ConnectionPoint;
import sonata.kernel.vimadaptor.commons.nsd.ConnectionPointRecord;
import sonata.kernel.vimadaptor.commons.nsd.ConnectionPointType;
import sonata.kernel.vimadaptor.commons.nsd.InterfaceRecord;
import sonata.kernel.vimadaptor.commons.nsd.NetworkFunction;
import sonata.kernel.vimadaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.vimadaptor.commons.nsd.VirtualLink;
import sonata.kernel.vimadaptor.commons.vnfd.VirtualDeploymentUnit;
import sonata.kernel.vimadaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.vimadaptor.commons.vnfd.VnfVirtualLink;
import sonata.kernel.vimadaptor.wrapper.ComputeWrapper;
import sonata.kernel.vimadaptor.wrapper.ResourceUtilisation;
import sonata.kernel.vimadaptor.wrapper.VimRepo;
import sonata.kernel.vimadaptor.wrapper.WrapperBay;
import sonata.kernel.vimadaptor.wrapper.WrapperConfiguration;
import sonata.kernel.vimadaptor.wrapper.WrapperStatusUpdate;
import sonata.kernel.vimadaptor.wrapper.openstack.javastackclient.models.Image.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;

public class OpenStackHeatWrapper extends ComputeWrapper {

  private static final org.slf4j.Logger Logger =
      LoggerFactory.getLogger(OpenStackHeatWrapper.class);
  private IpNetPool myPool;

  /**
   * Standard constructor for an Compute Wrapper of an OpenStack VIM using Heat.
   * 
   * @param config the config object for this Compute Wrapper
   */
  public OpenStackHeatWrapper(WrapperConfiguration config) {
    super(config);
    VimNetTable.getInstance().registerVim(this.getConfig().getUuid());
    this.myPool = VimNetTable.getInstance().getNetPool(this.getConfig().getUuid());
  }

  private HeatTemplate createInitStackTemplate(String instanceId) throws Exception {

    // TODO This values should be per User, now they are per VIM. This should be re-desinged once
    // user management is in place.
    JSONTokener tokener = new JSONTokener(this.getConfig().getConfiguration());
    JSONObject object = (JSONObject) tokener.nextValue();
    // String tenant = object.getString("tenant");
    // String tenantExtNet = object.getString("tenant_ext_net");
    String tenantExtRouter = object.getString("tenant_ext_router");
    // END COMMENT

    Logger.debug("Creating init stack template");

    HeatModel model = new HeatModel();
    int subnetIndex = 0;
    ArrayList<String> subnets = myPool.reserveSubnets(instanceId, 2);

    if (subnets == null) {
      throw new Exception("Unable to allocate internal addresses. Too many service instances");
    }

    HeatResource mgmtNetwork = new HeatResource();
    mgmtNetwork.setType("OS::Neutron::Net");
    mgmtNetwork.setName("SonataService.mgmt.net." + instanceId);
    mgmtNetwork.putProperty("name", "SonatService" + ".mgmt.net." + instanceId);
    model.addResource(mgmtNetwork);

    HeatResource mgmtSubnet = new HeatResource();

    mgmtSubnet.setType("OS::Neutron::Subnet");
    mgmtSubnet.setName("SonataService.mgmt.subnet." + instanceId);
    mgmtSubnet.putProperty("name", "SonataService.mgmt.subnet." + instanceId);
    String cidr = subnets.get(subnetIndex);
    mgmtSubnet.putProperty("cidr", cidr);
    mgmtSubnet.putProperty("gateway_ip", myPool.getGateway(cidr));
    String[] dnsArray = {"8.8.8.8"};
    mgmtSubnet.putProperty("dns_nameservers", dnsArray);
    subnetIndex++;
    HashMap<String, Object> mgmtNetMap = new HashMap<String, Object>();
    mgmtNetMap.put("get_resource", "SonataService.mgmt.net." + instanceId);
    mgmtSubnet.putProperty("network", mgmtNetMap);
    model.addResource(mgmtSubnet);

    // Internal mgmt router interface
    HeatResource mgmtRouterInterface = new HeatResource();
    mgmtRouterInterface.setType("OS::Neutron::RouterInterface");
    mgmtRouterInterface.setName("SonataService.mgmt.internal." + instanceId);
    HashMap<String, Object> mgmtSubnetMapInt = new HashMap<String, Object>();
    mgmtSubnetMapInt.put("get_resource", "SonataService.mgmt.subnet." + instanceId);
    mgmtRouterInterface.putProperty("subnet", mgmtSubnetMapInt);
    mgmtRouterInterface.putProperty("router", tenantExtRouter);
    model.addResource(mgmtRouterInterface);

    HeatResource dataNetwork = new HeatResource();
    dataNetwork.setType("OS::Neutron::Net");
    dataNetwork.setName("SonataService.data.net." + instanceId);
    dataNetwork.putProperty("name", "SonatService.data.net." + instanceId);
    model.addResource(dataNetwork);

    // Create the data subnet
    HeatResource dataSubnet = new HeatResource();

    dataSubnet.setType("OS::Neutron::Subnet");
    dataSubnet.setName("SonataService.data.subnet." + instanceId);
    dataSubnet.putProperty("name", "SonataService.data.subnet." + instanceId);
    cidr = subnets.get(subnetIndex);
    dataSubnet.putProperty("cidr", cidr);
    dataSubnet.putProperty("gateway_ip", myPool.getGateway(cidr));
    subnetIndex++;
    HashMap<String, Object> dataNetMap = new HashMap<String, Object>();
    dataNetMap.put("get_resource", "SonataService.data.net." + instanceId);
    dataSubnet.putProperty("network", dataNetMap);
    model.addResource(dataSubnet);

    model.prepare();

    HeatTemplate template = new HeatTemplate();
    Logger.debug("Created " + model.getResources().size() + " resurces.");
    for (HeatResource resource : model.getResources()) {
      template.putResource(resource.getResourceName(), resource);
    }
    return template;
  }

  @Override
  public boolean deployService(ServiceDeployPayload data, String callSid) {

    // TODO This values should be per User, now they are per VIM. This should be re-desinged once
    // user management is in place.
    JSONTokener tokener = new JSONTokener(getConfig().getConfiguration());
    JSONObject object = (JSONObject) tokener.nextValue();
    String tenant = object.getString("tenant");
    // String tenantExtNet = object.getString("tenant_ext_net");
    // String tenantExtRouter = object.getString("tenant_ext_router");
    // END COMMENT
    OpenStackHeatClient client = null;
    OpenStackNovaClient novaClient=null;

    try{
    client = new OpenStackHeatClient(getConfig().getVimEndpoint().toString(),
        getConfig().getAuthUserName(), getConfig().getAuthPass(), tenant);
    novaClient = new OpenStackNovaClient(getConfig().getVimEndpoint().toString(),
        getConfig().getAuthUserName(), getConfig().getAuthPass(), tenant);
    }catch(IOException e){
      Logger.error("OpenStackHeat wrapper - Unable to connect to the VIM");
      this.setChanged();
      WrapperStatusUpdate errorUpdate = new WrapperStatusUpdate(callSid, "ERROR", e.getMessage());
      this.notifyObservers(errorUpdate);
      return false;
    }
    ArrayList<Flavor> vimFlavors = novaClient.getFlavors();
    Collections.sort(vimFlavors);
    HeatModel stack;
    try {
      stack = translate(data, vimFlavors);

      HeatTemplate template = new HeatTemplate();
      for (HeatResource resource : stack.getResources()) {
        template.putResource(resource.getResourceName(), resource);
      }
      DeployServiceFsm fsm = new DeployServiceFsm(this, client, callSid, data, template);

      Thread thread = new Thread(fsm);
      thread.start();
    } catch (Exception e) {
      this.setChanged();
      WrapperStatusUpdate errorUpdate = new WrapperStatusUpdate(callSid, "ERROR", e.getMessage());
      this.notifyObservers(errorUpdate);
      return false;
    }

    return true;

  }

  /**
   * Returns a heat template translated from the given descriptors. Mainly used for unit testing
   * scope
   * 
   * @param data the service descriptors to translate
   * @param vimFlavors the list of available compute flavors
   * @return an HeatTemplate object translated from the given descriptors
   * @throws Exception if unable to translate the descriptor.
   */
  public HeatTemplate getHeatTemplateFromSonataDescriptor(ServiceDeployPayload data,
      ArrayList<Flavor> vimFlavors) throws Exception {
    HeatModel model = this.translate(data, vimFlavors);
    HeatTemplate template = new HeatTemplate();
    for (HeatResource resource : model.getResources()) {
      template.putResource(resource.getResourceName(), resource);
    }
    return template;
  }

  @Override
  public ResourceUtilisation getResourceUtilisation() {
    long start = System.currentTimeMillis(); 
    // TODO This values should be per User, now they are per VIM. This should be re-desinged once
    // user management is in place.
    JSONTokener tokener = new JSONTokener(getConfig().getConfiguration());
    JSONObject object = (JSONObject) tokener.nextValue();
    String tenant = object.getString("tenant");
    // String tenantExtNet = object.getString("tenant_ext_net");
    // String tenantExtRouter = object.getString("tenant_ext_router");
    // END COMMENT

    ResourceUtilisation output=null;
    Logger.info("OpenStack wrapper - Getting resource utilisation...");
    OpenStackNovaClient client;
    try {
      client = new OpenStackNovaClient(getConfig().getVimEndpoint(),
          getConfig().getAuthUserName(), getConfig().getAuthPass(), tenant);
      output = client.getResourceUtilizasion();
      Logger.info("OpenStack wrapper - Resource utilisation retrieved.");
    } catch (IOException e) {
      Logger.error("OpenStack wrapper - Unable to connect to PoP.");;
      output = new ResourceUtilisation();
      output.setTotCores(0);
      output.setUsedCores(0);
      output.setTotMemory(0);
      output.setUsedMemory(0);      
    }
    long stop = System.currentTimeMillis();
    Logger.info("[OpenStackWrapper]getResourceUtilisation-time: " + (stop-start) + " ms");
    return output;
  }



  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#prepareService(java.lang.String)
   */
  @Override
  public boolean prepareService(String instanceId) throws Exception {
    long start = System.currentTimeMillis();
    // TODO This values should be per User, now they are per VIM. This should be re-desinged once
    // user management is in place.
    JSONTokener tokener = new JSONTokener(getConfig().getConfiguration());
    JSONObject object = (JSONObject) tokener.nextValue();
    String tenant = object.getString("tenant");
    // String tenantExtNet = object.getString("tenant_ext_net");
    // String tenantExtRouter = object.getString("tenant_ext_router");
    // END COMMENT

    // To prepare a service instance management and data networks/subnets
    // must be created. The Management Network must also be attached to the external router.
    OpenStackHeatClient client = new OpenStackHeatClient(getConfig().getVimEndpoint().toString(),
        getConfig().getAuthUserName(), getConfig().getAuthPass(), tenant);

    HeatTemplate template = createInitStackTemplate(instanceId);

    Logger.info("Deploying new stack for service preparation.");
    ObjectMapper mapper = SonataManifestMapper.getSonataMapper();
    Logger.info("Serializing stack...");
    try {
      String stackString = mapper.writeValueAsString(template);
      Logger.debug(stackString);
      String stackName = "SonataService-" + instanceId;
      Logger.info("Pushing stack to Heat...");
      String stackUuid = client.createStack(stackName, stackString);

      if (stackUuid == null) {
        Logger.error("unable to contact the VIM to instantiate the service");
        return false;
      }
      int counter = 0;
      int wait = 1000;
      int maxCounter = 10;
      String status = null;
      while ((status == null || !status.equals("CREATE_COMPLETE")
          || !status.equals("CREATE_FAILED")) && counter < maxCounter) {
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
        Logger.error("unable to contact the VIM to check the instantiation status");
        return false;
      }
      if (status.equals("CREATE_FAILED")) {
        Logger.error("Heat Stack creation process failed on the VIM side.");
        return false;

      }
      Logger.info("VIM prepared succesfully. Creating record in Infra Repo.");
      WrapperBay.getInstance().getVimRepo().writeServiceInstanceEntry(instanceId, stackUuid,
          stackName, this.getConfig().getUuid());

    } catch (Exception e) {
      Logger.error("Error during stack creation.");
      Logger.error(e.getMessage());
      return false;
    }
    long stop = System.currentTimeMillis();
    Logger.info("[OpenStackWrapper]PrepareService-time: " + (stop-start) + " ms");
    return true;

  }

  @Override
  public boolean removeService(String instanceUuid, String callSid) {
    long start = System.currentTimeMillis();
    // TODO This values should be per User, now they are per VIM. This should be re-desinged once
    // user management is in place.
    JSONTokener tokener = new JSONTokener(getConfig().getConfiguration());
    JSONObject object = (JSONObject) tokener.nextValue();
    String tenant = object.getString("tenant");
    // String tenantExtNet = object.getString("tenant_ext_net");
    // String tenantExtRouter = object.getString("tenant_ext_router");
    // END COMMENT


    VimRepo repo = WrapperBay.getInstance().getVimRepo();
    Logger.info("Trying to remove NS instance: " + instanceUuid);
    String stackName = repo.getServiceInstanceVimName(instanceUuid);
    String stackUuid = repo.getServiceInstanceVimUuid(instanceUuid);
    Logger.info("NS instance mapped to stack name: " + stackName);
    Logger.info("NS instance mapped to stack uuid: " + stackUuid);

    OpenStackHeatClient client = null;
    
    try{
    client = new OpenStackHeatClient(getConfig().getVimEndpoint().toString(),
        getConfig().getAuthUserName(), getConfig().getAuthPass(), tenant);
    }catch(IOException e){
      Logger.error("OpenStackHeat wrapper - Unable to connect to the VIM");
      this.setChanged();
      WrapperStatusUpdate errorUpdate = new WrapperStatusUpdate(callSid, "ERROR", e.getMessage());
      this.notifyObservers(errorUpdate);
      return false;
    }
    
    try {
      String output = client.deleteStack(stackName, stackUuid);

      if (output.equals("DELETED")) {
        repo.removeServiceInstanceEntry(instanceUuid, this.getConfig().getUuid());
        myPool.freeSubnets(instanceUuid);
        this.setChanged();
        String body =
            "{\"status\":\"COMPLETED\",\"wrapper_uuid\":\"" + this.getConfig().getUuid() + "\"}";
        WrapperStatusUpdate update = new WrapperStatusUpdate(callSid, "SUCCESS", body);
        this.notifyObservers(update);
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.setChanged();
      WrapperStatusUpdate errorUpdate = new WrapperStatusUpdate(callSid, "ERROR", e.getMessage());
      this.notifyObservers(errorUpdate);
      return false;
    }
    long stop = System.currentTimeMillis();
    Logger.info("[OpenStackWrapper]RemoveService-time: " + (stop-start) + " ms");
    return true;
  }

  private String selectFlavor(int vcpu, double memoryInGB, double storageIngGB,
      ArrayList<Flavor> vimFlavors) {
    Logger.debug("Flavor Selecting routine...");
    for (Flavor flavor : vimFlavors) {
      if (vcpu <= flavor.getVcpu() && ((memoryInGB * 1024) <= flavor.getRam())
          && (storageIngGB <= flavor.getStorage())) {
        Logger.debug("Flavor found");
        return flavor.getFlavorName();
      }
    }
    Logger.debug("Flavor not found");
    return null;
  }

  private HeatModel translate(ServiceDeployPayload data, ArrayList<Flavor> vimFlavors)
      throws Exception {

    // TODO This values should be per User, now they are per VIM. This should be re-desinged once
    // user management is in place.
    JSONTokener tokener = new JSONTokener(getConfig().getConfiguration());
    JSONObject object = (JSONObject) tokener.nextValue();
    // String tenant = object.getString("tenant");
    String tenantExtNet = object.getString("tenant_ext_net");
    String tenantExtRouter = object.getString("tenant_ext_router");
    // END COMMENT


    ServiceDescriptor nsd = data.getNsd();

    // Allocate Ip Addresses on the basis of the service requirements:
    int numberOfSubnets = 1;
    int subnetIndex = 0;

    for (VnfDescriptor vnfd : data.getVnfdList()) {
      ArrayList<VnfVirtualLink> links = vnfd.getVirtualLinks();
      for (VnfVirtualLink link : links) {
        if (!link.getId().equals("mgmt")) {
          numberOfSubnets++;
        }
      }
    }
    ArrayList<String> subnets = myPool.reserveSubnets(nsd.getInstanceUuid(), numberOfSubnets);

    if (subnets == null) {
      throw new Exception("Unable to allocate internal addresses. Too many service instances");
    }

    // Create the management Net and subnet for all the VNFCs and VNFs
    HeatResource mgmtNetwork = new HeatResource();
    mgmtNetwork.setType("OS::Neutron::Net");
    mgmtNetwork.setName(nsd.getName() + ".mgmt.net." + nsd.getInstanceUuid());
    mgmtNetwork.putProperty("name", nsd.getName() + ".mgmt.net." + nsd.getInstanceUuid());



    HeatModel model = new HeatModel();
    model.addResource(mgmtNetwork);

    HeatResource mgmtSubnet = new HeatResource();

    mgmtSubnet.setType("OS::Neutron::Subnet");
    mgmtSubnet.setName(nsd.getName() + ".mgmt.subnet." + nsd.getInstanceUuid());
    mgmtSubnet.putProperty("name", nsd.getName() + ".mgmt.subnet." + nsd.getInstanceUuid());
    String cidr = subnets.get(subnetIndex);
    mgmtSubnet.putProperty("cidr", cidr);
    mgmtSubnet.putProperty("gateway_ip", myPool.getGateway(cidr));

    // mgmtSubnet.putProperty("cidr", "192.168." + subnetIndex + ".0/24");
    // mgmtSubnet.putProperty("gateway_ip", "192.168." + subnetIndex + ".1");

    subnetIndex++;
    HashMap<String, Object> mgmtNetMap = new HashMap<String, Object>();
    mgmtNetMap.put("get_resource", nsd.getName() + ".mgmt.net." + nsd.getInstanceUuid());
    mgmtSubnet.putProperty("network", mgmtNetMap);
    model.addResource(mgmtSubnet);


    // Internal mgmt router interface
    HeatResource mgmtRouterInterface = new HeatResource();
    mgmtRouterInterface.setType("OS::Neutron::RouterInterface");
    mgmtRouterInterface.setName(nsd.getName() + ".mgmt.internal." + nsd.getInstanceUuid());
    HashMap<String, Object> mgmtSubnetMapInt = new HashMap<String, Object>();
    mgmtSubnetMapInt.put("get_resource", nsd.getName() + ".mgmt.subnet." + nsd.getInstanceUuid());
    mgmtRouterInterface.putProperty("subnet", mgmtSubnetMapInt);
    mgmtRouterInterface.putProperty("router", tenantExtRouter);
    model.addResource(mgmtRouterInterface);

    cidr = null;
    // One virtual router for NSD virtual links connecting VNFS (no router for external virtual
    // links and management links)

    ArrayList<VnfDescriptor> vnfs = data.getVnfdList();
    for (VirtualLink link : nsd.getVirtualLinks()) {
      ArrayList<String> connectionPointReference = link.getConnectionPointsReference();
      boolean isInterVnf = true;
      boolean isMgmt = link.getId().equals("mgmt");
      for (String cpRef : connectionPointReference) {
        if (cpRef.startsWith("ns:")) {
          isInterVnf = false;
          break;
        }
      }
      if (isInterVnf && !isMgmt) {
        HeatResource router = new HeatResource();
        router.setName(nsd.getName() + "." + link.getId() + "." + nsd.getInstanceUuid());
        router.setType("OS::Neutron::Router");
        router.putProperty("name",
            nsd.getName() + "." + link.getId() + "." + nsd.getInstanceUuid());
        model.addResource(router);
      }
    }

    ArrayList<String> mgmtPortNames = new ArrayList<String>();

    for (VnfDescriptor vnfd : vnfs) {
      // One network and subnet for vnf virtual link (mgmt links handled later)
      ArrayList<VnfVirtualLink> links = vnfd.getVirtualLinks();
      for (VnfVirtualLink link : links) {
        if (!link.getId().equals("mgmt")) {
          HeatResource network = new HeatResource();
          network.setType("OS::Neutron::Net");
          network.setName(vnfd.getName() + "." + link.getId() + ".net." + nsd.getInstanceUuid());
          network.putProperty("name",
              vnfd.getName() + "." + link.getId() + ".net." + nsd.getInstanceUuid());
          model.addResource(network);
          HeatResource subnet = new HeatResource();
          subnet.setType("OS::Neutron::Subnet");
          subnet.setName(vnfd.getName() + "." + link.getId() + ".subnet." + nsd.getInstanceUuid());
          subnet.putProperty("name",
              vnfd.getName() + "." + link.getId() + ".subnet." + nsd.getInstanceUuid());
          cidr = subnets.get(subnetIndex);
          subnet.putProperty("cidr", cidr);
          // getConfig() parameter
          // String[] dnsArray = { "10.30.0.11", "8.8.8.8" };
          // TODO DNS should not be hardcoded, VMs should automatically get OpenStack subnet DNS.
          String[] dnsArray = {"8.8.8.8"};
          subnet.putProperty("dns_nameservers", dnsArray);
          // subnet.putProperty("gateway_ip", myPool.getGateway(cidr));
          // subnet.putProperty("cidr", "192.168." + subnetIndex + ".0/24");
          // subnet.putProperty("gateway_ip", "192.168." + subnetIndex + ".1");
          subnetIndex++;
          HashMap<String, Object> netMap = new HashMap<String, Object>();
          netMap.put("get_resource",
              vnfd.getName() + "." + link.getId() + ":net:" + nsd.getInstanceUuid());
          subnet.putProperty("network", netMap);
          model.addResource(subnet);
        }
      }
      // One virtual machine for each VDU

      for (VirtualDeploymentUnit vdu : vnfd.getVirtualDeploymentUnits()) {
        HeatResource server = new HeatResource();
        server.setType("OS::Nova::Server");
        server.setName(vnfd.getName() + "." + vdu.getId() + "." + nsd.getInstanceUuid());
        server.putProperty("name",
            vnfd.getName() + "." + vdu.getId() + "." + nsd.getInstanceUuid());
        server.putProperty("image", vdu.getVmImage());
        int vcpu = vdu.getResourceRequirements().getCpu().getVcpus();
        double memoryInBytes = vdu.getResourceRequirements().getMemory().getSize()
            * vdu.getResourceRequirements().getMemory().getSizeUnit().getMultiplier();
        double storageInBytes = vdu.getResourceRequirements().getStorage().getSize()
            * vdu.getResourceRequirements().getStorage().getSizeUnit().getMultiplier();
        String flavorName = this.selectFlavor(vcpu, memoryInBytes, storageInBytes, vimFlavors);
        if (flavorName == null) {
          throw new Exception("Cannot find an available flavor for requirements. CPU: " + vcpu
              + " - mem: " + memoryInBytes + " - sto: " + storageInBytes);
        }
        server.putProperty("flavor", flavorName);
        ArrayList<HashMap<String, Object>> net = new ArrayList<HashMap<String, Object>>();
        for (ConnectionPoint cp : vdu.getConnectionPoints()) {
          // create the port resource
          boolean isMgmtPort = false;
          String linkIdReference = null;
          for (VnfVirtualLink link : vnfd.getVirtualLinks()) {
            if (link.getConnectionPointsReference().contains(cp.getId())) {
              if (link.getId().equals("mgmt")) {
                isMgmtPort = true;
              } else {
                linkIdReference = link.getId();
              }
              break;
            }
          }
          if (isMgmtPort) {
            // connect this VNFC CP to the mgmt network
            HeatResource port = new HeatResource();
            port.setType("OS::Neutron::Port");
            port.setName(vnfd.getName() + "." + cp.getId() + "." + nsd.getInstanceUuid());
            port.putProperty("name",
                vnfd.getName() + "." + cp.getId() + "." + nsd.getInstanceUuid());
            HashMap<String, Object> netMap = new HashMap<String, Object>();
            netMap.put("get_resource", nsd.getName() + ".mgmt.net." + nsd.getInstanceUuid());
            port.putProperty("network", netMap);
            model.addResource(port);
            mgmtPortNames.add(vnfd.getName() + "." + cp.getId() + "." + nsd.getInstanceUuid());

            // add the port to the server
            HashMap<String, Object> n1 = new HashMap<String, Object>();
            HashMap<String, Object> portMap = new HashMap<String, Object>();
            portMap.put("get_resource",
                vnfd.getName() + "." + cp.getId() + "." + nsd.getInstanceUuid());
            n1.put("port", portMap);
            net.add(n1);
          } else if (linkIdReference != null) {
            HeatResource port = new HeatResource();
            port.setType("OS::Neutron::Port");
            port.setName(vnfd.getName() + "." + cp.getId() + "." + nsd.getInstanceUuid());
            port.putProperty("name",
                vnfd.getName() + "." + cp.getId() + "." + nsd.getInstanceUuid());
            HashMap<String, Object> netMap = new HashMap<String, Object>();
            netMap.put("get_resource",
                vnfd.getName() + "." + linkIdReference + ".net." + nsd.getInstanceUuid());
            port.putProperty("network", netMap);

            model.addResource(port);
            // add the port to the server
            HashMap<String, Object> n1 = new HashMap<String, Object>();
            HashMap<String, Object> portMap = new HashMap<String, Object>();
            portMap.put("get_resource",
                vnfd.getName() + "." + cp.getId() + "." + nsd.getInstanceUuid());
            n1.put("port", portMap);
            net.add(n1);
          }
        }
        server.putProperty("networks", net);
        model.addResource(server);
      }

      // One Router interface per VNF cp connected to a inter-VNF link of the NSD
      for (ConnectionPoint cp : vnfd.getConnectionPoints()) {
        boolean isMgmtPort = cp.getId().contains("mgmt");

        if (!isMgmtPort) {
          // Resolve vnf_id from vnf_name
          String vnfId = null;
          // Logger.info("[TRANSLATION] VNFD.name: " + vnfd.getName());

          for (NetworkFunction vnf : nsd.getNetworkFunctions()) {
            // Logger.info("[TRANSLATION] NSD.network_functions.vnf_name: " + vnf.getVnfName());
            // Logger.info("[TRANSLATION] NSD.network_functions.vnf_id: " + vnf.getVnfId());

            if (vnf.getVnfName().equals(vnfd.getName())) {
              vnfId = vnf.getVnfId();
            }
          }

          if (vnfId == null) {
            throw new Exception("Error binding VNFD.connection_point: "
                + "Cannot resolve VNFD.name in NSD.network_functions. " + "VNFD.name = "
                + vnfd.getName() + " - VFND.connection_point = " + cp.getId());

          }
          boolean isInOut = false;
          String nsVirtualLink = null;
          boolean isVirtualLinkFound = false;
          for (VirtualLink link : nsd.getVirtualLinks()) {
            if (link.getConnectionPointsReference().contains(cp.getId().replace("vnf", vnfId))) {
              isVirtualLinkFound = true;
              for (String cpRef : link.getConnectionPointsReference()) {
                if (cpRef.startsWith("ns:")) {
                  isInOut = true;
                  break;
                }
              }
              if (!isInOut) {
                nsVirtualLink = nsd.getName() + "." + link.getId() + "." + nsd.getInstanceUuid();
              }
              break;
            }
          }
          if (!isVirtualLinkFound) {
            throw new Exception("Error binding VNFD.connection_point:"
                + " Cannot find NSD.virtual_link attached to VNFD.connection_point."
                + " VNFD.connection_point = " + vnfd.getName() + "." + cp.getId());
          }
          if (!isInOut) {
            HeatResource routerInterface = new HeatResource();
            routerInterface.setType("OS::Neutron::RouterInterface");
            routerInterface
                .setName(vnfd.getName() + "." + cp.getId() + "." + nsd.getInstanceUuid());

            for (VnfVirtualLink link : links) {
              if (link.getConnectionPointsReference().contains(cp.getId())) {
                HashMap<String, Object> subnetMap = new HashMap<String, Object>();
                subnetMap.put("get_resource",
                    vnfd.getName() + "." + link.getId() + ".subnet." + nsd.getInstanceUuid());
                routerInterface.putProperty("subnet", subnetMap);
                break;
              }
            }

            // Attach to the virtual router
            HashMap<String, Object> routerMap = new HashMap<String, Object>();
            routerMap.put("get_resource", nsVirtualLink);
            routerInterface.putProperty("router", routerMap);
            model.addResource(routerInterface);
          }
        }
      }

    }

    for (String portName : mgmtPortNames) {
      // allocate floating IP
      HeatResource floatingIp = new HeatResource();
      floatingIp.setType("OS::Neutron::FloatingIP");
      floatingIp.setName("floating:" + portName);


      floatingIp.putProperty("floating_network_id", tenantExtNet);

      HashMap<String, Object> floatMapPort = new HashMap<String, Object>();
      floatMapPort.put("get_resource", portName);
      floatingIp.putProperty("port_id", floatMapPort);

      model.addResource(floatingIp);
    }
    model.prepare();
    return model;
  }

  private HeatModel translate(VnfDescriptor vnfd, ArrayList<Flavor> flavors, String instanceUuid)
      throws Exception {
    // TODO This values should be per User, now they are per VIM. This should be re-desinged once
    // user management is in place.
    JSONTokener tokener = new JSONTokener(getConfig().getConfiguration());
    JSONObject object = (JSONObject) tokener.nextValue();
    // String tenant = object.getString("tenant");
    String tenantExtNet = object.getString("tenant_ext_net");
    // String tenantExtRouter = object.getString("tenant_ext_router");
    // END COMMENT

    HeatModel model = new HeatModel();

    ArrayList<String> publicPortNames = new ArrayList<String>();

    for (VirtualDeploymentUnit vdu : vnfd.getVirtualDeploymentUnits()) {
      Logger.debug("Each VDU goes into a resource group with a number of Heat Server...");
      HeatResource resourceGroup = new HeatResource();
      resourceGroup.setType("OS::Heat::ResourceGroup");
      resourceGroup.setName(vnfd.getName() + "." + vdu.getId() + "." + instanceUuid);
      resourceGroup.putProperty("count", new Integer(1));
      HeatResource server = new HeatResource();
      server.setType("OS::Nova::Server");
      server.setName(null);
      server.putProperty("name", vnfd.getName() + "." + vdu.getId() + "." + instanceUuid + ".%index%");
      server.putProperty("image",
          vnfd.getVendor() + "_" + vnfd.getName() + "_" + vnfd.getVersion() + "_" + vdu.getId());
      int vcpu = vdu.getResourceRequirements().getCpu().getVcpus();
      double memoryInGB = vdu.getResourceRequirements().getMemory().getSize()
          * vdu.getResourceRequirements().getMemory().getSizeUnit().getMultiplier();
      double storageInGB = vdu.getResourceRequirements().getStorage().getSize()
          * vdu.getResourceRequirements().getStorage().getSizeUnit().getMultiplier();
      String flavorName = null;
      try {
        flavorName = this.selectFlavor(vcpu, memoryInGB, storageInGB, flavors);
      } catch (Exception e) {
        Logger.error("Exception while searching for available flavor for the requirements: "
            + e.getMessage());
        throw new Exception("Cannot find an available flavor for requirements. CPU: " + vcpu
            + " - mem: " + memoryInGB + " - sto: " + storageInGB);
      }
      if (flavorName == null) {
        Logger.error("Cannot find an available flavor for the requirements. CPU: " + vcpu
            + " - mem: " + memoryInGB + " - sto: " + storageInGB);
        throw new Exception("Cannot find an available flavor for requirements. CPU: " + vcpu
            + " - mem: " + memoryInGB + " - sto: " + storageInGB);
      }
      server.putProperty("flavor", flavorName);
      ArrayList<HashMap<String, Object>> net = new ArrayList<HashMap<String, Object>>();
      for (ConnectionPoint cp : vdu.getConnectionPoints()) {
        // create the port resource
        HeatResource port = new HeatResource();
        port.setType("OS::Neutron::Port");
        port.setName(vnfd.getName() + "." + cp.getId() + "." + instanceUuid);
        port.putProperty("name", vnfd.getName() + "." + cp.getId() + "." + instanceUuid);
        HashMap<String, Object> netMap = new HashMap<String, Object>();
        Logger.debug("Mapping CP Type to the relevant network");
        if (cp.getType().equals(ConnectionPointType.INT)) {
          //Only able access other VNFC from this port
          netMap.put("get_resource", "SonataService.data.net." + instanceUuid);
        } else if (cp.getType().equals(ConnectionPointType.EXT)) {
          //Able to access internet through this port, but not vice-versa
          netMap.put("get_resource", "SonataService.mgmt.net." + instanceUuid);
        } else if (cp.getType().equals(ConnectionPointType.MANAGEMENT)) {
          //Get a public IP
          netMap.put("get_resource", "SonataService.mgmt.net." + instanceUuid);
          publicPortNames.add(vnfd.getName() + "." + cp.getId() + "." + instanceUuid);
        } else{
          Logger.error("Cannot map the parsed CP type " + cp.getType() + " to a known one");
          throw new Exception("Unable to translate CP "+ vnfd.getName()+":"+cp.getId());
        }
        port.putProperty("network", netMap);
        model.addResource(port);

        // add the port to the server
        HashMap<String, Object> n1 = new HashMap<String, Object>();
        HashMap<String, Object> portMap = new HashMap<String, Object>();
        portMap.put("get_resource", vnfd.getName() + "." + cp.getId() + "." + instanceUuid);
        n1.put("port", portMap);
        net.add(n1);
      }
      server.putProperty("networks", net);
      resourceGroup.putProperty("resource_def", server);
      model.addResource(resourceGroup);
    }

    for (String portName : publicPortNames) {
      // allocate floating IP
      HeatResource floatingIp = new HeatResource();
      floatingIp.setType("OS::Neutron::FloatingIP");
      floatingIp.setName("floating:" + portName);


      floatingIp.putProperty("floating_network_id", tenantExtNet);

      HashMap<String, Object> floatMapPort = new HashMap<String, Object>();
      floatMapPort.put("get_resource", portName);
      floatingIp.putProperty("port_id", floatMapPort);

      model.addResource(floatingIp);
    }
    model.prepare();
    return model;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * sonata.kernel.vimadaptor.wrapper.ComputeWrapper#deployFunction(sonata.kernel.vimadaptor.commons
   * .FunctionDeployPayload, java.lang.String)
   */
  @Override
  public synchronized void deployFunction(FunctionDeployPayload data, String sid) {
    Long start = System.currentTimeMillis();
    // TODO This values should be per User, now they are per VIM. This should be re-desinged once
    // user management is in place.
    JSONTokener tokener = new JSONTokener(getConfig().getConfiguration());
    JSONObject object = (JSONObject) tokener.nextValue();
    String tenant = object.getString("tenant");
    // String tenantExtNet = object.getString("tenant_ext_net");
    // String tenantExtRouter = object.getString("tenant_ext_router");
    // END COMMENT

    OpenStackHeatClient client = null;
    OpenStackNovaClient novaClient=null;

    try{
    client = new OpenStackHeatClient(getConfig().getVimEndpoint().toString(),
        getConfig().getAuthUserName(), getConfig().getAuthPass(), tenant);
    novaClient = new OpenStackNovaClient(getConfig().getVimEndpoint().toString(),
        getConfig().getAuthUserName(), getConfig().getAuthPass(), tenant);
    }catch(IOException e){
      Logger.error("OpenStackHeat wrapper - Unable to connect to the VIM");
      this.setChanged();
      WrapperStatusUpdate errorUpdate = new WrapperStatusUpdate(sid, "ERROR", e.getMessage());
      this.notifyObservers(errorUpdate);
      return;
    }

    Logger.debug("Getting VIM stack name and UUID for service instance ID "+data.getServiceInstanceId());
    String stackUuid = WrapperBay.getInstance().getVimRepo()
        .getServiceInstanceVimUuid(data.getServiceInstanceId(), this.getConfig().getUuid());
    String stackName = WrapperBay.getInstance().getVimRepo()
        .getServiceInstanceVimName(data.getServiceInstanceId(), this.getConfig().getUuid());
    ArrayList<Flavor> vimFlavors = novaClient.getFlavors();
    Collections.sort(vimFlavors);
    HeatModel stackAddendum;

    HeatTemplate template = client.getStackTemplate(stackName, stackUuid);
    if (template == null) {
      Logger.error("Error retrieveing the stack template.");
      WrapperStatusUpdate update =
          new WrapperStatusUpdate(sid, "ERROR", "Cannot retrieve service stack from VIM.");
      this.markAsChanged();
      this.notifyObservers(update);
      return;
    }
    try {
      stackAddendum = translate(data.getVnfd(), vimFlavors, data.getServiceInstanceId());
    } catch (Exception e) {
      Logger.error("Error: " + e.getMessage());
      e.printStackTrace();
      WrapperStatusUpdate update =
          new WrapperStatusUpdate(sid, "ERROR", "Exception during VNFD translation.");
      this.markAsChanged();
      this.notifyObservers(update);
      return;
    }
    for (HeatResource resource : stackAddendum.getResources()) {
      template.putResource(resource.getResourceName(), resource);
    }

    Logger.info("Deploying new stack for service preparation.");
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
    mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    mapper.setSerializationInclusion(Include.NON_NULL);
    Logger.info("Serializing updated stack...");
    String stackString = null;
    try {
      stackString = mapper.writeValueAsString(template);
    } catch (JsonProcessingException e) {
      Logger.error(e.getMessage());
      WrapperStatusUpdate update =
          new WrapperStatusUpdate(sid, "ERROR", "Exception during VNF Deployment");
      this.markAsChanged();
      this.notifyObservers(update);
      return;
    }
    Logger.debug(stackString);
    try{
    client.updateStack(stackName, stackUuid, stackString);
    } catch (Exception e) {
      Logger.error(e.getMessage());
      WrapperStatusUpdate update =
          new WrapperStatusUpdate(sid, "ERROR", "Exception during VNF Deployment");
      this.markAsChanged();
      this.notifyObservers(update);
      return;
    }
    int counter = 0;
    int wait = 1000;
    int maxCounter = 10;
    String status = null;
    while ((status == null || !status.equals("UPDATE_COMPLETE") || !status.equals("UPDATE_FAILED"))
        && counter < maxCounter) {
      status = client.getStackStatus(stackName, stackUuid);
      Logger.info("Status of stack " + stackUuid + ": " + status);
      if (status != null && (status.equals("UPDATE_COMPLETE") || status.equals("UPDATE_FAILED"))) {
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
      Logger.error("unable to contact the VIM to check the update status");
      WrapperStatusUpdate update = new WrapperStatusUpdate(sid, "ERROR",
          "Functiono deployment process failed. Can't get update status.");
      this.markAsChanged();
      this.notifyObservers(update);
      return;
    }
    if (status.equals("UPDATE_FAILED")) {
      Logger.error("Heat Stack update process failed on the VIM side.");
      WrapperStatusUpdate update = new WrapperStatusUpdate(sid, "ERROR",
          "Function deployment process failed on the VIM side.");
      this.markAsChanged();
      this.notifyObservers(update);
      return;
    }


    counter = 0;
    wait = 1000;
    StackComposition composition = null;
    while (composition == null && counter < maxCounter) {
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
      Logger.error("unable to contact the VIM to get the stack composition");
      WrapperStatusUpdate update =
          new WrapperStatusUpdate(sid, "ERROR", "Unable to get updated stack composition");
      this.markAsChanged();
      this.notifyObservers(update);
      return;
    }

    Logger.info("Creating function deploy response");
    // Aux data structures for efficient mapping
    Hashtable<String, VirtualDeploymentUnit> vduTable =
        new Hashtable<String, VirtualDeploymentUnit>();
    Hashtable<String, VduRecord> vdurTable = new Hashtable<String, VduRecord>();

    // Create the response

    FunctionDeployResponse response = new FunctionDeployResponse();
    VnfDescriptor vnfd = data.getVnfd();
    response.setRequestStatus("COMPLETED");
    response.setInstanceVimUuid(stackUuid);
    response.setInstanceName(stackName);
    response.setVimUuid(this.getConfig().getUuid());
    response.setMessage("");


    VnfRecord vnfr = new VnfRecord();
    vnfr.setDescriptorVersion("vnfr-schema-01");
    vnfr.setId(vnfd.getInstanceUuid());
    vnfr.setDescriptorReference(vnfd.getUuid());
    vnfr.setStatus(Status.offline);
    // vnfr.setDescriptorReferenceName(vnf.getName());
    // vnfr.setDescriptorReferenceVendor(vnf.getVendor());
    // vnfr.setDescriptorReferenceVersion(vnf.getVersion());

    for (VirtualDeploymentUnit vdu : vnfd.getVirtualDeploymentUnits()) {
      Logger.debug("Inspecting VDU " + vdu.getId());
      VduRecord vdur = new VduRecord();
      vdur.setId(vdu.getId());
      vdur.setNumberOfInstances(1);
      vdur.setVduReference(vnfd.getName() + ":" + vdu.getId());
      vdur.setVmImage(vdu.getVmImage());
      vdurTable.put(vdur.getVduReference(), vdur);
      vnfr.addVdu(vdur);
      Logger.debug("VDU table created: " + vduTable.toString());

      // HeatServer matchingServer = null;
      for (HeatServer server : composition.getServers()) {
        String[] identifiers = server.getServerName().split("\\.");
        String vnfName = identifiers[0];
        if(!vnfName.equals(vnfd.getName())){
          continue;
        }
        String vduName = identifiers[1];
        //String instanceId = identifiers[2];
        String vnfcIndex = identifiers[3];
        if (vdu.getId().equals(vduName)) {
          VnfcInstance vnfc = new VnfcInstance();
          vnfc.setId(vnfcIndex);
          vnfc.setVimId(data.getVimUuid());
          vnfc.setVcId(server.getServerId());
          ArrayList<ConnectionPointRecord> cpRecords = new ArrayList<ConnectionPointRecord>();
          for (ConnectionPoint cp : vdu.getConnectionPoints()) {
            Logger.debug("Mapping CP " + cp.getId());
            Logger.debug("Looking for port " + vnfd.getName() + "." + cp.getId() + "."
                + data.getServiceInstanceId());
            ConnectionPointRecord cpr = new ConnectionPointRecord();
            cpr.setId(cp.getId());


            // add each composition.ports information in the response. The IP, the netmask (and
            // maybe MAC address)
            boolean found = false;
            for (HeatPort port : composition.getPorts()) {
              Logger.debug("port " + port.getPortName());
              if (port.getPortName()
                  .equals(vnfd.getName() + "." + cp.getId() + "." + data.getServiceInstanceId())) {
                found = true;
                Logger.debug("Found! Filling VDUR parameters");
                InterfaceRecord ip = new InterfaceRecord();
                if (port.getFloatinIp() != null) {
                  ip.setAddress(port.getFloatinIp());
                  ip.setHardwareAddress(port.getMacAddress());
                  // Logger.info("Port:" + port.getPortName() + "- Addr: " +
                  // port.getFloatinIp());
                } else {
                  ip.setAddress(port.getIpAddress());
                  ip.setHardwareAddress(port.getMacAddress());
                  // Logger.info("Port:" + port.getPortName() + "- Addr: " +
                  // port.getFloatinIp());
                  ip.setNetmask("255.255.255.248");

                }
                cpr.setInterface(ip);
                cpr.setType(cp.getType());
                break;
              }
            }
            if (!found) {
              Logger.error("Can't find the VIM port that maps to this CP");
            }
            cpRecords.add(cpr);
          }
          vnfc.setConnectionPoints(cpRecords);
          VduRecord referenceVdur = vdurTable.get(vnfd.getName() + ":" + vdu.getId());
          referenceVdur.addVnfcInstance(vnfc);

        }
      }

    }

    response.setVnfrs(vnfr);
    String body = null;
    try {
      body = mapper.writeValueAsString(response);
    } catch (JsonProcessingException e) {
      Logger.error(e.getMessage());
      WrapperStatusUpdate update =
          new WrapperStatusUpdate(sid, "ERROR", "Exception during VNF Deployment");
      this.markAsChanged();
      this.notifyObservers(update);
      return;
    }
    Logger.info("Response created");
    // Logger.info("body");

    WrapperBay.getInstance().getVimRepo().writeFunctionInstanceEntry(vnfd.getInstanceUuid(),
        data.getServiceInstanceId(), this.getConfig().getUuid());
    WrapperStatusUpdate update = new WrapperStatusUpdate(sid, "SUCCESS", body);
    this.markAsChanged();
    this.notifyObservers(update);
    long stop = System.currentTimeMillis();
    
    Logger.info("[OpenStackWrapper]FunctionDeploy-time: " + (stop-start) + " ms");
  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#uploadImage(java.lang.String)
   */
  @Override
  public void uploadImage(VnfImage image) throws IOException {
    long start = System.currentTimeMillis();
    // TODO This values should be per User, now they are per VIM. This should be re-desinged once
    // user management is in place.
    JSONTokener tokener = new JSONTokener(getConfig().getConfiguration());
    JSONObject object = (JSONObject) tokener.nextValue();
    String tenant = object.getString("tenant");
    // END COMMENT

    OpenStackGlanceClient glance = new OpenStackGlanceClient(getConfig().getVimEndpoint().toString(),
        getConfig().getAuthUserName(), getConfig().getAuthPass(), tenant);

    Logger.debug("Creating new image: " + image.getUuid());
    String imageUuid = glance.createImage(image.getUuid());

    URL website = new URL(image.getUrl());
    String fileName = website.getPath().substring(website.getPath().lastIndexOf("/"));
    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
    String fileAbsolutePath = "/tmp/" + fileName;
    FileOutputStream fos = new FileOutputStream(fileAbsolutePath);
    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    fos.flush();
    fos.close();

    Logger.debug("Uploading new image from " + fileAbsolutePath);

    glance.uploadImage(imageUuid, fileAbsolutePath);


    File f = new File(fileAbsolutePath);
    if (f.delete()) {
      Logger.debug("temporary image file deleted succesfully from local environment.");
    } else {
      Logger.error("Error deleting the temporary image file " + fileName
          + " from local environment. Relevant VNF: " + image.getUuid());
      throw new IOException("Error deleting the temporary image file " + fileName
          + " from local environment. Relevant VNF: " + image.getUuid());
    }
    long stop = System.currentTimeMillis();
    
    Logger.info("[OpenStackWrapper]UploadImage-time: " + (stop-start) + " ms");
  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#isImageStored(java.lang.String)
   */
  @Override
  public boolean isImageStored(VnfImage image, String callSid) {
    long start = System.currentTimeMillis();
    // TODO This values should be per User, now they are per VIM. This should be re-desinged once
    // user management is in place.
    Logger.debug("Checking image: " + image.getUuid());
    JSONTokener tokener = new JSONTokener(getConfig().getConfiguration());
    JSONObject object = (JSONObject) tokener.nextValue();
    String tenant = object.getString("tenant");
    // END COMMENT

    
    OpenStackGlanceClient glance = null;
    try{
      glance = new OpenStackGlanceClient(getConfig().getVimEndpoint().toString(),
        getConfig().getAuthUserName(), getConfig().getAuthPass(), tenant);
    }catch (IOException e){
      Logger.error("OpenStackHeat wrapper - Unable to connect to the VIM");
      this.setChanged();
      WrapperStatusUpdate errorUpdate = new WrapperStatusUpdate(callSid, "ERROR", e.getMessage());
      this.notifyObservers(errorUpdate);
      return false;
    }
    ArrayList<Image> glanceImages = glance.listImages();

    for (Image glanceImage : glanceImages) {
      Logger.debug("Checking " + glanceImage.getName());
      if (glanceImage.getName().equals(image.getUuid())) {
        long stop = System.currentTimeMillis();
        Logger.info("[OpenStackWrapper]isImageStored-time: " + (stop-start) + " ms");
        return true;
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.vimadaptor.wrapper.ComputeWrapper#removeImage(java.lang.String)
   */
  @Override
  public void removeImage(VnfImage image) {
    // TODO Auto-generated method stub

  }

}
