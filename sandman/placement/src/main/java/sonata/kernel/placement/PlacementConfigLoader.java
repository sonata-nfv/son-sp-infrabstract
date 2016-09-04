package sonata.kernel.placement;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import sonata.kernel.VimAdaptor.commons.nsd.ServiceDescriptor;
import sonata.kernel.placement.config.PlacementConfig;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

public final class PlacementConfigLoader {
	final static Logger logger = Logger.getLogger(PlacementConfigLoader.class);
    public final static String CONFIG_FILENAME = "placementd.yml";

    public final static String[] CONFIG_FOLDERS = new String[]{""};

    public static PlacementConfig loadPlacementConfig(){
    	logger.info("Placement config loader");
        PlacementConfig config = null;

        for (String configFolder : CONFIG_FOLDERS){

            File configFile = new File(Paths.get(configFolder,CONFIG_FILENAME).toString());
            logger.debug("Config Folder is: "+ configFile.getPath());
            if (configFile.exists()) {

                config = mapConfigFile(configFile);

                if(config != null)
                    break;
            }
        }

        if(config == null)
            config = createDefaultConfig();

        return config;
    }

    public static PlacementConfig mapConfigFile(File configFile) {
    	logger.info("Map config file");
        PlacementConfig config = null;
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SimpleModule module = new SimpleModule();

        try {

            StringBuilder bodyBuilder = new StringBuilder();
            BufferedReader in = null;

            in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(configFile), Charset.forName("UTF-8")));

            String line;
            while ((line = in.readLine()) != null)
                bodyBuilder.append(line + "\n\r");

            mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            config = mapper.readValue(bodyBuilder.toString(), PlacementConfig.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    public static PlacementConfig createDefaultConfig(){
    	logger.info("Create Default Config");
        PlacementConfig config = new PlacementConfig();
        config.pluginPath = "";
        config.placementPlugin = "";
        return config;
    }
}
