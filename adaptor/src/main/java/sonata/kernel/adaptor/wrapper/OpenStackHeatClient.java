package sonata.kernel.adaptor.wrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by smendel on 4/20/16.
 * <p/>
 * This class wraps a Heat Client written in python
 * when instantiated the connection details of the OpenStack instance should be provided
 */
public class OpenStackHeatClient {

    private String url; //url of the OpenStack Client

    private String userName;    //OpenStack Client user

    private String password;    //OpenStack Client paswword

    private String tenantName;    //OpenStack tenant name


    public OpenStackHeatClient(String url, String userName, String password, String tenantName) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.tenantName = tenantName;
    }

    /**
     * Create stack
     *
     * @param stackName - usually service tenant
     * @param template  - the content of the hot template that describes the file
     * @return - the uuid of the created stack, if the process failed the returned value is null
     */
    public String createStack(String stackName, String template) {
        String uuid = null;
        String s = null;

        //todo - log in debug mode the template as well

        System.out.println("Creating stack: " + stackName);

        try {

            //Call the python client for creating the stack
            ProcessBuilder processBuilder = new ProcessBuilder("python2.7", "heat-api.py",
                    "--configuration", url, userName, password, tenantName,
                    "--create", stackName, template);
            Process p = processBuilder.start();

            //Read the errors of creating the stack
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            if (stdError.read() != -1) {
                System.out.println("The errors of creating stack (if any):");
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }
            }
            //Read the results of creating the stack
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            System.out.println("The results of creating the stack:");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                uuid = s;
            }


            if (uuid != null) {
                System.out.println("UUID of new stack: " + uuid);
            }

        } catch (Exception e) {
            System.out.println("Runtime error creating stack : " + stackName + " error message: " + e.getMessage());
        }

        return uuid;
    }


    /**
     * Get the status of existing stack
     *
     * @param stackName - used for logging, usually service tenant
     * @param uuid      - OpenStack UUID of the stack
     * @return - the OpenStack status of the stack
     */
    public String getStackStatus(String stackName, String uuid) {

        String status = null;
        String s = null;
        System.out.println("Getting status for stack: " + stackName);

        try {
            //Call the python client for the status of the stack
            ProcessBuilder processBuilder = new ProcessBuilder("python2.7", "heat-api.py",
                    "--configuration", url, userName, password, tenantName,
                    "--status", uuid);
            Process p = processBuilder.start();

            //Read the status of the stack
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                status = s;
            }

            System.out.println("The status of stack: " + stackName + " with uuid: " + uuid + " : " + status);
        } catch (Exception e) {
            System.out.println("Runtime error getting stack status for stack : " + stackName + " error message: " + e.getMessage());
        }

        return status;

    }

    /**
     * @param stackName - used for logging, usually service tenant
     * @param uuid-     OpenStack UUID of the stack
     * @return - if the operation was sent successfully to OpenStack - 'DELETED'
     */
    public String deleteStack(String stackName, String uuid) {

        String isDeleted = null;
        String s = null;

        System.out.println("Deleting stack: " + stackName);

        try {
            //Call the python client for deleting of the stack
            ProcessBuilder processBuilder = new ProcessBuilder("python2.7", "heat-api.py",
                    "--configuration", url, userName, password, tenantName,
                    "--delete", uuid);
            Process p = processBuilder.start();

            //Read the results
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                isDeleted = s;
            }

            System.out.println("Request was sent for stack: " + stackName + " with uuid: " + uuid + " : " + isDeleted);
        } catch (Exception e) {
            System.out.println("Runtime error when deleting stack : " + stackName + " error message: " + e.getMessage());
        }


        return isDeleted;

    }

    @Override
    public String toString() {
        return "OpenStackHeatClient{" +
                "url='" + url + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", tenantName='" + tenantName + '\'' +
                '}';
    }
}