package org.example.app.controller.Venda;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.example.app.util.BuscaModalController;
import org.example.app.dao.ClienteDAO;
import org.example.app.dao.VendaDAO;
import org.example.app.dao.VendedorDAO;
import org.example.app.model.Cliente;
import org.example.app.model.Venda;
import org.example.app.model.Vendedor;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VendaHistoricoController {

    @FXML private TableView<Venda> tblHistorico;
    @FXML private TableColumn<Venda, Long> colId;
    @FXML private TableColumn<Venda, String> colData;
    @FXML private TableColumn<Venda, String> colCliente;
    @FXML private TableColumn<Venda, String> colVendedor;
    @FXML private TableColumn<Venda, Double> colTotal;

    private final VendaDAO vendaDAO = new VendaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VendedorDAO vendedorDAO = new VendedorDAO();

    private final ObservableList<Venda> listaVendas =
            FXCollections.observableArrayList();

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
    }

    // =====================================================
    // CONFIGURAÇÃO DA TABELA
    // =====================================================

    private void configurarColunas() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colData.setCellValueFactory(fila -> {
            Venda v = fila.getValue();
            String dataHora = v.getDataVenda().format(formatter);
            return new SimpleStringProperty(dataHora);
        });

        colCliente.setCellValueFactory(fila ->
                new SimpleStringProperty(
                        fila.getValue().getCliente().getNome()
                )
        );

        colVendedor.setCellValueFactory(fila ->
                new SimpleStringProperty(
                        fila.getValue().getVendedor().getNome()
                )
        );

        colTotal.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );
    }

    private void carregarDados() {
        listaVendas.setAll(vendaDAO.listarHistorico());
        tblHistorico.setItems(listaVendas);
    }

    // =====================================================
    // AÇÕES
    // =====================================================

    @FXML
    private void editarCliente() {
        Venda selecionada = tblHistorico.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            abrirBuscaCliente(selecionada);
        }
    }

    @FXML
    private void editarVendedor() {
        Venda selecionada = tblHistorico.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            abrirBuscaVendedor(selecionada);
        }
    }

    // =====================================================
    // MODAIS DE BUSCA
    // =====================================================

    private void abrirBuscaCliente(Venda venda) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/BuscaModal.fxml")
            );
            Parent root = loader.load();

            BuscaModalController<Cliente> controller =
                    loader.getController();

            TableColumn<Cliente, String> colNome =
                    new TableColumn<>("Nome");
            colNome.setCellValueFactory(
                    new PropertyValueFactory<>("nome")
            );

            controller.configurar(
                    "ALTERAR CLIENTE DA VENDA #" + venda.getId(),
                    clienteDAO.listar(),
                    List.of(colNome),
                    cliente -> {
                        vendaDAO.atualizarCliente(
                                venda.getId(),
                                cliente.getId()
                        );
                        carregarDados();
                    }
            );

            abrirModal(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirBuscaVendedor(Venda venda) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/BuscaModal.fxml")
            );
            Parent root = loader.load();

            BuscaModalController<Vendedor> controller =
                    loader.getController();

            TableColumn<Vendedor, String> colNome =
                    new TableColumn<>("Nome");
            colNome.setCellValueFactory(
                    new PropertyValueFactory<>("nome")
            );

            controller.configurar(
                    "ALTERAR VENDEDOR DA VENDA #" + venda.getId(),
                    vendedorDAO.listar(),
                    List.of(colNome),
                    vendedor -> {
                        vendaDAO.atualizarVendedor(
                                venda.getId(),
                                vendedor.getId()
                        );
                        carregarDados();
                    }
            );

            abrirModal(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // UTIL
    // =====================================================

    private void abrirModal(Parent root) {
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    private TableView<Venda> tabelaVendas;


    @FXML
    private void cancelarVenda() {
        Venda selecionada = tabelaVendas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            alerta("Selecione uma venda para cancelar.");
            return;
        }

        vendaDAO.cancelarVenda(selecionada.getId()); // só muda status para 2
        carregarTabela(); // recarrega a lista de vendas
    }

    private void carregarTabela() {
        tabelaVendas.setItems(FXCollections.observableArrayList(vendaDAO.listarHistorico()));
    }

    private void alerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
