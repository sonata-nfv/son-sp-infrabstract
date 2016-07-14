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

import sonata.kernel.VimAdaptor.commons.heat.StackComposition;

import java.io.BufferedReader;
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
    String string = null;

    // todo - log in debug mode the template as well

    System.out.println("Creating stack: " + stackName);

    try {

      // Call the python client for creating the stack
      ProcessBuilder processBuilder = new ProcessBuilder(PYTHON2_7, ADAPTOR_HEAT_API_PY,
          "--configuration", url, userName, password, tenantName, "--create", stackName, template);
      Process process = processBuilder.start();

      // Read the errors of creating the stack
      BufferedReader stdError = new BufferedReader(
          new InputStreamReader(process.getErrorStream(), Charset.forName("UTF-8")));
      if (stdError.read() != -1) {
        System.out.println("The errors of creating stack (if any):");
        while ((string = stdError.readLine()) != null) {
          System.out.println(string);
        }
      }
      stdError.close();
      // Read the results of creating the stack
      BufferedReader stdInput = new BufferedReader(
          new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")));
      System.out.println("The results of creating the stack:");
      while ((string = stdInput.readLine()) != null) {
        System.out.println(string);
        uuid = string;
      }
      stdInput.close();
      process.destroy();

      if (uuid != null) {
        System.out.println("UUID of new stack: " + uuid);
      }

    } catch (Exception e) {
      System.out.println(
          "Runtime error creating stack : " + stackName + " error message: " + e.getMessage());
    }

    return uuid;
  }


  /**
   * Get the status of existing stack.
   *
   * @param stackName used for logging, usually service tenant
   * @param uuid OpenStack UUID of the stack
   * @return the OpenStack status of the stack
   */
  public String getStackStatus(String stackName, String uuid) {

    String status = null;
    String string = null;
    System.out.println("Getting status for stack: " + stackName);

    try {
      // Call the python client for the status of the stack
      ProcessBuilder processBuilder = new ProcessBuilder(PYTHON2_7, ADAPTOR_HEAT_API_PY,
          "--configuration", url, userName, password, tenantName, "--status", uuid);
      Process process = processBuilder.start();

      // Read the status of the stack
      BufferedReader stdInput = new BufferedReader(
          new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")));

      while ((string = stdInput.readLine()) != null) {
        System.out.println(string);
        status = string;
      }
      stdInput.close();
      process.destroy();
      System.out
          .println("The status of stack: " + stackName + " with uuid: " + uuid + " : " + status);
    } catch (Exception e) {
      System.out.println("Runtime error getting stack status for stack : " + stackName
          + " error message: " + e.getMessage());
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
    String string = null;

    System.out.println("Deleting stack: " + stackName);

    try {
      // Call the python client for deleting of the stack
      ProcessBuilder processBuilder = new ProcessBuilder(PYTHON2_7, ADAPTOR_HEAT_API_PY,
          "--configuration", url, userName, password, tenantName, "--delete", uuid);
      Process process = processBuilder.start();

      // Read the results
      BufferedReader stdInput = new BufferedReader(
          new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")));
      while ((string = stdInput.readLine()) != null) {
        // System.out.println(string);
        isDeleted = string;
      }
      stdInput.close();
      process.destroy();

      System.out.println(
          "Request was sent for stack: " + stackName + " with uuid: " + uuid + " : " + isDeleted);
    } catch (Exception e) {
      System.out.println(
          "Runtime error when deleting stack : " + stackName + " error message: " + e.getMessage());
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
      // System.out.println(compositionString);
      composition = mapper.readValue(compositionString, StackComposition.class);

    } catch (Exception e) {
      System.out.println("Runtime error getting stack status for stack : " + stackName
          + " error message: " + e.getMessage());
    }

    return composition;
  }
}
