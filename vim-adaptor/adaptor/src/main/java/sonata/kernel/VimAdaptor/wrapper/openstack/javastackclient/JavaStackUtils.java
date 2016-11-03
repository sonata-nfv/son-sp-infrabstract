package sonata.kernel.VimAdaptor.wrapper.openstack.javastackclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.http.HttpResponse;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JavaStackUtils {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(JavaStackCore.class);

    public static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static String convertYamlToJson(String yamlToConvert) throws IOException {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = yamlReader.readValue(yamlToConvert, Object.class);

        ObjectMapper jsonWriter = new ObjectMapper();
        return jsonWriter.writeValueAsString(obj);
    }

    public static String convertHttpResponseToString(HttpResponse response) throws IOException {

        int status = response.getStatusLine().getStatusCode();
        String statusCode = Integer.toString(status);

        if (statusCode.startsWith("2") || statusCode.startsWith("3")) {
            System.out.println(response.getStatusLine().getStatusCode());
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } else if (status == 403){
            throw new IOException("Access forbidden, make sure you are using the correct credentials: " + statusCode);
        } else if (status == 409) {
            throw new IOException("Stack is already created, conflict detected " + statusCode);
        } else {
            throw new IOException("Failed Request with Status Code: " + statusCode);
        }
    }
}

