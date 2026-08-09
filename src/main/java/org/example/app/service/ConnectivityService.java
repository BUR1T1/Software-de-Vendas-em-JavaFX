package org.example.app.service;

import org.example.app.database.ConnectionManager;

/**
 * Detecta se o banco principal (Postgres / online) estÃƒÆ’Ã‚Â¡ acessÃƒÆ’Ã‚Â­vel.
 * O sistema usa essa verificaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o para decidir entre modo ONLINE e OFFLINE.
 */
public class ConnectivityService {

    /**
     * Tenta abrir uma conexÃƒÆ’Ã‚Â£o com o Postgres.
     * @return true se conectado (online), false caso contrÃƒÆ’Ã‚Â¡rio (offline).
     */
    public static boolean estaOnline() {
        return ConnectionManager.estaOnline();
    }

    /**
     * Retorna uma descriÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o legÃƒÆ’Ã‚Â­vel do estado da conexÃƒÆ’Ã‚Â£o.
     */
    public static String estadoConexao() {
        return estaOnline() ? "ONLINE" : "OFFLINE";
    }
}
