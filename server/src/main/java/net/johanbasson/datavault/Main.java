package net.johanbasson.datavault;

import net.johanbasson.fp.api.system.Application;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws ConfigurationException, IOException, TimeoutException {
        Server server = new Server(Application.initialize());
        server.start();
    }

}
