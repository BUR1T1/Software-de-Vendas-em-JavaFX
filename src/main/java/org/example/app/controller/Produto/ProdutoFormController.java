package org.example.app.controller.Produto;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.app.model.Produto;

public class ProdutoFormController {

    @FXML private TextField txtNome;
    @FXML private TextField txtPreco;
    @FXML private TextField txtEstoque;
    @FXML private Label lblMensagem;

    private Produto produto;
    private boolean salvo = false;

    public void setProduto(Produto produto) {
        if (produto != null) {
            this.produto = produto;
            txtNome.setText(produto.getNome());
            txtPreco.setText(String.valueOf(produto.getPreco()));
            txtEstoque.setText(String.valueOf(produto.getEstoque()));
        }
    }

    @FXML
    private void salvar() {
        try {
            String nome = txtNome.getText();
            double preco = Double.parseDouble(txtPreco.getText());
            int estoque = Integer.parseInt(txtEstoque.getText());

            if (nome.isBlank()) {
                lblMensagem.setText("Nome obrigatório.");
                return;
            }

            if (produto == null) {
                produto = new Produto(nome, preco, estoque);
            } else {
                produto.setNome(nome);
                produto.setPreco(preco);
                produto.setEstoque(estoque);
            }

            salvo = true;
            fechar();

        } catch (NumberFormatException e) {
            lblMensagem.setText("Valores inválidos.");
        }
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    public boolean isSalvo() {
        return salvo;
    }

    public Produto getProduto() {
        return produto;
    }
}
