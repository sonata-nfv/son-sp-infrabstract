/**
 * @author Bruno Vidalenc (Ph.D.)
 * @mail bruno.vidalenc@thalesgroup.com
 *
 *       Copyright 2016 [bruno Vidalenc]
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
package sonata.kernel.adaptor.wrapper.openstack;

public class Flavor {


  public Flavor(String flavorName, int vcpu, int ram, int storage) {
    super();
    this.flavorName = flavorName;
    this.vcpu = vcpu;
    this.ram = ram;
    this.storage = storage;
  }

  private String flavorName;

  private int vcpu;

  private int ram;

  private int storage;

  public int getVcpu() {
    return vcpu;
  }

  public void setVcpu(int vcpu) {
    this.vcpu = vcpu;
  }

  public int getRam() {
    return ram;
  }

  public void setRam(int ram) {
    this.ram = ram;
  }

  public int getStorage() {
    return storage;
  }

  public void setStorage(int storage) {
    this.storage = storage;
  }

  public String getFlavorName() {
    return flavorName;
  }

  public void setFlavorName(String flavorName) {
    this.flavorName = flavorName;
  }


}
