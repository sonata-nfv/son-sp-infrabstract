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

package sonata.kernel.WimAdaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.WimAdaptor.commons.vnfd.ResourceRequirements;

import java.util.ArrayList;

public class VduRecord {

  private String id;
  @JsonProperty("vm_image")
  private String vmImage;

  @JsonProperty("vdu_reference")
  private String vduReference;

  @JsonProperty("number_of_instances")
  private int numberOfInstances;

  @JsonProperty("resource_requirements")
  private ResourceRequirements resourceRequirements;

  @JsonProperty("vnfc_instance")
  private ArrayList<VnfcInstance> vnfcInstance;


  public VduRecord() {
    vnfcInstance = new ArrayList<VnfcInstance>();
  }

  public void addVnfcInstance(VnfcInstance instance) {
    vnfcInstance.add(instance);
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getId() {
    return id;
  }

  public String getVmImage() {
    return vmImage;
  }

  public String getVduReference() {
    return vduReference;
  }

  public int getNumberOfInstances() {
    return numberOfInstances;
  }

  public ResourceRequirements getResourceRequirements() {
    return resourceRequirements;
  }

  public ArrayList<VnfcInstance> getVnfcInstance() {
    return vnfcInstance;
  }

  public void setVmImage(String vmImage) {
    this.vmImage = vmImage;
  }

  public void setVduReference(String vduReference) {
    this.vduReference = vduReference;
  }

  public void setNumberOfInstances(int numberOfInstances) {
    this.numberOfInstances = numberOfInstances;
  }

  public void setResourceRequirements(ResourceRequirements resourceRequirements) {
    this.resourceRequirements = resourceRequirements;
  }

  public void setVnfcInstance(ArrayList<VnfcInstance> vnfcInstance) {
    this.vnfcInstance = vnfcInstance;
  }

}
