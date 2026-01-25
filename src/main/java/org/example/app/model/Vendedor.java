package org.example.app.model;

public class Vendedor {

    private int id;
    private String nome;
    private double comissao;

    public Vendedor() {
    }

    public Vendedor(int id, String nome, double comissao) {
        this.id = id;
        this.nome = nome;
        this.comissao = comissao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getComissao() {
        return comissao;
    }

    public void setComissao(double comissao) {
        this.comissao = comissao;
    }
}
