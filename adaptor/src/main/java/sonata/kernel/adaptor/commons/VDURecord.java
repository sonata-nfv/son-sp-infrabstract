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

import sonata.kernel.adaptor.commons.vnfDescriptor.ResourceRequirements;

public class VDURecord {

  private String id;
  private String vm_image;
  private String vdu_reference;
  private int number_of_instances;
  private ResourceRequirements resource_requirements;

  public void setId(String id) {
    this.id = id;
  }

  public void setVm_image(String vm_image) {
    this.vm_image = vm_image;
  }

  public void setVdu_reference(String vdu_reference) {
    this.vdu_reference = vdu_reference;
  }

  public void setNumber_of_instances(int number_of_instances) {
    this.number_of_instances = number_of_instances;
  }

  public void setResource_requirements(ResourceRequirements resource_requirements) {
    this.resource_requirements = resource_requirements;
  }

  public String getId() {
    return id;
  }

  public String getVm_image() {
    return vm_image;
  }

  public String getVdu_reference() {
    return vdu_reference;
  }

  public int getNumber_of_instances() {
    return number_of_instances;
  }

  public ResourceRequirements getResource_requirements() {
    return resource_requirements;
  }

}
