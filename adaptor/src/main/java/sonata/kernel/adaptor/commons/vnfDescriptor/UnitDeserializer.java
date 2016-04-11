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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import sonata.kernel.adaptor.commons.vnfDescriptor.Unit.BandwidthUnit;
import sonata.kernel.adaptor.commons.vnfDescriptor.Unit.FrequencyUnit;
import sonata.kernel.adaptor.commons.vnfDescriptor.Unit.GeneralUnit;
import sonata.kernel.adaptor.commons.vnfDescriptor.Unit.MemoryUnit;

public class UnitDeserializer extends JsonDeserializer<Unit> {

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.
   * JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
   */
  @Override
  public Unit deserialize(JsonParser jp, DeserializationContext context)
      throws IOException, JsonProcessingException {
    JsonNode node = jp.getCodec().readTree(jp);
    JsonNode unitNode = node.get("unit");
    Unit out = GeneralUnit.percentage;
   
    if(unitNode!=null){
      String unit = unitNode.asText(); 
    if (unit.contains("ps")) {
      if (unit.contains("T"))
        out = BandwidthUnit.Tbps;
      else if (unit.contains("G"))
        out = BandwidthUnit.Gbps;
      else if (unit.contains("M"))
        out = BandwidthUnit.Mbps;
      else if (unit.contains("k"))
        out = BandwidthUnit.kbps;
      else
        out = BandwidthUnit.bps;
    } else if (unit.contains("Hz")) {
      if (unit.contains("T"))
        out = FrequencyUnit.THz;
      else if (unit.contains("G"))
        out = FrequencyUnit.GHz;
      else if (unit.contains("M"))
        out = FrequencyUnit.MHz;
      else if (unit.contains("k"))
        out = FrequencyUnit.kHz;
      else
        out = FrequencyUnit.Hz;
    } else if (unit.endsWith("B")) {
      if (unit.equals("PB"))
        out = MemoryUnit.PB;
      else if (unit.equals("TB"))
        out = MemoryUnit.TB;
      else if (unit.equals("GB"))
        out = MemoryUnit.GB;
      else if (unit.equals("MB"))
        out = MemoryUnit.MB;
      else if (unit.equals("kB"))
        out = MemoryUnit.kB;
      else if (unit.equals("PiB"))
        out = MemoryUnit.PiB;
      else if (unit.equals("TiB"))
        out = MemoryUnit.TiB;
      else if (unit.equals("GiB"))
        out = MemoryUnit.GiB;
      else if (unit.equals("MiB"))
        out = MemoryUnit.MiB;
      else if (unit.equals("KiB"))
        out = MemoryUnit.KiB;
      else
        out = MemoryUnit.B;
    } else if (unit.equals("Percentage")) out = GeneralUnit.percentage;
    } return out;
  }

}
