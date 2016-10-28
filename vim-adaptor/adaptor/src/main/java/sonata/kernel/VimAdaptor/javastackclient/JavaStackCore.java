package sonata.kernel.VimAdaptor.javastackclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import sonata.kernel.VimAdaptor.javastackclient.models.authentication.AuthenticationData;


import java.io.File;
import java.io.IOException;


public class JavaStackCore {

    public enum Constants {
        AUTH_PORT("5000"),
        HEAT_PORT("8004"),
        IMAGE_PORT("9292"),
        HEAT_VERSION("v1"),
        IMAGE_VERSION("v2"),
        AUTHTOKEN_HEADER("X-AUTH-TOKEN"),
        AUTH_URI("/v2.0/tokens");

        private final String constantValue;

        Constants(String constantValue) {
            this.constantValue=constantValue;
        }

        @Override
        public String toString() {
            return this.constantValue;
        }
    }
    private static JavaStackCore _javaStackCore;
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(JavaStackCore.class);

    private String endpoint;
    private String username;
    private String password;
    private String tenant_id;
    private ObjectMapper mapper;
    private String token_id;
    private String image_id;


    private JavaStackCore(){}
/*    public JavaStackCore(String endpoint, String username, String password, String tenant_name) {
        this.username = username;
        this.password = password;
        this.tenant_id = tenant_name;
        this.endpoint = endpoint;
    }*/

    public static JavaStackCore getJavaStackCore(){
        if (_javaStackCore == null) {
            _javaStackCore = new JavaStackCore();
        }

        return _javaStackCore;
    }
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getPassword(){
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }
    public String getTenant_id(){
        return this.tenant_id;
    }

    private boolean isAuthenticated = false;

    public void authenticateClient(String endpoint) throws IOException {

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post;
        HttpResponse response = null;

        if (!isAuthenticated) {
            StringBuilder buildUrl = new StringBuilder();
            buildUrl.append("http://");
            buildUrl.append(endpoint);
            buildUrl.append(":");
            buildUrl.append(Constants.AUTH_PORT.toString());
            buildUrl.append(Constants.AUTH_URI.toString());

            post = new HttpPost(buildUrl.toString());

            String body = String.format(
                    "{\"auth\": {\"tenantName\": \"%s\", \"passwordCredentials\": {\"username\": \"%s\", \"password\": \"%s\"}}}",
                    this.tenant_id,
                    this.username,
                    this.password);

            post.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));

            response = httpClient.execute(post);
            mapper = new ObjectMapper();
            AuthenticationData auth = mapper.readValue(
                                                        JavaStackUtils.convertHttpResponseToString(response),
                                                        AuthenticationData.class
            );
            this.token_id = auth.getAccess().getToken().getId();
            this.isAuthenticated = true;

        } else {
            Logger.info("You are already authenticated");
        }
    }


    public  HttpResponse listStacksRequest(String endpoint) throws IOException {

        HttpGet getStack;
        HttpClient httpClient = HttpClientBuilder.create().build();

        if (isAuthenticated){

            StringBuilder buildUrl = new StringBuilder();
            buildUrl.append("http://");
            buildUrl.append(endpoint);
            buildUrl.append(":");
            buildUrl.append(Constants.HEAT_PORT.toString());
            buildUrl.append(String.format("/%s/%s/stacks", Constants.HEAT_VERSION.toString(),this.tenant_id));

            System.out.println(buildUrl);
            System.out.println(token_id);

            getStack = new HttpGet(buildUrl.toString());
            getStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

            return httpClient.execute(getStack);

        } else {
            throw new IOException("You must Authenticate before issuing this request, please re-authenticate. ");
        }

    }

    public  HttpResponse createImage( String template,
                                      String containerFormat,
                                      String diskFormat,
                                      String name) throws IOException {
        HttpPost createImage;
        HttpClient httpClient = HttpClientBuilder.create().build();

        if (isAuthenticated) {
            StringBuilder buildUrl = new StringBuilder();
            buildUrl.append("http://");
            buildUrl.append(this.endpoint);
            buildUrl.append(":");
            buildUrl.append(Constants.IMAGE_PORT.toString());
            buildUrl.append(String.format("/%s/images", Constants.IMAGE_VERSION.toString()));

            createImage = new HttpPost(buildUrl.toString());
            String requestBody =  String.format("{ \"container_format\": \"bare\"," +
                                    "\"disk_format\": \"raw\"," +
                                    " \"name\": \"%s\"}", name);

            createImage.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            createImage.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

        } else {
            throw new IOException("You must Authenticate before issuing this request, please re-authenticate. ");
        }
        return httpClient.execute(createImage);
    }

    public  HttpResponse uploadBinaryImageData(String endpoint, String imageId,String binaryImage) throws IOException {

        HttpPut uploadImage;
        HttpClient httpClient = HttpClientBuilder.create().build();

        if (isAuthenticated) {
            StringBuilder buildUrl = new StringBuilder();
            buildUrl.append("http://");
            buildUrl.append(endpoint);
            buildUrl.append(":");
            buildUrl.append(Constants.IMAGE_PORT.toString());
            buildUrl.append(String.format("/%s/images/%s/file", Constants.IMAGE_VERSION.toString(), imageId));

            uploadImage = new HttpPut(buildUrl.toString());
            uploadImage.setHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);
            uploadImage.setHeader("Content-Type", "application/octet-stream");
            uploadImage.setEntity(new FileEntity(new File(binaryImage)));

        } else {
            throw new IOException("You must Authenticate before issuing this request, please re-authenticate. ");
        }
        return httpClient.execute(uploadImage);
    }
    public  HttpResponse createStack(String template , String stackName) throws IOException {

        HttpPost createStack;
        HttpClient httpClient = HttpClientBuilder.create().build();


        String jsonTemplate = JavaStackUtils.convertYamlToJson(template);
        JSONObject modifiedObject = new JSONObject();
        modifiedObject.put("stack_name", stackName);
        modifiedObject.put("template", new JSONObject(jsonTemplate));

        if (isAuthenticated) {

            StringBuilder buildUrl = new StringBuilder();
            buildUrl.append("http://");
            buildUrl.append(this.endpoint);
            buildUrl.append(":");
            buildUrl.append(Constants.HEAT_PORT.toString());
            buildUrl.append(String.format("/%s/%s/stacks", Constants.HEAT_VERSION.toString() ,tenant_id));

            createStack = new HttpPost(buildUrl.toString());
            createStack.setEntity(new StringEntity(modifiedObject.toString(), ContentType.APPLICATION_JSON));
            createStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

            return httpClient.execute(createStack);
        } else {
            throw new IOException("You must Authenticate before issuing this request, please re-authenticate. ");
        }
    }

    public  HttpResponse deleteStack(String stackName, String stackId) throws IOException {

        HttpDelete deleteStack ;
        HttpClient httpClient = HttpClientBuilder.create().build();
        if (isAuthenticated) {

            StringBuilder buildUrl = new StringBuilder();
            buildUrl.append("http://");
            buildUrl.append(this.endpoint);
            buildUrl.append(":");
            buildUrl.append(Constants.HEAT_PORT.toString());
            buildUrl.append(String.format("/%s/%s/stacks/%s/%s",
                                            Constants.HEAT_VERSION.toString(),
                                            tenant_id,
                                            stackName,
                                            stackId));
            deleteStack = new HttpDelete(buildUrl.toString());
            deleteStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

            return httpClient.execute(deleteStack);
        } else {
            throw new IOException("You must Authenticate before issuing this request, please re-authenticate. ");
        }
    }

    public  HttpResponse findStack(String stackIdentity) throws  IOException {
        HttpGet findStack;
        HttpClient httpClient = HttpClientBuilder.create().build();

        if (isAuthenticated){

            StringBuilder buildUrl = new StringBuilder();
            buildUrl.append("http://");
            buildUrl.append(this.endpoint);
            buildUrl.append(":");
            buildUrl.append(Constants.HEAT_PORT.toString());
            buildUrl.append(String.format("/%s/%s/stacks/%s", Constants.HEAT_VERSION.toString(),tenant_id, stackIdentity));

            System.out.println(buildUrl);
            System.out.println(token_id);

            findStack = new HttpGet(buildUrl.toString());
            findStack.addHeader(Constants.AUTHTOKEN_HEADER.toString(), this.token_id);

            return httpClient.execute(findStack);

        } else {
            throw new IOException("You must Authenticate before issuing this request, please re-authenticate. ");
        }

    }


}
