package sonata.kernel.placement;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.*;
import java.util.*;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;

import java.lang.String;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

//import javax.xml.ws.Response;

@SuppressWarnings("ALL")
class RestHelper extends NanoHTTPD {

    public RestHelper () throws IOException {
        super(9002);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:9001/ \n");
    }

    @SuppressWarnings("deprecation")
    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();
        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
            try {
                //session.parseBody(files);
            	session.getParms();
            	Integer contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
            	
            	byte[] buffer = new byte[contentLength];
            	int i = buffer.length;
            	System.out.println(i);
            	//ZipFile zipFile = new ZipFile("http://localhost:9002");
            	//zipFile.extractAll("");
                //ZipFile zipFile = new ZipFile();
            	session.getInputStream().read(buffer, 0, contentLength);
            	String str = new String(buffer);
            	String[] lines = str.split(System.getProperty("line.separator"));
            	for(int j=0;j<lines.length;j++){
            	    if(lines[j].startsWith("<") || lines[j].endsWith(">")){
            	        lines[j]="";
            	    }
            	}
            	StringBuilder finalStringBuilder = new StringBuilder();
            	for(String s:lines){
            	   if(!s.equals("")){
            	       finalStringBuilder.append(s).append(System.getProperty("line.separator"));
            	    }
            	}
            	String finalString = finalStringBuilder.toString();
            	System.out.println("RequestBody: " + finalString);
            	return newFixedLengthResponse(Response.Status.OK, null, finalString);
            } catch (IOException ioe) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            }
                //            } catch (ResponseException re) {
//                return newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
//            }
        }
        // get the POST body
        String postBody = session.getQueryParameterString();
        System.out.println(postBody.length());        // or you can access the POST request's parameters
        String postParameter = session.getParms().get("parameter");

        return newFixedLengthResponse(postBody); // Or postParameter.
    }
}
   // @SuppressWarnings("deprecation")
   // @Override
  /* public Response serve(IHTTPSession session) {
    	
    	Method method = session.getMethod();
    	String uri = session.getUri();
    	 if (Method.POST.equals(method)) {
    		 POST(session);
    	 }
    	 else
    	 {
	        String msg = "<html><body><h1>Hello server</h1>\n";
	        Map<String, String> parms = session.getParms();
	        if (parms.get("username") == null) {
	            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
	        } else {
	            msg += "<p>Hello, " + parms.get("username") + "!</p>";
	        }
	        return newFixedLengthResponse(msg + "</body></html>\n");
    	 }
		return newFixedLengthResponse(Status.OK, uri, null);
    }
    
    
    
    private Response POST(IHTTPSession session) {

        try {
            Map<String, String> files = new HashMap<String, String>();
            session.parseBody(files);
            System.out.println(files);            
            Set<String> keys = files.keySet();
            for(String key: keys){
                String name = key;
                String loaction = files.get(key);

                File tempfile = new File(loaction);
                Files.copy(tempfile.toPath(), new File("destinamtio_path"+name).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }


        } catch (IOException e) {
            System.out.println("i am error file upload post ");
            e.printStackTrace();
        }
        catch (ResponseException e) {
        	System.out.println("i am error file upload post ");
            e.printStackTrace();
        }
		return null;

        //return createResponse("ok i am ");
    }
}


   /* public Response serve(IHTTPSession session) {
        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();
        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException ioe) {
                return new NanoHTTPD.Response(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            } catch (ResponseException re) {
                return new Response(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
            }
        }
        // get the POST body
        String postBody = session.getQueryParameterString();
        // or you can access the POST request's parameters
        String postParameter = session.getParms().get("parameter");
        System.out.println(postBody);
        System.out.println(postParameter);

        return new Response(postBody); // Or postParameter.
    }*/


