/**
 * Copyright (c) 2015 SONATA-NFV, UCL, NOKIA, THALES, NCSR Demokritos ALL RIGHTS RESERVED.
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
 * Neither the name of the SONATA-NFV, UCL, NOKIA, THALES NCSR Demokritos nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * This work has been performed in the framework of the SONATA project, funded by the European
 * Commission under Grant number 671517 through the Horizon 2020 and 5G-PPP programmes. The authors
 * would like to acknowledge the contributions of their colleagues of the SONATA partner consortium
 * (www.sonata-nfv.eu).
 *
 * @author Dario Valocchi (Ph.D.), UCL
 * @author Bruno Vidalenc (Ph.D.), THALES
 * 
 */

package sonata.kernel.VimAdaptor.wrapper.openstack;



import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.LoggerFactory;
import sonata.kernel.VimAdaptor.wrapper.ResourceUtilisation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * This class wraps a Nova Client written in python when instantiated the onnection details of the
 * OpenStack instance should be provided.
 * 
 */
public class OpenStackNovaClient {

  private static final String ADAPTOR_NOVA_API_PY = "/adaptor/nova-api.py";

  private static final String PYTHON2_7 = "python2.7";

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(OpenStackNovaClient.class);

  private String url; // url of the OpenStack Client

  private String userName; // OpenStack Client user

  private String password; // OpenStack Client password

  private String tenantName; // OpenStack tenant name


  /**
   * Construct a new Openstack Nova Client.
   *
   * @param url of the OpenStack endpoint
   * @param userName to log into the OpenStack service
   * @param password to log into the OpenStack service
   * @param tenantName to log into the OpenStack service
   */
  public OpenStackNovaClient(String url, String userName, String password, String tenantName) {
    this.url = url;
    this.userName = userName;
    this.password = password;
    this.tenantName = tenantName;
  }

  /**
   * Get the limits and utilisation.
   * 
   * @return a ResourceUtilisation Object with the limits and utilization for this tenant
   */
  public ResourceUtilisation getResourceUtilizasion() {
    ResourceUtilisation resources = null;

    Logger.info("Getting limits");

    try {
      // Call the python client for the flavors of the openstack instance
      ProcessBuilder processBuilder = new ProcessBuilder(PYTHON2_7, ADAPTOR_NOVA_API_PY,
          "--configuration", url, userName, password, tenantName, "--limits");

      Process process = processBuilder.start();

      BufferedReader stdInput = new BufferedReader(
          new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")));

      StringBuilder builder = new StringBuilder();
      String string = null;
      while ((string = stdInput.readLine()) != null) {
        Logger.info("Line: " + string);
        builder.append(string);
      }
      stdInput.close();
      process.destroy();
      String resourceString = builder.toString();
      resourceString = resourceString.replace("'", "\"");
      Logger.info("Resources: " + resourceString);
      ObjectMapper mapper = new ObjectMapper(new JsonFactory());
      mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
      // Logger.info(compositionString);
      resources = mapper.readValue(resourceString, ResourceUtilisation.class);


    } catch (Exception e) {
      Logger.error("Runtime error getting openstack limits" + " error message: " + e.getMessage(),
          e);
    }

    return resources;
  }

  /**
   * Get the flavors.
   *
   * @return the flavors
   */
  public ArrayList<Flavor> getFlavors() {

    String string = null;
    Flavor flavor = null;
    String flavorName = null;
    int cpu;
    int ram;
    int disk;
    ArrayList<Flavor> flavors = new ArrayList<Flavor>();
    String[] flavorString;

    Logger.info("Getting flavors");

    try {
      // Call the python client for the flavors of the openstack instance
      ProcessBuilder processBuilder = new ProcessBuilder(PYTHON2_7, ADAPTOR_NOVA_API_PY,
          "--configuration", url, userName, password, tenantName, "--flavors");
      Process process = processBuilder.start();

      Logger.info("The available flavors are:");

      // Read the flavors
      BufferedReader stdInput = new BufferedReader(
          new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")));
      while ((string = stdInput.readLine()) != null) {
        Logger.info(string);
        flavorString = string.split(" ");
        flavorName = flavorString[0];
        cpu = Integer.parseInt(flavorString[2]);
        ram = Integer.parseInt(flavorString[4]);
        disk = Integer.parseInt(flavorString[6]);
        flavor = new Flavor(flavorName, cpu, ram, disk);
        flavors.add(flavor);
      }
      stdInput.close();

    } catch (Exception e) {
      Logger.error("Runtime error getting openstack flavors" + " error message: " + e.getMessage());
    }

    return flavors;

  }


  @Override
  public String toString() {
    return "OpenStackNovaClient{" + "url='" + url + '\'' + ", userName='" + userName + '\''
        + ", password='" + password + '\'' + ", tenantName='" + tenantName + '\'' + '}';
  }

}
