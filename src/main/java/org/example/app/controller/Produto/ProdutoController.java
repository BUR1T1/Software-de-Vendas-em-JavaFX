package org.example.app.controller.Produto;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.app.dao.ProdutoDAO;
import org.example.app.model.Produto;

public class ProdutoController {

    @FXML
    private TableView<Produto> tabelaProdutos;
    @FXML private TableView<Produto> tabelaInativos;

    @FXML private TableColumn<Produto, Long> colId;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colEstoque;

    @FXML private TableColumn<Produto, Long> colInativoId;
    @FXML private TableColumn<Produto, String> colInativoNome;
    @FXML private TableColumn<Produto, Double> colInativoPreco;
    @FXML private TableColumn<Produto, Integer> colInativoEstoque;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @FXML
    public void initialize() {

        // ATIVOS
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));

        // INATIVOS
        colInativoId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colInativoNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colInativoPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colInativoEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));

        carregarTabela();
        carregarTabelaInativos();
    }

    private void carregarTabela() {
        tabelaProdutos.setItems(
                FXCollections.observableArrayList(produtoDAO.listar())
        );
    }

    private void carregarTabelaInativos() {
        tabelaInativos.setItems(
                FXCollections.observableArrayList(produtoDAO.listarInativos())
        );
    }

    @FXML
    private void inativar() {
        Produto p = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (p == null) {
            alerta("Selecione um produto");
            return;
        }
        produtoDAO.alterarStatus(p.getId(), 2);
        carregarTabela();
        carregarTabelaInativos();
    }

    @FXML
    private void reativar() {
        Produto p = tabelaInativos.getSelectionModel().getSelectedItem();
        if (p == null) {
            alerta("Selecione um produto");
            return;
        }
        produtoDAO.alterarStatus(p.getId(), 1);
        carregarTabela();
        carregarTabelaInativos();
    }

    private void alerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
