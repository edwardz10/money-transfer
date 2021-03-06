package com.revolut.moneytransfer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class MoneyTransferApplication {

    public static void main(String[] args) {
        ResourceConfig config = new ResourceConfig();
        config.packages("com.revolut.moneytransfer");
        config.register(JacksonFeature.class);
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));

        Server server = new Server(8090);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.err.println("Failed to start a server: " + e);
        } finally {
            server.destroy();
        }
    }
}
