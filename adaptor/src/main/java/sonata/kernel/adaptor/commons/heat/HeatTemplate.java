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

package sonata.kernel.adaptor.commons.heat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;

@JsonPropertyOrder({"templateVersion", "resources"})
public class HeatTemplate {

  @JsonProperty("heat_template_version")
  private String templateVersion = "2013-05-23";

  private HashMap<String, Object> resources;

  public HeatTemplate() {
    resources = new HashMap<String, Object>();
  }

  public String getTemplateVersion() {
    return templateVersion;
  }

  public HashMap<String, Object> getResources() {
    return resources;
  }

  public void setTemplateVersion(String templateVersion) {
    this.templateVersion = templateVersion;
  }

  public void setResources(HashMap<String, Object> resources) {
    this.resources = resources;
  }

  public void putResource(String key, Object value) {
    this.resources.put(key, value);
  }
}
