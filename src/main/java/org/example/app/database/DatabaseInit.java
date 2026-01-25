package org.example.app.database;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Classe responsável por inicializar o banco SQLite
 * Cria todas as tabelas necessárias para o sistema
 * Deve ser chamada uma única vez na inicialização do sistema
 */
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
                        nome TEXT NOT NULL,
                        cpf TEXT NOT NULL UNIQUE,
                        telefone TEXT,
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
                               FOREIGN KEY (cliente_id) REFERENCES cliente(id),
                               FOREIGN KEY (vendedor_id) REFERENCES vendedor(id),
                               data_venda TEXT NOT NULL,
                               hora_venda TEXT NOT NULL,  
                               data_hora_insercao DATETIME DEFAULT CURRENT_TIMESTAMP 
                        
            );
        """;

        // =========================
        // TABELA ITEM_VENDA
        // =========================
        String sqlItemVenda = """
            CREATE TABLE IF NOT EXISTS item_venda (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                            cliente_id INTEGER NOT NULL,
                            vendedor_id INTEGER NOT NULL,
                            data TEXT NOT NULL,
                            valor_bruto REAL NOT NULL,    
                            desconto REAL DEFAULT 0,     
                            acrescimo REAL DEFAULT 0,    
                            total REAL NOT NULL,        
                            FOREIGN KEY (cliente_id) REFERENCES cliente(id),
                            FOREIGN KEY (vendedor_id) REFERENCES vendedor(id)
            );
        """;

        // =========================
        // EXECUÇÃO DAS TABELAS
        // =========================
        try (Connection conn = ConexaoSQLite.conectar();
             Statement stmt = conn.createStatement()) {

            // Importante para SQLite respeitar FK
            stmt.execute("PRAGMA foreign_keys = ON");

            // Ordem correta de criação
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
