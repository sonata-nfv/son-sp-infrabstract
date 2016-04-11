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

public class DeployServiceResponse {

  private Status request_status;
  private ServiceRecord nsr;
  private ArrayList<VNFRecord> vnfrs;

  public DeployServiceResponse() {
    this.vnfrs = new ArrayList<VNFRecord>();
  }

  public void setStatus(Status status) {
    this.request_status = status;
  }

  public void setNSR(ServiceRecord record) {
    this.nsr = record;
  }

  public void addVNFRecord(VNFRecord vnfr) {
    this.vnfrs.add(vnfr);
  }

  public ServiceRecord getNSR() {
    return nsr;
  }

  public ArrayList<VNFRecord> getVNFRs() {
    return vnfrs;
  }

  public Status getRequest_status() {
    return request_status;
  }

}
