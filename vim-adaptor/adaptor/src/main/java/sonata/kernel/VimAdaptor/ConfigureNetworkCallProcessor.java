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
package sonata.kernel.VimAdaptor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.glassfish.hk2.api.DescriptorType;
import org.slf4j.LoggerFactory;

import sonata.kernel.VimAdaptor.commons.FunctionDeployPayload;
import sonata.kernel.VimAdaptor.commons.NetworkConfigurePayload;
import sonata.kernel.VimAdaptor.commons.SFCPoint;
import sonata.kernel.VimAdaptor.commons.VnfRecord;
import sonata.kernel.VimAdaptor.commons.nsd.ForwardingGraph;
import sonata.kernel.VimAdaptor.commons.nsd.NetworkForwardingPath;
import sonata.kernel.VimAdaptor.commons.nsd.NetworkFunction;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.VimAdaptor.commons.vnfd.ConnectionPointReference;
import sonata.kernel.VimAdaptor.commons.vnfd.Unit;
import sonata.kernel.VimAdaptor.commons.vnfd.UnitDeserializer;
import sonata.kernel.VimAdaptor.commons.vnfd.VnfDescriptor;
import sonata.kernel.VimAdaptor.messaging.ServicePlatformMessage;
import sonata.kernel.VimAdaptor.wrapper.ComputeWrapper;
import sonata.kernel.VimAdaptor.wrapper.NetworkWrapper;
import sonata.kernel.VimAdaptor.wrapper.WrapperBay;
import sonata.kernel.VimAdaptor.wrapper.ovsWrapper.OrderedMacAddress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;

public class ConfigureNetworkCallProcessor extends AbstractCallProcessor {

  NetworkConfigurePayload data = null;

  private static final org.slf4j.Logger Logger =
      LoggerFactory.getLogger(DeployFunctionCallProcessor.class);

  /**
   * @param message
   * @param sid
   * @param mux
   */
  public ConfigureNetworkCallProcessor(ServicePlatformMessage message, String sid, AdaptorMux mux) {
    super(message, sid, mux);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  @Override
  public void update(Observable arg0, Object arg1) {

  }

  /*
   * (non-Javadoc)
   * 
   * @see sonata.kernel.VimAdaptor.AbstractCallProcessor#process(sonata.kernel.VimAdaptor.messaging.
   * ServicePlatformMessage)
   */
  @Override
  public boolean process(ServicePlatformMessage message) {

    data = null;
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Unit.class, new UnitDeserializer());
    mapper.registerModule(module);
    mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    try {
      data = mapper.readValue(message.getBody(), NetworkConfigurePayload.class);
      Logger.info("payload parsed");
    } catch (IOException e) {
      Logger.error("Unable to parse the payload received");
      String responseJson = "{\"status\":\"ERROR\",\"message\":\"Unable to parse API payload\"}";
      this.sendToMux(new ServicePlatformMessage(responseJson, "application/json",
          message.getReplyTo(), message.getSid(), null));
      return false;
    }

    ServiceDescriptor nsd = data.getNsd();
    ArrayList<VnfRecord> vnfrs = data.getVnfrs();
    ArrayList<VnfDescriptor> vnfds = data.getVnfds();
    Logger.info("Processing Forwarding graphs...");
    for (ForwardingGraph graph : nsd.getForwardingGraphs()) {
      for (NetworkForwardingPath path : graph.getNetworkForwardingPaths()) {

        ArrayList<ConnectionPointReference> pathCp = path.getConnectionPoints();

        Collections.sort(pathCp);
        int portIndex = 0;

        // Pre-populate structures for efficent search.

        HashMap<String, String> vnfId2NameMap = new HashMap<String, String>();

        for (NetworkFunction nf : nsd.getNetworkFunctions()) {
          vnfId2NameMap.put(nf.getVnfId(), nf.getVnfName());
        }

        HashMap<String, VnfDescriptor> vnfName2VnfdMap = new HashMap<String, VnfDescriptor>();
        for (VnfDescriptor vnfd : vnfds) {
          vnfName2VnfdMap.put(vnfd.getName(), vnfd);
        }

        HashMap<String, VnfRecord> vnfd2VnfrMap = new HashMap<String, VnfRecord>();
        for (VnfRecord vnfr : vnfrs) {
          vnfd2VnfrMap.put(vnfr.getDescriptorReference(), vnfr);
        }
        HashMap<String, ArrayList<ConnectionPointReference>> netVim2SubGraphMap =
            new HashMap<String, ArrayList<ConnectionPointReference>>();

        HashMap<String, VnfDescriptor> cpRef2VnfdMap =
            new HashMap<String, VnfDescriptor>();
        
        HashMap<String, VnfRecord> cpRef2VnfrMap = new HashMap<String,VnfRecord>();
        
        for (ConnectionPointReference cpr : pathCp) {
          String name = cpr.getConnectionPointRef();
          if (name.startsWith("ns")) {
            continue;
          } else {
            String[] split = name.split(":");
            if (split.length != 2) {
              Logger.error("Unable to parse the service graph");
              String responseJson =
                  "{\"status\":\"ERROR\",\"message\":\"Unable to parse NSD service graph. Error in the connection_point_reference fields: "
                      + name + "\"}";
              this.sendToMux(new ServicePlatformMessage(responseJson, "application/json",
                  message.getReplyTo(), message.getSid(), null));
              return false;
            }
            String vnfId = split[0];
            String cpRef = split[1];
            String vnfName = vnfId2NameMap.get(vnfId);
            VnfDescriptor vnfd = vnfName2VnfdMap.get(vnfName);
            cpRef2VnfdMap.put(name, vnfd);
            cpRef2VnfrMap.put(name, vnfd2VnfrMap.get(vnfd.getUuid()));
            //Logger.debug("Getting id for vnf: " + vnfName);
            String vnfInstanceUuid = vnfd.getInstanceUuid();
            String computeVimUuid = WrapperBay.getInstance().getVimRepo()
                .getComputeVimUuidByFunctionInstanceId(vnfInstanceUuid);
            String netVimUuid = WrapperBay.getInstance().getVimRepo().getNetworkVimFromComputeVimUuid(computeVimUuid)
                .getConfig().getUuid();
            if (netVim2SubGraphMap.containsKey(netVimUuid)) {
              netVim2SubGraphMap.get(netVimUuid).add(cpr);
            } else {
              netVim2SubGraphMap.put(netVimUuid, new ArrayList<ConnectionPointReference>());
              netVim2SubGraphMap.get(netVimUuid).add(cpr);
            }
          }
        }
        
        
        
        for (String netVimUuid : netVim2SubGraphMap.keySet()) {
          ArrayList<VnfDescriptor> descriptorsSublist = new ArrayList<VnfDescriptor>();
          ArrayList<VnfRecord> recordsSublist = new ArrayList<VnfRecord>();
          
          
          ServiceDescriptor partialNsd = new ServiceDescriptor();
          partialNsd.setConnectionPoints(nsd.getConnectionPoints());
          partialNsd.setNetworkFunctions(nsd.getNetworkFunctions());
          ForwardingGraph partialGraph = new ForwardingGraph();
          NetworkForwardingPath partialPath = new NetworkForwardingPath();
          ArrayList<ConnectionPointReference> connectionPoints = netVim2SubGraphMap.get(netVimUuid);
          partialPath.setConnectionPoints(connectionPoints);
          ArrayList<NetworkForwardingPath> tempPaths = new ArrayList<NetworkForwardingPath>();
          tempPaths.add(partialPath);
          partialGraph.setNetworkForwardingPaths(tempPaths);
          ArrayList<ForwardingGraph> tempGraph = new ArrayList<ForwardingGraph>();
          tempGraph.add(partialGraph);
          partialNsd.setForwardingGraphs(tempGraph);
          
          for(ConnectionPointReference cpr : connectionPoints){
            VnfDescriptor vnfd = cpRef2VnfdMap.get(cpr.getConnectionPointRef());
            VnfRecord vnfr = cpRef2VnfrMap.get(cpr.getConnectionPointRef());
            if(!descriptorsSublist.contains(vnfd))
              descriptorsSublist.add(vnfd);
            if(!recordsSublist.contains(vnfr))
              recordsSublist.add(vnfr);
          }
          
          NetworkConfigurePayload wrapperPayload = new NetworkConfigurePayload();
          wrapperPayload.setNsd(partialNsd);
          wrapperPayload.setVnfds(descriptorsSublist);
          wrapperPayload.setVnfrs(recordsSublist);
          wrapperPayload.setServiceInstanceId(nsd.getInstanceUuid());
          
          NetworkWrapper netWr = (NetworkWrapper) WrapperBay.getInstance().getWrapper(netVimUuid);
          try {
            netWr.configureNetworking(wrapperPayload);
          } catch (Exception e) {
            Logger.error("Unable to configure networking on VIM: "+ netVimUuid,e);
            String responseJson =
                "{\"status\":\"ERROR\",\"message\":\""+e.getMessage()+"\"}";
            this.sendToMux(new ServicePlatformMessage(responseJson, "application/json",
                message.getReplyTo(), message.getSid(), null));
            return false;          }
          
        }
      }
    }

    String responseJson = "{\"status\":\"COMPLETED\",\"message\":\"\"}";
    Logger.info(
        "Received networking.configure call for service instance " + data.getServiceInstanceId());
    this.sendToMux(new ServicePlatformMessage(responseJson, "application/json",
        message.getReplyTo(), message.getSid(), null));
    return true;
  }

}
