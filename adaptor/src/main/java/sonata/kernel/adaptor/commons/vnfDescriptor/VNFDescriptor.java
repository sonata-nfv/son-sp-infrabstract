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
package sonata.kernel.adaptor.commons.vnfDescriptor;

import java.util.ArrayList;

import sonata.kernel.adaptor.commons.serviceDescriptor.ConnectionPoint;

public class VNFDescriptor {



  private String descriptor_version;
  private String vendor;
  private String name;
  private String version;
  private String author;
  private String description;
  private ArrayList<VirtualDeploymentUnit> virtual_deployment_units;
  private ArrayList<ConnectionPoint> connection_points;
  private ArrayList<VNFVirtualLink> virtual_links;
  private ArrayList<DeploymentFlavor> deployment_flavors;
  private ArrayList<VNFLifeCycleEvent> lifecycle_events;

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

  public ArrayList<VirtualDeploymentUnit> getVirtual_deployment_units() {
    return virtual_deployment_units;
  }

  public ArrayList<ConnectionPoint> getConnection_points() {
    return connection_points;
  }

  public ArrayList<VNFVirtualLink> getVirtual_links() {
    return virtual_links;
  }

  public ArrayList<DeploymentFlavor> getDeployment_flavors() {
    return deployment_flavors;
  }

  public ArrayList<VNFLifeCycleEvent> getLifecycle_events() {
    return lifecycle_events;
  }

}
