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
        this.produto = produto;

        if (produto != null) {
            txtNome.setText(produto.getNome());
            txtPreco.setText(String.valueOf(produto.getPreco()));
            txtEstoque.setText(String.valueOf(produto.getEstoque()));
        }
    }



    public Produto getProduto() {
        return produto;
    }

    public boolean isSalvo() {
        return salvo;
    }

    @FXML
    private void salvar() {
        try {
            String nome = txtNome.getText();
            double preco = Double.parseDouble(txtPreco.getText());
            int estoque = Integer.parseInt(txtEstoque.getText());

            if (nome == null || nome.isBlank()) {
                lblMensagem.setText("Nome obrigatório.");
                return;
            }

            if (produto == null) {
                produto = new Produto(nome, preco, estoque);
                produto.setStatus(1); // ativo
            } else {
                produto.setNome(nome);
                produto.setPreco(preco);
                produto.setEstoque(estoque);
            }

            salvo = true;
            fechar();

        } catch (NumberFormatException e) {
            lblMensagem.setText("Preço ou estoque inválido.");
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
}
