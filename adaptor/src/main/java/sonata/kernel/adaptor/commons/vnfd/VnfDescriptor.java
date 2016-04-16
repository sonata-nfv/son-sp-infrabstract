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

package sonata.kernel.adaptor.commons.vnfd;


import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.adaptor.commons.nsd.ConnectionPoint;

import java.util.ArrayList;

public class VnfDescriptor {



  @JsonProperty("descriptor_version")
  private String descriptorVersion;
  private String vendor;
  private String name;
  private String version;
  private String author;
  private String description;
  @JsonProperty("virtual_deployment_units")
  private ArrayList<VirtualDeploymentUnit> virtualDeploymentUnits;
  @JsonProperty("connection_points")
  private ArrayList<ConnectionPoint> connectionPoints;
  @JsonProperty("virtual_links")
  private ArrayList<VnfVirtualLink> virtualLinks;
  @JsonProperty("deployment_flavors")
  private ArrayList<DeploymentFlavor> deploymentFlavors;
  @JsonProperty("lifecycle_events")
  private ArrayList<VnfLifeCycleEvent> lifecycleEvents;

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

  public ArrayList<VirtualDeploymentUnit> getVirtual_deployment_units() {
    return virtualDeploymentUnits;
  }

  public ArrayList<ConnectionPoint> getConnection_points() {
    return connectionPoints;
  }

  public ArrayList<VnfVirtualLink> getVirtual_links() {
    return virtualLinks;
  }

  public ArrayList<DeploymentFlavor> getDeployment_flavors() {
    return deploymentFlavors;
  }

  public ArrayList<VnfLifeCycleEvent> getLifecycle_events() {
    return lifecycleEvents;
  }

}
