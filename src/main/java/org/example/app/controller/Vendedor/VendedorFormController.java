package org.example.app.controller.vendedor;

import org.example.app.model.Vendedor;
import org.example.app.util.Alerta;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class VendedorFormController {
    @FXML private Label lblTitulo;
    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtComissao;

    private Vendedor vendedor;
    private boolean salvo = false;

    public void setVendedor(Vendedor v) {
        this.vendedor = (v != null) ? v : new Vendedor();
        if (v != null) {
            lblTitulo.setText("EDITAR VENDEDOR");
            txtNome.setText(v.getNome());
            txtCpf.setText(v.getCpf());
            txtComissao.setText(String.valueOf(v.getComissao()));
        }
        txtNome.requestFocus();
    }

    @FXML
    private void salvar() {
        try {
            if (txtNome.getText().isBlank()) {
                Alerta.info("ValidaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o", "Informe o nome do vendedor.");
                return;
            }

            if (txtCpf.getText().isBlank()) {
                Alerta.info("ValidaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o", "Informe o CPF.");
                return;
            }

            vendedor.setNome(txtNome.getText());
            vendedor.setCpf(txtCpf.getText().replaceAll("\\D", ""));
            vendedor.setComissao(
                    Double.parseDouble(txtComissao.getText().replace(",", "."))
            );

            this.salvo = true;
            fechar();

        } catch (NumberFormatException e) {
            Alerta.error("Erro", "ComissÃƒÆ’Ã‚Â£o invÃƒÆ’Ã‚Â¡lida.");
        }
    }

      /* =========================================================
       MÃƒÆ’Ã¢â‚¬Â°TODOS PRA VALIDAR CAMPOS OBRIGATORIO;
       ========================================================= */

       /* =========================================================
       MÃƒÆ’Ã¢â‚¬Â°TODOS PRA ATUALIZAR DADOS DO VENDEDOR
       ========================================================= */

    @FXML public void initialize() {
        txtCpf.setTextFormatter(new TextFormatter<>(change -> {
            String novoTexto = change.getControlNewText();
            if (novoTexto.matches("\\d{0,11}")) {
                return change;
            } return null;
        }));
    }

    @FXML private void cancelar() { fechar(); }
    private void fechar() { ((Stage) txtNome.getScene().getWindow()).close(); }

    public boolean isSalvo() { return salvo; }
    public Vendedor getVendedor() { return vendedor; }
}