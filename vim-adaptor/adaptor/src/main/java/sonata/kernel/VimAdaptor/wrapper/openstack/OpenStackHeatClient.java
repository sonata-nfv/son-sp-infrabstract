/**
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
 * @author Sharon Mendel Brin (Ph.D.), Nokia
 * @author Dario Valocchi (Ph.D.), UCL
 * @author Akis Kourtis, NCSR Demokritos
 *
 */

package sonata.kernel.VimAdaptor.wrapper.openstack;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;

import sonata.kernel.VimAdaptor.commons.heat.StackComposition;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.JavaStackCore;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.JavaStackUtils;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.stacks.StackData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by smendel on 4/20/16.
 * <p/>
 * This class wraps a Heat Client written in python when instantiated the connection details of the
 * OpenStack instance should be provided
 */
public class OpenStackHeatClient {

  private static final String PYTHON2_7 = "python2.7";

  private static final String ADAPTOR_HEAT_API_PY = "/adaptor/heat-api.py";

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

    Logger.debug("User: " + userName + " Tenant: " + tenantName + " Pass: " + password);

    javaStack = JavaStackCore.getJavaStackCore();
    javaStack.setEndpoint(url);
    javaStack.setUsername(userName);
    javaStack.setPassword(password);
    javaStack.setTenant_id(tenantName);

    //Authenticate
    try {
      javaStack.authenticateClient();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create stack.
   *
   * @param stackName - usually service tenant
   * @param template - the content of the hot template that describes the file
   * @return - the uuid of the created stack, if the process failed the returned value is null
   */
  public String createStack(String stackName, String template) {
    String uuid = null;
    template = "   heat_template_version: '2013-05-23'\n" +
            "  description: Simple template to test heat commands\n" +
            "  parameters:\n" +
            "    flavor:\n" +
            "      default: m1.tiny\n" +
            "      type: string\n" +
            "  resources:\n" +
            "    my_instance:\n" +
            "      type: OS::Nova::Server\n" +
            "      properties:\n" +
            "        image: 356d8e0f-3332-48bc-a306-8b51876ef3c5\n" +
            "        flavor: m1.small\n" +
            "        key_name: test\n" +
            "        networks:\n" +
            "        - network: sonata-subnet-one\n";

    Logger.info("Creating stack: " + stackName);
    Logger.debug("Template:\n" + template);

    try {
      mapper = new ObjectMapper();
      String createStackResponse = JavaStackUtils.convertHttpResponseToString(javaStack.createStack(template, stackName));
      StackData stack = mapper.readValue(createStackResponse, StackData.class);
      uuid = stack.getStack().getId();

    } catch (Exception e) {
      Logger.error(
              "Runtime error creating stack : " + stackName + " error message: " + e.getMessage());
    }

    return uuid;
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
    Logger.info("Getting status for stack: " + stackName);
    try {
      mapper = new ObjectMapper();
      String findStackResponse = JavaStackUtils.convertHttpResponseToString(javaStack.findStack(stackName));
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
    Logger.info("Deleting stack: " + stackName);

    try {
      String stackIdToDelete = new ObjectMapper().readValue(
              JavaStackUtils.convertHttpResponseToString(
                      javaStack.findStack(stackName)), StackData.class).getStack().getId();
      javaStack.deleteStack(stackName, stackIdToDelete);
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

    StackComposition composition = null;
    StringBuilder builder = new StringBuilder();
    String line = null;
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(PYTHON2_7, ADAPTOR_HEAT_API_PY,
          "--configuration", url, userName, password, tenantName, "--composition", uuid);
      Process process = processBuilder.start();

      // Read the status of the stack
      BufferedReader stdInput = new BufferedReader(
          new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")));
      while ((line = stdInput.readLine()) != null) {
        builder.append(line);
      }
      stdInput.close();
      process.destroy();
      String compositionString = builder.toString();
      compositionString = compositionString.replace("'", "\"");
      compositionString = compositionString.replace(": u", " : ");
      ObjectMapper mapper = new ObjectMapper(new JsonFactory());
      mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
      Logger.info(compositionString);
      composition = mapper.readValue(compositionString, StackComposition.class);

    } catch (Exception e) {
      Logger.error("Runtime error getting stack status for stack : " + stackName
          + " error message: " + e.getMessage());
    }

    return composition;
  }
}
