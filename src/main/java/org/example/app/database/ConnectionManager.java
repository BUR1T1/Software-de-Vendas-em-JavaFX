package org.example.app.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gerenciador GLOBAL de conexÃƒÆ’Ã‚Â£o (Online/Offline).
 *
 * ÃƒÆ’Ã…Â¡nico responsÃƒÆ’Ã‚Â¡vel por testar a conectividade com a internet e decidir
 * qual banco utilizar em cada operaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o:
 *
 *  - ONLINE  -> retorna uma conexÃƒÆ’Ã‚Â£o com o banco PRINCIPAL (PostgreSQL / localhost).
 *  - OFFLINE -> intercepta a falha e retorna uma conexÃƒÆ’Ã‚Â£o com o banco de
 *               CONTINGÃƒÆ’Ã…Â NCIA local (loja.db / SQLite).
 *
 * Os DAOs consomem este gerenciador de forma TRANSPARENTE, ou seja, executam
 * as mesmas instruÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Âµes SQL independentemente de qual banco estÃƒÆ’Ã‚Â¡ ativo.
 */
public class ConnectionManager {

    private ConnectionManager() {
        // Classe utilitÃƒÆ’Ã‚Â¡ria - nÃƒÆ’Ã‚Â£o deve ser instanciada.
    }

    /**
     * Retorna a conexÃƒÆ’Ã‚Â£o ativa de acordo com o estado da rede.
     *
     * @return conexÃƒÆ’Ã‚Â£o com o PostgreSQL local/host (online) ou com o SQLite offline.
     * @throws SQLException se NENHUM banco estiver acessÃƒÆ’Ã‚Â­vel.
     */
    public static Connection getConnection() throws SQLException {
        if (estaOnline()) {
            return ConexaoPostgres.conectar();
        }
        return ConexaoOffline.conectar();
    }

    /**
     * Testa a conectividade com o banco PRINCIPAL (PostgreSQL em localhost).
     *
     * @return true se o Postgres local estiver acessÃƒÆ’Ã‚Â­vel; false caso contrÃƒÆ’Ã‚Â¡rio.
     */
    public static boolean estaOnline() {
        try (Connection conn = ConexaoPostgres.conectar()) {
            return conn != null && conn.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean estaOffline() {
        return !estaOnline();
    }
}

