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

import com.fasterxml.jackson.annotation.JsonProperty;

public class VnfLifeCycleEvent {

  @JsonProperty("authentication_username")
  private String authenticationUsername;
  private String driver;
  @JsonProperty("authentication_type")
  private String authenticationType;
  private String authentication;
  @JsonProperty("vnf_container")
  private String vnfContainer;
  private Events events;



  public void setAuthenticationUsername(String authenticationUsername) {
    this.authenticationUsername = authenticationUsername;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public void setAuthenticationType(String authenticationType) {
    this.authenticationType = authenticationType;
  }

  public void setAuthentication(String authentication) {
    this.authentication = authentication;
  }

  public void setVnfContainer(String vnfContainer) {
    this.vnfContainer = vnfContainer;
  }

  public void setEvents(Events events) {
    this.events = events;
  }

  public String getAuthenticationUsername() {
    return authenticationUsername;
  }

  public String getDriver() {
    return driver;
  }

  public String getAuthenticationType() {
    return authenticationType;
  }

  public String getAuthentication() {
    return authentication;
  }

  public String getVnfContainer() {
    return vnfContainer;
  }

  public Events getEvents() {
    return events;
  }

}
