package org.example.app.controller.Vendedor;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.app.model.Vendedor;

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
                alerta("Validação", "Informe o nome do vendedor.");
                return;
            }

            if (txtCpf.getText().isBlank()) {
                alerta("Validação", "Informe o CPF.");
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
            alerta("Erro", "Comissão inválida.");
        }
    }

    private void alerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }


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