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

package sonata.kernel.adaptor.commons.nsd;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class VirtualLink {


  public enum ConnectivityType {
    E_LINE("E-Line"), E_TREE("E-Tree"), E_LAN("E-LAN");

    private final String name;

    ConnectivityType(String name) {
      this.name = name;
    }

    public String toString() {
      return this.name;
    }
  }


  // Virtual Link reference case
  @JsonProperty("vl_group")
  private String vlGroup;
  @JsonProperty("vl_name")
  private String vlName;
  @JsonProperty("vl_version")
  private String vlVersion;
  @JsonProperty("vl_description")
  private String vlDescription;

  // Virtual Link description case;

  private String id;
  @JsonProperty("connectivity_type")
  private ConnectivityType connectivityType;
  @JsonProperty("connection_points_reference")
  private ArrayList<String> connectionPointsReference;
  private boolean access;
  @JsonProperty("external_access")
  private boolean externalAccess;
  @JsonProperty("root_requirement")
  private String rootRequirement;
  @JsonProperty("leaf_requirement")
  private String leafRequirement;
  private boolean dhcp;
  private String qos;

  public String getVl_group() {
    return vlGroup;
  }

  public String getVl_name() {
    return vlName;
  }

  public String getVl_version() {
    return vlVersion;
  }

  public String getVl_description() {
    return vlDescription;
  }

  public String getId() {
    return id;
  }

  public ConnectivityType getConnectivity_type() {
    return connectivityType;
  }

  public ArrayList<String> getConnection_points_reference() {
    return connectionPointsReference;
  }

  public boolean isAccess() {
    return access;
  }

  public boolean isExternal_access() {
    return externalAccess;
  }

  public String getRoot_requirement() {
    return rootRequirement;
  }

  public String getLeaf_requirement() {
    return leafRequirement;
  }

  public boolean isDhcp() {
    return dhcp;
  }

  public String getQos() {
    return qos;
  }

  public String getVlGroup() {
    return vlGroup;
  }

  public String getVlName() {
    return vlName;
  }

  public String getVlVersion() {
    return vlVersion;
  }

  public String getVlDescription() {
    return vlDescription;
  }

  public ConnectivityType getConnectivityType() {
    return connectivityType;
  }

  public ArrayList<String> getConnectionPointsReference() {
    return connectionPointsReference;
  }

  public boolean isExternalAccess() {
    return externalAccess;
  }

  public String getRootRequirement() {
    return rootRequirement;
  }

  public String getLeafRequirement() {
    return leafRequirement;
  }

  public void setVlGroup(String vlGroup) {
    this.vlGroup = vlGroup;
  }

  public void setVlName(String vlName) {
    this.vlName = vlName;
  }

  public void setVlVersion(String vlVersion) {
    this.vlVersion = vlVersion;
  }

  public void setVlDescription(String vlDescription) {
    this.vlDescription = vlDescription;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setConnectivityType(ConnectivityType connectivityType) {
    this.connectivityType = connectivityType;
  }

  public void setConnectionPointsReference(ArrayList<String> connectionPointsReference) {
    this.connectionPointsReference = connectionPointsReference;
  }

  public void setAccess(boolean access) {
    this.access = access;
  }

  public void setExternalAccess(boolean externalAccess) {
    this.externalAccess = externalAccess;
  }

  public void setRootRequirement(String rootRequirement) {
    this.rootRequirement = rootRequirement;
  }

  public void setLeafRequirement(String leafRequirement) {
    this.leafRequirement = leafRequirement;
  }

  public void setDhcp(boolean dhcp) {
    this.dhcp = dhcp;
  }

  public void setQos(String qos) {
    this.qos = qos;
  }


}
