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
package sonata.kernel.WimAdaptor.commons.vnfd;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VmFormat {
  RAW("raw"), VHD("vhd"), VMDK("vmdk"), VDI("vdi"), ISO("iso"), QCOW2("qcow2"), DOCKER(
      "docker"), OVA("ova"), OVF("ovf"), BARE("bare");

  private final String name;

  @JsonCreator
  VmFormat(String name) {
    this.name = name.toLowerCase();
  }

  @Override
  public String toString() {
    return this.name;
  }

}
