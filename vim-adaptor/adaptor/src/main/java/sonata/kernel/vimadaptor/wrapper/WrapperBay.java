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

package sonata.kernel.vimadaptor.wrapper;

import org.slf4j.LoggerFactory;

import sonata.kernel.vimadaptor.commons.VimNetTable;

import java.util.ArrayList;
import java.util.Hashtable;

public class WrapperBay {

  private static WrapperBay myInstance = null;
  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(WrapperBay.class);

  private VimRepo repository = null;

  private Hashtable<String, ComputeWrapper> computeWrapperCache;
  private Hashtable<String, NetworkWrapper> networkWrapperCache;

  private WrapperBay() {
    computeWrapperCache = new Hashtable<String, ComputeWrapper>();
    networkWrapperCache = new Hashtable<String, NetworkWrapper>();
  }

  /**
   * Singleton method to get the instance of the wrapperbay.
   * 
   * @return the instance of the wrapperbay
   */
  public static WrapperBay getInstance() {
    if (myInstance == null) {
      myInstance = new WrapperBay();
    }
    return myInstance;
  }


  /**
   * Set the Database reader/writer to use as a repository for VIMs.
   * 
   * @param repo the Database reader/writer to store the wrappers
   */
  public void setRepo(VimRepo repo) {
    this.repository = repo;
  }


  /**
   * Register a new compute wrapper.
   * 
   * @param config The configuration object representing the Wrapper to register
   * @return a JSON representing the output of the API call
   */
  public String registerComputeWrapper(WrapperConfiguration config) {
    Wrapper newWrapper = WrapperFactory.createWrapper(config);
    String output = "";
    if (newWrapper == null) {
      output = "{\"request_status\":\"ERROR\",\"message\":\"Cannot Attach To Vim\"}";
    } else if (newWrapper.getType().equals(WrapperType.COMPUTE)) {
      //WrapperRecord record = new WrapperRecord(newWrapper, config, null);
      this.repository.writeVimEntry(config.getUuid(),newWrapper);
      output = "{\"request_status\":\"COMPLETED\",\"uuid\":\"" + config.getUuid() + "\"}";
    }

    return output;
  }

  /**
   * Order the list of available compute wrapper to find the best basing on an OptimizationStrategy.
   *
   * @return A ComputeWrapper object, the best according to the OptimizationStrategy.
   */
  public ComputeWrapper getBestComputeWrapper() {
    ComputeWrapper bestWrapper = null;

    return bestWrapper;
  }

  /**
   * Utility methods to clear registry tables.
   */
  public void clear() {}

  /**
   * Remove a registered compute wrapper from the IA.
   * 
   * @param uuid the uuid of the wrapper to remove
   * @return a JSON representing the output of the API call
   */
  public String removeComputeWrapper(String uuid) {
    VimNetTable.getInstance().deregisterVim(uuid);
    repository.removeVimEntry(uuid);
    return "{\"request_status\":\"COMPLETED\"}";
  }


  /**
   * Return the list of the registered compute VIMs.
   * 
   * @return an arraylist of String representing the UUIDs of the registered VIMs
   */
  public ArrayList<String> getComputeWrapperList() {
    return repository.getComputeVims();

  }

  /**
   * Return the wrapper of the compute VIM identified by the given UUID.
   * 
   * @param vimUuid the UUID of the compute VIM
   * 
   * @return the wrapper of the requested VIM or null if the UUID does not correspond to a
   *         registered VIM
   */
  public ComputeWrapper getComputeWrapper(String vimUuid) {
    if (computeWrapperCache.containsKey(vimUuid))
      return (ComputeWrapper) computeWrapperCache.get(vimUuid);
    ComputeWrapper vimEntry = (ComputeWrapper) this.repository.readVimEntry(vimUuid);
    if (vimEntry == null) {
      return null;
    } else {
      computeWrapperCache.put(vimUuid, vimEntry);
      return vimEntry;
    }
  }



  /**
   * Registre a new Network VIM to the wrapper bay.
   * 
   * @param config
   * @param computeVimRef
   * @return a JSON formatte string with the result of the registration.
   */
  public String registerNetworkWrapper(WrapperConfiguration config, String computeVimRef) {
    Wrapper newWrapper = WrapperFactory.createWrapper(config);
    String output = "";
    if (newWrapper == null) {
      output = "{\"request_status\":\"ERROR\",\"message\":\"Cannot Attach To Vim\"}";
    } else {
      this.repository.writeVimEntry(config.getUuid(), newWrapper);
      this.repository.writeNetworkVimLink(computeVimRef, config.getUuid());
      output = "{\"request_status\":\"COMPLETED\",\"uuid\":\"" + config.getUuid() + "\"}";
    }
    return output;
  }


  /**
   * Return the VimRepo
   * 
   * @return the VimRepo object.
   */
  public VimRepo getVimRepo() {
    return repository;
  }

  /**
   * @param uuid
   * @return
   */
  public String removeNetworkWrapper(String uuid) {
    this.repository.removeNetworkVimLink(uuid);
    this.repository.removeVimEntry(uuid);
    return "{\"request_status\":\"COMPLETED\"}";
  }

  /**
   * Return a generic Vim Wrapper for the given Vim UUID
   * 
   * @param uuid
   * @return
   */
  public Wrapper getWrapper(String uuid) {
    return this.repository.readVimEntry(uuid);
  }

  /**
   * @param vimUuid
   * @return
   */
  public NetworkWrapper getNetworkVimFromComputeVimUuid(String vimUuid) {
    String netVimUuid = this.repository.getNetworkVimFromComputeVimUuid(vimUuid);
    if (netVimUuid == null) {
      Logger.error("can't find Networking VIM for compute VIM UUID: " + vimUuid);
    }
    if (networkWrapperCache.containsKey(netVimUuid)) {
      return networkWrapperCache.get(netVimUuid);
    }
    NetworkWrapper netWrapper = this.repository.getNetworkVim(netVimUuid);
    if (netWrapper == null) {
      return null;
    } else {
      networkWrapperCache.put(vimUuid, netWrapper);
      return netWrapper;
    }
  }

  /**
   * @return
   */
  public ArrayList<String> getNetworkWrapperList() {
    return repository.getNetworkVims();
  }



}
