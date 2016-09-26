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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.heat.Stack;
import org.openstack4j.openstack.OSFactory;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.placement.net.TranslatorNetwork;
import org.apache.log4j.Logger;
import sonata.kernel.placement.pd.SonataPackage;

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
            String uri = session.getUri();
            if("/packages".equals(uri) && session.getMethod().equals(Method.POST)) {
                session.getParms();
                Integer contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
                logger.info("Content Length is " + contentLength);
                byte[] buffer = new byte[contentLength];
                int alreadyRead = 0;
                int read = -1;
                while(alreadyRead < contentLength) {
                    read = session.getInputStream().read(buffer, alreadyRead, contentLength-alreadyRead);
                    if(read > 0)
                        alreadyRead += read;
                    if(read == -1)
                        break;
                }
                if(alreadyRead < contentLength) {
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, null, "Please try again, but next time a little bit slower.");
                }

                List<MultiPartFormDataPart> parts = parseMultiPartFormData(session, buffer);
                if(parts.size()==1) {
                    buffer = parts.get(0).data;
                } else {
                    // Fallback if above code fails
                    buffer = stripMultiPartFormDataHeader(session, buffer);
                }

                String base_dir = PackageLoader.processZipFile(buffer);

                MessageQueueData q_data = new MessageQueueData(MessageType.TRANSLATE_DESC, base_dir);
                MessageQueue.get_rest_serverQ().put(q_data);

                String jsonPackage = "OK";
                SonataPackage pack = PackageLoader.zipByteArrayToSonataPackage(buffer);
                if(pack != null) {
                    int newIndex = Catalogue.addPackage(pack);
                    jsonPackage = Catalogue.getJsonPackageDescriptor(newIndex);
                }
                return newFixedLengthResponse(Response.Status.CREATED, "application/json", jsonPackage);
            }
            else
            if("/packages".equals(uri) && session.getMethod().equals(Method.GET)) {
                String jsonPackageList = Catalogue.getJsonPackageList();
                if(jsonPackageList == null)
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, null, null);
                else
                    return newFixedLengthResponse(Response.Status.OK, "application/json", jsonPackageList);
            }
            else
            if("/requests".equals(uri) && session.getMethod().equals(Method.POST)) {
                Integer contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
                byte[] buffer = new byte[contentLength];
                session.getInputStream().read(buffer, 0, contentLength);
                List<MultiPartFormDataPart> parts = parseMultiPartFormData(session, buffer);
                if(parts.size()!=1)
                    return newFixedLengthResponse(Response.Status.BAD_REQUEST, null, null);
                String requestIndexStr = new String(parts.get(0).data);
                if(requestIndexStr.startsWith(":"))
                    requestIndexStr = requestIndexStr.substring(1);
                int requestIndex = Integer.valueOf(requestIndexStr);
                // TODO: add deploy code
                return newFixedLengthResponse(Response.Status.CREATED, null, null);
            }
            else
                return newFixedLengthResponse(Response.Status.NOT_IMPLEMENTED, null, null);

        } catch (IOException e) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, null, null);
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

    public static List<MultiPartFormDataPart> parseMultiPartFormData(IHTTPSession session, byte[] buffer){
        List<MultiPartFormDataPart> partList = new ArrayList<MultiPartFormDataPart>();

        if (session.getMethod().compareTo(Method.POST) != 0 || !session.getHeaders().containsKey("content-type") ||
                !session.getHeaders().get("content-type").startsWith("multipart/form-data"))
            return partList;

        // Get boundary from content-type
        String contentType = session.getHeaders().get("content-type");
        String[] typeParams = contentType.split(";");
        String boundary = null;
        for(String typeParam: typeParams) {
            if(typeParam.trim().startsWith("boundary=")){
                boundary = typeParam.trim().substring(9);
                break;
            }
        }
        if(boundary == null)
            return partList;

        // Get parts without any boundarys
        List<byte[]> parts = parseMultiPartFormDataBinaryPart(boundary, buffer);

        // Get header lines and body
        byte[] crlf = "\r\n".getBytes();
        for(int partI=0; partI<parts.size(); partI++){
            MultiPartFormDataPart newPart = new MultiPartFormDataPart();
            byte[] partData = parts.get(partI);

            // Parse headers and body
            int lastCrlfStart = 0;
            int lastCrlfEnd = 0;
            for(int i=0; i<partData.length; i++) {
                boolean crlfFound = true;
                for(int crlfI=0; crlfI<crlf.length; crlfI++){
                    if(partData[i+crlfI] != crlf[crlfI]) {
                        crlfFound = false;
                        break;
                    }
                }
                if(crlfFound == false)
                    continue;
                int crlfStart = i;
                int crlfEnd = i+crlf.length;
                if (crlfStart - lastCrlfStart <=2) {
                    // End of header
                    newPart.data = Arrays.copyOfRange(partData, crlfEnd, partData.length);
                    break;
                } else {
                    newPart.header.add(new String(Arrays.copyOfRange(partData, lastCrlfEnd, crlfStart)));
                    i = crlfEnd-1;
                }
                lastCrlfStart = crlfStart;
                lastCrlfEnd = crlfEnd;
            }

            partList.add(newPart);
        }

        return partList;
    }

    /**
     * https://tools.ietf.org/html/rfc2046#section-5.1
     * @param boundaryStr
     * @param data
     * @return
     */
    protected static List<byte[]> parseMultiPartFormDataBinaryPart(String boundaryStr, byte[] data){
        List<byte[]> parts = new ArrayList<byte[]>();
        byte[] boundary = ("--"+boundaryStr).getBytes();
        byte[] boundaryEnd = ("--"+boundaryStr+"--").getBytes();
        byte[] crlf = "\r\n".getBytes();

        int boundaryCount = 0;
        int partStartOffset = -1;
        int partEndOffset = -1;

        // Naive byte comparison algorithm
        for(int i=0; i<data.length; i++) {

            // Find next boundary start
            boolean foundBoundary = true;
            for(int boundaryI=0; boundaryI<boundary.length; boundaryI++) {
                if(i+boundaryI>data.length-1) {
                    foundBoundary = false;
                    break;
                }
                if(data[i+boundaryI] != boundary[boundaryI]) {
                    foundBoundary = false;
                    break;
                }
            }
            if(foundBoundary == false)
                continue;

            // boundary looks like this:
            // <start1>[crlf]<start2>--boundary<end1>[--][whitespaces]crlf<end2>
            // leading crlf optional for first boundary
            // -- after boundary only for last boundary
            // whitespaces after boundary optional

            // Search for boundary start
            int boundaryStart2 = i;
            int boundaryStart1 = -1;

            if(i<crlf.length) // first boundary, no crlf before boundary
                boundaryStart1 = 0;
            else {
                boolean crlfFound = true;
                for (int crlfI = 0; crlfI<crlf.length; crlfI++){
                    if(data[i-crlf.length+crlfI] != crlf[crlfI]) {
                        crlfFound = false;
                        break;
                    }
                }
                if(crlfFound)
                    boundaryStart1 = i-crlf.length;
                else
                    boundaryStart1 = i; // last body part now has some garbage at the end
            }

            int boundaryEnd1 = i+boundary.length;
            int boundaryEnd2 = -1;
            // Find boundary end (boundary ends with crlf)
            for(int j=boundaryEnd1; j<data.length; j++) {
                boolean crlfFound = true;
                for (int crlfI = 0; crlfI<crlf.length; crlfI++){
                    if(j+crlfI>data.length-1) {
                        crlfFound = false;
                        break;
                    }
                    if(data[j+crlfI] != crlf[crlfI]) {
                        crlfFound = false;
                        break;
                    }
                }
                if(crlfFound) {
                    boundaryEnd2 = j+crlf.length;
                    break;
                }
            }
            if(boundaryEnd2 == -1) // crlf not found, broken data
                break;

            // First part
            if(boundaryCount == 0) {
                partStartOffset = boundaryEnd2;
                boundaryCount++;
                i = boundaryEnd2-1;
            } else {
                // Next Part
                partEndOffset = boundaryStart1;

                byte[] newPart = Arrays.copyOfRange(data, partStartOffset, partEndOffset);
                parts.add(newPart);

                partStartOffset = boundaryEnd2;
                boundaryCount++;
                i = boundaryEnd2-1;
            }
            // Check for last part
            boolean foundBoundaryEnd = true;
            for(int boundaryI=0; boundaryI<boundaryEnd.length; boundaryI++) {
                if(boundaryStart2+boundaryI>data.length-1) {
                    foundBoundaryEnd = false;
                    break;
                }
                if(data[boundaryStart2+boundaryI] != boundaryEnd[boundaryI]) {
                    foundBoundaryEnd = false;
                    break;
                }

            }
            if(foundBoundaryEnd) // that's it
                break;
        }

        return parts;
    }

    public static class MultiPartFormDataPart{

        public List<String> header = new ArrayList<String>();
        public byte[] data = null;
    }
}

