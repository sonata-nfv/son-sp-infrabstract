/*
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos ALL RIGHTS RESERVED.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Neither the name of the SONATA-NFV, UCL, NOKIA, NCSR Demokritos nor the names of its contributors
 * may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * This work has been performed in the framework of the SONATA project, funded by the European
 * Commission under Grant number 671517 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the SONATA partner consortium
 * (www.sonata-nfv.eu).
 *
 * @author Dario Valocchi (Ph.D.), UCL
 * 
 */

package sonata.kernel.vimadaptor.commons.vnfd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;


import java.io.IOException;

public class VmFormatDeserializer extends JsonDeserializer<VmFormat> {

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.
   * JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
   */
  @Override
  public VmFormat deserialize(JsonParser jp, DeserializationContext context)
      throws IOException, JsonProcessingException {
    JsonNode node = jp.getCodec().readTree(jp);
    JsonNode unitNode = node.get("vm_image_format");
    VmFormat out = null;
    if (unitNode != null) {
      String vmFormat = unitNode.asText();
      out = VmFormat.valueOf(vmFormat.toLowerCase());
    }
    
    return out;
  }

}
