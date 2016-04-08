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
package sonata.kernel.adaptor.commons;

import java.util.ArrayList;


public class ServiceDescriptor {


  private String descriptor_version;
  private String vendor;
  private String name;
  private String version;
  private String author;
  private String description;
  private ArrayList<NetworkFunction> network_functions;
  private ArrayList<String> network_services;
  private ArrayList<ConnectionPoint> connection_points;
  private ArrayList<VirtualLink> virtual_links;
  private ArrayList<ForwardingGraph> forwarding_graphs;
  private LifeCycleEvent lifecycle_events;
  private ArrayList<String> vnf_depencency;
  private ArrayList<String> services_dependency;
  private ArrayList<MonitoringParameter> monitoring_parameters;
  private AutoScalePolicy auto_scale_policy;


  public String getDescriptor_version() {
    return descriptor_version;
  }

  public String getVendor() {
    return vendor;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public String getAuthor() {
    return author;
  }

  public String getDescription() {
    return description;
  }

  public ArrayList<NetworkFunction> getNetwork_functions() {
    return network_functions;
  }

  public ArrayList<String> getNetwork_services() {
    return network_services;
  }

  public ArrayList<ConnectionPoint> getConnection_points() {
    return connection_points;
  }

  public ArrayList<VirtualLink> getVirtual_links() {
    return virtual_links;
  }

  public ArrayList<ForwardingGraph> getForwarding_graphs() {
    return forwarding_graphs;
  }

  public LifeCycleEvent getLifecycle_events() {
    return lifecycle_events;
  }

  public ArrayList<String> getVnf_depencency() {
    return vnf_depencency;
  }

  public ArrayList<String> getServices_dependency() {
    return services_dependency;
  }

  public ArrayList<MonitoringParameter> getMonitoring_Parameters() {
    return monitoring_parameters;
  }

  public AutoScalePolicy getAuto_scale_policy() {
    return auto_scale_policy;
  }



}
