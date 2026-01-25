package org.example.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.app.database.DatabaseInit;

public class MainApp extends Application {

    private static Stage primaryStage; // Referência estática para trocar de tela depois

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Inicializa o banco de dados
        DatabaseInit.inicializar();

        // Começa sempre pela tela de Login
        trocarTela("/org/example/view/login.fxml", "Sistema de Loja - Login");

        stage.show();
    }

    /**
     * Método utilitário para trocar a cena principal do sistema
     * @param fxml Caminho do arquivo FXML
     * @param titulo Título da janela
     */
    public static void trocarTela(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxml));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle(titulo);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}