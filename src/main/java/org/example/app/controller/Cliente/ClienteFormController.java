package org.example.app.controller.Cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.example.app.dao.ClienteDAO;
import org.example.app.model.Cliente;
import org.example.app.util.Alerta;

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

       /* =========================================================
       MÉTODOS PRA VALIDAR CAMPOS
       ========================================================= */
       private boolean validarPrenchimentoCPF() {
           if (txtCpf.getText().isEmpty()) {
               Alerta.warning("Validação", "CPF do cliente precisa ser preenchido");
               return false;
           }
           return true;
       }

    private boolean validarPrenchimentoNome() {
        if (txtNome.getText().isEmpty()) {
            Alerta.warning("Validação", "Nome do cliente precisa ser preenchido");
            return false;
        }
        return true;
    }

    /* =========================================================
      MÉTODOS PRA CRIAR CLIENTE
      ========================================================= */
    public void criarClient(){
        Cliente c = new Cliente(
                txtNome.getText(),
                txtCpf.getText(),
                txtTelefone.getText(),
                1
        );
        clienteDAO.salvar(c);
    }

     /* =========================================================
       MÉTODOS PRA ATUALIZAR CLIENTE
       ========================================================= */
    public void atualizarClinete(){
        clienteSelecionado.setNome(txtNome.getText());
        clienteSelecionado.setCpf(txtCpf.getText());
        clienteSelecionado.setTelefone(txtTelefone.getText());
        clienteSelecionado.markAsUpdated();

        clienteDAO.atualizar(clienteSelecionado);
    }

       /* =========================================================
      MÉTODOS PRA CRIAR OU ATUALIZAR O CLIENTE NO CENARIO DELE JÁ EXISTIR.
      ========================================================= */

    public void verificarCliente(){
        if(clienteDAO.existeCpf(txtCpf.getText())){
            throw new IllegalArgumentException("Cliente com esse CPF já existe");
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
