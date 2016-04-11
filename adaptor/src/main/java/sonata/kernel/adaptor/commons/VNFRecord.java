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

import java.util.ArrayList;


public class VNFRecord {

  private String descriptor_version;
  private String id;
  private Status status;
  private String vnf_address;
  private ArrayList<VDURecord> virtual_deployment_units;


  public VNFRecord() {
    this.virtual_deployment_units = new ArrayList<VDURecord>();
  }

  public void addVDU(VDURecord unit) {
    this.virtual_deployment_units.add(unit);
  }

  public String getId() {
    return id;
  }

  public Status getStatus() {
    return status;
  }

  public String getVnf_address() {
    return vnf_address;
  }

  public String getDescriptor_version() {
    return descriptor_version;
  }

  public void setDescriptor_version(String descriptor_version) {
    this.descriptor_version = descriptor_version;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void setVnf_address(String vnf_address) {
    this.vnf_address = vnf_address;
  }

  public ArrayList<VDURecord> getVirtual_deployment_units() {
    return virtual_deployment_units;
  }



}
