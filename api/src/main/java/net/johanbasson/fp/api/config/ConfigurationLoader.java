package net.johanbasson.fp.api.config;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

public class ConfigurationLoader {

    public static Configuration load(String configFile) throws ConfigurationException {
        Configurations configs = new Configurations();
        PropertiesConfiguration conf = configs.properties(new File(configFile));
        Database db = new Database(conf.getString("database.url"), conf.getString("database.username"), conf.getString("database.password"));
        Server server = new Server(conf.getInt("server.port"));
        RabbitMq rabbitMq = new RabbitMq(conf.getString("rabbitmq.host"), conf.getInt("rabbitmq.port"), conf.getString("rabbitmq.username"), conf.getString("rabbitmq.password"));
        return new Configuration(db, server, rabbitMq);
    }
}
