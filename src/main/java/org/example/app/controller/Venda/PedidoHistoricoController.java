package org.example.app.controller.Venda;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.app.dao.PedidosDAO;
import org.example.app.model.Pedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PedidoHistoricoController {

    @FXML private TableView<Pedido> tblPedidos;
    @FXML private TableColumn<Pedido, Long> colId;
    @FXML private TableColumn<Pedido, String> colData;
    @FXML private TableColumn<Pedido, String> colCliente;
    @FXML private TableColumn<Pedido, String> colVendedor;
    @FXML private TableColumn<Pedido, Double> colTotal;
    @FXML private TableColumn<Pedido, String> colStatus;

    private final PedidosDAO pedidosDAO = new PedidosDAO();
    private final ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colData.setCellValueFactory(fila -> {
            Pedido p = fila.getValue();
            if (p.getDataPedido() != null && p.getHoraPedido() != null) {
                LocalDateTime dataHora = LocalDateTime.of(p.getDataPedido(), p.getHoraPedido());
                return new SimpleStringProperty(dataHora.format(formatter));
            }
            return new SimpleStringProperty("");
        });

        colCliente.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().getCliente() != null
                        ? fila.getValue().getCliente().getNome() : "N/A"));

        colVendedor.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().getVendedor() != null
                        ? fila.getValue().getVendedor().getNome() : "N/A"));

        colTotal.setCellValueFactory(fila ->
                new javafx.beans.property.SimpleObjectProperty<>(fila.getValue().getTotal()));

        colTotal.setCellFactory(tc -> new TableCell<Pedido, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.format("R$ %.2f", item));
                    setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #16a34a;");
                }
            }
        });

        colStatus.setCellValueFactory(fila -> {
            int status = fila.getValue().getStatus();
            String texto = status == 1 ? "PENDENTE"
                         : status == 3 ? "CONCLUÍDO"
                         : "CANCELADO";
            return new SimpleStringProperty(texto);
        });

        colStatus.setCellFactory(tc -> new TableCell<Pedido, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "PENDENTE" -> setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold;");
                        case "CONCLUÍDO" -> setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                        default -> setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void carregarDados() {
        listaPedidos.setAll(pedidosDAO.listarHistorico());
        tblPedidos.setItems(listaPedidos);
    }

    @FXML
    private void atualizarTela() {
        carregarDados();
    }

    @FXML
    private void visualizarItens() {
        Pedido selecionado = tblPedidos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            alerta("Selecione um pedido para visualizar os itens.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Pedido #").append(selecionado.getId()).append("\n\n");

        if (selecionado.getItens() == null || selecionado.getItens().isEmpty()) {
            sb.append("Sem itens registrados.");
        } else {
            selecionado.getItens().forEach(item -> sb
                    .append(item.getProduto() != null ? item.getProduto().getNome() : "?")
                    .append(" - Qtd: ").append(item.getQuantidade())
                    .append(" - Unit: R$ ").append(String.format("%.2f", item.getPrecoUnitario()))
                    .append(" - Total: R$ ").append(String.format("%.2f", item.getTotal()))
                    .append("\n"));
        }

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText(null);
        info.setContentText(sb.toString());
        info.showAndWait();
    }

    @FXML
    private void cancelarPedido() {
        Pedido selecionado = tblPedidos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            alerta("Selecione um pedido para cancelar.");
            return;
        }

        if (selecionado.getStatus() == 3) {
            alerta("Este pedido já foi concluído.");
            return;
        }

        pedidosDAO.cancelarPedido(selecionado.getId());
        carregarDados();
    }

    @FXML
    private void concluirPedido() {
        Pedido selecionado = tblPedidos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            alerta("Selecione um pedido para concluir.");
            return;
        }

        if (selecionado.getStatus() == 2) {
            alerta("Este pedido foi cancelado.");
            return;
        }

        pedidosDAO.marcarConcluido(selecionado.getId());
        carregarDados();
    }

    private void alerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

