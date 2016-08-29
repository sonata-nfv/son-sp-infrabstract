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
package sonata.kernel.VimAdaptor.wrapper.odlWrapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderedMacAddress implements Comparable<OrderedMacAddress> {

  @JsonProperty("port")
  private String mac;
  @JsonProperty("order")
  private int position;

  @JsonIgnore
  private String referenceCp;

  public String getMac() {
    return mac;
  }

  public int getPosition() {
    return position;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public void setPosition(int position) {
    this.position = position;
  }


  @Override
  public int compareTo(OrderedMacAddress o) {
    return (int) Math.signum(this.position - o.getPosition());
  }

  @Override
  public String toString() {
    return "{port:" + mac + ",order:" + position + ", cp: " + referenceCp + "}";
  }

  public String getReferenceCp() {
    return referenceCp;
  }

  public void setReferenceCp(String referenceCp) {
    this.referenceCp = referenceCp;
  }

}
