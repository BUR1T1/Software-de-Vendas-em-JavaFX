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
            String sql = "INSERT INTO cliente (nome, cpf, telefone, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = ConexaoSQLite.conectar();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, c.getNome());
                ps.setString(2, c.getCpf());
                ps.setString(3, c.getTelefone());
                ps.setInt(4, 1); // Status ativo
                ps.setTimestamp(5, Timestamp.valueOf(c.getCreatedAt()));
                ps.setTimestamp(6, Timestamp.valueOf(c.getUpdatedAt()));
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao inserir cliente. Verifique se o CPF já existe.");
            }
        } else {
            atualizar(c);
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
            throw new RuntimeException("Cliente reativado com sucesso !!!");
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
                        rs.getNString("cpf"),
                        rs.getString("telefone"),
                        rs.getInt("status")
                );
                c.setNome(rs.getString("nome"));
                c.setCpf(rs.getString("cpf"));
                c.setTelefone(rs.getString("telefone"));
                lista.add(c);
            }

        } catch (Exception e) {
            throw new RuntimeException(" Nenhum cliente foi cadastrado !!!");
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

                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}

