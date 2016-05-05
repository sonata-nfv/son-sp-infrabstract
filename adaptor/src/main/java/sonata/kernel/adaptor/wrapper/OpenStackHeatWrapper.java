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

package sonata.kernel.adaptor.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import sonata.kernel.adaptor.StartServiceCallProcessor;
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

public class OpenStackHeatWrapper extends ComputeWrapper {

  public OpenStackHeatWrapper(WrapperConfiguration config) {
    super();
  }

  @Override
  public boolean deployService(DeployServiceData data,
      StartServiceCallProcessor startServiceCallProcessor) {
    return false;
  }

  public HeatTemplate getHeatTemplateFromSonataDescriptor(DeployServiceData data){
    HeatModel model = this.translate(data);
    HeatTemplate template = new HeatTemplate();
    for (HeatResource resource : model.getResources()) {
      template.putResource(resource.getResourceName(), resource);
    }
    return template;
  }
  
  private HeatModel translate(DeployServiceData data) {

    ServiceDescriptor nsd = data.getNsd();
    ArrayList<VnfDescriptor> vnfs = data.getVnfdList();

    HeatModel model = new HeatModel();
    int subnetIndex=0;
    // One virtual router for NSD virtual links
    // TODO how we connect to the tenant network?
    for (VirtualLink link : nsd.getVirtual_links()) {
      HeatResource router = new HeatResource();
      router.setName(link.getId());
      router.setType("OS::Neutron::Router");
      router.putProperty("name", link.getId());
      model.addResource(router);
    }

    for (VnfDescriptor vnfd : vnfs) {
      // One network and subnet for vnf virtual link
      ArrayList<VnfVirtualLink> links = vnfd.getVirtual_links();
      for (VnfVirtualLink link : links) {
        HeatResource network = new HeatResource();
        network.setType("OS::Neutron::Net");
        network.setName( vnfd.getName() + ":" + link.getId() + ":net");
        network.putProperty("name", link.getId());
        model.addResource(network);
        HeatResource subnet = new HeatResource();
        subnet.setType("OS::Neutron::Subnet");
        subnet.setName( vnfd.getName() + ":" + link.getId() + ":subnet");
        subnet.putProperty("name", link.getId());
        subnet.putProperty("cidr", "10.10."+subnetIndex+".0/24");
        subnet.putProperty("gateway_ip", "10.10."+subnetIndex+".1");
        subnetIndex++;
        HashMap<String, Object> netMap = new HashMap<String, Object>();
        netMap.put("get_resource",
             vnfd.getName() + ":" + link.getId() + ":net");
        subnet.putProperty("network", netMap);
        model.addResource(subnet);
      }
      // One virtual machine for each VDU
      for (VirtualDeploymentUnit vdu : vnfd.getVirtual_deployment_units()) {
        HeatResource server = new HeatResource();
        server.setType("OS::Nova::Server");
        server.setName(vnfd.getName() + ":" + vdu.getId());
        server.putProperty("name",  vnfd.getName() + ":" + vdu.getId() + ":"
            + UUID.randomUUID().toString().substring(0, 4));
        server.putProperty("image", vdu.getVm_image());
        server.putProperty("flavor", "m1.small");
        ArrayList<HashMap<String, Object>> net = new ArrayList<HashMap<String, Object>>();
        for (ConnectionPoint cp : vdu.getConnection_points()) {
          // create the port resource
          HeatResource port = new HeatResource();
          port.setType("OS::Neutron::Port");
          port.setName(vnfd.getName() + ":" + cp.getId());
          port.putProperty("name",  cp.getId());
          for (VnfVirtualLink link : links) {
            if (link.getConnection_points_reference().contains(cp.getId())) {
              HashMap<String, Object> netMap = new HashMap<String, Object>();
              netMap.put("get_resource",
                   vnfd.getName() + ":" + link.getId() + ":net");
              port.putProperty("network", netMap);
              break;
            }
          }
          model.addResource(port);
          // add the port to the server
          HashMap<String, Object> n1 = new HashMap<String, Object>();
          HashMap<String, Object> portMap = new HashMap<String, Object>();
          portMap.put("get_resource", vnfd.getName() + ":" + cp.getId());
          n1.put("port", portMap);
          net.add(n1);
        }
        server.putProperty("networks", net);
        model.addResource(server);
      }

      // One Router interface per VNF cp
      for (ConnectionPoint cp : vnfd.getConnection_points()) {
        HeatResource routerInterface = new HeatResource();
        routerInterface.setType("OS::Neutron::RouterInterface");
        routerInterface.setName(vnfd.getName() + ":" + cp.getId());
        for (VnfVirtualLink link : links) {
          if (link.getConnection_points_reference().contains(cp.getId())) {
            HashMap<String, Object> subnetMap = new HashMap<String, Object>();
            subnetMap.put("get_resource",
                 vnfd.getName() + ":" + link.getId() + ":subnet");
            routerInterface.putProperty("subnet", subnetMap);
            break;
          }
        }
        // Resolve vnf_id from vnf_name
        String vnfId = null;
        for (NetworkFunction vnf : nsd.getNetwork_functions()) {
          if (vnf.getVnf_name().equals(vnfd.getName())) {
            vnfId = vnf.getVnf_id();
          }
        }
        // Attach to the virtual router
        for (VirtualLink link : nsd.getVirtual_links()) {
          if (link.getConnection_points_reference().contains(cp.getId().replace("vnf", vnfId))) {
            HashMap<String, Object> routerMap = new HashMap<String, Object>();
            routerMap.put("get_resource", link.getId());
            routerInterface.putProperty("router", routerMap);
            break;
          }

        }
        model.addResource(routerInterface);
      }

    }

    model.prepare();
    return model;
  }
}
