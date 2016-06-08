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

import java.util.Observable;

public abstract class AbstractWrapper extends Observable {

  private String type;

  protected void setType(String type) {
    this.type = type;
  }

  /**
   * return the type of this wrapper.
   * 
   * @return a String in {"compute","storage","network"}
   */
  public String getType() {
    return type;
  }

  /**
   * expose this observable object's method setChanged().
   * 
   */
  public void markAsChanged() {
    this.setChanged();
  }



}


