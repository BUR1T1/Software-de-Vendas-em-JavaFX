package org.example.app.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class PedidoConsumer {

    private static final String FILA = "pedidos";

    public static void iniciar() {
        try {
            Channel channel = RabbitMQConnection.abrirCanal();
            channel.queueDeclare(FILA, true, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String mensagem = new String(delivery.getBody(), "UTF-8");
                System.out.println("Pedido recebido: " + mensagem);
                // próximo passo: parse do JSON + PedidosDao.salvar(...)
            };

            channel.basicConsume(FILA, true, deliverCallback, consumerTag -> {});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}