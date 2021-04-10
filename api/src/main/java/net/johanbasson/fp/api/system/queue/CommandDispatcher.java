package net.johanbasson.fp.api.system.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.johanbasson.fp.api.config.Configuration;
import net.johanbasson.fp.api.system.Constants;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CommandDispatcher implements AutoCloseable{

    private final Connection connection;
    private final Channel channel;
    private final ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

    public CommandDispatcher(Configuration configuration) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(configuration.rabbitMq().host());
        factory.setPort(configuration.rabbitMq().port());
        factory.setUsername(configuration.rabbitMq().username());
        factory.setPassword(configuration.rabbitMq().password());
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(Constants.Queues.COMMANDS, true, false, false, null);
    }

    public void dispatch(Object command) throws IOException {
        byte[] body = objectMapper.writeValueAsBytes(command);
        channel.basicPublish("", Constants.Queues.COMMANDS, null, objectMapper.writeValueAsBytes(new CommandEnvelope(body, command.getClass().getName())));
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }
}
