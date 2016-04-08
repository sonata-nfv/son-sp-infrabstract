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

import java.util.ArrayList;

public class DeploymentFlavor {

  private ArrayList<String> vdu_reference;
  private String constraint;
  private ArrayList<String> vlink_reference;
  private String id;
  private ArrayList<AssuranceParameter> assurance_parameters;

  public ArrayList<String> getVdu_reference() {
    return vdu_reference;
  }

  public String getConstraint() {
    return constraint;
  }

  public ArrayList<String> getVlink_reference() {
    return vlink_reference;
  }

  public String getId() {
    return id;
  }

  public ArrayList<AssuranceParameter> getAssurance_parameters() {
    return assurance_parameters;
  }

}
