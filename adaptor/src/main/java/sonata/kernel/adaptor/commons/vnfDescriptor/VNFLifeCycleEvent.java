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
package sonata.kernel.adaptor.commons.vnfDescriptor;

public class VNFLifeCycleEvent {

  private String authentication_username;
  private String driver;
  private String authentication_type;
  private String authentication;
  private String vnf_container;
  private Events events;

  public String getAuthentication_username() {
    return authentication_username;
  }

  public String getDriver() {
    return driver;
  }

  public String getAuthentication_type() {
    return authentication_type;
  }

  public String getAuthentication() {
    return authentication;
  }

  public String getVnf_container() {
    return vnf_container;
  }

  public Events getEvents() {
    return events;
  }

}
