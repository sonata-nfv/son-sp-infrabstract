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
package sonata.kernel.VimAdaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class VimPreDeploymentList {

  @JsonProperty("uuid")
  private String uuid;
  @JsonProperty("vm_images")
  private ArrayList<VnfImage> images;
  public String getUuid() {
    return uuid;
  }
  public ArrayList<VnfImage> getImages() {
    return images;
  }
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
  public void setImages(ArrayList<VnfImage> images) {
    this.images = images;
  }
  
}
