/*
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the SONATA-NFV, UCL, NOKIA, NCSR Demokritos nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the SONATA project, funded by the European
 * Commission under Grant number 671517 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the SONATA partner consortium
 * (www.sonata-nfv.eu).
 *
 * @author Adel Zaalouk (Ph.D.), NEC
 * 
 */

package sonata.kernel.vimadaptor.wrapper.openstack.javastackclient.models.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {


  private String id;
  private String[] methods;
  private Project project;
  @JsonProperty("issued_at")
  private String issuedAt;
  private String expires;
  @JsonProperty("audit_ids")
  private List<String> auditIds;


  public Project getProject() {
    return this.project;
  }

  public void setTenant(Project project) {
    this.project = project;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getExpires() {
    return expires;
  }

  public void setExpires(String expires) {
    this.expires = expires;
  }

  public String[] getMethods() {
    return methods;
  }

  public String getIssuedAt() {
    return issuedAt;
  }

  public List<String> getAuditIds() {
    return auditIds;
  }

  public void setMethods(String[] methods) {
    this.methods = methods;
  }

  public void setIssuedAt(String issuedAt) {
    this.issuedAt = issuedAt;
  }

  public void setAuditIds(List<String> auditIds) {
    this.auditIds = auditIds;
  }
}
