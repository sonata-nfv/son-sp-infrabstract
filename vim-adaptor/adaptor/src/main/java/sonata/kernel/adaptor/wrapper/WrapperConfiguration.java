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

package sonata.kernel.adaptor.wrapper;

public class WrapperConfiguration {

  private String vimEndpoint;
  private String vimVendor;
  private String wrapperType;
  private String authUserName;
  private String tenantName;
  private String authPass;
  private String authKey;
  private String uuid;
  private String tenantExtNet;
  private String tenantExtRouter;
  

  public String getWrapperType() {
    return wrapperType;
  }

  public void setWrapperType(String wrapperType) {
    this.wrapperType = wrapperType;
  }

  public String getVimEndpoint() {
    return vimEndpoint;
  }

  public void setVimEndpoint(String vimEndpoint2) {
    this.vimEndpoint = vimEndpoint2;
  }

  public String getVimVendor() {
    return vimVendor;
  }

  public void setVimVendor(String vimType) {
    this.vimVendor = vimType;
  }

  public String getAuthUserName() {
    return authUserName;
  }

  public void setAuthUserName(String authUserName) {
    this.authUserName = authUserName;
  }

  public String getAuthPass() {
    return authPass;
  }

  public void setAuthPass(String authPass) {
    this.authPass = authPass;
  }

  public String getAuthKey() {
    return authKey;
  }

  public void setAuthKey(String authKey) {
    this.authKey = authKey;
  }

  public String getUuid() {
    return this.uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @Override
  public String toString() {
    String out = "";

    out += "sid: " + uuid + "\n\r";
    out += "WrapperType: " + wrapperType + "\n\r";
    out += "VimType: " + vimVendor + "\n\r";
    out += "VimEndpount: " + vimEndpoint + "\n\r";
    out += "User: " + authUserName + "\n\r";
    out += "pass: " + authPass + "\n\r";
    return out;
  }

  public String getTenantName() {
    return tenantName;
  }

  public void setTenantName(String tenantName) {
    this.tenantName = tenantName;
  }

  public String getTenantExtNet() {
    return tenantExtNet;
  }

  public void setTenantExtNet(String tenantExtNet) {
    this.tenantExtNet = tenantExtNet;
  }

  public String getTenantExtRouter() {
    return tenantExtRouter;
  }

  public void setTenantExtRouter(String tenantExtRouter) {
    this.tenantExtRouter = tenantExtRouter;
  }

}
