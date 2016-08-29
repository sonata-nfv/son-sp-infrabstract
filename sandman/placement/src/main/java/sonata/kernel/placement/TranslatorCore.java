package sonata.kernel.placement;

import sonata.kernel.placement.config.PlacementConfig;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TranslatorCore {

    private DescriptorTranslator desc_translator;

    public TranslatorCore() {
    }

    public static void main(String[] args) throws InterruptedException {

        // Load configuration
        System.out.println("Current path: "+new File("").getAbsolutePath());
        PlacementConfig config = PlacementConfigLoader.loadPlacementConfig();
        System.out.println("Plugin-path: "+config.pluginPath);
        System.out.println("Placement-plugin: "+config.placementPlugin);

        // Load placement plugin
        PlacementPluginLoader.loadPlacementPlugin(config.pluginPath,config.placementPlugin);
        System.out.println("Loaded placement-plugin: "+PlacementPluginLoader.placementPlugin.getClass().getName());

        try {
            new Thread(new RestInterfaceClientApi()).start();
            new RestInterfaceServerApi("localhost", 8080).start();

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
                    String out = DescriptorTranslator.process_descriptor(q_data.data);
                    MessageQueueData c_data = new MessageQueueData(MessageType.POST_MESSAGE, out, "http://131.234.244.233:8080");
                    MessageQueue.get_rest_clientQ().put(c_data);

                } catch (IOException e)
                {
                    e.printStackTrace();
                    System.err.println("TranslatorCore::main() : Encountered exception whilte translating");
                }

            }

        }
    }


}