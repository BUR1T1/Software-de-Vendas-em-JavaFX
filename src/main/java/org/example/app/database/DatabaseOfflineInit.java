package org.example.app.database;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Inicializa as tabelas do banco OFFLINE (offline.db).
 *
 * Estrutura: - offline_catalogo_cliente -> cache de clientes -
 * offline_catalogo_vendedor -> cache de vendedores - offline_catalogo_produto
 * -> cache de produtos - offline_venda -> vendas pendentes (aguardando sync) -
 * offline_item_venda -> itens das vendas pendentes - sync_controle -> metadados
 * de sincronizaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o
 */
public class DatabaseOfflineInit {

    public static void inicializar() {

        String sqlCatalogoCliente
                = "CREATE TABLE IF NOT EXISTS offline_catalogo_cliente ("
                + " id INTEGER PRIMARY KEY,"
                + " nome TEXT NOT NULL,"
                + " cpf TEXT NOT NULL,"
                + " telefone TEXT,"
                + " status INTEGER DEFAULT 1"
                + ");";

        String sqlCatalogoVendedor
                = "CREATE TABLE IF NOT EXISTS offline_catalogo_vendedor ("
                + " id INTEGER PRIMARY KEY,"
                + " nome TEXT NOT NULL,"
                + " cpf TEXT NOT NULL,"
                + " comissao REAL DEFAULT 0,"
                + " status INTEGER DEFAULT 1"
                + ");";

        String sqlCatalogoProduto
                = "CREATE TABLE IF NOT EXISTS offline_catalogo_produto ("
                + " id INTEGER PRIMARY KEY,"
                + " nome TEXT NOT NULL,"
                + " preco REAL NOT NULL,"
                + " estoque INTEGER DEFAULT 0,"
                + " status INTEGER DEFAULT 1"
                + ");";

        String sqlVenda
                = "CREATE TABLE IF NOT EXISTS offline_venda ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " cliente_id INTEGER NOT NULL,"
                + " vendedor_id INTEGER NOT NULL,"
                + " total REAL NOT NULL,"
                + " forma_pagamento TEXT NOT NULL,"
                + " parcelas INTEGER DEFAULT 1,"
                + " valor_parcela REAL DEFAULT 0,"
                + " data_venda TEXT NOT NULL,"
                + " hora_venda TEXT NOT NULL,"
                + " created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + " sincronizado INTEGER DEFAULT 0,"
                + " FOREIGN KEY (cliente_id) REFERENCES offline_catalogo_cliente(id),"
                + " FOREIGN KEY (vendedor_id) REFERENCES offline_catalogo_vendedor(id)"
                + ");";

        String sqlItemVenda
                = "CREATE TABLE IF NOT EXISTS offline_item_venda ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " venda_id INTEGER NOT NULL,"
                + " produto_id INTEGER NOT NULL,"
                + " quantidade INTEGER NOT NULL,"
                + " preco_unitario REAL NOT NULL,"
                + " FOREIGN KEY (venda_id) REFERENCES offline_venda(id),"
                + " FOREIGN KEY (produto_id) REFERENCES offline_catalogo_produto(id)"
                + ");";

        String sqlControle
                = "CREATE TABLE IF NOT EXISTS sync_controle ("
                + " chave TEXT PRIMARY KEY,"
                + " valor TEXT"
                + ");";

        try (Connection conn = ConexaoOffline.conectar(); Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute(sqlCatalogoCliente);
            stmt.execute(sqlCatalogoVendedor);
            stmt.execute(sqlCatalogoProduto);
            stmt.execute(sqlVenda);
            stmt.execute(sqlItemVenda);
            stmt.execute(sqlControle);

            System.out.println("Banco offline inicializado com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco offline:");
            e.printStackTrace();
        }
    }
}
