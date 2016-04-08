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
  private String vm_image;
  private VmFormat vm_image_format;
  private String vm_image_md5;
  private ResourceRequirements resource_requirements;
  private ArrayList<ConnectionPoint> connection_points;
  private ArrayList<VDUMonitoringParameter> monitoring_parameters;
  private ScaleInOut scale_in_out;


  public String getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }

  public String getVm_image() {
    return vm_image;
  }

  public VmFormat getVm_image_format() {
    return vm_image_format;
  }

  public String getVm_image_md5() {
    return vm_image_md5;
  }

  public ResourceRequirements getResource_requirements() {
    return resource_requirements;
  }

  public ArrayList<ConnectionPoint> getConnection_points() {
    return connection_points;
  }

  public ArrayList<VDUMonitoringParameter> getMonitoring_parameters() {
    return monitoring_parameters;
  }

  public ScaleInOut getScale_in_out() {
    return scale_in_out;
  }


}
