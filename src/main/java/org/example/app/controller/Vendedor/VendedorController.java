package org.example.app.controller.Vendedor;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.app.dao.VendedorDAO;
import org.example.app.model.Vendedor;
import java.io.IOException;

public class VendedorController {

    @FXML private TableView<Vendedor> tabelaVendedores;
    @FXML private TableColumn<Vendedor, Integer> colId;
    @FXML private TableColumn<Vendedor, String> colNome;
    @FXML private TableColumn<Vendedor, Double> colComissao;

    private final VendedorDAO vendedorDAO = new VendedorDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colComissao.setCellValueFactory(new PropertyValueFactory<>("comissao"));
        carregarTabela();
    }

    @FXML
    private void abrirNovo() { exibirFormulario(null); }

    @FXML
    private void abrirEdicao() {
        Vendedor selecionado = tabelaVendedores.getSelectionModel().getSelectedItem();
        if (selecionado != null) exibirFormulario(selecionado);
    }

    private void exibirFormulario(Vendedor v) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/Vendedor/VendedorForm.fxml"));
            Parent root = loader.load();

            VendedorFormController formCtrl = loader.getController();
            formCtrl.setVendedor(v);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (formCtrl.isSalvo()) {
                if (v == null) vendedorDAO.salvar(formCtrl.getVendedor());
                else vendedorDAO.atualizar(formCtrl.getVendedor());
                carregarTabela();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void carregarTabela() {
        tabelaVendedores.setItems(FXCollections.observableArrayList(vendedorDAO.listar()));
    }

    @FXML
    private void excluir() {
        Vendedor selecionado = tabelaVendedores.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            vendedorDAO.excluir(selecionado.getId());
            carregarTabela();
        }
    }
}