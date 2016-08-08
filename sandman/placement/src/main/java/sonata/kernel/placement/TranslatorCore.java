package sonata.kernel.placement;

import java.io.IOException;
import java.util.Map;

public class TranslatorCore {

    private DescriptorTranslator desc_translator;

    public TranslatorCore() {
    }

    public static void main(String[] args) throws InterruptedException {
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
                } catch (IOException e)
                {
                    e.printStackTrace();
                    System.err.println("TranslatorCore::main() : Encountered exception whilte translating");
                }

            }

        }
    }


}