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

package sonata.kernel.adaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.adaptor.commons.vnfd.ResourceRequirements;

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

  public void setId(String id) {
    this.id = id;
  }

  public void setVm_image(String vmImage) {
    this.vmImage = vmImage;
  }

  public void setVdu_reference(String vduReference) {
    this.vduReference = vduReference;
  }

  public void setNumber_of_instances(int numberOfInstances) {
    this.numberOfInstances = numberOfInstances;
  }

  public void setResource_requirements(ResourceRequirements resourceRequirements) {
    this.resourceRequirements = resourceRequirements;
  }

  public String getId() {
    return id;
  }

  public String getVm_image() {
    return vmImage;
  }

  public String getVdu_reference() {
    return vduReference;
  }

  public int getNumber_of_instances() {
    return numberOfInstances;
  }

  public ResourceRequirements getResource_requirements() {
    return resourceRequirements;
  }

}
