package org.example.app.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInit {

    public static void inicializar() {

        // =========================
        // TABELA USUÁRIO
        // =========================
        String sqlUsuario = """
            CREATE TABLE IF NOT EXISTS usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                login TEXT NOT NULL UNIQUE,
                senha TEXT NOT NULL,
                perfil TEXT NOT NULL CHECK (perfil IN ('ADMIN','VENDEDOR'))
            );
        """;

        // =========================
        // TABELA VENDEDOR
        // =========================
        String sqlVendedor = """
            CREATE TABLE IF NOT EXISTS vendedor (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                cpf TEXT NOT NULL UNIQUE,
                status INTEGER NOT NULL DEFAULT 1,
                comissao REAL NOT NULL CHECK (comissao >= 0),
                FOREIGN KEY (usuario_id) REFERENCES usuario(id)
            );
        """;

        // =========================
        // TABELA CLIENTE
        // =========================
        String sqlCliente = """
            CREATE TABLE IF NOT EXISTS cliente (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cpf TEXT NOT NULL UNIQUE,
                nome TEXT,
                telefone TEXT,
                created_at TIMESTAMP NOT NULL,
                updated_at TIMESTAMP NOT NULL
            );
        """;

        // =========================
        // TABELA PRODUTO
        // =========================
        String sqlProduto = """
            CREATE TABLE IF NOT EXISTS produto (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                preco REAL NOT NULL CHECK (preco >= 0),
                estoque INTEGER NOT NULL CHECK (estoque >= 0)
            );
        """;

        // =========================
        // TABELA VENDA
        // =========================
        String sqlVenda = """
            CREATE TABLE IF NOT EXISTS venda (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_id INTEGER NOT NULL,
                vendedor_id INTEGER NOT NULL,
                total REAL NOT NULL CHECK (total >= 0),
                data_venda TEXT NOT NULL,
                hora_venda TEXT NOT NULL,
                data_hora_insercao DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (cliente_id) REFERENCES cliente(id),
                FOREIGN KEY (vendedor_id) REFERENCES vendedor(id)
            );
        """;

        // =========================
        // TABELA ITEM_VENDA
        // =========================
        String sqlItemVenda = """
            CREATE TABLE IF NOT EXISTS item_venda (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                venda_id INTEGER NOT NULL,
                produto_id INTEGER NOT NULL,
                quantidade INTEGER NOT NULL,
                preco_unitario REAL NOT NULL,
                FOREIGN KEY (venda_id) REFERENCES venda(id),
                FOREIGN KEY (produto_id) REFERENCES produto(id)
            );
        """;

        // =========================
        // EXECUÇÃO DAS TABELAS
        // =========================
        try (Connection conn = ConexaoSQLite.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute(sqlUsuario);
            stmt.execute(sqlVendedor);
            stmt.execute(sqlCliente);
            stmt.execute(sqlProduto);
            stmt.execute(sqlVenda);
            stmt.execute(sqlItemVenda);

            System.out.println("Banco de dados inicializado com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco de dados:");
            e.printStackTrace();
        }
    }
}
