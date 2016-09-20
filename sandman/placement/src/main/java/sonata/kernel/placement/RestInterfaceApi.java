package sonata.kernel.placement;

//import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.heat.Stack;
import org.openstack4j.openstack.OSFactory;
import sonata.kernel.VimAdaptor.wrapper.WrapperConfiguration;
import sonata.kernel.VimAdaptor.wrapper.openstack.OpenStackHeatClient;
import sonata.kernel.placement.net.TranslatorNet;
import org.apache.log4j.Logger;

class RestInterfaceClientApi implements Runnable{
	
    public RestInterfaceClientApi()
    {
        //Do something.
    }

    public void run()
    {
        /*
         * Wait for message on queue.
         * Receive message from queue and forward it out to the PoP as required.
         * Request message from the PoP and forward it to the queue as required.
         */

        while(true) {

            try {
                MessageQueueData q_data = MessageQueue.get_rest_clientQ().take();
                if(q_data.message_type == MessageType.TERMINATE_MESSAGE) {
                    System.out.println("RestInterfaceClientApi:: Terminating");
                    return;
                } else if (q_data.message_type == MessageType.GET_MESSAGE) {
                    System.out.println("RestInterfaceClientApi:: Process GET message");
                    get_message(q_data.uri, q_data.data);
                } else if (q_data.message_type == MessageType.POST_MESSAGE) {
                    System.out.println("RestInterfaceClientApi:: Process POST message");
                    post_message(q_data.uri, q_data.data);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void get_message(String uri, String data)
    {
        String output;


        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(uri);
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("RestInterfaceClientApi::get_message(): Error : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            httpClient.getConnectionManager().shutdown();
            return;

        }  catch (IOException e) {

            e.printStackTrace();
        }

        return;
    }
    /*
     * Post message to PoP.
     * uri : PoP address
     * data: Heat/Nova template.
     */

    public String post_message(String uri, String data)
    {
        String output;
        OSClient.OSClientV2 os = OSFactory.builderV2()
                .endpoint( uri + "/v2.0")
                .credentials("bla","bla")
                .tenantName("fc394f2ab2df4114bde39905f800dc57")
                .authenticate();

        Stack stack = os.heat().stacks().create(Builders.stack()
                .name("XYZ")
                .template(data)
                .timeoutMins(5L).build());

        TranslatorNet.create_subnet(os, "testNetwork", "testNetworkId", "testTenantId", "192.168.0.1",
            "192.168.0.10");


       /* Stack stack2 = os.heat().stacks().getDetails(stack.getName(), stack.getId());
        System.out.println(stack2); */

        return null;

//        try {
//
//            /*WrapperConfiguration config = new WrapperConfiguration();
//            OpenStackHeatClient heatClient = new OpenStackHeatClient(uri, config.)
//*/
//            /*
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            HttpPost postRequest = new HttpPost(uri);
//
//            StringEntity input = new StringEntity(data);
//            input.setContentType("application/json");
//            postRequest.setEntity(input);
//
//            HttpResponse response = httpClient.execute(postRequest);
//
//            if (response.getStatusLine().getStatusCode() != 200) {
//                throw new RuntimeException("RestInterfaceClientApi::post_message(): Error : HTTP error code : "
//                        + response.getStatusLine().getStatusCode());
//            }
//
//
//            BufferedReader br = new BufferedReader(
//                    new InputStreamReader((response.getEntity().getContent())));
//
//
//            while ((output = br.readLine()) != null) {
//                System.out.println(output);
//            }
//
//            httpClient.getConnectionManager().shutdown();
//            return output;
//
//        }  catch (IOException e) {
//
//            e.printStackTrace();
//
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//        }
//        return null;

    }
}
class RestInterfaceServerApi extends NanoHTTPD implements Runnable {
    final Logger logger = Logger.getLogger(RestInterfaceServerApi.class);

    public RestInterfaceServerApi() {
        super(8080);
    }

    public RestInterfaceServerApi(String hostname, int port) throws IOException {
        super(hostname, port);
        logger.debug("Content Length is " + hostname + " " + port);
        System.out.println("RestInterfaceServerApi:: Started RESTful server Hostname: "
                + hostname + " Port: " + port);


    }

    public void start_server() throws IOException {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public void run() {
        try {
            start_server();
        } catch (IOException ioe) {
            System.err.println("RestInterfaceServerApi::run : Failed to start server " + ioe);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        final Logger logger = Logger.getLogger(RestInterfaceClientApi.class);
        try {
            session.getParms();
            Integer contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
            logger.info("Content Length is " + contentLength);
            byte[] buffer = new byte[contentLength];
            session.getInputStream().read(buffer, 0, contentLength);
            buffer = stripMultiPartFormDataHeader(session, buffer);
            String base_dir = PackageLoader.processZipFile(buffer);

            MessageQueueData q_data = new MessageQueueData(MessageType.TRANSLATE_DESC, base_dir);
            MessageQueue.get_rest_serverQ().put(q_data);


        } catch (IOException e) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return newFixedLengthResponse(Response.Status.CREATED, null, "OK");
    }

    public static byte[] stripMultiPartFormDataHeader(IHTTPSession session, byte[] buffer) {
        // TODO: Maybe add a real multipart/form-data parser
        // Check if POST request contains multipart/form-data
        if (session.getMethod().compareTo(Method.POST) == 0 && session.getHeaders().containsKey("content-type") &&
                session.getHeaders().get("content-type").startsWith("multipart/form-data")) {

            // Assume UTF-8 encoding
            CharsetDecoder dec = Charset.forName("UTF-8").newDecoder();

            // Create comparison charbuffers
            CharBuffer nlnl = CharBuffer.wrap("\n\n");
            CharBuffer crnlcrnl = CharBuffer.wrap("\r\n\r\n");
            int formDataBorder = -1;
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

            // Find border for first multipart/form-data boundary
            // Assume there is only one part
            for (int i = 0; i < buffer.length; i++) {

                byteBuffer.position(0);
                byteBuffer.limit(i);

                try {
                    CharBuffer charBuffer = dec.decode(byteBuffer);
                    // Check if end of sequence is "\n\n" or "\r\n\r\n"
                    if ((charBuffer.length() > 1 && charBuffer.subSequence(charBuffer.length() - 2, charBuffer.length()).compareTo(nlnl) == 0) ||
                            (charBuffer.length() > 3 && charBuffer.subSequence(charBuffer.length() - 4, charBuffer.length()).compareTo(crnlcrnl) == 0)) {
                        formDataBorder = i;
                        break;
                    }
                } catch (CharacterCodingException e) {
                    return buffer;
                }

            }
            if (formDataBorder != -1)
                return Arrays.copyOfRange(buffer, formDataBorder, buffer.length);
            else
                return buffer;
        } else
            return buffer;
    }
}

