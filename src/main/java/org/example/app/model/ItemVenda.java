package org.example.app.model;

import org.example.app.BaseEntity;

public class ItemVenda extends BaseEntity {

    private Produto produto;
    private Venda venda;
    private int quantidade;

    public ItemVenda(Produto produto, int quantidade) {
        super();
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Venda getVenda() { return venda; }
    public void setVenda(Venda venda) { this.venda = venda; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getTotal() {
        return produto.getPreco() * quantidade;
    }
}
