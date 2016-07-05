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

public class ServiceRecord {

  @JsonProperty("descriptor_version")
  private String descriptorVersion;
  private Status status;
  @JsonProperty("id")
  private String id;
  @JsonProperty("descriptor_reference")
  private String descriptorReference;



  public Status getStatus() {
    return status;
  }


  public void setStatus(Status status) {
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getDescriptorVersion() {
    return descriptorVersion;
  }


  public void setDescriptorVersion(String descriptorVersion) {
    this.descriptorVersion = descriptorVersion;
  }


  public String getDescriptorReference() {
    return descriptorReference;
  }


  public void setDescriptorReference(String descriptorReference) {
    this.descriptorReference = descriptorReference;
  }





}
