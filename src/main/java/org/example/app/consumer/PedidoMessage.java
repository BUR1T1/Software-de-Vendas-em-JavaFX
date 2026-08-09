package org.example.app.consumer;

import java.util.List;

/**
 * DTO correspondente ao JSON recebido na fila "pedidos".
 *
 * Formato esperado:
 * {
 *   "clienteId": 1,
 *   "vendedorId": 1,
 *   "itens": [ { "produtoId": 1, "quantidade": 2 } ],
 *   "data": "2026-08-06",
 *   "hora": "10:30:00"
 * }
 */
public class PedidoMessage {

    private Long clienteId;
    private Long vendedorId;
    private String data;
    private String hora;
    private List<ItemMessage> itens;

    public static class ItemMessage {
        private Long produtoId;
        private int quantidade;

        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getVendedorId() { return vendedorId; }
    public void setVendedorId(Long vendedorId) { this.vendedorId = vendedorId; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public List<ItemMessage> getItens() { return itens; }
    public void setItens(List<ItemMessage> itens) { this.itens = itens; }
}
