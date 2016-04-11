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

public class CPU {
  private int vcpus;
  private String cpu_support_accelerator;
  private String cpu_model;
  private String cpu_clock_speed;

  public int getVcpus() {
    return vcpus;
  }

  public String getCpu_support_accelerator() {
    return cpu_support_accelerator;
  }

  public String getCpu_model() {
    return cpu_model;
  }

  public String getCpu_clock_speed() {
    return cpu_clock_speed;
  }
}
