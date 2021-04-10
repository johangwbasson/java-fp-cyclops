package net.johanbasson.fp.api.system.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.johanbasson.fp.api.config.Configuration;
import net.johanbasson.fp.api.system.Constants;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class EventDispatcher implements AutoCloseable {

    private final Connection connection;
    private final Channel channel;
    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
    private static final Logger log = LoggerFactory.getLogger(EventDispatcher.class);

    public EventDispatcher(Configuration configuration) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(configuration.rabbitMq().host());
        factory.setPort(configuration.rabbitMq().port());
        factory.setUsername(configuration.rabbitMq().username());
        factory.setPassword(configuration.rabbitMq().password());
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(Constants.Queues.EVENTS, true, false, false, null);
    }

    public void dispatch(Object event)  {
        try {
            byte[] body = objectMapper.writeValueAsBytes(event);
            channel.basicPublish("", Constants.Queues.COMMANDS, null, objectMapper.writeValueAsBytes(new EventEnvelope(body, event.getClass().getName())));
        } catch (IOException ex) {
            log.error("Unable to publish to RabbitMQ", ex);
        }
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }
}
