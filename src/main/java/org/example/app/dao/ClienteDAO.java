package org.example.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.app.database.ConnectionManager;
import org.example.app.model.Cliente;

/**
 * DAO de Clientes.
 *
 * Consome o {@link ConnectionManager} de forma TRANSPARENTE: o mÃƒÆ’Ã‚Â©todo
 * {@code ConnectionManager.getConnection()} decide automaticamente se a
 * operaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o irÃƒÆ’Ã‚Â¡ para o banco PRINCIPAL (PostgreSQL / online) ou para o banco
 * de CONTINGÃƒÆ’Ã…Â NCIA (loja.db / SQLite), conforme o estado da rede.
 *
 * Os controllers chamam este DAO normalmente, sem conhecer o estado da rede.
 */
public class ClienteDAO {

    /* =========================================================
       SALVAR (INSERT / UPDATE)
       ========================================================= */
    public void salvar(Cliente c) {
        if (c.getId() == null) {
            String sql = "INSERT INTO cliente (cpf, nome, telefone, status, sincronizado) VALUES (?, ?, ?, ?, 0)";
            try (Connection conn = ConnectionManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, c.getCpf());
                ps.setString(2, c.getNome());
                ps.setString(3, c.getTelefone());
                ps.setInt(4, 1); // status ativo
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao inserir cliente: " + e.getMessage(), e);
            }
        } else {
            atualizar(c);
        }
    }

    /* =========================================================
       VALIDAR CPF
       ========================================================= */
    public boolean existeCpf(String cpf) {
        String sql = "SELECT 1 FROM cliente WHERE cpf = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* =========================================================
       INATIVAR / REATIVAR
       ========================================================= */
    public void inativar(Long id) {
        String sql = "UPDATE cliente SET status = 2, updated_at = ? WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inativar cliente.", e);
        }
    }

    public void reativar(List<Long> ids) {
        String sql = "UPDATE cliente SET status = 1 WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
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

    /* =========================================================
       LISTAR
       ========================================================= */
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Connection conn = ConnectionManager.getConnection();
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

/* =========================================================
       BUSCAR POR ID
       ========================================================= */
    public Cliente buscarPorId(Long id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente(
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getString("telefone"),
                            rs.getInt("status")
                    );
                    c.setId(rs.getLong("id"));
                    return c;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar cliente por id.", e);
        }
        return null;
    }

    public List<Cliente> listarAtivos() {
        String sql = "SELECT * FROM cliente WHERE status = 1";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
            e.printStackTrace();
        }
        return lista;
    }

    public List<Cliente> listarInativos() {
        String sql = "SELECT * FROM cliente WHERE status = 2";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
            e.printStackTrace();
        }
        return lista;
    }

    /* =========================================================
       ATUALIZAR
       ========================================================= */
    public void atualizar(Cliente c) {
        c.markAsUpdated();
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, telefone = ?, updated_at = ?, sincronizado = 0 WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
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

    /* =========================================================
       SINCRONIZAÃƒÆ’Ã¢â‚¬Â¡ÃƒÆ’Ã†â€™O
       ========================================================= */
    public List<Cliente> listarPendentes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE sincronizado = 0";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        rs.getInt("status")
                );
                c.setId(rs.getLong("id"));
                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void marcarSincronizado(Long id) {
        String sql = "UPDATE cliente SET sincronizado = 1 WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizarIdAposSync(Long idLocal, Long idNuvem) {
        String sql = "UPDATE cliente SET id = ?, sincronizado = 1 WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idNuvem);
            ps.setLong(2, idLocal);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

