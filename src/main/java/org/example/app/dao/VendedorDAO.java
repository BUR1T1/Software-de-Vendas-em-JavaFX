package org.example.app.dao;

import org.example.app.database.ConexaoSQLite;
import org.example.app.model.Vendedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendedorDAO {

    public void salvar(Vendedor vendedor) {
        if (vendedor.getId() == null) {
            String sql = "INSERT INTO vendedor (nome, cpf, comissao, status) VALUES (?, ?, ?, ?)";
            try (Connection conn = ConexaoSQLite.conectar();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, vendedor.getNome());
                ps.setString(2, vendedor.getCpf());
                ps.setDouble(3, vendedor.getComissao());
                ps.setInt(4, 1);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        vendedor.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao salvar vendedor. CPF já cadastrado? " + e.getMessage(), e);
            }
        } else {
            atualizar(vendedor);
        }
    }

    public void inativar(Long id) {
        String sql = "UPDATE vendedor SET status = 2 WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inativar vendedor", e);
        }
    }

    public void reativar(List<Long> ids) {
        String sql = "UPDATE vendedor SET status = 1 WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Long id : ids) {
                ps.setLong(1, id);
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao reativar vendedores", e);
        }
    }

    public List<Vendedor> listar() {
        List<Vendedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM vendedor";

        try (Connection conn = ConexaoSQLite.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Vendedor v = new Vendedor();
                v.setId(rs.getLong("id"));
                v.setNome(rs.getString("nome"));
                v.setCpf(rs.getString("cpf"));
                v.setComissao(rs.getDouble("comissao"));
                v.setStatus(rs.getInt("status"));
                lista.add(v);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vendedores", e);
        }

        return lista;
    }

    public void atualizar(Vendedor vendedor) {
        String sql = "UPDATE vendedor SET nome = ? ,cpf = ?, comissao = ?, status = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1,vendedor.getNome());
            ps.setString(2, vendedor.getCpf());
            ps.setDouble(3, vendedor.getComissao());
            ps.setInt(4, vendedor.getStatus());
            ps.setLong(5, vendedor.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar vendedor", e);
        }
    }

    public List<Vendedor> listarAtivos() {
        String sql = "SELECT * FROM vendedor WHERE status = 1";
        List<Vendedor> list = new ArrayList<>();

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vendedor v = new Vendedor();
                v.setId(rs.getLong("id"));
                v.setNome(rs.getString("nome"));
                v.setCpf(rs.getString("cpf"));
                v.setComissao(rs.getDouble("comissao"));
                v.setStatus(rs.getInt("status"));
                list.add(v);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vendedores ativos", e);
        }
        return list;
    }

    public List<Vendedor> listarInativos() {
        String sql = "SELECT * FROM vendedor WHERE status = 2";
        List<Vendedor> list = new ArrayList<>();

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vendedor v = new Vendedor();
                v.setId(rs.getLong("id"));
                v.setNome(rs.getString("nome"));
                v.setCpf(rs.getString("cpf"));
                v.setComissao(rs.getDouble("comissao"));
                v.setStatus(rs.getInt("status"));
                list.add(v);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
