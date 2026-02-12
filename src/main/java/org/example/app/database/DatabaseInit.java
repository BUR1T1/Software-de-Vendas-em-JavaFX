package org.example.app.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInit {

    public static void inicializar() {

        // =========================
        // TABELA USUARIO
        // =========================
        String sqlUsuario = """
            CREATE TABLE IF NOT EXISTS usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                login TEXT NOT NULL UNIQUE,
                senha TEXT NOT NULL,
                perfil TEXT NOT NULL CHECK (perfil IN ('ADMIN','VENDEDOR')),
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status INTEGER NOT NULL DEFAULT 1
            );
        """;

        // =========================
        // TABELA VENDEDOR
        // =========================
        String sqlVendedor = """
            CREATE TABLE IF NOT EXISTS vendedor (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                cpf TEXT NOT NULL UNIQUE,
                comissao REAL NOT NULL CHECK (comissao >= 0),
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status INTEGER NOT NULL DEFAULT 1
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
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status INTEGER NOT NULL DEFAULT 1
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
                estoque INTEGER NOT NULL CHECK (estoque >= 0),
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status INTEGER NOT NULL DEFAULT 1
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

                forma_pagamento TEXT NOT NULL,
                parcelas INTEGER DEFAULT 1,
                valor_parcela REAL DEFAULT 0,

                data_venda TEXT NOT NULL,
                hora_venda TEXT NOT NULL,

                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status INTEGER NOT NULL DEFAULT 1,

                FOREIGN KEY (cliente_id) REFERENCES cliente(id),
                FOREIGN KEY (vendedor_id) REFERENCES vendedor(id)
            );
        """;

        // =========================
        // TABELA ITEM VENDA
        // =========================
        String sqlItemVenda = """
            CREATE TABLE IF NOT EXISTS item_venda (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                venda_id INTEGER NOT NULL,
                produto_id INTEGER NOT NULL,
                quantidade INTEGER NOT NULL,
                preco_unitario REAL NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (venda_id) REFERENCES venda(id),
                FOREIGN KEY (produto_id) REFERENCES produto(id)
            );
        """;

        // =========================
        // EXECUÇÃO
        // =========================
        try (Connection conn = ConexaoSQLite.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

            // Criação principal
            stmt.execute(sqlUsuario);
            stmt.execute(sqlVendedor);
            stmt.execute(sqlCliente);
            stmt.execute(sqlProduto);
            stmt.execute(sqlVenda);
            stmt.execute(sqlItemVenda);

            // =========================
            // ATUALIZAÇÃO DE SCHEMA (BANCO ANTIGO)
            // =========================

            // venda
            try { stmt.execute("ALTER TABLE venda ADD COLUMN forma_pagamento TEXT"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE venda ADD COLUMN parcelas INTEGER DEFAULT 1"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE venda ADD COLUMN valor_parcela REAL DEFAULT 0"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE venda ADD COLUMN data_venda TEXT"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE venda ADD COLUMN hora_venda TEXT"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE venda ADD COLUMN status INTEGER NOT NULL DEFAULT 1"); } catch (Exception ignored) {}

            // status geral
            try { stmt.execute("ALTER TABLE usuario ADD COLUMN status INTEGER NOT NULL DEFAULT 1"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE vendedor ADD COLUMN status INTEGER NOT NULL DEFAULT 1"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE cliente ADD COLUMN status INTEGER NOT NULL DEFAULT 1"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE produto ADD COLUMN status INTEGER NOT NULL DEFAULT 1"); } catch (Exception ignored) {}

            System.out.println("Banco de dados inicializado/atualizado com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco de dados:");
            e.printStackTrace();
        }
    }
}
