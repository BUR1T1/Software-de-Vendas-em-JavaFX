package org.example.app.model;

public class ItemPedido extends BaseEntity {

    private Produto produto;
    private int quantidade;
    private double precoUnitario;

    public ItemPedido() {
        super();
    }

    public ItemPedido(Produto produto, int quantidade) {
        super();
        this.produto = produto;
        this.quantidade = quantidade;
        if (produto != null) {
            this.precoUnitario = produto.getPreco();
        }
    }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) {
        this.produto = produto;
        if (produto != null) {
            this.precoUnitario = produto.getPreco();
        }
    }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }

    public double getTotal() {
        return precoUnitario * quantidade;
    }
}
