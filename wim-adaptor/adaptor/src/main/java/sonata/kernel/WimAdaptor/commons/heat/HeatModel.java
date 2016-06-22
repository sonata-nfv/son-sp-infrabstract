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

package sonata.kernel.WimAdaptor.commons.heat;

import java.util.ArrayList;
import java.util.Collections;

public class HeatModel {

  private ArrayList<HeatResource> resources;

  public HeatModel() {
    this.resources = new ArrayList<HeatResource>();
  }

  public void addResource(HeatResource res) {
    this.resources.add(res);
  }

  public void prepare() {
    Collections.sort(resources);
    return;
  }

  public ArrayList<HeatResource> getResources() {
    return this.resources;
  }

}
