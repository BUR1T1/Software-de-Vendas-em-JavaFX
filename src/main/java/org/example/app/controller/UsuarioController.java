package org.example.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.app.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioController {

    // Simulação de banco local
    public static List<Usuario> usuarios = new ArrayList<>();

    @FXML private TextField txtNome;
    @FXML private TextField txtLogin;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblMensagem;

    @FXML
    public void salvar() {

        // Validação básica
        if (txtNome.getText().isEmpty() ||
                txtLogin.getText().isEmpty() ||
                txtSenha.getText().isEmpty()) {

            lblMensagem.setText("Preencha todos os campos.");
            return;
        }

        // Criação do usuário
        Usuario usuario = new Usuario(
                txtNome.getText(),
                txtLogin.getText(),
                txtSenha.getText()
        );

        usuarios.add(usuario);

        lblMensagem.setText("Usuário cadastrado com sucesso!");

        // Limpa campos
        txtNome.clear();
        txtLogin.clear();
        txtSenha.clear();
    }
}
