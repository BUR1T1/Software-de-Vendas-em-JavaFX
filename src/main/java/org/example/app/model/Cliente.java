package org.example.app.model;

import org.example.app.BaseEntity;

public class Cliente extends BaseEntity {

    private String nome;
    private String cpf;
    private String telefone;

    public Cliente(String nome, String cpf, String telefone, int status) {
        super();
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    public Cliente() {
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
    public String getTelefoneFormatted() {
        if (telefone == null)
            return telefone;
        if (telefone.length() == 10) {
            // formato fixo (XX) XXXX-XXXX
            return String.format("(%s) %s-%s",
                    telefone.substring(0, 2),
                    telefone.substring(2, 6),
                    telefone.substring(6));
        } else if (telefone.length() == 11) {
            // celular (XX) XXXXX-XXXX
            return String.format("(%s) %s-%s",
                    telefone.substring(0, 2),
                    telefone.substring(2, 7),
                    telefone.substring(7));
        } return telefone;
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
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF deve conter exatamente 11 dígitos numéricos.");
        }
        this.cpf = cpf;
    }


    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || !(telefone.matches("\\d{10}") || telefone.matches("\\d{11}"))) {
            throw new IllegalArgumentException("Telefone deve conter 10 ou 11 dígitos numéricos.");
        }
        this.telefone = telefone;
    }

}

