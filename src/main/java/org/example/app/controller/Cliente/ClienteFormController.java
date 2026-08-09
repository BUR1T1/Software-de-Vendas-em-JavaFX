package org.example.app.controller.cliente;

import org.example.app.dao.ClienteDAO;
import org.example.app.model.Cliente;
import org.example.app.util.Alerta;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class ClienteFormController {

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtCpf;
    @FXML
    private TextField txtTelefone;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private Cliente clienteSelecionado;

    public void setCliente(Cliente cliente) {
        this.clienteSelecionado = cliente;

        txtNome.setText(cliente.getNome());
        txtCpf.setText(cliente.getCpf());
        txtTelefone.setText(cliente.getTelefone());
    }

    /* =========================================================
    MÃƒÆ’Ã¢â‚¬Â°TODOS PRA VALIDAR CAMPOS
    ========================================================= */
    private boolean validarPrenchimentoCPF() {
        if (txtCpf.getText().isEmpty()) {
            Alerta.warning("ValidaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o", "CPF do cliente precisa ser preenchido");
            return false;
        }
        return true;
    }

    private boolean validarPrenchimentoNome() {
        if (txtNome.getText().isEmpty()) {
            Alerta.warning("ValidaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o", "Nome do cliente precisa ser preenchido");
            return false;
        }
        return true;
    }

    /* =========================================================
      MÃƒÆ’Ã¢â‚¬Â°TODOS PRA CRIAR CLIENTE
      ========================================================= */
    public void criarClient() {
        Cliente c = new Cliente(
                txtNome.getText(),
                txtCpf.getText(),
                txtTelefone.getText(),
                1
        );
        clienteDAO.salvar(c);
    }

    /* =========================================================
       MÃƒÆ’Ã¢â‚¬Â°TODOS PRA ATUALIZAR CLIENTE
       ========================================================= */
    public void atualizarClinete() {
        clienteSelecionado.setNome(txtNome.getText());
        clienteSelecionado.setCpf(txtCpf.getText());
        clienteSelecionado.setTelefone(txtTelefone.getText());
        clienteSelecionado.markAsUpdated();

        clienteDAO.atualizar(clienteSelecionado);
    }

    /* =========================================================
      MÃƒÆ’Ã¢â‚¬Â°TODOS PRA CRIAR OU ATUALIZAR O CLIENTE NO CENARIO DELE JÃƒÆ’Ã‚Â EXISTIR.
      ========================================================= */
    public void verificarCliente() {
        if (clienteDAO.existeCpf(txtCpf.getText())) {
            throw new IllegalArgumentException("Cliente com esse CPF jÃƒÆ’Ã‚Â¡ existe");
        }
    }

    @FXML
    private void salvar() {
        if (!validarPrenchimentoNome() || !validarPrenchimentoCPF()) {
            return;
        }
        try {
            if (clienteSelecionado == null) {
                verificarCliente();
                criarClient();
            } else {
                atualizarClinete();
            }
            fecharFormulario();
        } catch (RuntimeException e) {
            Alerta.error("Erro", e.getMessage());
        }
    }

    @FXML
    private void fecharFormulario() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() {
        txtCpf.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*") && (txtCpf.getText().length() + change.getText().length() <= 11)) {
                return change;
            }
            return null;
        }));
        txtTelefone.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*") && (txtTelefone.getText().length() + change.getText().length() <= 11)) {
                return change;
            }
            return null;
        }));
    }
}
