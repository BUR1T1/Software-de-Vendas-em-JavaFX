package org.example.app.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private void entrar() {
        if (txtUsuario.getText().isEmpty() || txtSenha.getText().isEmpty()) {
            return;
        }

        // login mock de teste
        if (txtUsuario.getText().equals("admin") && txtSenha.getText().equals("123")) {
            abrirTelaPrincipal();
        } else {
            System.out.println("Login ou senha inválidos!");
        }
    }

    private void abrirTelaPrincipal() {
        try {
            Stage stageAtual = (Stage) txtUsuario.getScene().getWindow();

            Parent root = FXMLLoader.load(
                    getClass().getResource("/org/example/view/principal.fxml")
            );

            stageAtual.setScene(new Scene(root));
            stageAtual.setTitle("Sistema de Loja - Principal");
            stageAtual.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
