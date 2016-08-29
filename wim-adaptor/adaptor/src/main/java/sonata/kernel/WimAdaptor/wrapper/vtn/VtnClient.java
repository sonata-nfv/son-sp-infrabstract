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

package sonata.kernel.WimAdaptor.wrapper.vtn;

import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by smendel on 4/20/16.
 * <p/>
 * This class wraps a Heat Client written in python when instantiated the connection details of the
 * OpenStack instance should be provided
 */
public class VtnClient {

  private static final String PYTHON2_7 = "python2.7";

  private static final String ADAPTOR_HEAT_API_PY = "/adaptor/vtn-odl.py";

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(VtnClient.class);

  private String url; // url of the VTN

  private String userName; // VTN Client user

  private String password; // VTN Client password


  /**
   * Construct a new VTN Client.
   * 
   * @param url of the VTN endpoint
   * @param userName to log into the OpenStack service
   * @param password to log into the OpenStack service
   * @param tenantName to log into the OpenStack service
   */
  public VtnClient(String url, String userName, String password) {
    this.url = url;
    this.userName = userName;
    this.password = password;
  }

  /**
   * create VTN flow.
   *
   * @param vtnName the name of the VTN this flow belongs
   * @param condName a name for the flow condition
   * @return true if the operation has been completed without error, false otherwise
   */
  public boolean setupFlow(String vtnName, String condName) {
    String out = null;
    String string = null;
    vtnName = formatField(vtnName);
    condName = formatField(condName);

    Logger.info("Creating Flow Rule: " + condName + " for vtn: " + vtnName);

    try {

      // Call the python client for creating the stack
      ProcessBuilder processBuilder =
          new ProcessBuilder(PYTHON2_7, ADAPTOR_HEAT_API_PY, "--configuration", url, userName,
              password, "-sf", condName, "10.100.16.40/32", "10.100.32.40/32", vtnName);
      Process process = processBuilder.start();

      // Read the errors of creating the stack
      BufferedReader stdError = new BufferedReader(
          new InputStreamReader(process.getErrorStream(), Charset.forName("UTF-8")));
      if (stdError.read() != -1) {
        Logger.error("The errors of creating VTN flow (if any):");
        while ((string = stdError.readLine()) != null) {
          Logger.error("  " + string);
        }
      }
      stdError.close();
      // Read the results of creating the stack
      BufferedReader stdInput = new BufferedReader(
          new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")));
      Logger.info("The results of creating the VTN flow:");
      while ((string = stdInput.readLine()) != null) {
        Logger.info("  " + string);
        out = string;
      }
      stdInput.close();
      process.destroy();

    } catch (Exception e) {
      Logger.error("Runtime error creating VTN : " + vtnName + " error message: " + e.getMessage(),
          e);
      return false;
    }
    if (!out.equals("SUCCESS")) {
      Logger.error("unexpected response: " + out);
      return false;
    }
    return true;
  }

  private String formatField(String field) {

    String out, temp;
    temp = field.replace("-", "");
    if (temp.length() > 31) {
      out = temp.substring(0, 30);
    } else {
      out = temp;
    }
    return out;
  }

  /**
   * configure the VTN .
   *
   * @param vtnName the name of the VTN to configure
   * @return true if the operation has been completed without error, false otherwise
   */
  public boolean setupVtn(String vtnName) {

    String string = null;
    String out = null;
    vtnName = formatField(vtnName);
    Logger.info("Creating VTN: " + vtnName);

    try {

      // Call the python client for creating the stack
      ProcessBuilder processBuilder = new ProcessBuilder(PYTHON2_7, ADAPTOR_HEAT_API_PY,
          "--configuration", url, userName, password, "-i", vtnName);
      Process process = processBuilder.start();

      // Read the errors of creating the stack
      BufferedReader stdError = new BufferedReader(
          new InputStreamReader(process.getErrorStream(), Charset.forName("UTF-8")));
      if (stdError.read() != -1) {
        Logger.error("The errors of creating VTN (if any):");
        while ((string = stdError.readLine()) != null) {
          Logger.error("  " + string);
        }
      }
      stdError.close();
      // Read the results of creating the stack
      BufferedReader stdInput = new BufferedReader(
          new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")));
      Logger.info("The results of creating the VTN:");
      while ((string = stdInput.readLine()) != null) {
        Logger.info("  " + string);
        out = string;
      }
      stdInput.close();
      process.destroy();

      if (!out.equals("SUCCESS")) {
        Logger.error("unexpected response: " + out);
        return false;
      }

    } catch (Exception e) {
      Logger.error("Runtime error creating VTN : " + vtnName + " error message: " + e.getMessage(),
          e);
      Logger.error("unexpected response: " + out);
      return false;
    }

    return true;
  }

  /**
   * Delete VTN.
   * 
   * @param vtnName - used for logging, usually service tenant
   * @return - true if the operation was completed successfully
   */
  public boolean deleteVtn(String vtnName) {

    String isDeleted = null;
    String string = null;
    vtnName = formatField(vtnName);
    Logger.info("Deleting VTN: " + vtnName);

    try {
      // Call the python client for deleting of the stack
      ProcessBuilder processBuilder = new ProcessBuilder(PYTHON2_7, ADAPTOR_HEAT_API_PY,
          "--configuration", url, userName, password, "-d", vtnName);
      Process process = processBuilder.start();

      // Read the results
      BufferedReader stdInput = new BufferedReader(
          new InputStreamReader(process.getInputStream(), Charset.forName("UTF-8")));
      while ((string = stdInput.readLine()) != null) {
        // Logger.info(string);
        isDeleted = string;
      }
      stdInput.close();
      process.destroy();

      Logger.info("Request was sent for VTN: " + vtnName + " : " + isDeleted);
    } catch (Exception e) {
      Logger.error(
          "Runtime error when deleting stack : " + vtnName + " error message: " + e.getMessage(),
          e);
      return false;
    }

    return isDeleted.equals("SUCCESS");

  }

  @Override
  public String toString() {
    return "VtnClient{" + "url='" + url + '\'' + ", userName='" + userName + '\'' + ", password='"
        + password + '\'' + '}';
  }

}
