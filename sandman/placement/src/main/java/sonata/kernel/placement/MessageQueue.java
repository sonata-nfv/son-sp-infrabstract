package sonata.kernel.placement;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

//Modify them as requrired.
enum MessageType {
    POST_MESSAGE, GET_MESSAGE, TERMINATE_MESSAGE, TRANSLATE_DESC
}
class MessageQueueData
{
	final static Logger logger = Logger.getLogger(MessageQueueData.class);
    MessageType message_type;
    String data;
    String uri;

    public MessageQueueData(MessageType message_type, String data) {
        this.message_type = message_type;
        this.data = data;
        logger.debug("Message Queue Data: "+ data);
    }

    public MessageQueueData(MessageType message_type, String data, String uri) {
        this.message_type = message_type;
        this.data = data;
        this.uri = uri;
        logger.debug("Message Queue Data: "+ data);
        logger.debug("Message Queue uri: "+ uri);
    }
}
class MessageQueue
{
	final static Logger logger = Logger.getLogger(MessageQueue.class);
    private static BlockingQueue<MessageQueueData> rest_serverQ = new LinkedBlockingDeque<MessageQueueData>();
    private static BlockingQueue<MessageQueueData> rest_clientQ = new LinkedBlockingDeque<MessageQueueData>();

    static public BlockingQueue<MessageQueueData> get_rest_serverQ()
    {
    	logger.info("Rest Server");
        return rest_serverQ;
    }

    static public BlockingQueue<MessageQueueData> get_rest_clientQ()
    {
    	logger.info("Rest Client");
        return rest_clientQ;
    }
}
