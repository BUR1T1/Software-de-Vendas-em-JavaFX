package org.example.app.controller.Produto;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.app.dao.ProdutoDAO;
import org.example.app.model.Produto;
import org.example.app.util.Alerta;

public class ProdutoFormController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtPreco;

    @FXML
    private TextField txtEstoque;

    @FXML
    private Label lblMensagem;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private Produto produto;
    private boolean salvo = false;

    /* =========================================================
       GETTERS E SETTERS
       ========================================================= */

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

    /* =========================================================
       VALIDAÇÕES
       ========================================================= */

    public void validarNome(String nome) {
        if (produtoDAO.buscarNome(nome)) {
            Alerta.warning(
                    "Nome já utilizado.",
                    "Adicione alguma especificação ao título."
            );
            throw new IllegalArgumentException("Valor inesperado");
        }

        if (nome == null || nome.isBlank()) {
            Alerta.warning(
                    "Aviso",
                    "Nome do produto obrigatório."
            );
            throw new IllegalArgumentException("Valor inesperado");
        }
    }

    public void valorPositivo(Double valor) {
        if (valor <= 0) {
            Alerta.info(
                    "Valor imprevisto",
                    "O valor precisa ser superior a zero."
            );
            throw new IllegalArgumentException("Valor imprevisto");
        }
    }

    public void quantidadeDeEstoquePositio(Integer quantidade) {
        if (quantidade <= 0) {
            Alerta.warning(
                    "Valor imprevisto",
                    "O estoque não pode ser menor ou igual a zero."
            );
            throw new IllegalArgumentException("Valor imprevisto");
        }
    }

    /* =========================================================
       MONTAGEM DO PRODUTO
       ========================================================= */

    public Produto montarProduto() {
        String nome = txtNome.getText();
        double preco = Double.parseDouble(txtPreco.getText());
        int estoque = Integer.parseInt(txtEstoque.getText());

        validarNome(nome);
        valorPositivo(preco);
        quantidadeDeEstoquePositio(estoque);

        return new Produto(nome, preco, estoque);
    }

    /* =========================================================
       EVENTOS DA TELA
       ========================================================= */

    @FXML
    private void salvar() {
        try {
            produto = montarProduto();
            produtoDAO.salvar(produto);

            salvo = true;
            fechar();
        } catch (Exception e) {
            Alerta.error("Erro", "Erro ao salvar produto.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    /* =========================================================
       MÉTODOS AUXILIARES
       ========================================================= */

    private void fechar() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }
}