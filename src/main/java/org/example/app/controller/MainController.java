package org.example.app.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import static java.util.Map.entry;
import java.io.IOException;
import java.util.Map;


public class MainController {

    @FXML private VBox sideMenu;
    @FXML private StackPane contentArea;
    private boolean menuAberto = true;

    // Mapa de rotas: chave -> caminho completo do FXML
    private static final Map<String, String> rotas = Map.ofEntries(
            entry("clientes",    "/org/example/view/Cliente-Views/Cliente.fxml"),
            entry("clienteForm", "/org/example/view/Cliente-Views/ClienteForm.fxml"),
            entry("produtos",    "/org/example/view/Produto-Views/produto.fxml"),
            entry("usuarios",    "/org/example/view/usuario.fxml"),
            entry("vendas",      "/org/example/view/Venda-Views/venda.fxml"),
            entry("vendedores",  "/org/example/view/Vendedor/vendedor.fxml"),
            entry("vendedorForm","/org/example/view/Vendedor/VendedorForm.fxml"),
            entry("principal",   "/org/example/view/principal.fxml"),
            entry("login",       "/org/example/view/login.fxml"),
            entry("mainShell",   "/org/example/view/main_shell.fxml"),
            entry("historico",   "/org/example/view/Venda-Views/venda-historico.fxml")
    );


    // Função que faz o menu abrir e fechar (Animação)
    @FXML
    public void toggleMenu() {
        double larguraAlvo = menuAberto ? 60 : 220;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(sideMenu.prefWidthProperty(), larguraAlvo))
        );
        timeline.play();
        menuAberto = !menuAberto;
    }

    // Função principal que carrega os teus arquivos FXML no centro
    private void carregarPagina(String chave) {
        try {
            String caminho = rotas.get(chave);
            if (caminho == null) {
                System.err.println("Rota não encontrada: " + chave);
                return;
            }
            Parent page = FXMLLoader.load(getClass().getResource(caminho));
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            System.err.println("Erro ao carregar a página: " + chave);
            e.printStackTrace();
        }
    }

    // Estas são as funções que os botões do FXML chamam:
    @FXML public void showVendas() { carregarPagina("vendas"); }
    @FXML public void showProdutos() { carregarPagina("produtos"); }
    @FXML public void showClientes() { carregarPagina("clientes"); }
    @FXML public void showVendedores() { carregarPagina("vendedores"); }
    @FXML public void showUsuarios() { carregarPagina("usuarios"); }
    @FXML public void showHistoricoVendas() { carregarPagina("historico"); }
}
