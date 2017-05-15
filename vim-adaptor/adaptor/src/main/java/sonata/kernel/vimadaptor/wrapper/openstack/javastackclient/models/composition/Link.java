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
package sonata.kernel.vimadaptor.wrapper.openstack.javastackclient.models.composition;

public class Link {

  String href;
  String rel;
  
  public String getHref() {
    return href;
  }
  public String getRel() {
    return rel;
  }
  public void setHref(String href) {
    this.href = href;
  }
  public void setRel(String rel) {
    this.rel = rel;
  }
  
  
}
