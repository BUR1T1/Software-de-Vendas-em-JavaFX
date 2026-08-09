package org.example.app.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Compatibilidade temporÃƒÆ’Ã‚Â¡ria para manter chamadas antigas funcionando.
 * O projeto agora usa apenas dois caminhos: PostgreSQL local/host e SQLite offline.
 */
public class ConexaoSQLite {

    private ConexaoSQLite() {
    }

    public static Connection conectar() throws SQLException {
        return ConexaoOffline.conectar();
    }
}

