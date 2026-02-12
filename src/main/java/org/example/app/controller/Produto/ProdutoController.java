package org.example.app.controller.Produto;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.app.dao.ProdutoDAO;
import org.example.app.model.Produto;
import org.example.app.util.Alerta;

import javax.xml.transform.Source;

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
    public void abrirNovo() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/Produto-Views/ProdutoForm.fxml")
            );

            Parent root = loader.load();

            ProdutoFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Novo Produto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            if (controller.isSalvo()) {
                carregarTabela();
                carregarTabelaInativos();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alerta.error("Erro", "Não foi possível abrir o formulário.");
        }
    }


    @FXML
    public void abrirEdicao(){
        Produto p = tabelaProdutos.getSelectionModel().getSelectedItem();

        if (p == null){
            Alerta.info("Selecione um produto", "selecrione um item para poder editar");
        return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/Produto-Views/ProdutoForm.fxml")
            );

            Parent root =loader.load();
            ProdutoFormController controller = loader.getController();
            controller.setProduto(p);

            Stage stage = new Stage();
            stage.setTitle("Editar Produto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            carregarTabela();
        }catch (Exception e){
            e.printStackTrace();
            Alerta.error("Erro", "Não foi possível abrir o formulário.");
        }
    }



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
            Alerta.info("Selecione um produto", "Selecione um produto para inativar");
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
            Alerta.info("Selecione um produto", "Selecione um produto para reativar");
            return;
        }
        produtoDAO.alterarStatus(p.getId(), 1);
        carregarTabela();
        carregarTabelaInativos();
    }

}
