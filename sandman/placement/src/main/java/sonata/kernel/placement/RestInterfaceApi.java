package sonata.kernel.placement;

//import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                .endpoint("http://131.234.31.45:5001/v2.0")
                .credentials("admin","sample")
                .tenantName("admin")
                .authenticate();
        Stack stack = os.heat().stacks().create(Builders.stack()
                .name("XYZ")
                .template(data)
                .timeoutMins(5L).build());

        System.out.println("ZZZZZz");
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
class RestInterfaceServerApi extends NanoHTTPD implements Runnable{

    public RestInterfaceServerApi()
    {
        super(8080);
    }
    public RestInterfaceServerApi(String hostname, int port) throws IOException {
        super(hostname, port);

            System.out.println("RestInterfaceServerApi:: Started RESTful server Hostname: "
                    + hostname + " Port: " + port);


    }

    public void start_server() throws IOException
    {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public void run()
    {
        try{
            start_server();
        } catch (IOException ioe) {
            System.err.println("RestInterfaceServerApi::run : Failed to start server " + ioe);
        }
    }
    @Override
    public Response serve(IHTTPSession session) {

      try{
          session.getParms();
          Integer contentLength = Integer.parseInt(session.getHeaders().get("content-length"));

          byte[] buffer = new byte[contentLength];
          session.getInputStream().read(buffer, 0, contentLength);
          String base_dir = PackageLoader.processZipFile(buffer);

          MessageQueueData q_data = new MessageQueueData(MessageType.TRANSLATE_DESC, base_dir);
          MessageQueue.get_rest_serverQ().put(q_data);



      } catch (IOException e){

      } catch (InterruptedException e) {
          e.printStackTrace();
      }

        return newFixedLengthResponse(Response.Status.OK, null, "OK");
    }
}

