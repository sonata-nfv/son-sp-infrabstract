package sonata.kernel.placement;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

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

    public MessageQueueData(MessageType message_type, String data, String uri) {
        this.message_type = message_type;
        this.data = data;
        this.uri = uri;
    }
}
class MessageQueue
{
    private static BlockingQueue<MessageQueueData> rest_serverQ = new LinkedBlockingDeque<MessageQueueData>();
    private static BlockingQueue<MessageQueueData> rest_clientQ = new LinkedBlockingDeque<MessageQueueData>();

    static public BlockingQueue<MessageQueueData> get_rest_serverQ()
    {
        return rest_serverQ;
    }

    static public BlockingQueue<MessageQueueData> get_rest_clientQ()
    {
        return rest_clientQ;
    }
}
