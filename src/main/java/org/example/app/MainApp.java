package org.example.app;

import org.example.app.consumer.PedidoConsumer;
import org.example.app.consumer.RabbitMQConnection;
import org.example.app.database.ConnectionManager;
import org.example.app.database.DatabaseInit;
import org.example.app.database.DatabaseOfflineInit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Define o ÃƒÆ’Ã‚Â­cone da loja na janela e na barra de tarefas
        try {
            stage.getIcons().add(new Image(
                    MainApp.class.getResourceAsStream("/org/example/view/img/logo.png")));
        } catch (Exception e) {
            System.err.println("Não foi possivel carregar " + e.getMessage());
        }

        DatabaseOfflineInit.inicializar();

        if (ConnectionManager.estaOnline()) {
            DatabaseInit.inicializar();
        } else {
            System.out.println("Aplicação foi iniciada em modo offline. Usando apenas o schema de contingÃƒÆ’Ã‚Âªncia SQLite.");
        }

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
     * MÃƒÆ’Ã‚Â©todo utilitÃƒÆ’Ã‚Â¡rio para trocar a cena principal do sistema
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

@Override
    public void stop() throws Exception {
        // Encerra a conexão com o RabbitMQ ao fechar a aplicação,
        // evitando o acúmulo de consumers/filas órfãs no servidor.
        RabbitMQConnection.fechar();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
