package org.example.app.controller.Cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.example.app.dao.ClienteDAO;
import org.example.app.model.Cliente;

public class ClienteFormController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtTelefone;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private Cliente clienteSelecionado;

    public void setCliente(Cliente cliente) {
        this.clienteSelecionado = cliente;

        txtNome.setText(cliente.getNome());
        txtCpf.setText(cliente.getCpf());
        txtTelefone.setText(cliente.getTelefone());
    }

    @FXML
    private void salvar() {
        if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty()) {
            alerta("Validação", "Nome e CPF são obrigatórios.");
            return;
        }

        try {
            if (clienteSelecionado == null) {
                if (clienteDAO.existeCpf(txtCpf.getText())) {
                    alerta("Validação", "Já existe um cliente com este CPF.");
                    return;
                }

                Cliente c = new Cliente(
                        txtNome.getText(),
                        txtCpf.getText(),
                        txtTelefone.getText(),
                        1
                );
                clienteDAO.salvar(c);

            } else {
                clienteSelecionado.setNome(txtNome.getText());
                clienteSelecionado.setCpf(txtCpf.getText());
                clienteSelecionado.setTelefone(txtTelefone.getText());
                clienteSelecionado.markAsUpdated();

                clienteDAO.atualizar(clienteSelecionado);
            }

            fecharFormulario();

        } catch (RuntimeException e) {
            alerta("Erro", e.getMessage());
        }
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

    @FXML public void initialize() {
        txtCpf.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*") && (txtCpf.getText().length() + change.getText().length() <= 11)) {
                return change;
            } return null;
        }));
          txtTelefone.setTextFormatter(new TextFormatter<>(change -> {
              if (change.getText().matches("[0-9]*") && (txtTelefone.getText().length() + change.getText().length() <= 11)) {
                  return change;
              }
              return null;
          }));
    }
}
