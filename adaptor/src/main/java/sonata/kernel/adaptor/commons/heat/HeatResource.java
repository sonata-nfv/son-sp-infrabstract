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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;

@JsonPropertyOrder({"type","properties"})
public class HeatResource implements Comparable<HeatResource>{
 
  @JsonIgnore
  private String resourceName;
  private String type;
  private HashMap<String, Object> properties;
  
  public HeatResource(){
    this.properties = new HashMap<String,Object>();
  }
  
  public String getType() {
    return type;
  }
  public HashMap<String, Object> getProperties() {
    return properties;
  }
  public void setType(String type) {
    this.type = type;
  }
  public void setProperties(HashMap<String, Object> properties) {
    this.properties = properties;
  }
  
  public void putProperty(String key, Object value){
    this.properties.put(key, value);
  }
  
  public void setName(String name){
    this.resourceName=name;
  }

  public String getResourceName() {
    return resourceName;
  }

  @Override
  public int compareTo(HeatResource o) {
    return this.type.compareTo(o.getType());
  }
  
}
