package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.authentication.AuthenticationData;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class JavaStackCore {

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(JavaStackCore.class);
  private String endpoint;
  private String username;
  private String password;
  private String tenant_id;
  private ObjectMapper mapper;
  private String token_id;
  // private String image_id;
  private boolean isAuthenticated = false;

  private JavaStackCore() {}

  public enum Constants {
    AUTH_PORT("5000"), 
    HEAT_PORT("8004"), 
    IMAGE_PORT("9292"), 
    COMPUTE_PORT("8774"),
    HEAT_VERSION("v1"),
    IMAGE_VERSION("v2"),
    COMPUTE_VERSION("v2"),
    AUTHTOKEN_HEADER("X-AUTH-TOKEN"),
    AUTH_URI("/v2.0/tokens");

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

  public String getTenant_id() {
    return this.tenant_id;
  }

  public void setTenant_id(String tenant_id) {
    this.tenant_id = tenant_id;
  }

  public String getToken_id() {
    return this.token_id;
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
        "{\"auth\": {\"tenantName\": \"%s\", \"passwordCredentials\": {\"username\": \"%s\", \"password\": \"%s\"}}}",
        this.tenant_id, this.username, this.password);

    post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));

    response = httpClient.execute(post);
    mapper = new ObjectMapper();

    AuthenticationData auth = mapper.readValue(JavaStackUtils.convertHttpResponseToString(response),
        AuthenticationData.class);

    this.token_id = auth.getAccess().getToken().getId();
    this.tenant_id = auth.getAccess().getToken().getTenant().getId();
    this.isAuthenticated = true;


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
      buildUrl.append(String.format("/%s/%s/stacks", Constants.HEAT_VERSION.toString(), tenant_id));

      // Logger.debug(buildUrl.toString());
      createStack = new HttpPost(buildUrl.toString());
      createStack
          .setEntity(new StringEntity(modifiedObject.toString(), ContentType.APPLICATION_JSON));
      // Logger.debug(this.token_id);
      createStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

      Logger.debug("Request: " + createStack.toString());
      Logger.debug("Request body: " + modifiedObject.toString());

      response = httpClient.execute(createStack);
      int statusCode = response.getStatusLine().getStatusCode();
      String responsePhrase = response.getStatusLine().getReasonPhrase();
      
      Logger.debug("Response: " + response.toString());
      Logger.debug("Response body:");
      
      if (statusCode != 201){
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line=null;
        
        while((line=in.readLine())!=null)
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
          tenant_id, stackName, stackUuid));

      // Logger.debug(buildUrl.toString());
      updateStack = new HttpPatch(buildUrl.toString());
      updateStack
          .setEntity(new StringEntity(modifiedObject.toString(), ContentType.APPLICATION_JSON));
      // Logger.debug(this.token_id);
      updateStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

      Logger.debug("Request: " + updateStack.toString());
      Logger.debug("Request body: " + modifiedObject.toString());
      
      response = httpClient.execute(updateStack);
      int statusCode = response.getStatusLine().getStatusCode();
      String responsePhrase = response.getStatusLine().getReasonPhrase();

      Logger.debug("Response: " + response.toString());
      Logger.debug("Response body:");

      
      if (statusCode != 202){
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line=null;
        
        while((line=in.readLine())!=null)
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
          tenant_id, stackName, stackId));
      deleteStack = new HttpDelete(buildUrl.toString());
      deleteStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

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
          this.tenant_id, stackIdentity));

      // Logger.debug("URL: " + buildUrl);
      // Logger.debug("Token: " + this.token_id);

      findStack = new HttpGet(buildUrl.toString());
      findStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

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
          String.format("/%s/%s/stacks", Constants.HEAT_VERSION.toString(), this.tenant_id));

      System.out.println(buildUrl);
      System.out.println(this.token_id);

      listStacks = new HttpGet(buildUrl.toString());
      listStacks.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

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
          this.tenant_id, stackName, stackId);

      builder.setScheme("http").setHost(endpoint)
          .setPort(Integer.parseInt(Constants.HEAT_PORT.toString())).setPath(path);

      URI uri = builder.build();

      getStackTemplate = new HttpGet(uri);
      getStackTemplate.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);
      
      Logger.debug("Request: "+getStackTemplate.toString());
      
      response = httpclient.execute(getStackTemplate);
      int status_code = response.getStatusLine().getStatusCode();

      Logger.debug("Response: "+response.toString());
      
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
          Constants.HEAT_VERSION.toString(), this.tenant_id, stackName, stackId, resourceName);

      builder.setScheme("http").setHost(endpoint)
          .setPort(Integer.parseInt(Constants.HEAT_PORT.toString())).setPath(path);

      URI uri = builder.build();

      showResourceData = new HttpGet(uri);
      showResourceData.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

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
          Constants.HEAT_VERSION.toString(), this.tenant_id, stackName, stackId);

      builder.setScheme("http").setHost(endpoint)
          .setPort(Integer.parseInt(Constants.HEAT_PORT.toString())).setPath(path);

      URI uri = builder.build();

      listResources = new HttpGet(uri);
      listResources.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);


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
          "{ \"container_format\": \"bare\"," + "\"disk_format\": \"raw\"," + " \"name\": \"%s\"}",
          name);

      createImage.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
      createImage.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }
    return httpClient.execute(createImage);
  }

  public synchronized HttpResponse uploadBinaryImageData(String endpoint, String imageId,
      String binaryImage) throws IOException {

    HttpPut uploadImage;
    HttpClient httpClient = HttpClientBuilder.create().build();

    if (this.isAuthenticated) {
      StringBuilder buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(endpoint);
      buildUrl.append(":");
      buildUrl.append(Constants.IMAGE_PORT.toString());
      buildUrl
          .append(String.format("/%s/images/%s/file", Constants.IMAGE_VERSION.toString(), imageId));

      uploadImage = new HttpPut(buildUrl.toString());
      uploadImage.setHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);
      uploadImage.setHeader("Content-Type", "application/octet-stream");
      uploadImage.setEntity(new FileEntity(new File(binaryImage)));

    } else {
      throw new IOException(
          "You must Authenticate before issuing this request, please re-authenticate. ");
    }
    return httpClient.execute(uploadImage);
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
          String.format("/%s/%s/limits", Constants.COMPUTE_VERSION.toString(), this.tenant_id));

      getLimits = new HttpGet(buildUrl.toString());
      getLimits.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

      response = httpClient.execute(getLimits);
      int status_code = response.getStatusLine().getStatusCode();
      return (status_code == 200)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, status_code,
              "List Failed with Status: " + status_code), null);
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
          this.tenant_id));

      getFlavors = new HttpGet(buildUrl.toString());
      getFlavors.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

      response = httpClient.execute(getFlavors);
      int status_code = response.getStatusLine().getStatusCode();
      return (status_code == 200)
          ? response
          : factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, status_code,
              "List Failed with Status: " + status_code), null);
    }
    return response;
  }

}
