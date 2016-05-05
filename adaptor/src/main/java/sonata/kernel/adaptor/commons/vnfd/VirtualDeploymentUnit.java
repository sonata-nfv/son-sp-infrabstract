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

public class VirtualDeploymentUnit {

  public enum VmFormat {
    RAW("raw"), VHD("vhd"), VMDK("vmdk"), VDI("vdi"), ISO("iso"), QCOW2("qcow2"), DOCKER(
        "docker"), OVA("ova"), OVF("ovf"), BARE("bare");

    private final String name;

    VmFormat(String name) {
      this.name = name;
    }

    public String toString() {
      return this.name;
    }

  }

  private String description;
  private String id;
  @JsonProperty("vm_image")
  private String vmImage;
  @JsonProperty("vm_image_format")
  private VmFormat vmImageFormat;
  @JsonProperty("vm_image_md5")
  private String vmImageMd5;
  @JsonProperty("resource_requirements")
  private ResourceRequirements resourceRequirements;
  @JsonProperty("connection_points")
  private ArrayList<ConnectionPoint> connectionPoints;
  @JsonProperty("monitoring_parameters")
  private ArrayList<VduMonitoringParameter> monitoringParameters;
  @JsonProperty("scale_in_out")
  private ScaleInOut scaleInOut;


  public String getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }

  public String getVm_image() {
    return vmImage;
  }

  public VmFormat getVm_image_format() {
    return vmImageFormat;
  }

  public String getVm_image_md5() {
    return vmImageMd5;
  }

  public ResourceRequirements getResource_requirements() {
    return resourceRequirements;
  }

  public ArrayList<ConnectionPoint> getConnection_points() {
    return connectionPoints;
  }

  public ArrayList<VduMonitoringParameter> getMonitoring_parameters() {
    return monitoringParameters;
  }

  public ScaleInOut getScale_in_out() {
    return scaleInOut;
  }

  public String getVmImage() {
    return vmImage;
  }

  public VmFormat getVmImageFormat() {
    return vmImageFormat;
  }

  public String getVmImageMd5() {
    return vmImageMd5;
  }

  public ResourceRequirements getResourceRequirements() {
    return resourceRequirements;
  }

  public ArrayList<ConnectionPoint> getConnectionPoints() {
    return connectionPoints;
  }

  public ArrayList<VduMonitoringParameter> getMonitoringParameters() {
    return monitoringParameters;
  }

  public ScaleInOut getScaleInOut() {
    return scaleInOut;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setVmImage(String vmImage) {
    this.vmImage = vmImage;
  }

  public void setVmImageFormat(VmFormat vmImageFormat) {
    this.vmImageFormat = vmImageFormat;
  }

  public void setVmImageMd5(String vmImageMd5) {
    this.vmImageMd5 = vmImageMd5;
  }

  public void setResourceRequirements(ResourceRequirements resourceRequirements) {
    this.resourceRequirements = resourceRequirements;
  }

  public void setConnectionPoints(ArrayList<ConnectionPoint> connectionPoints) {
    this.connectionPoints = connectionPoints;
  }

  public void setMonitoringParameters(ArrayList<VduMonitoringParameter> monitoringParameters) {
    this.monitoringParameters = monitoringParameters;
  }

  public void setScaleInOut(ScaleInOut scaleInOut) {
    this.scaleInOut = scaleInOut;
  }


}
