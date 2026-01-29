package org.example.app.model;


import org.example.app.BaseEntity;

public class Usuario extends BaseEntity {

    private String login;
    private String senha;
    private String perfil; // ADMIN / OPERADOR

    public Usuario(String login, String senha, String perfil) {
        super();
        this.login =login;
      this.senha= senha;
      this.perfil= perfil;
    }



    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}
