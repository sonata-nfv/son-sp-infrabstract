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

import sonata.kernel.adaptor.commons.serviceDescriptor.VirtualLink.ConnectivityType;

public class VNFVirtualLink {


  private String id;
  private ConnectivityType connectivity_type;
  private ArrayList<String> connection_points_reference;
  private boolean access;
  private boolean external_access;
  private String root_requirement;
  private String leaf_requirement;
  private boolean dhcp;
  private String qos;

  public String getId() {
    return id;
  }

  public ConnectivityType getConnectivity_type() {
    return connectivity_type;
  }

  public ArrayList<String> getConnection_points_reference() {
    return connection_points_reference;
  }

  public boolean isAccess() {
    return access;
  }

  public boolean isExternal_access() {
    return external_access;
  }

  public String getRoot_requirement() {
    return root_requirement;
  }

  public String getLeaf_requirement() {
    return leaf_requirement;
  }

  public boolean isDhcp() {
    return dhcp;
  }

  public String getQos() {
    return qos;
  }
}
