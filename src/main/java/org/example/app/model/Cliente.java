package org.example.app.model;

public class Cliente {

    private int id;
    private String nome;
    private String cpf;
    private String telefone;
    private int status; // 1 = ativo | 2 = inativo

    public Cliente(int id, String nome, String cpf, String telefone, int status) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.status = status;
    }

    public Cliente() {

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

