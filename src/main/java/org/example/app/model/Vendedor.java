package org.example.app.model;

import org.example.app.BaseEntity;

public class Vendedor extends BaseEntity {

    private String nome;
    private double comissao;
    private String CPF;


    public Vendedor() {
    }

    public Vendedor(String nome, double comissao, String CPF) {
        super();
        this.nome = nome;
        this.comissao = comissao;
        this.CPF = CPF;

    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
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
