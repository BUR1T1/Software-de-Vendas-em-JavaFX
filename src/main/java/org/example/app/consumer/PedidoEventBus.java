package org.example.app.consumer;

import org.example.app.model.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Barramento de eventos simples para notificar a interface JavaFX
 * quando um novo pedido chega pela fila (RabbitMQ).
 */
public class PedidoEventBus {

    // Fila de pedidos pendentes ainda não representados na tela de venda
    private static final List<Pedido> pedidosPendentes = new ArrayList<>();

    private static final List<Consumer<Pedido>> listeners = new ArrayList<>();

    public static synchronized void publicar(Pedido pedido) {
        pedidosPendentes.add(pedido);
        for (Consumer<Pedido> listener : new ArrayList<>(listeners)) {
            try {
                listener.accept(pedido);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void registrar(Consumer<Pedido> listener) {
        listeners.add(listener);
    }

    public static synchronized void remover(Consumer<Pedido> listener) {
        listeners.remove(listener);
    }

    public static synchronized List<Pedido> getPedidosPendentes() {
        return new ArrayList<>(pedidosPendentes);
    }

    public static synchronized void removerPedido(Pedido pedido) {
        pedidosPendentes.removeIf(p -> p.getId() != null && p.getId().equals(pedido.getId()));
    }

    public static synchronized void limparPendentes() {
        pedidosPendentes.clear();
    }
}

