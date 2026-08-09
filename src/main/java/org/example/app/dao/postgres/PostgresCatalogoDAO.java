package org.example.app.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.example.app.database.ConexaoPostgres;
import org.example.app.model.Cliente;
import org.example.app.model.Produto;
import org.example.app.model.Vendedor;

/**
 * DAO de catÃƒÂ¡logo (clientes, vendedores, produtos) a partir do Postgres
 * (online). Usado quando o sistema estÃƒÂ¡ conectado, para popular o cache
 * offline.
 */
public class PostgresCatalogoDAO {

    /* =========================================================
    CLIENTES
       ========================================================= */
    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, telefone, status FROM cliente WHERE status = 1";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes no Postgres", e);
        }
        return lista;
    }

    /* =========================================================
       CLIENTES - ESCRITA (nuvem)
       ========================================================= */
    /**
     * Insere um novo cliente no Postgres e define o id gerado.
     *
     * @return id gerado no banco, ou null se falhar
     */
    public Long salvarCliente(Cliente c) {
        String sql = "INSERT INTO cliente (cpf, nome, telefone, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id"})) {
            ps.setString(1, c.getCpf());
            ps.setString(2, c.getNome());
            ps.setString(3, c.getTelefone());
            ps.setInt(4, c.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    c.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cliente no Postgres", e);
        }
        return null;
    }

    public void atualizarCliente(Cliente c) {
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, telefone = ?, status = ? WHERE id = ?";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getCpf());
            ps.setString(3, c.getTelefone());
            ps.setInt(4, c.getStatus());
            ps.setLong(5, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente no Postgres", e);
        }
    }

    public void inativarCliente(Long id) {
        String sql = "UPDATE cliente SET status = 2 WHERE id = ?";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inativar cliente no Postgres", e);
        }
    }

    public void reativarCliente(Long id) {
        String sql = "UPDATE cliente SET status = 1 WHERE id = ?";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao reativar cliente no Postgres", e);
        }
    }

    public boolean existeCpf(String cpf) {
        String sql = "SELECT 1 FROM cliente WHERE cpf = ?";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar CPF no Postgres", e);
        }
    }

    /**
     * Insere o cliente na nuvem se o CPF ainda nÃƒÂ£o existir; caso contrÃƒÂ¡rio,
     * atualiza.
     *
     * @return o id do cliente na nuvem (novo ou jÃƒÂ¡ existente)
     */
    public Long salvarClienteClienteExistente(Cliente c) {
        String sqlCount = "SELECT id FROM cliente WHERE cpf = ?";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sqlCount)) {
            ps.setString(1, c.getCpf());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // JÃƒÂ¡ existe: atualiza e retorna o id existente
                    Long id = rs.getLong("id");
                    c.setId(id);
                    atualizarCliente(c);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar CPF para sincronizaÃƒÂ§ÃƒÂ£o", e);
        }
        // NÃƒÂ£o existe: insere na nuvem
        return salvarCliente(c);
    }

    public List<Cliente> listarClientesAtivos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, telefone, status FROM cliente WHERE status = 1";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes ativos no Postgres", e);
        }
        return lista;
    }

    public List<Cliente> listarClientesInativos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, telefone, status FROM cliente WHERE status = 2";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes inativos no Postgres", e);
        }
        return lista;
    }

    /* =========================================================
       VENDEDORES
       ========================================================= */
    public List<Vendedor> listarVendedores() {
        List<Vendedor> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cpf, comissao, status FROM vendedor WHERE status = 1";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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
            throw new RuntimeException("Erro ao listar vendedores no Postgres", e);
        }
        return lista;
    }

    /* =========================================================
       PRODUTOS
       ========================================================= */
    public List<Produto> listarProdutos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT id, nome, preco, estoque, status FROM produto WHERE status = 1";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setPreco(rs.getDouble("preco"));
                p.setEstoque(rs.getInt("estoque"));
                p.setStatus(rs.getInt("status"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos no Postgres", e);
        }
        return lista;
    }

    /* =========================================================
       ATUALIZAR ESTOQUE (apÃƒÂ³s venda online)
       ========================================================= */
    public void atualizarEstoque(Long produtoId, int quantidade) {
        String sql = "UPDATE produto SET estoque = estoque - ? WHERE id = ?";
        try (Connection conn = ConexaoPostgres.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantidade);
            ps.setLong(2, produtoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar estoque no Postgres", e);
        }
    }
}
