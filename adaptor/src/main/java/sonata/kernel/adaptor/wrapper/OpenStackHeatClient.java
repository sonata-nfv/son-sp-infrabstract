package sonata.kernel.adaptor.wrapper;

import org.omg.CORBA.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 * Created by smendel on 4/20/16.
 */
public class OpenStackHeatClient {


    /**
     * Create stack
     * @param stackName - usually service + tenant
     * @param template - the content of the hot template thet describes the file
     * @return
     */

    public String createStack(String stackName, String template) {

        String uuid = null;
        String s = null;
        try {
            System.out.println("Creating stack: " + stackName);
            Process p = Runtime.getRuntime().exec("python heat-api.py "+stackName + " " + template);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
    
               BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));
    
               // read the output from the command
               System.out.println("Here is the standard output of the command:\n");
               while ((s = stdInput.readLine()) != null) {
                   System.out.println(s);
                   uuid = s;
               }
                
               // read any errors from the attempted command
               System.out.println("Here is the standard error of the command (if any):\n");
               while ((s = stdError.readLine()) != null) {
                   System.out.println(s);
               }
            
            System.out.println("UUID of new stack: " +uuid);
        }catch(Exception e){
            System.out.println("Runtime error creating stack : " + stackName + " error message: "+ e.getMessage());
        }
    
        return uuid;
    }


    public String getCreateStackStatus(String stackName, String uuid){

        String status = null;

        System.out.println("Getting status for stack: " + stackName);

        try {
            Process p = Runtime.getRuntime().exec("python heat-api.py "+ uuid);
            BufferedReader in = new BufferedReader(new InputStreamReader ((p.getInputStream())));
            status = in.readLine();
            System.out.println("The status of stack: " + stackName+ " with uuid: " + uuid +" : " + status );
        }catch(Exception e){
            System.out.println("Runtime error getting stack status for stack : " + stackName + " error message: "+ e.getMessage());
        }

        return status;

    }

//    public String deleteStack(String stackName, String uuid){
//
//        String status = null;
//
//        System.out.println("Deleting stack: " + stackName);
//
//        try {
//            Process p = Runtime.getRuntime().exec("python heat-api.py "+  "delete" +  " "+ uuid);
//            BufferedReader in = new BufferedReader(new InputStreamReader ((p.getInputStream())));
//            status = in.readLine();
//            System.out.println("The status of stack: " + stackName+ " with uuid: " + uuid +" : " + status );
//        }catch(Exception e){
//            System.out.println("Runtime error getting stack status for stack : " + stackName + " error message: "+ e.getMessage());
//        }
//
//        return status;
//
//    }



}
