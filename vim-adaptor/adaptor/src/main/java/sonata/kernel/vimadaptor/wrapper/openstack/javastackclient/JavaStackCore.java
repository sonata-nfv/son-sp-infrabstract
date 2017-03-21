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
 * @author Adel Zaalouk (Ph.D.), NEC
 * 
 * @author Dario Valocchi (Ph.D.), UCL
 */

package sonata.kernel.vimadaptor.wrapper.openstack.javastackclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import sonata.kernel.vimadaptor.wrapper.openstack.javastackclient.models.authentication.AuthenticationData;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class JavaStackCore {

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(JavaStackCore.class);
  private String endpoint;
  private String username;
  private String password;
  private String projectId;
  private ObjectMapper mapper;
  private String tokenId;
  // private String image_id;
  private boolean isAuthenticated = false;

  private JavaStackCore() {}

  public enum Constants {
    AUTH_PORT("5000"), HEAT_PORT("8004"), IMAGE_PORT("9292"), COMPUTE_PORT("8774"), HEAT_VERSION(
        "v1"), IMAGE_VERSION("v2"), COMPUTE_VERSION(
            "v2"), AUTHTOKEN_HEADER("X-AUTH-TOKEN"), AUTH_URI("/v3/auth/tokens");

    private final String constantValue;

    Constants(String constantValue) {
      this.constantValue = constantValue;
    }

    @Override
    public String toString() {
      return this.constantValue;
    }
  }

  private static class SingeltonJavaStackCoreHelper {
    private static final JavaStackCore _javaStackCore = new JavaStackCore();
  }

  public static JavaStackCore getJavaStackCore() {
    return SingeltonJavaStackCoreHelper._javaStackCore;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getTenantId() {
    return this.projectId;
  }

  public void setTenantId(String tenant_id) {
    this.projectId = tenant_id;
  }

  public String getTokenId() {
    return this.tokenId;
  }

  public synchronized void authenticateClient() throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost post;
    HttpResponse response = null;

    StringBuilder buildUrl = new StringBuilder();
    buildUrl.append("http://");
    buildUrl.append(this.endpoint);
    buildUrl.append(":");
    buildUrl.append(Constants.AUTH_PORT.toString());
    buildUrl.append(Constants.AUTH_URI.toString());

    post = new HttpPost(buildUrl.toString());

    String body = String.format(
        // "{ \"auth\": {\"scope\": {\"project\": {\"name\": \"%s\"}}, \"identity\": { \"methods\":
        // [\"password\"], \"password\": { \"user\": { \"name\": \"%s\", \"domain\": { \"name\":
        // \"default\" }, \"password\": \"%s\" }}}}}",
        "{ \"auth\": {\"identity\": { \"methods\": [\"password\"], \"password\": { \"user\": { \"name\": \"%s\", \"domain\": { \"name\": \"default\" }, \"password\": \"%s\" }}}}}",
        // this.getTenantId(),
        this.username, this.password);
    Logger.debug("[JavaStack] Authenticating client...");
    post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
    // Logger.debug("[JavaStack] " + post.toString());
    // Logger.debug("[JavaStack] " + body);
    response = httpClient.execute(post);
    // Logger.debug("[JavaStack] Authentication response:");
    // Logger.debug(response.toString());
    mapper = new ObjectMapper();

    AuthenticationData auth = mapper.readValue(JavaStackUtils.convertHttpResponseToString(response),
        AuthenticationData.class);
    if (response.containsHeader("X-Subject-Token")) {
      this.tokenId = response.getFirstHeader("X-Subject-Token").getValue();
      if (auth.getToken().getProject() != null) {
        this.projectId = auth.getToken().getProject().getId();
      } // FIXME check the token structure to see what we get back and what we need to memorise.
      this.isAuthenticated = true;
    }


  }

  public synchronized HttpResponse createStack(String template, String stackName)
      throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpResponseFactory factory = new DefaultHttpResponseFactory();
    HttpPost createStack = null;
    HttpResponse response = null;

    String jsonTemplate = JavaStackUtils.convertYamlToJson(template);
    JSONObject modifiedObject = new JSONObject();
    modifiedObject.put("stack_name", stackName);
    modifiedObject.put("template", new JSONObject(jsonTemplate));

    if (this.isAuthenticated) {
      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(this.endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.HEAT_PORT.toString());
      buildUrl.append(String.format("/%s/%s/stacks", Constants.HEAT_VERSION.toString(), projectId));

      // Logger.debug(buildUrl.toString());
      createStack = new HttpPost(buildUrl.toString());
      createStack
          .setEntity(new StringEntity(modifiedObject.toString(), ContentType.APPLICATION_JSON));
      // Logger.debug(this.token_id);
      createStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);

      // Logger.debug("Request: " + createStack.toString());
      // Logger.debug("Request body: " + modifiedObject.toString());

      response = httpClient.execute(createStack);
      int statusCode = response.getStatusLine().getStatusCode();
      String responsePhrase = response.getStatusLine().getReasonPhrase();

      // Logger.debug("Response: " + response.toString());
      // Logger.debug("Response body:");

      if (statusCode != 201) {
        BufferedReader in =
            new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = null;

        while ((line = in.readLine()) != null)
          Logger.debug(line);
      }


      return (statusCode == 201)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode,
              responsePhrase + ". Create Failed with Status: " + statusCode), null);
    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }
  }

  public synchronized HttpResponse updateStack(String stackName, String stackUuid, String template)
      throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpResponseFactory factory = new DefaultHttpResponseFactory();
    HttpPatch updateStack = null;
    HttpResponse response = null;

    String jsonTemplate = JavaStackUtils.convertYamlToJson(template);
    JSONObject modifiedObject = new JSONObject();
    modifiedObject.put("stack_name", stackName);
    modifiedObject.put("template", new JSONObject(jsonTemplate));

    if (this.isAuthenticated) {
      Logger.debug("");
      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(this.endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.HEAT_PORT.toString());
      buildUrl.append(String.format("/%s/%s/stacks/%s/%s", Constants.HEAT_VERSION.toString(),
          projectId, stackName, stackUuid));

      // Logger.debug(buildUrl.toString());
      updateStack = new HttpPatch(buildUrl.toString());
      updateStack
          .setEntity(new StringEntity(modifiedObject.toString(), ContentType.APPLICATION_JSON));
      // Logger.debug(this.token_id);
      updateStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);

      Logger.debug("Request: " + updateStack.toString());
      Logger.debug("Request body: " + modifiedObject.toString());

      response = httpClient.execute(updateStack);
      int statusCode = response.getStatusLine().getStatusCode();
      String responsePhrase = response.getStatusLine().getReasonPhrase();

      Logger.debug("Response: " + response.toString());
      Logger.debug("Response body:");


      if (statusCode != 202) {
        BufferedReader in =
            new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = null;

        while ((line = in.readLine()) != null)
          Logger.debug(line);
      }

      return (statusCode == 202)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode,
              responsePhrase + ". Create Failed with Status: " + statusCode), null);
    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }
  }

  public synchronized HttpResponse deleteStack(String stackName, String stackId)
      throws IOException {

    HttpDelete deleteStack;
    HttpClient httpClient = HttpClientBuilder.create().build();
    if (this.isAuthenticated) {

      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(this.endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.HEAT_PORT.toString());
      buildUrl.append(String.format("/%s/%s/stacks/%s/%s", Constants.HEAT_VERSION.toString(),
          projectId, stackName, stackId));
      deleteStack = new HttpDelete(buildUrl.toString());
      deleteStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);

      return httpClient.execute(deleteStack);
    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }
  }

  public synchronized HttpResponse findStack(String stackIdentity) throws IOException {
    HttpGet findStack;
    HttpClient httpClient = HttpClientBuilder.create().build();

    if (this.isAuthenticated) {

      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(this.endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.HEAT_PORT.toString());
      buildUrl.append(String.format("/%s/%s/stacks/%s", Constants.HEAT_VERSION.toString(),
          this.projectId, stackIdentity));

      // Logger.debug("URL: " + buildUrl);
      // Logger.debug("Token: " + this.token_id);

      findStack = new HttpGet(buildUrl.toString());
      findStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);

      return httpClient.execute(findStack);

    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }

  }

  public synchronized HttpResponse listStacks(String endpoint) throws IOException {


    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpResponseFactory factory = new DefaultHttpResponseFactory();
    HttpResponse response = null;
    HttpGet listStacks = null;

    if (this.isAuthenticated) {

      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.HEAT_PORT.toString());
      buildUrl.append(
          String.format("/%s/%s/stacks", Constants.HEAT_VERSION.toString(), this.projectId));

      System.out.println(buildUrl);
      System.out.println(this.tokenId);

      listStacks = new HttpGet(buildUrl.toString());
      listStacks.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);

      response = httpClient.execute(listStacks);
      int status_code = response.getStatusLine().getStatusCode();

      return (status_code == 200)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, status_code,
              "List Failed with Status: " + status_code), null);

    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }

  }

  public synchronized HttpResponse getStackTemplate(String stackName, String stackId)
      throws IOException, URISyntaxException {

    HttpResponseFactory factory = new DefaultHttpResponseFactory();
    HttpClient httpclient = HttpClientBuilder.create().build();
    HttpGet getStackTemplate = null;
    HttpResponse response = null;

    if (isAuthenticated) {

      URIBuilder builder = new URIBuilder();
      String path = String.format("/%s/%s/stacks/%s/%s/template", Constants.HEAT_VERSION.toString(),
          this.projectId, stackName, stackId);

      builder.setScheme("http").setHost(endpoint)
          .setPort(Integer.parseInt(Constants.HEAT_PORT.toString())).setPath(path);

      URI uri = builder.build();

      getStackTemplate = new HttpGet(uri);
      getStackTemplate.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);

      Logger.debug("Request: " + getStackTemplate.toString());

      response = httpclient.execute(getStackTemplate);
      int status_code = response.getStatusLine().getStatusCode();

      Logger.debug("Response: " + response.toString());

      return (status_code == 200)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, status_code,
              "Get Template Failed with Status: " + status_code), null);

    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }

  }

  public synchronized HttpResponse showResourceData(String stackName, String stackId,
      String resourceName) throws IOException, URISyntaxException {
    HttpResponseFactory factory = new DefaultHttpResponseFactory();
    HttpClient httpclient = HttpClientBuilder.create().build();
    HttpGet showResourceData = null;
    HttpResponse response = null;

    if (isAuthenticated) {
      URIBuilder builder = new URIBuilder();
      String path = String.format("/%s/%s/stacks/%s/%s/resources/%s",
          Constants.HEAT_VERSION.toString(), this.projectId, stackName, stackId, resourceName);

      builder.setScheme("http").setHost(endpoint)
          .setPort(Integer.parseInt(Constants.HEAT_PORT.toString())).setPath(path);

      URI uri = builder.build();

      showResourceData = new HttpGet(uri);
      showResourceData.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);

      response = httpclient.execute(showResourceData);
      int status_code = response.getStatusLine().getStatusCode();

      return (status_code == 200)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, status_code,
              "List Failed with Status: " + status_code), null);

    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }
  }

  public synchronized HttpResponse listStackResources(String stackName, String stackId,
      ArrayList<String> resources) throws IOException, URISyntaxException {
    HttpResponseFactory factory = new DefaultHttpResponseFactory();
    HttpClient httpclient = HttpClientBuilder.create().build();
    HttpGet listResources = null;
    HttpResponse response = null;

    if (isAuthenticated) {
      URIBuilder builder = new URIBuilder();
      String path = String.format("/%s/%s/stacks/%s/%s/resources",
          Constants.HEAT_VERSION.toString(), this.projectId, stackName, stackId);

      builder.setScheme("http").setHost(endpoint)
          .setPort(Integer.parseInt(Constants.HEAT_PORT.toString())).setPath(path);

      URI uri = builder.build();

      listResources = new HttpGet(uri);
      listResources.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);


      response = httpclient.execute(listResources);
      int status_code = response.getStatusLine().getStatusCode();

      return (status_code == 200)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, status_code,
              "List Failed with Status: " + status_code), null);

    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }
  }

  public synchronized HttpResponse createImage(String template, String containerFormat,
      String diskFormat, String name) throws IOException {
    HttpPost createImage;
    HttpClient httpClient = HttpClientBuilder.create().build();

    if (this.isAuthenticated) {
      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(this.endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.IMAGE_PORT.toString());
      buildUrl.append(String.format("/%s/images", Constants.IMAGE_VERSION.toString()));

      createImage = new HttpPost(buildUrl.toString());
      String requestBody = String.format(
          "{ \"container_format\": \"bare\"," + "\"disk_format\": \"raw\"," + " \"name\": \"%s\""
              + ",\"visibility\":\"public\""
              + "}",
          name);

      createImage.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
      createImage.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);

    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }
    return httpClient.execute(createImage);
  }

  public synchronized HttpResponse uploadBinaryImageData(String endpoint, String imageId,
      String binaryImageLocalFilePath) throws IOException {

    HttpPut uploadImage;
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpResponse response;
    if (this.isAuthenticated) {
      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(this.endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.IMAGE_PORT.toString());
      buildUrl
          .append(String.format("/%s/images/%s/file", Constants.IMAGE_VERSION.toString(), imageId));

      uploadImage = new HttpPut(buildUrl.toString());
      uploadImage.setHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);
      uploadImage.setHeader("Content-Type", "application/octet-stream");
      uploadImage.setEntity(new FileEntity(new File(binaryImageLocalFilePath)));
      response = httpClient.execute(uploadImage);
      Logger.debug("[JavaStackCore] Response of binary Image upload");
      Logger.debug(response.toString());      
    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }
    
    return response;
  }

  public HttpResponse listImages() throws IOException {

    Logger.debug("RESTful request to glance image list");
    HttpGet listImages = null;
    HttpResponse response = null;

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpResponseFactory factory = new DefaultHttpResponseFactory();

    if (isAuthenticated) {

      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.IMAGE_PORT.toString());
      buildUrl.append(String.format("/%s/images", Constants.IMAGE_VERSION.toString()));

      listImages = new HttpGet(buildUrl.toString());
      listImages.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);

      Logger.debug("HTTP request:");
      Logger.debug(listImages.toString());
      
      response = httpClient.execute(listImages);
      Logger.debug("HTTP response:");
      Logger.debug(response.toString());
      int status_code = response.getStatusLine().getStatusCode();
      return (status_code == 200)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, status_code,
              "Listing Images Failed with Status: " + status_code), null);
    }
    return response;
  }

  public synchronized HttpResponse listComputeLimits() throws IOException {
    HttpGet getLimits = null;
    HttpResponse response = null;

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpResponseFactory factory = new DefaultHttpResponseFactory();

    if (isAuthenticated) {
      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.COMPUTE_PORT.toString());
      buildUrl.append(
          String.format("/%s/%s/limits", Constants.COMPUTE_VERSION.toString(), this.projectId));

      getLimits = new HttpGet(buildUrl.toString());
      getLimits.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);

      response = httpClient.execute(getLimits);
      int status_code = response.getStatusLine().getStatusCode();
      return (status_code == 200)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, status_code,
              "List Limits Failed with Status: " + status_code), null);
    }
    return response;
  }

  public synchronized HttpResponse listComputeFlavors() throws IOException {
    HttpGet getFlavors = null;
    HttpResponse response = null;

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpResponseFactory factory = new DefaultHttpResponseFactory();

    if (isAuthenticated) {
      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.COMPUTE_PORT.toString());
      buildUrl.append(String.format("/%s/%s/flavors/detail", Constants.COMPUTE_VERSION.toString(),
          this.projectId));

      // Logger.debug("[JavaStack] Authenticating client...");
      getFlavors = new HttpGet(buildUrl.toString());
      getFlavors.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.tokenId);
      Logger.debug("[JavaStack] " + getFlavors.toString());

      response = httpClient.execute(getFlavors);
      Logger.debug("[JavaStack] GET Flavor gresponse:");
      Logger.debug(response.toString());
      int status_code = response.getStatusLine().getStatusCode();
      return (status_code == 200)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, status_code,
              "List Flavors  Failed with Status: " + status_code), null);
    }
    return response;
  }

}
