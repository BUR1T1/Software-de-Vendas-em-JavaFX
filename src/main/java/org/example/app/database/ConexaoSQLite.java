package org.example.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoSQLite {

    private static final String URL = "jdbc:sqlite:loja.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}

