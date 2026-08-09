package org.example.app.consumer;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.example.app.dao.ClienteDAO;
import org.example.app.dao.PedidosDAO;
import org.example.app.dao.ProdutoDAO;
import org.example.app.dao.VendedorDAO;
import org.example.app.model.Cliente;
import org.example.app.model.ItemPedido;
import org.example.app.model.Pedido;
import org.example.app.model.Produto;
import org.example.app.model.Vendedor;

import java.time.LocalDate;
import java.time.LocalTime;

public class PedidoConsumer {

    private static final String FILA = "pedidos";

    private static final Gson gson = new Gson();

    private static final PedidosDAO pedidosDAO = new PedidosDAO();
    private static final ClienteDAO clienteDAO = new ClienteDAO();
    private static final VendedorDAO vendedorDAO = new VendedorDAO();
    private static final ProdutoDAO produtoDAO = new ProdutoDAO();

    public static void iniciar() {
        try {
            Channel channel = RabbitMQConnection.abrirCanal();
            channel.queueDeclare(FILA, true, false, false, null);

            System.out.println("[CONSUMER] Aguardando pedidos na fila '" + FILA + "'...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String mensagem = new String(delivery.getBody(), "UTF-8");
                System.out.println("[CONSUMER] Pedido recebido: " + mensagem);

                try {
                    PedidoMessage msg = gson.fromJson(mensagem, PedidoMessage.class);
                    Pedido pedido = montarPedido(msg);

                    if (pedido != null) {
                        pedidosDAO.salvar(pedido);
                        // Define o id retornado para o evento (opcional)
                        PedidoEventBus.publicar(pedido);
                        System.out.println("[CONSUMER] Pedido salvo com sucesso.");
                    } else {
                        System.err.println("[CONSUMER] Pedido inválido (cliente, vendedor ou produto não localizado). " + mensagem);
                    }
                } catch (Exception e) {
                    System.err.println("[CONSUMER] Erro ao processar pedido:");
                    e.printStackTrace();
                }
            };

            String consumerTag = channel.basicConsume(FILA, true, deliverCallback, consumerTagX -> {
            });
            System.out.println("[CONSUMER] Consumer registrado na fila '" + FILA + "' com tag: " + consumerTag);

        } catch (Exception e) {
            System.err.println("[CONSUMER] Falha ao conectar no RabbitMQ (localhost:5672). Verifique se o servidor RabbitMQ não está rodando.");
            e.printStackTrace();
        }
    }

    private static Pedido montarPedido(PedidoMessage msg) {
    if (msg == null || msg.getItens() == null || msg.getItens().isEmpty()) {
        return null;
    }

    Cliente cliente = buscarCliente(msg.getClienteId());
    Vendedor vendedor = buscarVendedor(msg.getVendedorId());

    if (cliente == null || vendedor == null) {
        return null;
    }

    Pedido pedido = new Pedido();

    if (msg.getData() != null && !msg.getData().isBlank()) {
        pedido.setDataPedido(LocalDate.parse(msg.getData()));
    }
    if (msg.getHora() != null && !msg.getHora().isBlank()) {
        pedido.setHoraPedido(LocalTime.parse(msg.getHora()));
    }

    pedido.setCliente(cliente);
    pedido.setVendedor(vendedor);

    for (PedidoMessage.ItemMessage itemMsg : msg.getItens()) {
        Produto produto = buscarProduto(itemMsg.getProdutoId());
        if (produto == null || itemMsg.getQuantidade() <= 0) {
            return null; // pedido inválido se algum item não existir
        }
        pedido.getItens().add(new ItemPedido(produto, itemMsg.getQuantidade()));
    }

    return pedido;
}

    private static Cliente buscarCliente(Long id) {
        if (id == null) {
            return null;
        }
        return clienteDAO.buscarPorId(id);
    }

    private static Vendedor buscarVendedor(Long id) {
        if (id == null) {
            return null;
        }
        return vendedorDAO.buscarPorId(id);
    }

    private static Produto buscarProduto(Long id) {
        if (id == null) {
            return null;
        }
        return produtoDAO.buscarPorId(id);
    }
}
