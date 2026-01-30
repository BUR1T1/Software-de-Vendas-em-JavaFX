package org.example.app.dao;

import org.example.app.database.ConexaoSQLite;
import org.example.app.model.Cliente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void salvar(Cliente c) {
        if (c.getId() == null) {
            String sql = "INSERT INTO cliente (cpf, nome, telefone, status) VALUES (?, ?, ?, ?)";
            try (Connection conn = ConexaoSQLite.conectar();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, c.getCpf());
                ps.setString(2, c.getNome());
                ps.setString(3, c.getTelefone());
                ps.setInt(4, 1); // status ativo
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao inserir cliente: " + e.getMessage(), e);
            }
        } else {
            atualizar(c);
        }
    }


    public boolean existeCpf(String cpf) {
        String sql = "SELECT 1 FROM cliente WHERE cpf = ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //=======================================
    // METHODO PARA INATIVAR O REATIVAR CLIENTES.
    //=======================================
    public void inativar(Long id) {
        String sql = "UPDATE cliente SET status = 2, updated_at = ? WHERE id = ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inativar cliente.");
        }
    }

    public void reativar(List<Long> ids) {
        String sql = "UPDATE cliente SET status = 1 WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Long id : ids) {
                ps.setLong(1, id);
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao reativar cliente", e);
        }

    }


    //=======================================
    // METHODO PARA LISTAR CLIENTES
    //=======================================

    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Connection conn = ConexaoSQLite.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        rs.getInt("status")
                );
                c.setId(rs.getLong("id")); // ESSENCIAL

                lista.add(c);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar clientes.", e);
        }

        return lista;
    }

    //=======================================
    // METHODO PARA ATUALIZAR
    //=======================================

    public void atualizar(Cliente c) {
        c.markAsUpdated();
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, telefone = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getCpf());
            ps.setString(3, c.getTelefone());
            ps.setTimestamp(4, Timestamp.valueOf(c.getUpdatedAt()));
            ps.setLong(5, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Cliente> listarAtivos() {
        String sql = "SELECT * FROM cliente WHERE status = 1";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        rs.getInt("status")
                );
                c.setId(rs.getLong("id")); // ← ESSENCIAL


                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Cliente> listarInativos() {
        String sql = "SELECT * FROM cliente WHERE status = 2";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        rs.getInt("status")
                );
                c.setId(rs.getLong("id")); // ← ESSENCIAL


                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}

