package net.johanbasson.fp.api.system.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.rabbitmq.client.*;
import net.johanbasson.fp.api.config.Configuration;
import net.johanbasson.fp.api.system.Constants;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class EventWorker implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(EventWorker.class);
    private final Connection connection;
    private final Channel channel;
    private final EventBus eventBus;
    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

    public EventWorker(Configuration configuration, EventBus eventBus) throws IOException, TimeoutException {
        this.eventBus = eventBus;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(configuration.rabbitMq().host());
        factory.setPort(configuration.rabbitMq().port());
        factory.setUsername(configuration.rabbitMq().username());
        factory.setPassword(configuration.rabbitMq().password());
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(Constants.Queues.EVENTS, true, false, false, null);
        start();
    }

    private void start() throws IOException {
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(
                    String consumerTag,
                    Envelope envelope,
                    AMQP.BasicProperties properties,
                    byte[] body) throws IOException {

                try {
                    EventEnvelope eventEnvelope = objectMapper.readValue(body, EventEnvelope.class);
                    Object command = objectMapper.readValue(eventEnvelope.getBody(), Class.forName(eventEnvelope.getClazz()));
                    eventBus.post(command);
                } catch (ClassNotFoundException e) {
                    log.error("Unable to deserialize event envelope", e);
                    throw new IOException("Unable to process message - " + e.getLocalizedMessage());
                }

            }
        };
        channel.basicConsume(Constants.Queues.COMMANDS, true, consumer);
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }
}
