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

package sonata.kernel.vimadaptor.wrapper.ovsWrapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;

import sonata.kernel.vimadaptor.commons.NetworkConfigurePayload;
import sonata.kernel.vimadaptor.commons.VduRecord;
import sonata.kernel.vimadaptor.commons.VnfRecord;
import sonata.kernel.vimadaptor.commons.VnfcInstance;
import sonata.kernel.vimadaptor.commons.nsd.ConnectionPointRecord;
import sonata.kernel.vimadaptor.commons.nsd.ForwardingGraph;
import sonata.kernel.vimadaptor.commons.nsd.NetworkForwardingPath;
import sonata.kernel.vimadaptor.commons.nsd.NetworkFunction;
import sonata.kernel.vimadaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.vimadaptor.commons.vnfd.ConnectionPointReference;
import sonata.kernel.vimadaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.vimadaptor.commons.vnfd.VnfVirtualLink;
import sonata.kernel.vimadaptor.wrapper.NetworkWrapper;
import sonata.kernel.vimadaptor.wrapper.WrapperConfiguration;

import java.io.File;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;


public class OvsWrapper extends NetworkWrapper {

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(OvsWrapper.class);

  private static final String ADAPTOR_SEGMENTS_CONF = "/adaptor/segments.conf";

  /**
   * Basic constructor.
   * 
   * @param config the configuration object of this wrapper.
   */
  public OvsWrapper(WrapperConfiguration config) {
    super(config);
  }

  @Override
  public void configureNetworking(NetworkConfigurePayload data) throws Exception {
    if (data.getNsd().getForwardingGraphs().size() <= 0)
      throw new Exception("No Forwarding Graph specified in the descriptor");

    // TODO the NSD specifies more than one graph, and the selected one is given in the flavor
    // section.
    // This is not implemented in the first version.


    String serviceInstanceId = data.getServiceInstanceId();
    ServiceDescriptor nsd = data.getNsd();
    ArrayList<VnfRecord> vnfrs = data.getVnfrs();
    ArrayList<VnfDescriptor> vnfds = data.getVnfds();
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
    for (VnfDescriptor vnfd : vnfds) {
      nameToVnfdMap.put(vnfd.getName(), vnfd);
    }

    HashMap<String, VnfRecord> vnfdToVnfrMap = new HashMap<String, VnfRecord>();
    for (VnfRecord vnfr : vnfrs) {
      vnfdToVnfrMap.put(vnfr.getDescriptorReference(), vnfr);
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

        VnfRecord vnfr = vnfdToVnfrMap.get(vnfd.getUuid());
        if (vnfr == null) {
          throw new Exception("Illegal Format: Unable to bind VNFD to the VNFR: " + vnfName);
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

        Logger.debug("Searching for CpRecord of Cp: " + vnfcCpName);
        ConnectionPointRecord matchingCpRec = null;
        for (VduRecord vdu : vnfr.getVirtualDeploymentUnits()) {
          for (VnfcInstance vnfc : vdu.getVnfcInstance()) {
            for (ConnectionPointRecord cpRec : vnfc.getConnectionPoints()) {
              Logger.debug("Checking " + cpRec.getId());
              if (vnfcCpName.equals(cpRec.getId())) {
                matchingCpRec = cpRec;
                break;
              }
            }
          }

        }

        String qualifiedName = vnfName + "." + vnfcCpName + "." + nsd.getInstanceUuid();
        // HeatPort connectedPort = null;
        // for (HeatPort port : composition.getPorts()) {
        // if (port.getPortName().equals(qualifiedName)) {
        // connectedPort = port;
        // break;
        // }
        // }
        if (matchingCpRec == null) {
          throw new Exception(
              "Illegal Format: cannot find the VNFR.VDU.VNFC.CPR matching: " + vnfcCpName);
        } else {
          // Eureka!
          OrderedMacAddress mac = new OrderedMacAddress();
          mac.setMac(matchingCpRec.getInterface().getHardwareAddress());
          mac.setPosition(portIndex);
          mac.setReferenceCp(qualifiedName);
          portIndex++;
          odlList.add(mac);
        }
      }
    }
    Properties segments = new Properties();
    segments.load(new FileReader(new File(ADAPTOR_SEGMENTS_CONF)));

    Collections.sort(odlList);
    OvsPayload odlPayload = new OvsPayload("add", serviceInstanceId, segments.getProperty("in"),
        segments.getProperty("out"), odlList);
    ObjectMapper mapper = new ObjectMapper(new JsonFactory());
    mapper.setSerializationInclusion(Include.NON_NULL);
    // Logger.info(compositionString);
    String payload = mapper.writeValueAsString(odlPayload);
    Logger.debug(this.getConfig().getUuid() + " - " + this.getConfig().getVimEndpoint());
    Logger.debug(payload);

    int sfcAgentPort = 55555;
    DatagramSocket clientSocket = new DatagramSocket(sfcAgentPort);
    InetAddress IPAddress = InetAddress.getByName(this.getConfig().getVimEndpoint());
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    sendData = payload.getBytes(Charset.forName("UTF-8"));
    DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddress, sfcAgentPort);
    clientSocket.send(sendPacket);
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.setSoTimeout(10000);
    try {
      clientSocket.receive(receivePacket);
    } catch (SocketTimeoutException e) {
      clientSocket.close();
      Logger.error("Timeout exception from the OVS SFC agent");
      throw new Exception("Request to OVS VIM agent timed out.");
    }
    clientSocket.close();
    String response =
        new String(receivePacket.getData(), 0, receivePacket.getLength(), Charset.forName("UTF-8"));
    Logger.info("SFC Agent response:\n" + response);
    if (!response.equals("SUCCESS")) {
      Logger.error("Unexpected response.");
      Logger.error("received string length: " + response.length());
      Logger.error("received string: " + response);
      throw new Exception(
          "Unexpected response from OVS SFC agent while trying to add a configuration.");
    }
    return;
  }

  @Override
  public void deconfigureNetworking(String instanceId) throws Exception {

    OvsPayload odlPayload = new OvsPayload("delete", instanceId, null, null, null);
    ObjectMapper mapper = new ObjectMapper(new JsonFactory());
    mapper.setSerializationInclusion(Include.NON_NULL);
    // Logger.info(compositionString);
    String payload = mapper.writeValueAsString(odlPayload);
    Logger.info(payload);

    int sfcAgentPort = 55555;

    InetAddress IPAddress = InetAddress.getByName(this.getConfig().getVimEndpoint());
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    sendData = payload.getBytes(Charset.forName("UTF-8"));
    DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddress, sfcAgentPort);
    DatagramSocket clientSocket = new DatagramSocket(sfcAgentPort);
    clientSocket.send(sendPacket);
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.setSoTimeout(10000);
    try {
      clientSocket.receive(receivePacket);
    } catch (SocketTimeoutException e) {
      clientSocket.close();
      Logger.error("Timeout exception from the OVS SFC agent");
      throw new Exception("Request to OVS VIM agent timed out.");
    }
    clientSocket.close();
    String response =
        new String(receivePacket.getData(), 0, receivePacket.getLength(), Charset.forName("UTF-8"));
    Logger.info("SFC Agent response:\n" + response);


    if (!response.equals("SUCCESS")) {
      Logger.error("received string length: " + response.length());
      Logger.error("received string: " + response + " not equal SUCCESS");
      throw new Exception(
          "Unexcepted response from ODL SFC agent while trying to delete a configuration.");
    }
    return;
  }
}
