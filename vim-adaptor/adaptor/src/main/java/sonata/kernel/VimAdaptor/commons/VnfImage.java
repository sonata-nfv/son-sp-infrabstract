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
package sonata.kernel.VimAdaptor.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VnfImage {

  @JsonProperty("image_uuid")
  private String uuid;
  @JsonProperty("image_url")
  private String url;

  /**
   * @param uuid
   * @param url
   */
  public VnfImage(String uuid, String url) {
    this.uuid = uuid;
    this.url = url;
  }

  public String getUuid() {
    return uuid;
  }

  public String getUrl() {
    return url;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public void setUrl(String url) {
    this.url = url;
  }



}
