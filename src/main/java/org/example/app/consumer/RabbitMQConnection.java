package org.example.app.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQConnection {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USUARIO = "guest";
    private static final String SENHA = "guest";

    public static Channel abrirCanal() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USUARIO);
        factory.setPassword(SENHA);

        Connection connection = factory.newConnection();
        return connection.createChannel();
    }
}
