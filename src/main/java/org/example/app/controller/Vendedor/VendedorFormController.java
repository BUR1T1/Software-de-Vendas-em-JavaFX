package org.example.app.controller.Vendedor;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.app.model.Vendedor;

public class VendedorFormController {
    @FXML private Label lblTitulo;
    @FXML private TextField txtNome;
    @FXML private TextField txtComissao;

    private Vendedor vendedor;
    private boolean salvo = false;

    public void setVendedor(Vendedor v) {
        this.vendedor = (v != null) ? v : new Vendedor();
        if (v != null) {
            lblTitulo.setText("EDITAR VENDEDOR");
            txtNome.setText(v.getNome());
            txtComissao.setText(String.valueOf(v.getComissao()));
        }
        txtNome.requestFocus();
    }

    @FXML
    private void salvar() {
        try {
            vendedor.setNome(txtNome.getText());
            vendedor.setComissao(Double.parseDouble(txtComissao.getText().replace(",", ".")));
            this.salvo = true;
            fechar();
        } catch (Exception e) {
            // Alerta de erro de número
        }
    }

    @FXML private void cancelar() { fechar(); }
    private void fechar() { ((Stage) txtNome.getScene().getWindow()).close(); }

    public boolean isSalvo() { return salvo; }
    public Vendedor getVendedor() { return vendedor; }
}