package sonata.kernel.placement.net;

import org.apache.commons.httpclient.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import sonata.kernel.placement.config.PopResource;

import java.io.IOException;

public class TranslatorChain {

    final static Logger logger = Logger.getLogger(TranslatorChain.class);

    public static void chain(PopResource popResource, String srcVnf, String srcInterface, String dstVnf, String dstInterface){

        String chainPath = getChainPath(popResource);
        String requestUri;

        if(srcInterface == null || dstInterface == null)
            requestUri = chainPath+"/v1/chain/"+srcVnf+"/"+dstVnf;
        else
            requestUri = chainPath+"/v1/chain/"+srcVnf+"/"+srcInterface+"/"+dstVnf+"/"+srcInterface;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet getRequest = new HttpGet(requestUri);
        CloseableHttpResponse response = null;

        logger.info("Chaining "+getRequest.getRequestLine().getUri());

        try {
            response = client.execute(getRequest);
            if (response.getStatusLine().getStatusCode() == 500) {
                logger.error("Chaining failed "+requestUri);
            } else {
                logger.info("Chaining successful "+requestUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Chaining request aborted "+requestUri);
        }
    }

    public static void unchain(PopResource popResource, String srcVnf, String dstVnf){
        String chainPath = getChainPath(popResource);

        CloseableHttpClient client = HttpClients.createDefault();
        String requestUri = chainPath+"/v1/unchain/"+srcVnf+"/"+dstVnf;

        HttpGet getRequest = new HttpGet(requestUri);
        CloseableHttpResponse response = null;

        logger.info("Unchaining "+getRequest.getRequestLine().getUri());

        try {
            response = client.execute(getRequest);
            if (response.getStatusLine().getStatusCode() == 500) {
                logger.error("Unchaining failed "+requestUri);
            } else {
                logger.info("Unchaining successful "+requestUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Unchaining request aborted "+requestUri);
        }
    }



    protected static String getChainPath(PopResource popResource){
        // TODO: replace string operations with a proper definition in the configuration file
        // infer chain path using keystone path
        String keystone = popResource.getEndpoint();
        int colonIndex = keystone.lastIndexOf(":");
        String host = keystone.substring(0,colonIndex);
        String portStr = keystone.substring(colonIndex+1);
        int port = Integer.parseInt(portStr);

        String chainPath = host+":"+(port-1000);
        return chainPath;
    }

}
