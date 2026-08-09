package org.example.app.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInit {

    public static void inicializar() {

        String sqlUsuario = """
            CREATE TABLE IF NOT EXISTS usuario (
                id SERIAL PRIMARY KEY,
                nome TEXT,
                login TEXT NOT NULL UNIQUE,
                senha TEXT NOT NULL,
                perfil TEXT NOT NULL CHECK (perfil IN ('ADMIN','VENDEDOR')),
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status INTEGER NOT NULL DEFAULT 1
            );
        """;

        String sqlVendedor = """
            CREATE TABLE IF NOT EXISTS vendedor (
                id SERIAL PRIMARY KEY,
                nome TEXT NOT NULL,
                cpf TEXT NOT NULL UNIQUE,
                comissao REAL NOT NULL CHECK (comissao >= 0),
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status INTEGER NOT NULL DEFAULT 1
            );
        """;

        String sqlCliente = """
            CREATE TABLE IF NOT EXISTS cliente (
                id SERIAL PRIMARY KEY,
                cpf TEXT NOT NULL UNIQUE,
                nome TEXT,
                telefone TEXT,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status INTEGER NOT NULL DEFAULT 1,
                sincronizado INTEGER NOT NULL DEFAULT 1
            );
        """;

        String sqlProduto = """
            CREATE TABLE IF NOT EXISTS produto (
                id SERIAL PRIMARY KEY,
                nome TEXT NOT NULL,
                preco REAL NOT NULL CHECK (preco >= 0),
                estoque INTEGER NOT NULL CHECK (estoque >= 0),
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status INTEGER NOT NULL DEFAULT 1
            );
        """;

        String sqlVenda = """
            CREATE TABLE IF NOT EXISTS venda (
                id SERIAL PRIMARY KEY,
                cliente_id INTEGER NOT NULL,
                vendedor_id INTEGER NOT NULL,
                total REAL NOT NULL CHECK (total >= 0),
                forma_pagamento TEXT,
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

        String sqlItemVenda = """
            CREATE TABLE IF NOT EXISTS item_venda (
                id SERIAL PRIMARY KEY,
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

        String sqlPedido = """
            CREATE TABLE IF NOT EXISTS pedido (
                id SERIAL PRIMARY KEY,
                cliente_id INTEGER NOT NULL,
                vendedor_id INTEGER NOT NULL,
                total REAL NOT NULL CHECK (total >= 0),
                data_pedido TEXT NOT NULL,
                hora_pedido TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                status INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY (cliente_id) REFERENCES cliente(id),
                FOREIGN KEY (vendedor_id) REFERENCES vendedor(id)
            );
        """;

        String sqlItemPedido = """
            CREATE TABLE IF NOT EXISTS item_pedido (
                id SERIAL PRIMARY KEY,
                pedido_id INTEGER NOT NULL,
                produto_id INTEGER NOT NULL,
                quantidade INTEGER NOT NULL,
                preco_unitario REAL NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (pedido_id) REFERENCES pedido(id),
                FOREIGN KEY (produto_id) REFERENCES produto(id)
            );
        """;

        // Conecta direto no Postgres Ã¢â‚¬â€ este DatabaseInit nÃƒÂ£o deve rodar contra SQLite.
        // O schema offline (SQLite) ÃƒÂ© responsabilidade exclusiva do DatabaseOfflineInit.
        try (Connection conn = ConexaoPostgres.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsuario);
            stmt.execute(sqlVendedor);
            stmt.execute(sqlCliente);
            stmt.execute(sqlProduto);
            stmt.execute(sqlVenda);
            stmt.execute(sqlItemVenda);
            stmt.execute(sqlPedido);
            stmt.execute(sqlItemPedido);

            stmt.execute("ALTER TABLE venda ALTER COLUMN forma_pagamento DROP NOT NULL");
            stmt.execute("ALTER TABLE venda ADD COLUMN IF NOT EXISTS parcelas INTEGER DEFAULT 1");
            stmt.execute("ALTER TABLE venda ADD COLUMN IF NOT EXISTS valor_parcela REAL DEFAULT 0");
            stmt.execute("ALTER TABLE venda ADD COLUMN IF NOT EXISTS data_venda TEXT");
            stmt.execute("ALTER TABLE venda ADD COLUMN IF NOT EXISTS hora_venda TEXT");
            stmt.execute("ALTER TABLE venda ADD COLUMN IF NOT EXISTS status INTEGER NOT NULL DEFAULT 1");

            stmt.execute("ALTER TABLE usuario ADD COLUMN IF NOT EXISTS nome TEXT");
            stmt.execute("ALTER TABLE usuario ADD COLUMN IF NOT EXISTS status INTEGER NOT NULL DEFAULT 1");
            stmt.execute("ALTER TABLE vendedor ADD COLUMN IF NOT EXISTS status INTEGER NOT NULL DEFAULT 1");
            stmt.execute("ALTER TABLE cliente ADD COLUMN IF NOT EXISTS status INTEGER NOT NULL DEFAULT 1");
            stmt.execute("ALTER TABLE produto ADD COLUMN IF NOT EXISTS status INTEGER NOT NULL DEFAULT 1");
            stmt.execute("ALTER TABLE cliente ADD COLUMN IF NOT EXISTS sincronizado INTEGER NOT NULL DEFAULT 1");

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuario WHERE login = 'admin'")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate(
                        "INSERT INTO usuario (nome, login, senha, perfil, status) " +
                        "VALUES ('Administrador', 'admin', '123', 'ADMIN', 1)"
                    );
                    System.out.println("UsuÃƒÂ¡rio admin padrÃƒÂ£o criado (admin/123).");
                }
            }

            System.out.println("Banco de dados (PostgreSQL) inicializado/atualizado com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco de dados:");
            e.printStackTrace();
        }
    }
}