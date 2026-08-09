package org.example.app.consumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConnection {

    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USUARIO = "guest";
    private static final String SENHA = "guest";

    // Mantém a conexão em campo estático para evitar que o GC a colete
    // e feche o canal silenciosamente (causa comum de consumer parar de receber mensagens).
    private static Connection connection;

    // Rastreia o canal do consumer para poder fechá-lo ao encerrar a aplicação,
    // evitando o acúmulo de consumers/filas órfãs no RabbitMQ.
    private static Channel channel;

    public static Channel abrirCanal() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USUARIO);
        factory.setPassword(SENHA);

        if (connection == null || !connection.isOpen()) {
            connection = factory.newConnection();
        }
        channel = connection.createChannel();
        return channel;
    }

    /**
     * Encerra o canal e a conexão com o RabbitMQ.
     * Deve ser chamado ao fechar a aplicação (ex: MainApp.stop()).
     * Isso evita que consumers/filas fiquem órfãos no servidor RabbitMQ.
     */
    public static void fechar() {
        Exception erro = null;

        // 1. Fecha o canal do consumer primeiro
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (Exception e) {
                erro = e;
            } finally {
                channel = null;
            }
        }

        // 2. Fecha a conexão
        if (connection != null && connection.isOpen()) {
            try {
                connection.close();
            } catch (Exception e) {
                if (erro == null) erro = e;
            } finally {
                connection = null;
            }
        }

        if (erro != null) {
            erro.printStackTrace();
        }
    }
}
