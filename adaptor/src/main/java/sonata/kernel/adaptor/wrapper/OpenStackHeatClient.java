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


    public boolean createStack(String stackName, String stack) {

        try {
              Process p = Runtime.getRuntime().exec("python test1.py "+stackName + stack);

        }catch(Exception e){
            return false;
        }
    
        return true;
    }
}
