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
 * @author Dario Valocchi(Ph.D.), UCL
 * @author Guy Paz, Nokia
 * 
 */

package sonata.kernel.VimAdaptor.wrapper.openstack;

import org.slf4j.LoggerFactory;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.IpNetPool;
import sonata.kernel.VimAdaptor.commons.heat.HeatModel;
import sonata.kernel.VimAdaptor.commons.heat.HeatResource;
import sonata.kernel.VimAdaptor.commons.heat.HeatTemplate;
import sonata.kernel.VimAdaptor.commons.nsd.ConnectionPoint;
import sonata.kernel.VimAdaptor.commons.nsd.NetworkFunction;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.nsd.VirtualLink;
import sonata.kernel.VimAdaptor.commons.vnfd.VirtualDeploymentUnit;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfVirtualLink;
import sonata.kernel.VimAdaptor.wrapper.ComputeWrapper;
import sonata.kernel.VimAdaptor.wrapper.ResourceUtilisation;
import sonata.kernel.VimAdaptor.wrapper.VimRepo;
import sonata.kernel.VimAdaptor.wrapper.WrapperBay;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.VimAdaptor.wrapper.WrapperStatusUpdate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class OpenStackHeatWrapper extends ComputeWrapper {

  private WrapperConfiguration config;
  private IpNetPool myPool;

  private static final org.slf4j.Logger Logger =
      LoggerFactory.getLogger(OpenStackHeatWrapper.class);

  /**
   * Standard constructor for an Compute Wrapper of an OpenStack VIM using Heat.
   * 
   * @param config the config object for this Compute Wrapper
   */
  public OpenStackHeatWrapper(WrapperConfiguration config) {
    super();
    this.config = config;
    this.myPool = IpNetPool.getInstance();
  }

  @Override
  public boolean deployService(DeployServiceData data, String callSid) {

    OpenStackHeatClient client = new OpenStackHeatClient(config.getVimEndpoint().toString(),
        config.getAuthUserName(), config.getAuthPass(), config.getTenantName());

    OpenStackNovaClient novaClient = new OpenStackNovaClient(config.getVimEndpoint().toString(),
        config.getAuthUserName(), config.getAuthPass(), config.getTenantName());
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
   * Returns a heat template translated from the given descriptors.
   * 
   * @param data the service descriptors to translate
   * @param vimFlavors the list of available compute flavors
   * @return an HeatTemplate object translated from the given descriptors
   * @throws Exception if unable to translate the descriptor.
   */
  public HeatTemplate getHeatTemplateFromSonataDescriptor(DeployServiceData data,
      ArrayList<Flavor> vimFlavors) throws Exception {
    HeatModel model = this.translate(data, vimFlavors);
    HeatTemplate template = new HeatTemplate();
    for (HeatResource resource : model.getResources()) {
      template.putResource(resource.getResourceName(), resource);
    }
    return template;
  }

  private HeatModel translate(DeployServiceData data, ArrayList<Flavor> vimFlavors)
      throws Exception {

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
    mgmtNetwork.setName(nsd.getName() + ":mgmt:net:" + nsd.getInstanceUuid());
    mgmtNetwork.putProperty("name", nsd.getName() + ":mgmt:net:" + nsd.getInstanceUuid());



    HeatModel model = new HeatModel();
    model.addResource(mgmtNetwork);

    HeatResource mgmtSubnet = new HeatResource();

    mgmtSubnet.setType("OS::Neutron::Subnet");
    mgmtSubnet.setName(nsd.getName() + ":mgmt:subnet:" + nsd.getInstanceUuid());
    mgmtSubnet.putProperty("name", nsd.getName() + ":mgmt:subnet:" + nsd.getInstanceUuid());
    String cidr = subnets.get(subnetIndex);
    mgmtSubnet.putProperty("cidr", cidr);
    mgmtSubnet.putProperty("gateway_ip", myPool.getGateway(cidr));

    // mgmtSubnet.putProperty("cidr", "192.168." + subnetIndex + ".0/24");
    // mgmtSubnet.putProperty("gateway_ip", "192.168." + subnetIndex + ".1");
    subnetIndex++;
    HashMap<String, Object> mgmtNetMap = new HashMap<String, Object>();
    mgmtNetMap.put("get_resource", nsd.getName() + ":mgmt:net:" + nsd.getInstanceUuid());
    mgmtSubnet.putProperty("network", mgmtNetMap);
    model.addResource(mgmtSubnet);


    // Internal mgmt router interface
    HeatResource mgmtRouterInterface = new HeatResource();
    mgmtRouterInterface.setType("OS::Neutron::RouterInterface");
    mgmtRouterInterface.setName(nsd.getName() + ":mgmt:internal:" + nsd.getInstanceUuid());
    HashMap<String, Object> mgmtSubnetMapInt = new HashMap<String, Object>();
    mgmtSubnetMapInt.put("get_resource", nsd.getName() + ":mgmt:subnet:" + nsd.getInstanceUuid());
    mgmtRouterInterface.putProperty("subnet", mgmtSubnetMapInt);
    mgmtRouterInterface.putProperty("router", this.config.getTenantExtRouter());
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
        router.setName(nsd.getName() + ":" + link.getId() + ":" + nsd.getInstanceUuid());
        router.setType("OS::Neutron::Router");
        router.putProperty("name",
            nsd.getName() + ":" + link.getId() + ":" + nsd.getInstanceUuid());
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
          network.setName(vnfd.getName() + ":" + link.getId() + ":net:" + nsd.getInstanceUuid());
          network.putProperty("name",
              vnfd.getName() + ":" + link.getId() + ":net:" + nsd.getInstanceUuid());
          model.addResource(network);
          HeatResource subnet = new HeatResource();
          subnet.setType("OS::Neutron::Subnet");
          subnet.setName(vnfd.getName() + ":" + link.getId() + ":subnet:" + nsd.getInstanceUuid());
          subnet.putProperty("name",
              vnfd.getName() + ":" + link.getId() + ":subnet:" + nsd.getInstanceUuid());
          cidr = subnets.get(subnetIndex);
          subnet.putProperty("cidr", cidr);
          // subnet.putProperty("gateway_ip", myPool.getGateway(cidr));
          // subnet.putProperty("cidr", "192.168." + subnetIndex + ".0/24");
          // subnet.putProperty("gateway_ip", "192.168." + subnetIndex + ".1");
          subnetIndex++;
          HashMap<String, Object> netMap = new HashMap<String, Object>();
          netMap.put("get_resource",
              vnfd.getName() + ":" + link.getId() + ":net:" + nsd.getInstanceUuid());
          subnet.putProperty("network", netMap);
          model.addResource(subnet);
        }
      }
      // One virtual machine for each VDU
      // TODO revise after seeing flavour definition in SON-SCHEMA

      for (VirtualDeploymentUnit vdu : vnfd.getVirtualDeploymentUnits()) {
        HeatResource server = new HeatResource();
        server.setType("OS::Nova::Server");
        server.setName(vnfd.getName() + ":" + vdu.getId() + ":" + nsd.getInstanceUuid());
        server.putProperty("name",
            vnfd.getName() + ":" + vdu.getId() + ":" + nsd.getInstanceUuid());
        server.putProperty("image", vdu.getVmImage());
        int vcpu = vdu.getResourceRequirements().getCpu().getVcpus();
        double memory = vdu.getResourceRequirements().getMemory().getSize();
        double storage = vdu.getResourceRequirements().getStorage().getSize();
        String flavorName = this.selectFlavor(vcpu, memory, storage, vimFlavors);
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
            port.setName(vnfd.getName() + ":" + cp.getId() + ":" + nsd.getInstanceUuid());
            port.putProperty("name",
                vnfd.getName() + ":" + cp.getId() + ":" + nsd.getInstanceUuid());
            HashMap<String, Object> netMap = new HashMap<String, Object>();
            netMap.put("get_resource", nsd.getName() + ":mgmt:net:" + nsd.getInstanceUuid());
            port.putProperty("network", netMap);
            model.addResource(port);
            mgmtPortNames.add(vnfd.getName() + ":" + cp.getId() + ":" + nsd.getInstanceUuid());

            // add the port to the server
            HashMap<String, Object> n1 = new HashMap<String, Object>();
            HashMap<String, Object> portMap = new HashMap<String, Object>();
            portMap.put("get_resource",
                vnfd.getName() + ":" + cp.getId() + ":" + nsd.getInstanceUuid());
            n1.put("port", portMap);
            net.add(n1);
          } else if (linkIdReference != null) {
            HeatResource port = new HeatResource();
            port.setType("OS::Neutron::Port");
            port.setName(vnfd.getName() + ":" + cp.getId() + ":" + nsd.getInstanceUuid());
            port.putProperty("name",
                vnfd.getName() + ":" + cp.getId() + ":" + nsd.getInstanceUuid());
            HashMap<String, Object> netMap = new HashMap<String, Object>();
            netMap.put("get_resource",
                vnfd.getName() + ":" + linkIdReference + ":net:" + nsd.getInstanceUuid());
            port.putProperty("network", netMap);

            model.addResource(port);
            // add the port to the server
            HashMap<String, Object> n1 = new HashMap<String, Object>();
            HashMap<String, Object> portMap = new HashMap<String, Object>();
            portMap.put("get_resource",
                vnfd.getName() + ":" + cp.getId() + ":" + nsd.getInstanceUuid());
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
                nsVirtualLink = nsd.getName() + ":" + link.getId() + ":" + nsd.getInstanceUuid();
              }
              break;
            }
          }
          if (!isVirtualLinkFound) {
            throw new Exception("Error binding VNFD.connection_point:"
                + " Cannot find NSD.virtual_link attached to VNFD.connection_point."
                + " VNFD.connection_point = " + vnfd.getName() + ":" + cp.getId());
          }
          if (!isInOut) {
            HeatResource routerInterface = new HeatResource();
            routerInterface.setType("OS::Neutron::RouterInterface");
            routerInterface
                .setName(vnfd.getName() + ":" + cp.getId() + ":" + nsd.getInstanceUuid());

            for (VnfVirtualLink link : links) {
              if (link.getConnectionPointsReference().contains(cp.getId())) {
                HashMap<String, Object> subnetMap = new HashMap<String, Object>();
                subnetMap.put("get_resource",
                    vnfd.getName() + ":" + link.getId() + ":subnet:" + nsd.getInstanceUuid());
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


      floatingIp.putProperty("floating_network_id", this.config.getTenantExtNet());

      HashMap<String, Object> floatMapPort = new HashMap<String, Object>();
      floatMapPort.put("get_resource", portName);
      floatingIp.putProperty("port_id", floatMapPort);

      model.addResource(floatingIp);
    }
    model.prepare();
    return model;
  }

  private String selectFlavor(int vcpu, double memory, double storage,
      ArrayList<Flavor> vimFlavors) {
    // TODO Implement a method to select the best flavor respecting the resource constraints.
    for (Flavor flavor : vimFlavors) {
      if (vcpu <= flavor.getVcpu() && (memory * 1024) <= flavor.getRam()
          && storage <= flavor.getStorage()) {
        return flavor.getFlavorName();
      }
    }
    return "ERROR";
  }



  @Override
  public boolean removeService(String instanceUuid, String callSid) {

    VimRepo repo = WrapperBay.getInstance().getVimRepo();
    Logger.info("Trying to remove NS instance: " + instanceUuid);
    String stackName = repo.getServiceVimName(instanceUuid);
    String stackUuid = repo.getServiceVimUuid(instanceUuid);
    Logger.info("NS instance mapped to stack name: " + stackName);
    Logger.info("NS instance mapped to stack uuid: " + stackUuid);

    OpenStackHeatClient client = new OpenStackHeatClient(config.getVimEndpoint(),
        config.getAuthUserName(), config.getAuthPass(), config.getTenantName());
    try {
      String output = client.deleteStack(stackName, stackUuid);

      if (output.equals("DELETED")) {
        repo.removeInstanceEntry(instanceUuid);
        myPool.freeSubnets(instanceUuid);
        this.setChanged();
        String body = "SUCCESS";
        WrapperStatusUpdate update = new WrapperStatusUpdate(null, "SUCCESS", body);
        this.notifyObservers(update);
      }
    } catch (Exception e) {
      this.setChanged();
      WrapperStatusUpdate errorUpdate = new WrapperStatusUpdate(callSid, "ERROR", e.getMessage());
      this.notifyObservers(errorUpdate);
      return false;
    }

    return true;
  }

  @Override
  public ResourceUtilisation getResourceUtilisation() {

    OpenStackNovaClient client = new OpenStackNovaClient(config.getVimEndpoint(),
        config.getAuthUserName(), config.getAuthPass(), config.getTenantName());

    return client.getResourceUtilizasion();
  }



}
