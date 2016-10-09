package sonata.kernel.placement;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonata.kernel.placement.config.PlacementConfig;

import java.io.File;

import sonata.kernel.placement.service.PlacementPluginLoader;
//import org.apache.log4j.Logger;



import java.io.IOException;

public class TranslatorCore {

    private DescriptorTranslator desc_translator;

    public TranslatorCore() {
    }
    final static Logger logger = LoggerFactory.getLogger(TranslatorCore.class);
    public static void main(String[] args) throws InterruptedException {

        // Load configuration
    	logger.info("Loading Configurations");
        System.out.println("Current path: "+new File("").getAbsolutePath());
        PlacementConfig config = PlacementConfigLoader.loadPlacementConfig();


        // Load placement plugin
        logger.info("Loading placement plugins");
        PlacementPluginLoader.loadPlacementPlugin(config.pluginPath,config.placementPlugin);
        System.out.println("Loaded placement-plugin: "+PlacementPluginLoader.placementPlugin.getClass().getName());

        try {
            new Thread(new RestInterfaceClientApi()).start();
            new RestInterfaceServerApi(config.restApi.getServerIp(), config.restApi.getPort()).start();

          } catch (IOException ioe) {
            System.err.println("TranslatorCore::main() : Encountered exception" + ioe);
        }

        DescriptorTranslator desc_translator = new DescriptorTranslator();
        while(true)
        {
            MessageQueueData q_data = MessageQueue.get_rest_serverQ().take();

            if(q_data.data == null)
                continue;

            if(q_data.message_type == MessageType.TRANSLATE_DESC){
                try{
                	logger.debug("Message_type is "+ q_data.message_type);
                    DescriptorTranslator.process_descriptor(q_data.data);
                    /*
                    MessageQueueData c_data = new MessageQueueData(MessageType.POST_MESSAGE, out, "http://131.234.31.45:8080");
                    MessageQueue.get_rest_clientQ().put(c_data);
                    */

                } catch (IOException e)
                {
                    e.printStackTrace();
                    System.err.println("TranslatorCore::main() : Encountered exception whilte translating");
                }

            }

        }
    }


}