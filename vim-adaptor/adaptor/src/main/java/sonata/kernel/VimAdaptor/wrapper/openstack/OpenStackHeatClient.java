/*
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, NCSR Demokritos ALL RIGHTS RESERVED. <p> Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at <p>
 * http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License. <p> Neither the name of the
 * SONATA-NFV, UCL, NOKIA, NCSR Demokritos nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written permission. <p>
 * This work has been performed in the framework of the SONATA project, funded by the European
 * Commission under Grant number 671517 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the SONATA partner consortium
 * (www.sonata-nfv.eu).
 * 
 * @author Sharon Mendel Brin (Ph.D.), Nokia
 * 
 * @author Dario Valocchi (Ph.D.), UCL
 * 
 * @author Akis Kourtis, NCSR Demokritos
 */

package sonata.kernel.VimAdaptor.wrapper.openstack;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;

import sonata.kernel.VimAdaptor.commons.heat.*;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.JavaStackCore;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.JavaStackUtils;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.composition.*;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.stacks.StackData;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by smendel on 4/20/16.
 * <p/>
 * This class wraps a Heat Client written in python when instantiated the connection details of the
 * OpenStack instance should be provided
 */
public class OpenStackHeatClient {

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(OpenStackHeatClient.class);
  private JavaStackCore javaStack; // instance for calling OpenStack APIs
  private ObjectMapper mapper;
  private String url; // url of the OpenStack Client
  private String userName; // OpenStack Client user
  private String password; // OpenStack Client password
  private String tenantName; // OpenStack tenant name


  /**
   * Construct a new Openstack Client.
   *
   * @param url of the OpenStack endpoint
   * @param userName to log into the OpenStack service
   * @param password to log into the OpenStack service
   * @param tenantName to log into the OpenStack service
   */
  public OpenStackHeatClient(String url, String userName, String password, String tenantName) {
    this.url = url;
    this.userName = userName;
    this.password = password;
    this.tenantName = tenantName;

    Logger.debug(
        "URL: " + url + "|User:" + userName + "|Tenant:" + tenantName + "|Pass:" + password + "|");

    javaStack = JavaStackCore.getJavaStackCore();
    javaStack.setEndpoint(url);
    javaStack.setUsername(userName);
    javaStack.setPassword(password);
    javaStack.setTenant_id(tenantName);

    // Authenticate
    try {
      javaStack.authenticateClient();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create stack.
   *
   * @param stackName - the name of the stack
   * @param template - the content of the hot template that describes the file
   * @return - the uuid of the created stack, if the process failed the returned value is null
   */
  public String createStack(String stackName, String template) {
    String uuid = null;

    Logger.info("Creating stack: " + stackName);
    // Logger.debug("Template:\n" + template);

    try {
      // template = JavaStackUtils.readFile("./test.yml");
      mapper = new ObjectMapper();
      String createStackResponse =
          JavaStackUtils.convertHttpResponseToString(javaStack.createStack(template, stackName));
      StackData stack = mapper.readValue(createStackResponse, StackData.class);
      uuid = stack.getStack().getId();

    } catch (Exception e) {
      Logger.error(
          "Runtime error creating stack : " + stackName + " error message: " + e.getMessage());
    }

    return uuid;
  }

  public void updateStack(String stackName, String stackUuid, String template) {

    Logger.info("Creating stack: " + stackName);
    // Logger.debug("Template:\n" + template);

    try {

      String response = JavaStackUtils
          .convertHttpResponseToString(javaStack.updateStack(stackName, stackUuid, template));

    } catch (Exception e) {
      Logger.error(
          "Runtime error creating stack : " + stackName + " error message: " + e.getMessage());
    }

    return;
  }

  /**
   * Get the heat template used to create the stack.
   *
   * @param stackName - the name of the stack in Heat
   * @param stackUuid - the UUID of the stack in Heat
   * @return - the HeatTemplate object representing the heat template.
   */
  public HeatTemplate getStackTemplate(String stackName, String stackUuid) {
    HeatTemplate template = null;

    try {
      // template = JavaStackUtils.readFile("./test.yml");
      mapper = new ObjectMapper();
      String getStackTemplateResponse = JavaStackUtils
          .convertHttpResponseToString(javaStack.getStackTemplate(stackName, stackUuid));
      template = mapper.readValue(getStackTemplateResponse, HeatTemplate.class);

    } catch (Exception e) {
      Logger.error(
          "Runtime error creating stack : " + stackName + " error message: " + e.getMessage());
    }

    return template;
  }

  /**
   * Get the status of existing stack. Using Stack Name or Stack Id
   *
   * @param stackName used for logging, usually service tenant
   * @param uuid OpenStack UUID of the stack
   * @return the OpenStack status of the stack
   */
  public String getStackStatus(String stackName, String uuid) {
    String status = null;
    Logger.info("Getting status for stack: " + stackName + "Stack ID: " + uuid);
    try {
      mapper = new ObjectMapper();
      String findStackResponse =
          JavaStackUtils.convertHttpResponseToString(javaStack.findStack(stackName));
      StackData stack = mapper.readValue(findStackResponse, StackData.class);
      status = stack.getStack().getStack_status();

    } catch (Exception e) {
      Logger.error(
          "Runtime error getStackStatus: " + stackName + " error message: " + e.getMessage());
    }
    return status;
  }


  /**
   * Delete Stack.
   *
   * @param stackName - used for logging, usually service tenant
   * @param uuid - OpenStack UUID of the stack
   * @return - if the operation was sent successfully to OpenStack - 'DELETED'
   */
  public String deleteStack(String stackName, String uuid) {

    String isDeleted = null;
    Logger.info("Deleting stack: " + stackName + "Stack ID: " + uuid);

    try {

      Logger.info("Fetching information about Stack...!");
      mapper = new ObjectMapper();

      String findStackResponse =
          JavaStackUtils.convertHttpResponseToString(javaStack.findStack(stackName));
      StackData stack = mapper.readValue(findStackResponse, StackData.class);
      String stackIdToDelete = stack.getStack().getId();

      Logger.info("Delete stack with ID: " + stackIdToDelete);

      javaStack.deleteStack(stackName, stackIdToDelete);
      Logger.info("Delete request sent");

      isDeleted = "DELETED";

    } catch (IOException e) {
      e.printStackTrace();
    }

    return isDeleted;
  }

  @Override
  public String toString() {
    return "OpenStackHeatClient{" + "url='" + url + '\'' + ", userName='" + userName + '\''
        + ", password='" + password + '\'' + ", tenantName='" + tenantName + '\'' + '}';
  }

  /**
   * Get stack composition.
   *
   * @param stackName - used for logging, usually service tenant
   * @param uuid - OpenStack UUID of the stack
   * @return a StackComposition object representing the stack resources
   */
  public StackComposition getStackComposition(String stackName, String uuid) {

    StackComposition composition = new StackComposition();

    try {
      // List Stack Resources
      String listResources = JavaStackUtils
          .convertHttpResponseToString(javaStack.listStackResources(stackName, uuid, null));
      // Logger.debug(listResources);

      ArrayList<Resource> resources =
          mapper.readValue(listResources, Resources.class).getResources();

      // Output lists
      ArrayList<HeatServer> servers = new ArrayList<>();
      ArrayList<HeatPort> ports = new ArrayList<>();
      ArrayList<HeatNet> networks = new ArrayList<>();
      ArrayList<HeatRouter> routers = new ArrayList<>();

      // Helper lists
      ArrayList<PortAttributes> portsAtts = new ArrayList<>();
      ArrayList<FloatingIpAttributes> floatingIps = new ArrayList<>();

      for (Resource resource : resources) {
        HeatServer heatServer = new HeatServer();
        HeatPort heatPort = new HeatPort();

        // Logger.debug(resource.getResource_type());

        // Show ResourceData
        // Logger.debug("StackID: " + uuid);

        String showResourceData = JavaStackUtils.convertHttpResponseToString(
            javaStack.showResourceData(stackName, uuid, resource.getResource_name()));

        switch (resource.getResource_type()) {

          case "OS::Nova::Server":
            ResourceData<ServerAttributes> serverResourceData = mapper.readValue(showResourceData,
                new TypeReference<ResourceData<ServerAttributes>>() {});

            // Set Server
            heatServer.setServerId(serverResourceData.getResource().getPhysical_resource_id());
            heatServer.setServerName(serverResourceData.getResource().getAttributes().getName());
            servers.add(heatServer);
            break;

          case "OS::Neutron::Port":
            ResourceData<PortAttributes> portResourceData = mapper.readValue(showResourceData,
                new TypeReference<ResourceData<PortAttributes>>() {});

            portsAtts.add(portResourceData.getResource().getAttributes());

            // Set Port
            heatPort.setIpAddress(portResourceData.getResource().getAttributes().getFixed_ips()
                .get(0).get("ip_address"));
            heatPort.setMacAddress(portResourceData.getResource().getAttributes().getMac_address());
            heatPort.setPortName(portResourceData.getResource().getAttributes().getName());
            ports.add(heatPort);
            break;

          case "OS::Neutron::FloatingIP":
            ResourceData<FloatingIpAttributes> floatingIPResourceData = mapper.readValue(
                showResourceData, new TypeReference<ResourceData<FloatingIpAttributes>>() {});
            floatingIps.add(floatingIPResourceData.getResource().getAttributes());
            String floatingIP =
                floatingIPResourceData.getResource().getAttributes().getFloating_ip_address();
            Logger.info("FloatingIP Resource Address: " + floatingIP);
            break;

          case "OS::Neutron::Net":
            // TODO
            break;
          case "OS::Neutron::Subnet":
            // TODO
            break;
          case "OS::Neutron::RouterInterface":
            // TODO
            break;
          case "OS::Neutron::Router":
            // TODO
            break;
          default:
            Logger.error("Unhandled Resource Type: " + resource.getResource_type());
        }
      }

      for (int i = 0; i < ports.size(); i++) {
        for (FloatingIpAttributes floatingIP : floatingIps) {
          if (portsAtts.get(i).getId().equals(floatingIP.getPort_id())) {
            ports.get(i).setFloatinIp(floatingIP.getFloating_ip_address());
          }
        }
      }

      // Create the composition object
      composition.setServers(servers);
      composition.setPorts(ports);
      composition.setNets(networks);
      composition.setRouters(routers);

    } catch (Exception e) {
      Logger.error("Runtime error getting composition for stack : " + stackName + " error message: "
          + e.getMessage());
    }

    return composition;
  }
}
