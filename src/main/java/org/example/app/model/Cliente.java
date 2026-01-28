package org.example.app.model;

import org.example.app.BaseEntity;

public class Cliente extends BaseEntity {

    private String nome;
    private String cpf;
    private String telefone;
    private int status; // 1 = ativo | 2 = inativo

    public Cliente(String nome, String cpf, String telefone, int status) {
        super();
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.status = status;
    }

    public Cliente() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}

