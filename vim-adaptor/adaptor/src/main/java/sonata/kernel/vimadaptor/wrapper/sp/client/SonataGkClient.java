package sonata.kernel.vimadaptor.wrapper.sp.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import sonata.kernel.vimadaptor.commons.ServiceRecord;
import sonata.kernel.vimadaptor.commons.SonataManifestMapper;
import sonata.kernel.vimadaptor.commons.VimResources;
import sonata.kernel.vimadaptor.commons.VnfRecord;
import sonata.kernel.vimadaptor.wrapper.openstack.javastackclient.JavaStackUtils;
import sonata.kernel.vimadaptor.wrapper.sp.client.model.GkRequestStatus;
import sonata.kernel.vimadaptor.wrapper.sp.client.model.GkServiceListEntry;
import sonata.kernel.vimadaptor.wrapper.sp.client.model.SonataAuthenticationResponse;
import sonata.kernel.vimadaptor.wrapper.sp.client.model.VimRequestStatus;


public class SonataGkClient {

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(SonataGkClient.class);

  private String host;
  private String username;
  private String password;
  private String token;

  public SonataGkClient(String host, String username, String password) {
    this.host = host;
    this.username = username;
    this.password = password;
  }

  /**
   * @return Return after a successful authentication.
   * @throws IOException for http client error or JSON parsing error
   * @throws ClientProtocolException for http client error
   */
  public boolean authenticate() {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost post;
    HttpResponse response = null;

    StringBuilder buildUrl = new StringBuilder();
    buildUrl.append("http://");
    buildUrl.append(this.host);
    buildUrl.append(":");
    buildUrl.append(32001);
    buildUrl.append("/api/v2/sessions");

    String body = "{\"username\":\"" + this.username + "\",\"password\":\"" + this.password + "\"}";

    post = new HttpPost(buildUrl.toString());

    post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
    Logger.debug("Authentication request:");

    Logger.debug(post.toString());

    try {
      response = httpClient.execute(post);

      String json = JavaStackUtils.convertHttpResponseToString(response);

      Logger.debug("Auth response: " + json);
      ObjectMapper mapper = new ObjectMapper();

      SonataAuthenticationResponse auth =
          mapper.readValue(json, SonataAuthenticationResponse.class);
      this.token = auth.getToken().getToken();

      if (response.getStatusLine().getStatusCode() == 200) {
        Logger.debug("Client authenticated");
        return true;
      } else {
        Logger.debug("Authentication failed");
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * @return a List of VimResource object taken from the Gatekeeper
   * @throws IOException for http client error or JSON parsing error
   * @throws ClientProtocolException for http client error
   */
  public VimResources[] getVims() throws ClientProtocolException, IOException {
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet get;
    HttpResponse response = null;

    StringBuilder buildUrl = new StringBuilder();
    buildUrl.append("http://");
    buildUrl.append(this.host);
    buildUrl.append(":");
    buildUrl.append(32001);
    buildUrl.append("/api/v2/vims");

    get = new HttpGet(buildUrl.toString());

    get.addHeader("Authorization", "Bearer " + this.token);
    response = httpClient.execute(get);

    Logger.debug("[SONATA-GK-CLient] /vim endpoint response (Request Object):");
    Logger.debug(response.toString());

    ObjectMapper mapper = new ObjectMapper();

    String stringResponse = JavaStackUtils.convertHttpResponseToString(response);
    Logger.debug(stringResponse);

    VimRequestStatus requestStatus = mapper.readValue(stringResponse, VimRequestStatus.class);

    if (requestStatus.getStatus() != 201) {
      throw new ClientProtocolException(
          "GK returned wrong status upon VIM request creation: " + requestStatus.getStatus());
    }
    String requestUuid = "";
    try {
      requestUuid = requestStatus.getItems().getRequestUuid();
    } catch (NullPointerException e) {
      throw new IOException(
          "The GK sent back an request status with empty values or values are not parsed correctly.");
    }
    VimResources[] list;
    do {
      buildUrl = new StringBuilder();
      buildUrl.append("http://");
      buildUrl.append(this.host);
      buildUrl.append("/api/v2/vims");
      buildUrl.append("/" + requestUuid);

      get = new HttpGet(buildUrl.toString());

      get.addHeader("Authorization", "Bearer " + this.token);
      response = httpClient.execute(get);

      Logger.debug("[SONATA-GK-CLient] /vim endpoint response (VIM list):");
      Logger.debug(response.toString());
      stringResponse = JavaStackUtils.convertHttpResponseToString(response);
      Logger.debug(stringResponse);

      list = mapper.readValue(stringResponse, VimResources[].class);
    } while (list.length == 0);

    return list;

  }

  /**
   * @return a List of ServiceDescriptor object taken from the Gatekeeper
   * @throws IOException for http client error or JSON parsing error
   * @throws ClientProtocolException for http client error
   */
  public GkServiceListEntry[] getServices() throws ClientProtocolException, IOException {

    Logger.debug("[SONATA-GK-CLient] Retrieving active services: ");

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet get;
    HttpResponse response = null;

    StringBuilder buildUrl = new StringBuilder();
    buildUrl.append("http://");
    buildUrl.append(this.host);
    buildUrl.append(":");
    buildUrl.append(32001);
    buildUrl.append("/api/v2/services?status=active");

    get = new HttpGet(buildUrl.toString());
    get.addHeader("Authorization", "Bearer " + this.token);
    Logger.debug("[SONATA-GK-CLient] /services endpoint request (Request Object):");
    Logger.debug(get.toString());

    response = httpClient.execute(get);

    Logger.debug("[SONATA-GK-CLient] /services endpoint response (Response Object):");
    Logger.debug(response.toString());

    ObjectMapper mapper = SonataManifestMapper.getSonataJsonMapper();

    String stringResponse = JavaStackUtils.convertHttpResponseToString(response);
    Logger.debug(stringResponse);

    GkServiceListEntry[] activeServices =
        mapper.readValue(stringResponse, GkServiceListEntry[].class);

    return activeServices;
  }

  /**
   * @param requestUuid uuid of the GK request
   * @return a String representing the status of the request
   * @throws IOException for http client error or JSON parsing error
   * @throws ClientProtocolException for http client error
   */
  public String getRequestStatus(String requestUuid) throws ClientProtocolException, IOException {
    Logger.debug("[SONATA-GK-CLient] Getting request information object");

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet get;
    HttpResponse response = null;

    StringBuilder buildUrl = new StringBuilder();
    buildUrl.append("http://");
    buildUrl.append(this.host);
    buildUrl.append(":");
    buildUrl.append(32001);
    buildUrl.append("/api/v2/requests");
    buildUrl.append("/" + requestUuid);

    get = new HttpGet(buildUrl.toString());
    get.addHeader("Authorization", "Bearer " + this.token);
    Logger.debug("[SONATA-GK-CLient] /requests request:");
    Logger.debug(get.toString());
    response = httpClient.execute(get);

    String stringResponse = JavaStackUtils.convertHttpResponseToString(response);
    Logger.debug("[SONATA-GK-CLient] /requests response:");
    Logger.debug(stringResponse);

    ObjectMapper mapper = SonataManifestMapper.getSonataJsonMapper();

    GkRequestStatus requestRequestObject = mapper.readValue(stringResponse, GkRequestStatus.class);


    return requestRequestObject.getStatus();
  }

  /**
   * @param serviceUuid the uuid of the NSD to be instantiated
   * @return a String representing the generated request UUID
   * @throws IOException for http client error or JSON parsing error
   * @throws ClientProtocolException for http client error
   */
  public String instantiateService(String serviceUuid) throws ClientProtocolException, IOException {

    Logger.debug("[SONATA-GK-CLient] Creating a new instantiation request");

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost post;
    HttpResponse response = null;

    StringBuilder buildUrl = new StringBuilder();
    buildUrl.append("http://");
    buildUrl.append(this.host);
    buildUrl.append(":");
    buildUrl.append(32001);
    buildUrl.append("/api/v2/requests");

    String body =
        String.format("{\"service_uuid\": \"%s\", \"ingresses\":[], \"egresses\":[]}", serviceUuid);

    post = new HttpPost(buildUrl.toString());
    post.addHeader("Authorization", "Bearer " + this.token);

    post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
    Logger.debug("[SONATA-GK-CLient] /requests POST request:");
    Logger.debug(post.toString());

    response = httpClient.execute(post);

    String stringResponse = JavaStackUtils.convertHttpResponseToString(response);
    Logger.debug("[SONATA-GK-CLient] /requests POST response:");
    Logger.debug(stringResponse);

    ObjectMapper mapper = SonataManifestMapper.getSonataJsonMapper();

    GkRequestStatus requestObject = mapper.readValue(stringResponse, GkRequestStatus.class);


    return requestObject.getId();
  }

  /**
   * @param serviceUuid the uuid of the NSD to be instantiated
   * @return a String representing the generated request UUID
   * @throws IOException for http client error or JSON parsing error
   * @throws ClientProtocolException for http client error
   */
  public String removeServiceInstance(String serviceUuid)
      throws ClientProtocolException, IOException {

    Logger.debug("[SONATA-GK-CLient] Creating a new instantiation request");

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost post;
    HttpResponse response = null;

    StringBuilder buildUrl = new StringBuilder();
    buildUrl.append("http://");
    buildUrl.append(this.host);
    buildUrl.append(":");
    buildUrl.append(32001);
    buildUrl.append("/api/v2/requests");

    String body = String.format(
        "{\"service_uuid\": \"%s\", \"ingresses\":[], \"egresses\":[], \"request_type\":\"TERMINATE\"}",
        serviceUuid);

    post = new HttpPost(buildUrl.toString());
    post.addHeader("Authorization", "Bearer " + this.token);

    post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
    Logger.debug("[SONATA-GK-CLient] /requests POST request:");
    Logger.debug(post.toString());

    response = httpClient.execute(post);

    String stringResponse = JavaStackUtils.convertHttpResponseToString(response);
    Logger.debug("[SONATA-GK-CLient] /requests POST response:");
    Logger.debug(stringResponse);

    ObjectMapper mapper = SonataManifestMapper.getSonataJsonMapper();

    GkRequestStatus requestObject = mapper.readValue(stringResponse, GkRequestStatus.class);


    return requestObject.getId();
  }

  /**
   * @param requestUuid the UUID of the request
   * @return a RequestObject that contains information on the request
   * @throws IOException for http client error or JSON parsing error
   * @throws ClientProtocolException for http client error
   */
  public GkRequestStatus getRequest(String requestUuid)
      throws ClientProtocolException, IOException {
    Logger.debug("[SONATA-GK-CLient] Getting request information object...");

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet get;
    HttpResponse response = null;

    StringBuilder buildUrl = new StringBuilder();
    buildUrl.append("http://");
    buildUrl.append(this.host);
    buildUrl.append(":");
    buildUrl.append(32001);
    buildUrl.append("/api/v2/requests");
    buildUrl.append("/" + requestUuid);

    get = new HttpGet(buildUrl.toString());
    get.addHeader("Authorization", "Bearer " + this.token);

    Logger.debug("[SONATA-GK-CLient] /requests request:");
    Logger.debug(get.toString());
    response = httpClient.execute(get);

    String stringResponse = JavaStackUtils.convertHttpResponseToString(response);
    Logger.debug("[SONATA-GK-CLient] /requests response:");
    Logger.debug(stringResponse);

    ObjectMapper mapper = SonataManifestMapper.getSonataJsonMapper();

    GkRequestStatus requestObject = mapper.readValue(stringResponse, GkRequestStatus.class);

    return requestObject;
  }

  /**
   * @param serviceInstanceUuid the UUID of the service instance
   * @return the ServiceRecord associated with this service instance
   * @throws IOException for http client error or JSON parsing error
   * @throws ClientProtocolException for http client error
   *
   */
  public ServiceRecord getNsr(String serviceInstanceUuid)
      throws ClientProtocolException, IOException {
    Logger.debug("[SONATA-GK-CLient] Getting request information object...");

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet get;
    HttpResponse response = null;

    StringBuilder buildUrl = new StringBuilder();
    buildUrl.append("http://");
    buildUrl.append(this.host);
    buildUrl.append(":");
    buildUrl.append(32001);
    buildUrl.append("/api/v2/records/services");
    buildUrl.append("/" + serviceInstanceUuid);

    get = new HttpGet(buildUrl.toString());
    get.addHeader("Authorization", "Bearer " + this.token);
    Logger.debug("[SONATA-GK-CLient] /record/services/id request:");
    Logger.debug(get.toString());
    response = httpClient.execute(get);

    String stringResponse = JavaStackUtils.convertHttpResponseToString(response);
    Logger.debug("[SONATA-GK-CLient] /record/services/id response:");
    Logger.debug(stringResponse);

    ObjectMapper mapper = SonataManifestMapper.getSonataJsonMapper();

    ServiceRecord serviceRecord = mapper.readValue(stringResponse, ServiceRecord.class);

    return serviceRecord;
  }

  /**
   * @param vnfrId the ID of the VNFR to retrieve
   * @return the VnfRecord object for the specified VNFR ID
   * @throws IOException for http client error or JSON parsing error
   * @throws ClientProtocolException for http client error
   */
  public VnfRecord getVnfr(String vnfrId) throws ClientProtocolException, IOException {
    Logger.debug("[SONATA-GK-CLient] Getting request information object...");

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpGet get;
    HttpResponse response = null;

    StringBuilder buildUrl = new StringBuilder();
    buildUrl.append("http://");
    buildUrl.append(this.host);
    buildUrl.append(":");
    buildUrl.append(32001);
    buildUrl.append("/api/v2/records/functions");
    buildUrl.append("/" + vnfrId);

    get = new HttpGet(buildUrl.toString());
    get.addHeader("Authorization", "Bearer " + this.token);
    Logger.debug("[SONATA-GK-CLient] /records/functions/ request:");
    Logger.debug(get.toString());

    response = httpClient.execute(get);

    String stringResponse = JavaStackUtils.convertHttpResponseToString(response);
    Logger.debug("[SONATA-GK-CLient] /records/functions/ response:");
    Logger.debug(stringResponse);

    ObjectMapper mapper = SonataManifestMapper.getSonataJsonMapper();

    VnfRecord functionRecord = mapper.readValue(stringResponse, VnfRecord.class);

    return functionRecord;
  }


  private HttpClient createHttpClientAcceptsUntrustedCerts() {
    HttpClientBuilder b = HttpClientBuilder.create();

    // setup a Trust Strategy that allows all certificates.
    //
    SSLContext sslContext;
    HttpClient client = null;
    try {
      sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
        public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
          return true;
        }
      }).build();
      b.setSSLContext(sslContext);

      // don't check Hostnames, either.
      // -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
      HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

      // here's the special part:
      // -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
      // -- and create a Registry, to register it.
      //
      SSLConnectionSocketFactory sslSocketFactory =
          new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
      Registry<ConnectionSocketFactory> socketFactoryRegistry =
          RegistryBuilder.<ConnectionSocketFactory>create()
              .register("http", PlainConnectionSocketFactory.getSocketFactory())
              .register("https", sslSocketFactory).build();

      // now, we create connection-manager using our Registry.
      // -- allows multi-threaded use
      PoolingHttpClientConnectionManager connMgr =
          new PoolingHttpClientConnectionManager(socketFactoryRegistry);
      b.setConnectionManager(connMgr);

      // finally, build the HttpClient;
      // -- done!
      client = b.build();

    } catch (KeyManagementException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (KeyStoreException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return client;
  }

}
