package org.example.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.app.consumer.PedidoConsumer;
import org.example.app.database.DatabaseInit;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        DatabaseInit.inicializar();
        // Consumidor da fila de pedidos (RabbitMQ) iniciado automaticamente
        new Thread(PedidoConsumer::iniciar).start();
        trocarTela("/org/example/view/login.fxml", "Sistema de Loja - Login");

        stage.show();
    }

    /**
     * Inicia o consumidor da fila de pedidos (RabbitMQ).
     */
    public static void iniciarConsumer() {
        new Thread(PedidoConsumer::iniciar).start();
    }

    /**
     * Método utilitário para trocar a cena principal do sistema
     * @param fxml Caminho do arquivo FXML
     * @param titulo
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