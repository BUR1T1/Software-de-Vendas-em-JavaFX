module org.example.app {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.rabbitmq.client;
    requires com.google.gson;

    opens org.example.app.consumer to com.google.gson, javafx.fxml;
    opens org.example.app.model to javafx.base;

    exports org.example.app;

// PERMITE que o FXMLLoader acesse os controllers
    opens org.example.app.controller to javafx.fxml;

    // Se o MainApp estiver nesse pacote
    opens org.example.app to javafx.fxml;

    // Exporta apenas o que for necessÃƒÆ’Ã‚Â¡rio
    opens org.example.app.controller.cliente to javafx.fxml;
    opens org.example.app.controller.produto to javafx.fxml;
    opens org.example.app.controller.vendedor to javafx.fxml;
    opens org.example.app.controller.venda to javafx.fxml;
    opens org.example.app.util to javafx.fxml;
    opens org.example.app.controller.login to javafx.fxml;
    opens org.example.app.controller.usuario to javafx.fxml;
    opens org.example.app.service to javafx.fxml;
    exports org.example.app.model;
}
