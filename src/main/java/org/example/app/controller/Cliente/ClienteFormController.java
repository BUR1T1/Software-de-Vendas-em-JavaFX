package org.example.app.controller.Cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.app.dao.ClienteDAO;
import org.example.app.model.Cliente;

public class ClienteFormController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtTelefone;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private Cliente clienteSelecionado;

    @FXML
    private void salvar() {
        if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty()) {
            alerta("Validação", "Nome e CPF são obrigatórios.");
            return;
        }

        if (clienteSelecionado == null) {
            Cliente c = new Cliente(0, txtNome.getText(), txtCpf.getText(), txtTelefone.getText(), 1);
            clienteDAO.salvar(c);
        } else {
            clienteSelecionado.setNome(txtNome.getText());
            clienteSelecionado.setCpf(txtCpf.getText());
            clienteSelecionado.setTelefone(txtTelefone.getText());
            clienteDAO.atualizar(clienteSelecionado);
        }

        fecharFormulario();
    }

    @FXML
    private void fecharFormulario() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    private void alerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
