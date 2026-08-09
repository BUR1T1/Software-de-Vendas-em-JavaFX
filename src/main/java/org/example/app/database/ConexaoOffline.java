package org.example.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConexÃƒÆ’Ã‚Â£o com o banco SQLite local de modo OFFLINE.
 * Este arquivo (loja.db) ÃƒÆ’Ã‚Â© usado apenas quando o sistema
 * nÃƒÆ’Ã‚Â£o consegue acessar o banco principal (Postgres/online).
 *
 * Nele ficam armazenados:
 *  - CatÃƒÆ’Ã‚Â¡logo cache (clientes, vendedores, produtos) p/ venda offline
 *  - Vendas realizadas offline (aguardando sincronizaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o)
 *  - Clientes criados offline (aguardando sincronizaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o)
 */
public class ConexaoOffline {

    private static final String URL = "jdbc:sqlite:loja.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
