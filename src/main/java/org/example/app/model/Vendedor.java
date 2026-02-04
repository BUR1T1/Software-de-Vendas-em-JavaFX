package org.example.app.model;

import org.example.app.BaseEntity;

public class Vendedor extends BaseEntity {

    private String nome;
    private double comissao;
    private String cpf;

    public Vendedor() {}

    public Vendedor(String nome, double comissao, String cpf) {
        this.nome = nome;
        this.comissao = comissao;
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }



    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCpfFormatted() {
        if (cpf == null || cpf.length() != 11)
            return cpf;
        return String.format("%s.%s.%s-%s",
                cpf.substring(0, 3),
                cpf.substring(3, 6),
                cpf.substring(6, 9),
                cpf.substring(9));
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getComissao() { return comissao; }
    public void setComissao(double comissao) { this.comissao = comissao; }
}
