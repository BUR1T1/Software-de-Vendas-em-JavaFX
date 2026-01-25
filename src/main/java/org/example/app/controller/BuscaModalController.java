package org.example.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

public class BuscaModalController<T> {

    @FXML private Label lblTitulo;
    @FXML private TextField txtFiltro;
    @FXML private TableView<T> tblResultados;

    private final ObservableList<T> masterData = FXCollections.observableArrayList();
    private FilteredList<T> filteredData;
    private Consumer<T> aoSelecionar;

    @FXML
    private void initialize() {

        tblResultados.setPlaceholder(
                new Label("Nenhum registro encontrado.")
        );

        tblResultados.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> filtrar());

        configurarAtalhos();
    }

    public void configurar(
            String titulo,
            List<T> itens,
            List<TableColumn<T, ?>> colunas,
            Consumer<T> callback
    ) {
        lblTitulo.setText(titulo);
        aoSelecionar = callback;

        masterData.setAll(itens);
        filteredData = new FilteredList<>(masterData, p -> true);

        tblResultados.getColumns().setAll(colunas);
        tblResultados.setItems(filteredData);

        if (!filteredData.isEmpty()) {
            tblResultados.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void filtrar() {

        if (filteredData == null) return;

        String texto = txtFiltro.getText();

        if (texto == null || texto.isBlank()) {
            filteredData.setPredicate(item -> true);
            return;
        }

        String filtro = texto.toLowerCase();

        filteredData.setPredicate(item ->
                item != null &&
                        item.toString() != null &&
                        item.toString().toLowerCase().contains(filtro)
        );
    }

    @FXML
    private void confirmar() {

        T selecionado = tblResultados.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null);
            alert.setContentText("Selecione um registro antes de confirmar.");
            alert.showAndWait();
            return;
        }

        aoSelecionar.accept(selecionado);
        fechar();
    }

    @FXML
    private void fechar() {
        Stage stage = (Stage) txtFiltro.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void verificarCliqueDuplo(MouseEvent event) {
        if (event.getClickCount() == 2) {
            confirmar();
        }
    }

    private void configurarAtalhos() {

        tblResultados.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) confirmar();
            if (event.getCode() == KeyCode.ESCAPE) fechar();
        });

        txtFiltro.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) confirmar();
        });
    }

    public static <T> TableColumn<T, String> criarColuna(
            String titulo,
            String propriedade,
            double largura
    ) {
        TableColumn<T, String> coluna = new TableColumn<>(titulo);
        coluna.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>(propriedade)
        );
        coluna.setPrefWidth(largura);
        return coluna;
    }
}
