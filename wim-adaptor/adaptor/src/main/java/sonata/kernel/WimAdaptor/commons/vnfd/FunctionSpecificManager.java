/**
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the SONATA-NFV, UCL, NOKIA, NCSR Demokritos nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the SONATA project, funded by the European
 * Commission under Grant number 671517 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the SONATA partner consortium
 * (www.sonata-nfv.eu).
 *
 * @author Dario Valocchi (Ph.D.), UCL
 * 
 */

package sonata.kernel.WimAdaptor.commons.vnfd;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class FunctionSpecificManager {

  private String description;
  private String id;
  private String image;
  @JsonProperty("image_md5")
  private String imageMd5;
  @JsonProperty("resource_requirements")
  private FsmResourceRequirements resourceRequirements;
  private ArrayList<FsmOption> options;

  public String getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }

  public String getImage() {
    return image;
  }

  public String getImageMd5() {
    return imageMd5;
  }

  public FsmResourceRequirements getResourceRequirements() {
    return resourceRequirements;
  }

  public ArrayList<FsmOption> getOptions() {
    return options;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setImageMd5(String imageMd5) {
    this.imageMd5 = imageMd5;
  }

  public void setResourceRequirements(FsmResourceRequirements resourceRequirements) {
    this.resourceRequirements = resourceRequirements;
  }

  public void setOptions(ArrayList<FsmOption> options) {
    this.options = options;
  }



}
