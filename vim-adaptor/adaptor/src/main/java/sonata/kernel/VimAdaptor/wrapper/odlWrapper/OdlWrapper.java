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
package sonata.kernel.VimAdaptor.wrapper.odlWrapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.slf4j.LoggerFactory;
import sonata.kernel.VimAdaptor.commons.DeployServiceData;
import sonata.kernel.VimAdaptor.commons.heat.HeatPort;
import sonata.kernel.VimAdaptor.commons.heat.StackComposition;
import sonata.kernel.VimAdaptor.commons.nsd.ForwardingGraph;
import sonata.kernel.VimAdaptor.commons.nsd.NetworkForwardingPath;
import sonata.kernel.VimAdaptor.commons.nsd.NetworkFunction;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.ConnectionPointReference;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfVirtualLink;
import sonata.kernel.VimAdaptor.wrapper.NetworkingWrapper;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class OdlWrapper extends NetworkingWrapper {

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(OdlWrapper.class);

  private WrapperConfiguration config;

  /**
   * Basic constructor.
   * 
   * @param config the configuration object of this wrapper.
   */
  public OdlWrapper(WrapperConfiguration config) {
    super();
    this.config = config;
  }

  @Override
  public void configureNetworking(DeployServiceData data, StackComposition composition)
      throws Exception {
    if (data.getNsd().getForwardingGraphs().size() <= 0)
      throw new Exception("No Forwarding Graph specified in the descriptor");

    // TODO the NSD specifies more than one graph, and the selected one is given in the flavor
    // section.
    // This is not implemented in the first version.



    ServiceDescriptor nsd = data.getNsd();

    ForwardingGraph graph = nsd.getForwardingGraphs().get(0);

    NetworkForwardingPath path = graph.getNetworkForwardingPaths().get(0);

    ArrayList<ConnectionPointReference> pathCp = path.getConnectionPoints();

    Collections.sort(pathCp);
    int portIndex = 0;

    ArrayList<OrderedMacAddress> odlList = new ArrayList<OrderedMacAddress>();
    // Pre-populate structures for efficent search.

    HashMap<String, String> vnfIdToNameMap = new HashMap<String, String>();

    for (NetworkFunction nf : nsd.getNetworkFunctions()) {
      vnfIdToNameMap.put(nf.getVnfId(), nf.getVnfName());
    }

    HashMap<String, VnfDescriptor> nameToVnfdMap = new HashMap<String, VnfDescriptor>();
    for (VnfDescriptor vnfd : data.getVnfdList()) {
      nameToVnfdMap.put(vnfd.getName(), vnfd);
    }

    for (ConnectionPointReference cpr : pathCp) {
      String name = cpr.getConnectionPointRef();
      if (name.startsWith("ns")) {
        continue;
      } else {
        String[] split = name.split(":");
        if (split.length != 2) {
          throw new Exception(
              "Illegal Format: A connection point reference should be in the format vnfId:CpName. It was "
                  + name);
        }
        String vnfId = split[0];
        String cpRef = split[1];
        String vnfName = vnfIdToNameMap.get(vnfId);
        if (vnfName == null) {
          throw new Exception("Illegal Format: Unable to bind vnfName to the vnfId: " + vnfId);
        }
        VnfDescriptor vnfd = nameToVnfdMap.get(vnfName);
        if (vnfd == null) {
          throw new Exception("Illegal Format: Unable to bind VNFD to the vnfName: " + vnfName);
        }

        VnfVirtualLink inputLink = null;
        for (VnfVirtualLink link : vnfd.getVirtualLinks()) {
          if (link.getConnectionPointsReference().contains("vnf:" + cpRef)) {
            inputLink = link;
            break;
          }
        }
        if (inputLink == null) {
          for (VnfVirtualLink link : vnfd.getVirtualLinks()) {
            Logger.info(link.getConnectionPointsReference().toString());
          }
          throw new Exception(
              "Illegal Format: unable to find the vnfd.VL connected to the VNFD.CP=" + cpRef);
        }

        if (inputLink.getConnectionPointsReference().size() != 2) {
          throw new Exception(
              "Illegal Format: A vnf in/out vl should connect exactly two CPs. found: "
                  + inputLink.getConnectionPointsReference().size());
        }
        String vnfcCpName = null;
        for (String cp : inputLink.getConnectionPointsReference()) {
          if (!cp.equals(cpRef)) {
            vnfcCpName = cp;
            break;
          }
        }
        if (vnfcCpName == null) {
          throw new Exception(
              "Illegal Format: Unable to find the VNFC Cp name connected to this in/out VNF VL");
        }
        String qualifiedName = vnfName + ":" + vnfcCpName + ":" + nsd.getInstanceUuid();
        HeatPort connectedPort = null;
        for (HeatPort port : composition.getPorts()) {
          if (port.getPortName().equals(qualifiedName)) {
            connectedPort = port;
            break;
          }
        }

        if (connectedPort == null) {
          throw new Exception(
              "Illegal Format: cannot find the Heat port with name: " + qualifiedName);
        } else {
          // Eureka!
          OrderedMacAddress mac = new OrderedMacAddress();
          mac.setMac(connectedPort.getMacAddress());
          mac.setPosition(portIndex);
          mac.setReferenceCp(qualifiedName);
          portIndex++;
          odlList.add(mac);
        }
      }
    }

    Collections.sort(odlList);
    OdlPayload odlPayload = new OdlPayload("add", data.getNsd().getInstanceUuid(), "254.0.0.1/32",
        "254.0.0.2/32", odlList);
    ObjectMapper mapper = new ObjectMapper(new JsonFactory());
    mapper.setSerializationInclusion(Include.NON_NULL);
    // Logger.info(compositionString);
    String payload = mapper.writeValueAsString(odlPayload);
    Logger.info(payload);

    int sfcAgentPort = 55555;
    DatagramSocket clientSocket = new DatagramSocket(sfcAgentPort);
    InetAddress IPAddress = InetAddress.getByName(config.getVimEndpoint());
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    sendData = payload.getBytes(Charset.forName("UTF-8"));
    DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddress, sfcAgentPort);
    clientSocket.send(sendPacket);
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);
    String response = new String(receivePacket.getData(), 0, receivePacket.getLength(),Charset.forName("UTF-8"));
    Logger.info("SFC Agent response:\n" + response);
    clientSocket.close();
    if (!response.equals("SUCCESS")) {
      Logger.error("received string length: "+response.length());  
      Logger.error("received string: "+response+" not equal SUCCESS");
    }
    return;
  }

  public void deconfigureNetworking(String instanceId) throws Exception {

    OdlPayload odlPayload = new OdlPayload("delete", instanceId,null,null,null);
    ObjectMapper mapper = new ObjectMapper(new JsonFactory());
    mapper.setSerializationInclusion(Include.NON_NULL);
    // Logger.info(compositionString);
    String payload = mapper.writeValueAsString(odlPayload);
    Logger.info(payload);

    int sfcAgentPort = 55555;
    DatagramSocket clientSocket = new DatagramSocket(sfcAgentPort);
    InetAddress IPAddress = InetAddress.getByName(config.getVimEndpoint());
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    sendData = payload.getBytes(Charset.forName("UTF-8"));
    DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddress, sfcAgentPort);
    clientSocket.send(sendPacket);
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);
    String response = new String(receivePacket.getData(), 0, receivePacket.getLength(),Charset.forName("UTF-8"));
    Logger.info("SFC Agent response:\n" + response);
    clientSocket.close();
    
    
    if (!response.equals("SUCCESS")) {
      Logger.error("received string length: "+response.length());  
      Logger.error("received string: "+response+" not equal SUCCESS");
      throw new Exception("Unexcepted response from ODL SFC agent.");
    }
    return;
  }
}
