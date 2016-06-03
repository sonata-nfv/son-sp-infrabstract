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

import sonata.kernel.adaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.adaptor.commons.vnfd.VnfDescriptor;

import java.util.ArrayList;

public class DeployServiceData {

  @JsonProperty("vim_uuid")
  private String vimUuid;
  private ServiceDescriptor nsd;
  private ArrayList<VnfDescriptor> vnfds;

  public DeployServiceData() {
    this.vnfds = new ArrayList<VnfDescriptor>();
  }

  public void setServiceDescriptor(ServiceDescriptor descriptor) {
    this.nsd = descriptor;
  }

  public void addVnfDescriptor(VnfDescriptor descriptor) {
    this.vnfds.add(descriptor);
  }

  public ServiceDescriptor getNsd() {
    return nsd;
  }

  public ArrayList<VnfDescriptor> getVnfdList() {
    return vnfds;
  }

  public String getVimUuid() {
    return vimUuid;
  }

  public void setVimUuid(String vimUuid) {
    this.vimUuid = vimUuid;
  }

  public void setNsd(ServiceDescriptor nsd) {
    this.nsd = nsd;
  }

  public void setVnfds(ArrayList<VnfDescriptor> vnfds) {
    this.vnfds = vnfds;
  }


}
