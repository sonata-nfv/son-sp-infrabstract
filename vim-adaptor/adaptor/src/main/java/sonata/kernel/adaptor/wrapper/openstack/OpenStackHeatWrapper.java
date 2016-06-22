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

package sonata.kernel.adaptor.wrapper.openstack;

import sonata.kernel.adaptor.DeployServiceCallProcessor;
import sonata.kernel.adaptor.commons.DeployServiceData;
import sonata.kernel.adaptor.commons.heat.HeatModel;
import sonata.kernel.adaptor.commons.heat.HeatResource;
import sonata.kernel.adaptor.commons.heat.HeatTemplate;
import sonata.kernel.adaptor.commons.nsd.ConnectionPoint;
import sonata.kernel.adaptor.commons.nsd.NetworkFunction;
import sonata.kernel.adaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.adaptor.commons.nsd.VirtualLink;
import sonata.kernel.adaptor.commons.vnfd.VirtualDeploymentUnit;
import sonata.kernel.adaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.adaptor.commons.vnfd.VnfVirtualLink;
import sonata.kernel.adaptor.wrapper.ComputeWrapper;
import sonata.kernel.adaptor.wrapper.ResourceUtilisation;
import sonata.kernel.adaptor.wrapper.VimRepo;
import sonata.kernel.adaptor.wrapper.WrapperBay;
import sonata.kernel.adaptor.wrapper.WrapperConfiguration;
import sonata.kernel.adaptor.wrapper.WrapperStatusUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class OpenStackHeatWrapper extends ComputeWrapper {

  private WrapperConfiguration config;


  public OpenStackHeatWrapper(WrapperConfiguration config) {
    super();
    this.config = config;
  }

  @Override
  public boolean deployService(DeployServiceData data,
      DeployServiceCallProcessor startServiceCallProcessor) {



    this.addObserver(startServiceCallProcessor);

    OpenStackHeatClient client = new OpenStackHeatClient(config.getVimEndpoint().toString(),
        config.getAuthUserName(), config.getAuthPass(), config.getTenantName());

    OpenStackNovaClient nova_client = new OpenStackNovaClient(config.getVimEndpoint().toString(),
        config.getAuthUserName(), config.getAuthPass(), config.getTenantName());
    ArrayList<Flavor> vimFlavors = nova_client.getFlavors();
    HeatModel stack = translate(data, vimFlavors);
    HeatTemplate template = new HeatTemplate();
    for (HeatResource resource : stack.getResources()) {
      template.putResource(resource.getResourceName(), resource);
    }
    DeployServiceFsm fsm =
        new DeployServiceFsm(this, client, startServiceCallProcessor.getSid(), data, template);

    Thread thread = new Thread(fsm);
    thread.start();


    return true;

  }

  /**
   * Returns a heat template translated from the given descriptors.
   * 
   * @param data the service descriptors to translate
   * @return an HeatTemplate object translated from the given descriptors
   */
  public HeatTemplate getHeatTemplateFromSonataDescriptor(DeployServiceData data,
      ArrayList<Flavor> vimFlavors) {
    HeatModel model = this.translate(data, vimFlavors);
    HeatTemplate template = new HeatTemplate();
    for (HeatResource resource : model.getResources()) {
      template.putResource(resource.getResourceName(), resource);
    }
    return template;
  }

  private HeatModel translate(DeployServiceData data, ArrayList<Flavor> vimFlavors) {

    ServiceDescriptor nsd = data.getNsd();


    // Create the management Net and subnet for all the VNFCs and VNFs
    HeatResource mgmtNetwork = new HeatResource();
    mgmtNetwork.setType("OS::Neutron::Net");
    mgmtNetwork.setName(nsd.getName() + ":mgmt:net");
    mgmtNetwork.putProperty("name", nsd.getName() + ":mgmt:net");

    HeatModel model = new HeatModel();
    model.addResource(mgmtNetwork);

    HeatResource mgmtSubnet = new HeatResource();
    int subnetIndex = 0;

    mgmtSubnet.setType("OS::Neutron::Subnet");
    mgmtSubnet.setName(nsd.getName() + ":mgmt:subnet");
    mgmtSubnet.putProperty("name", nsd.getName() + ":mgmt:subnet");
    mgmtSubnet.putProperty("cidr", "192.168." + subnetIndex + ".0/24");
    mgmtSubnet.putProperty("gateway_ip", "192.168." + subnetIndex + ".1");
    subnetIndex++;
    HashMap<String, Object> mgmtNetMap = new HashMap<String, Object>();
    mgmtNetMap.put("get_resource", nsd.getName() + ":mgmt:net");
    mgmtSubnet.putProperty("network", mgmtNetMap);
    model.addResource(mgmtSubnet);


    // Internal mgmt router interface
    HeatResource mgmtRouterInterface = new HeatResource();
    mgmtRouterInterface.setType("OS::Neutron::RouterInterface");
    mgmtRouterInterface.setName(nsd.getName() + ":mgmt:internal");
    HashMap<String, Object> mgmtSubnetMapInt = new HashMap<String, Object>();
    mgmtSubnetMapInt.put("get_resource", nsd.getName() + ":mgmt:subnet");
    mgmtRouterInterface.putProperty("subnet", mgmtSubnetMapInt);
    mgmtRouterInterface.putProperty("router", this.config.getTenantExtRouter());
    model.addResource(mgmtRouterInterface);


    // One virtual router for NSD virtual links connecting VNFS (no router for external virtual
    // links and management links)
    // TODO how we connect to the tenant network?
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
        router.setName(nsd.getName() + ":" + link.getId());
        router.setType("OS::Neutron::Router");
        router.putProperty("name", nsd.getName() + ":" + link.getId());
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
          network.setName(vnfd.getName() + ":" + link.getId() + ":net");
          network.putProperty("name", vnfd.getName() + ":" + link.getId() + ":net");
          model.addResource(network);
          HeatResource subnet = new HeatResource();
          subnet.setType("OS::Neutron::Subnet");
          subnet.setName(vnfd.getName() + ":" + link.getId() + ":subnet");
          subnet.putProperty("name", vnfd.getName() + ":" + link.getId() + ":subnet");
          subnet.putProperty("cidr", "192.168." + subnetIndex + ".0/24");
          subnet.putProperty("gateway_ip", "192.168." + subnetIndex + ".1");
          subnetIndex++;
          HashMap<String, Object> netMap = new HashMap<String, Object>();
          netMap.put("get_resource", vnfd.getName() + ":" + link.getId() + ":net");
          subnet.putProperty("network", netMap);
          model.addResource(subnet);
        }
      }
      // One virtual machine for each VDU
      // TODO revise after seeing flavour definition in SON-SCHEMA

      for (VirtualDeploymentUnit vdu : vnfd.getVirtualDeploymentUnits()) {
        HeatResource server = new HeatResource();
        server.setType("OS::Nova::Server");
        server.setName(vnfd.getName() + ":" + vdu.getId());
        server.putProperty("name", vnfd.getName() + ":" + vdu.getId() + ":"
            + UUID.randomUUID().toString().substring(0, 4));
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
            port.setName(vnfd.getName() + ":" + cp.getId());
            port.putProperty("name", vnfd.getName() + ":" + cp.getId());
            HashMap<String, Object> netMap = new HashMap<String, Object>();
            netMap.put("get_resource", nsd.getName() + ":mgmt:net");
            port.putProperty("network", netMap);
            model.addResource(port);
            mgmtPortNames.add(vnfd.getName() + ":" + cp.getId());

            // add the port to the server
            HashMap<String, Object> n1 = new HashMap<String, Object>();
            HashMap<String, Object> portMap = new HashMap<String, Object>();
            portMap.put("get_resource", vnfd.getName() + ":" + cp.getId());
            n1.put("port", portMap);
            net.add(n1);
          } else if (linkIdReference != null) {
            HeatResource port = new HeatResource();
            port.setType("OS::Neutron::Port");
            port.setName(vnfd.getName() + ":" + cp.getId());
            port.putProperty("name", vnfd.getName() + ":" + cp.getId());
            HashMap<String, Object> netMap = new HashMap<String, Object>();
            netMap.put("get_resource", vnfd.getName() + ":" + linkIdReference + ":net");
            port.putProperty("network", netMap);

            model.addResource(port);
            // add the port to the server
            HashMap<String, Object> n1 = new HashMap<String, Object>();
            HashMap<String, Object> portMap = new HashMap<String, Object>();
            portMap.put("get_resource", vnfd.getName() + ":" + cp.getId());
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
          for (NetworkFunction vnf : nsd.getNetworkFunctions()) {
            if (vnf.getVnfName().equals(vnfd.getName())) {
              vnfId = vnf.getVnfId();
            }
          }

          boolean isInOut = false;
          String nsVirtualLink = null;
          for (VirtualLink link : nsd.getVirtualLinks()) {
            if (link.getConnectionPointsReference().contains(cp.getId().replace("vnf", vnfId))) {

              for (String cpRef : link.getConnectionPointsReference()) {
                if (cpRef.startsWith("ns:")) {
                  isInOut = true;
                  break;
                }
              }
              if (!isInOut) {
                nsVirtualLink = nsd.getName() + ":" + link.getId();
              }
              break;
            }
          }

          if (!isInOut) {
            HeatResource routerInterface = new HeatResource();
            routerInterface.setType("OS::Neutron::RouterInterface");
            routerInterface.setName(vnfd.getName() + ":" + cp.getId());
            for (VnfVirtualLink link : links) {
              if (link.getConnectionPointsReference().contains(cp.getId())) {
                HashMap<String, Object> subnetMap = new HashMap<String, Object>();
                subnetMap.put("get_resource", vnfd.getName() + ":" + link.getId() + ":subnet");
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
      floatingIp.setName("floating_" + portName);


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
  public boolean removeService(String instanceUuid) {

    VimRepo repo = WrapperBay.getInstance().getVimRepo();
    System.out.println("Trying to remove NS instance: " + instanceUuid);
    String stackName = repo.getServiceVimName(instanceUuid);
    String stackUuid = repo.getServiceVimUuid(instanceUuid);

    OpenStackHeatClient client = new OpenStackHeatClient(config.getVimEndpoint(),
        config.getAuthUserName(), config.getAuthPass(), config.getTenantName());

    String output = client.deleteStack(stackName, stackUuid);

    if (output.equals("DELETED")) {
      repo.removeInstanceEntry(instanceUuid);
      this.setChanged();
      String body = "{\"status\":\"SUCCESS\"}";
      WrapperStatusUpdate update = new WrapperStatusUpdate(null, "SUCCESS", body);
      this.notifyObservers(update);
    }


    return true;
  }

  @Override
  public ResourceUtilisation getResourceUtilisation() {
    
    OpenStackNovaClient client = new OpenStackNovaClient(config.getVimEndpoint(), config.getAuthUserName(), config.getAuthPass(), config.getTenantName());
    
    return client.getResourceUtilizasion();
  }



}
