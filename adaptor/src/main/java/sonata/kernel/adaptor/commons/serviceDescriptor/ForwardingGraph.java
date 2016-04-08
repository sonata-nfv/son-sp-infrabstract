
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

import java.util.ArrayList;

public class ForwardingGraph {

  // Forwarding Graph reference case.
  private String fg_group;
  private String fg_name;
  private String fg_version;
  private String fg_description;

  // Forwarding Graph description case.
  private String fg_id;
  private int number_of_endpoints;
  private int number_of_virtual_links;
  private ArrayList<String> depedent_virtual_links;
  private ArrayList<String> constituent_vnfs;
  private ArrayList<String> constituent_services;
  private ArrayList<NetworkForwardingPath> network_forwarding_paths;

  public String getFg_group() {
    return fg_group;
  }

  public String getFg_name() {
    return fg_name;
  }

  public String getFg_version() {
    return fg_version;
  }

  public String getFg_description() {
    return fg_description;
  }

  public String getFg_id() {
    return fg_id;
  }

  public int getNumber_of_endpoints() {
    return number_of_endpoints;
  }

  public int getNumber_of_virtual_links() {
    return number_of_virtual_links;
  }

  public ArrayList<String> getDepedent_virtual_links() {
    return depedent_virtual_links;
  }

  public ArrayList<String> getConstituent_vnfs() {
    return constituent_vnfs;
  }

  public ArrayList<String> getConstituent_services() {
    return constituent_services;
  }

  public ArrayList<NetworkForwardingPath> getNetwork_forwarding_paths() {
    return network_forwarding_paths;
  }

}
