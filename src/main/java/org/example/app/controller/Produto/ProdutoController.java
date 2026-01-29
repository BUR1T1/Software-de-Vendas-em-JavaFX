package org.example.app.controller.Produto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.app.dao.ProdutoDAO;
import org.example.app.model.Produto;

import java.io.IOException;

public class ProdutoController {

    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, Integer> colId;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colEstoque;
    @FXML private TextField txtPesquisar; // Campo de busca da tabela

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @FXML
    public void initialize() {
        // Configura as colunas da tabela
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));

        carregarTabela();
    }

    // Abre o formulário vazio para um NOVO produto
    @FXML
    private void abrirNovo() {
        exibirFormulario(null);
    }

    // Abre o formulário preenchido para EDITAR um produto
    @FXML
    private void abrirEdicao() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            alerta("Selecione um produto para editar.");
            return;
        }
        exibirFormulario(selecionado);
    }

    @FXML
    private void inativarProduto() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            alerta("Selecione um produto para inativar.");
            return;
        }
        produtoDAO.alterarStatus(selecionado.getId(), 2); // 2 = inativo
        carregarTabela();
    }

    private void exibirFormulario(Produto produto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/Produto-Views/ProdutoForm.fxml"));
            Parent root = loader.load();

            // Pega o controller do formulário
            ProdutoFormController formController = loader.getController();
            formController.setProduto(produto); // Passa o produto (ou null)

            // Cria e exibe a janela modal
            Stage stage = new Stage();
            stage.setTitle(produto == null ? "Novo Produto" : "Editar Produto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Se o usuário salvou com sucesso, atualiza a tabela
            if (formController.isSalvo()) {
                if (produto == null) {
                    produtoDAO.salvar(formController.getProduto());
                } else {
                    produtoDAO.atualizar(formController.getProduto());
                }
                carregarTabela();
            }

        } catch (IOException e) {
            e.printStackTrace();
            alerta("Erro ao abrir a tela de cadastro.");
        }
    }

    private void carregarTabela() {
        ObservableList<Produto> lista = FXCollections.observableArrayList(produtoDAO.listar());
        tabelaProdutos.setItems(lista);
    }

    private void alerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}