module org.example.app {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // PERMITE que o FXMLLoader acesse os controllers
    opens org.example.app.controller to javafx.fxml;

    // Se o MainApp estiver nesse pacote
    opens org.example.app to javafx.fxml;

    // Exporta apenas o que for necessário
    exports org.example.app;
    opens org.example.app.controller.Cliente to javafx.fxml;
    opens org.example.app.controller.Produto to javafx.fxml;
    opens org.example.app.controller.Vendedor to javafx.fxml;
    opens org.example.app.controller.Venda to javafx.fxml;
}
