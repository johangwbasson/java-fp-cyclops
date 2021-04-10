package net.johanbasson.fp.api.system.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import net.johanbasson.fp.api.config.Configuration;
import net.johanbasson.fp.api.system.Constants;
import net.johanbasson.fp.api.system.commandbus.CommandBus;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CommandWorker implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(CommandWorker.class);

    private final Connection connection;
    private final Channel channel;
    private final CommandBus commandBus;
    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

    public CommandWorker(Configuration configuration, CommandBus commandBus) throws IOException, TimeoutException {
        this.commandBus = commandBus;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(configuration.rabbitMq().host());
        factory.setPort(configuration.rabbitMq().port());
        factory.setUsername(configuration.rabbitMq().username());
        factory.setPassword(configuration.rabbitMq().password());
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(Constants.Queues.COMMANDS, true, false, false, null);
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
                    CommandEnvelope commandEnvelope = objectMapper.readValue(body, CommandEnvelope.class);
                    Object command = objectMapper.readValue(commandEnvelope.body(), Class.forName(commandEnvelope.clazz()));
                    commandBus.execute(command);
                } catch (ClassNotFoundException e) {
                    log.error("Unable to deserialize command envelope", e);
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
