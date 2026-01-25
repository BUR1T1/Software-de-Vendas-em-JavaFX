package org.example.app.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class PrincipalController {

    @FXML
    private StackPane contentArea;

    private void carregarTela(String nomeFXML) {
        try {
            String caminho = "/org/example/view/" + nomeFXML + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminho));
            Parent tela = loader.load();

            contentArea.getChildren().setAll(tela);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirVendas() {
        carregarTela("venda");
    }

    @FXML
    private void abrirProdutos() {
        carregarTela("produto");
    }

    @FXML
    private void abrirClientes() {
        carregarTela("cliente");
    }
}
