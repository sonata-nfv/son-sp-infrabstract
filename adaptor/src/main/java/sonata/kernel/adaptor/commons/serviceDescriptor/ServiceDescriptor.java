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

package sonata.kernel.adaptor.commons.serviceDescriptor;

import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.adaptor.commons.vnfDescriptor.AutoScalePolicy;

import java.util.ArrayList;

public class ServiceDescriptor {

  @JsonProperty("descriptor_version")
  private String descriptorVersion;
  private String vendor;
  private String name;
  private String version;
  private String author;
  private String description;
  @JsonProperty("network_functions")
  private ArrayList<NetworkFunction> networkFunctions;
  @JsonProperty("network_services")
  private ArrayList<String> networkServices;
  @JsonProperty("connection_points")
  private ArrayList<ConnectionPoint> connectionPoints;
  @JsonProperty("virtual_links")
  private ArrayList<VirtualLink> virtualLinks;
  @JsonProperty("forwarding_graphs")
  private ArrayList<ForwardingGraph> forwardingGraphs;
  @JsonProperty("lifecycle_events")
  private LifeCycleEvent lifecycleEvents;
  @JsonProperty("vnf_depencency")
  private ArrayList<String> vnfDepencency;
  @JsonProperty("services_dependency")
  private ArrayList<String> servicesDependency;
  @JsonProperty("monitoring_parameters")
  private ArrayList<MonitoringParameter> monitoringParameters;
  @JsonProperty("auto_scale_policy")
  private AutoScalePolicy autoScalePolicy;

  
  public String getDescriptor_version() {
    return descriptorVersion;
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
    return networkFunctions;
  }

  public ArrayList<String> getNetwork_services() {
    return networkServices;
  }

  public ArrayList<ConnectionPoint> getConnection_points() {
    return connectionPoints;
  }

  public ArrayList<VirtualLink> getVirtual_links() {
    return virtualLinks;
  }

  public ArrayList<ForwardingGraph> getForwarding_graphs() {
    return forwardingGraphs;
  }

  public LifeCycleEvent getLifecycle_events() {
    return lifecycleEvents;
  }

  public ArrayList<String> getVnf_depencency() {
    return vnfDepencency;
  }

  public ArrayList<String> getServices_dependency() {
    return servicesDependency;
  }

  public ArrayList<MonitoringParameter> getMonitoring_Parameters() {
    return monitoringParameters;
  }

  public AutoScalePolicy getAuto_scale_policy() {
    return autoScalePolicy;
  }



}
