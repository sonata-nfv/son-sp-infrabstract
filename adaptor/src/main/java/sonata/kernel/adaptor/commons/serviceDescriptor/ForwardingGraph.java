
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

import java.util.ArrayList;

public class ForwardingGraph {

  // Forwarding Graph reference case.
  @JsonProperty("fg_group")
  private String fgGroup;
  @JsonProperty("fg_name")
  private String fgName;
  @JsonProperty("fg_version")
  private String fgVersion;
  @JsonProperty("fg_description")
  private String fgDescription;

  // Forwarding Graph description case.
  @JsonProperty("fg_id")
  private String fgId;
  @JsonProperty("number_of_endpoints")
  private int numberOfEndpoints;
  @JsonProperty("number_of_virtual_links")
  private int numberOfVirtualLinks;
  @JsonProperty("dependent_virtual_links")
  private ArrayList<String> dependentVirtualLinks;
  @JsonProperty("constituent_vnfs")
  private ArrayList<String> constituentVnfs;
  @JsonProperty("constituent_services")
  private ArrayList<String> constituentServices;
  @JsonProperty("network_forwarding_paths")
  private ArrayList<NetworkForwardingPath> networkForwardingPaths;

  public String getFg_group() {
    return fgGroup;
  }

  public String getFg_name() {
    return fgName;
  }

  public String getFg_version() {
    return fgVersion;
  }

  public String getFg_description() {
    return fgDescription;
  }

  public String getFg_id() {
    return fgId;
  }

  public int getNumber_of_endpoints() {
    return numberOfEndpoints;
  }

  public int getNumber_of_virtual_links() {
    return numberOfVirtualLinks;
  }

  public ArrayList<String> getDepedent_virtual_links() {
    return dependentVirtualLinks;
  }

  public ArrayList<String> getConstituent_vnfs() {
    return constituentVnfs;
  }

  public ArrayList<String> getConstituent_services() {
    return constituentServices;
  }

  public ArrayList<NetworkForwardingPath> getNetwork_forwarding_paths() {
    return networkForwardingPaths;
  }

}
