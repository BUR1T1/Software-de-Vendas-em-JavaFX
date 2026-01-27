package org.example.app.model;

public class Vendedor {

    private int id;
    private String nome;
    private double comissao;
    private int status;
    private int CPF;


    public Vendedor() {
    }

    public Vendedor(int id, String nome, double comissao, int CPF,int status) {
        this.id = id;
        this.nome = nome;
        this.comissao = comissao;
        this.CPF = CPF;
        this.status = status;

    }


    public int getCPF() {
        return CPF;
    }

    public void setCPF(int CPF) {
        this.CPF = CPF;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
