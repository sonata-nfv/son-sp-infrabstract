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
package sonata.kernel.VimAdaptor.wrapper;

public enum WrapperType {
  COMPUTE("compute"), NETWORK("network"), STORAGE("storage");

  private final String name;

  WrapperType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }

  private String getName() {
    return this.toString();
  }


  public static WrapperType getByName(String name) {
    for (WrapperType vendor : values()) {
      if (vendor.getName().toUpperCase().equals(name.toUpperCase())) {
        return vendor;
      }
    }

    throw new IllegalArgumentException(name + " is not a valid WrapperType");
  }
}
