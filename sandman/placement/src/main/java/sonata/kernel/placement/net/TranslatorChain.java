package sonata.kernel.placement.net;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import sonata.kernel.placement.config.PopResource;

import java.io.IOException;

public class TranslatorChain {

    final static Logger logger = Logger.getLogger(TranslatorChain.class);

    public static void chain(LinkChain chain){

        String chainPath = chain.popResource.getChainingEndpoint();
        if(!chainPath.endsWith("/"))
            chainPath += "/";
        String requestUri;

        requestUri = chainPath+"/v1/chain/"+chain.srcNode+"/"+chain.srcInterface+"/"+chain.dstNode+"/"+chain.srcInterface;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut getRequest = new HttpPut(requestUri);
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

    public static void unchain(LinkChain chain){

        String chainPath = chain.popResource.getChainingEndpoint();
        if(!chainPath.endsWith("/"))
            chainPath += "/";

        CloseableHttpClient client = HttpClients.createDefault();
        String requestUri = chainPath+"/v1/unchain/"+chain.srcNode+"/"+chain.srcNode;

        HttpDelete getRequest = new HttpDelete(requestUri);
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

}

