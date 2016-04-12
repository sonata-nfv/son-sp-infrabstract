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

import com.fasterxml.jackson.annotation.JsonProperty;

import sonata.kernel.adaptor.commons.vnfDescriptor.Unit.MemoryUnit;

public class Memory {


  private double size;
  @JsonProperty("size_unit")
  private MemoryUnit sizeUnit;
  @JsonProperty("large_pages_required")
  private boolean largePagesRequired;
  @JsonProperty("numa_allocation_policy")
  private String numaAllocationPolicy;

  public double getSize() {
    return size;
  }

  public MemoryUnit getSize_unit() {
    return sizeUnit;
  }

  public boolean isLarge_pages_required() {
    return largePagesRequired;
  }

  public String getNuma_allocation_policy() {
    return numaAllocationPolicy;
  }

}
