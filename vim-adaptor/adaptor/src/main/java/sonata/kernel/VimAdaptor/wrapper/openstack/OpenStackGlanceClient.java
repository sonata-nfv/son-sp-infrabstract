package sonata.kernel.VimAdaptor.wrapper.openstack;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.JavaStackCore;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.JavaStackUtils;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.Image.Image;
import sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient.models.Image.Images;

import java.io.IOException;
import java.util.ArrayList;


public class OpenStackGlanceClient {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(OpenStackNovaClient.class);

    private String url; // url of the OpenStack Client

    private String userName; // OpenStack Client user

    private String password; // OpenStack Client password

    private String tenantName; // OpenStack tenant name

    private JavaStackCore javaStack; // instance for calling OpenStack APIs

    private ObjectMapper mapper;

    public OpenStackGlanceClient (String url, String userName, String password, String tenantName ){
        this.url  = url;
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
    * List glance Images.
    *
    * @return - A list of image objects containing name, id, and other useful parameters
    */
   public ArrayList<Image> listImages () {

       Logger.debug("Listing available Images");
       Images images = null;

       String listImages = null;
       try {
           listImages = JavaStackUtils.convertHttpResponseToString(javaStack.listImages());
           images = mapper.readValue(listImages, Images.class);

       } catch (IOException e) {
           e.printStackTrace();
       }
    return images.getImages();
   }
}