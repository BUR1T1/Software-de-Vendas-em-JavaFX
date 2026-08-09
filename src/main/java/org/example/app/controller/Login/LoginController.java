package org.example.app.controller.login;

import org.example.app.dao.UsuarioDAO;
import org.example.app.model.Usuario;
import org.example.app.util.Alerta;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Label lblMensagem;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void entrar() {
        if (txtUsuario.getText().isEmpty() || txtSenha.getText().isEmpty()) {
            if (lblMensagem != null) {
                lblMensagem.setText("Preencha usuÃƒÆ’Ã‚Â¡rio e senha.");
            }
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(
                txtUsuario.getText().trim(),
                txtSenha.getText()
        );

        if (usuario != null) {
            abrirTelaPrincipal();
        } else {
            if (lblMensagem != null) {
                lblMensagem.setText("Login ou senha invÃƒÆ’Ã‚Â¡lidos");
            }
            Alerta.warning("Falha no acesso", "Login ou senha invÃƒÆ’Ã‚Â¡lidos");
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
