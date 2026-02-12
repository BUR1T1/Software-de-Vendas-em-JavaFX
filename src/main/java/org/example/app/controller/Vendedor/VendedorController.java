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
import java.util.List;
import javafx.scene.control.Alert;
import org.example.app.util.Alerta;

public class VendedorController {

    @FXML private TableView<Vendedor> tabelaVendedores;
    @FXML private TableColumn<Vendedor, Long> colId;
    @FXML private TableColumn<Vendedor, String> colNome;
    @FXML private TableColumn<Vendedor, String> colCpf;
    @FXML private TableColumn<Vendedor, Double> colComissao;

    @FXML private TableView<Vendedor> tabelaInativos;
    @FXML private TableColumn<Vendedor, Long> colIdInativo;
    @FXML private TableColumn<Vendedor, String> colNomeInativo;
    @FXML private TableColumn<Vendedor, String> colCpfInativo;
    @FXML private TableColumn<Vendedor, Double> colComissaoInativo;

    private final VendedorDAO vendedorDAO = new VendedorDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colComissao.setCellValueFactory(new PropertyValueFactory<>("comissao"));

        colIdInativo.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNomeInativo.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpfInativo.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colComissaoInativo.setCellValueFactory(new PropertyValueFactory<>("comissao"));

        tabelaInativos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        carregarTabelas();
    }

    private void carregarTabelas() {
        tabelaVendedores.setItems(
                FXCollections.observableArrayList(vendedorDAO.listarAtivos())
        );
        tabelaInativos.setItems(
                FXCollections.observableArrayList(vendedorDAO.listarInativos())
        );
    }

    @FXML
    private void abrirNovo() {
        exibirFormulario(null);
    }

    @FXML
    private void abrirEdicao() {
        Vendedor selecionado = tabelaVendedores.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            exibirFormulario(selecionado);
        } else {
            Alerta.info("Seleção", "Selecione um vendedor para editar.");
        }
    }

    private void exibirFormulario(Vendedor v) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/Vendedor/VendedorForm.fxml")
            );
            Parent root = loader.load();

            VendedorFormController formCtrl = loader.getController();
            formCtrl.setVendedor(v);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (formCtrl.isSalvo()) {
                vendedorDAO.salvar(formCtrl.getVendedor());
                carregarTabelas();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void inativarVendedor() {
        Vendedor v = tabelaVendedores.getSelectionModel().getSelectedItem();

        if (v != null) {
            vendedorDAO.inativar(v.getId());
            carregarTabelas();
        } else {
            Alerta.info("Seleção", "Selecione um vendedor para inativar.");
        }
    }

    @FXML
    private void reativar() {
        List<Long> ids = tabelaInativos.getSelectionModel()
                .getSelectedItems()
                .stream()
                .map(Vendedor::getId)
                .toList();

        if (!ids.isEmpty()) {
            vendedorDAO.reativar(ids);
            carregarTabelas();
        } else {
            Alerta.info("Seleção", "Selecione ao menos um vendedor para reativar.");
        }
    }


}


