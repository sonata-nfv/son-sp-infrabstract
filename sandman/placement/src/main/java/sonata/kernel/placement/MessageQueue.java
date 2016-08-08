package sonata.kernel.placement;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

//Modify them as requrired.
enum MessageType {
    POST_MESSAGE, GET_MESSAGE, TERMINATE_MESSAGE, TRANSLATE_DESC
}
class MessageQueueData
{
    MessageType message_type;
    String data;
    String uri;

    public MessageQueueData(MessageType message_type, String data) {
        this.message_type = message_type;
        this.data = data;
    }
}
class MessageQueue
{
    private static BlockingQueue<MessageQueueData> rest_serverQ;
    private static BlockingQueue<MessageQueueData> rest_clientQ;

    static public BlockingQueue<MessageQueueData> get_rest_serverQ()
    {
        return rest_serverQ;
    }

    static public BlockingQueue<MessageQueueData> get_rest_clientQ()
    {
        return rest_clientQ;
    }
}
