package org.example.app.dao.offline;

import org.example.app.database.ConexaoOffline;
import org.example.app.model.Cliente;
import org.example.app.model.Produto;
import org.example.app.model.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de catÃƒÂ¡logo cacheado no banco OFFLINE (offline.db).
 * Usado para permitir vendas quando o Postgres (online) nÃƒÂ£o estÃƒÂ¡ acessÃƒÂ­vel.
 */
public class OfflineCatalogoDAO {

    /* =========================================================
       CLIENTES
       ========================================================= */

    public void salvarCliente(Cliente c) {
        String sql =
            "INSERT OR REPLACE INTO offline_catalogo_cliente (id, nome, cpf, telefone, status) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoOffline.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, c.getId());
            ps.setString(2, c.getNome());
            ps.setString(3, c.getCpf());
            ps.setString(4, c.getTelefone());
            ps.setInt(5, c.getStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar cliente offline", e);
        }
    }

    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM offline_catalogo_cliente WHERE status = 1";
        try (Connection conn = ConexaoOffline.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
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
            throw new RuntimeException("Erro ao listar clientes offline", e);
        }
        return lista;
    }

    public void limparClientes() {
        try (Connection conn = ConexaoOffline.conectar();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM offline_catalogo_cliente");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao limpar catÃƒÂ¡logo de clientes offline", e);
        }
    }

    /* =========================================================
       VENDEDORES
       ========================================================= */

    public void salvarVendedor(Vendedor v) {
        String sql =
            "INSERT OR REPLACE INTO offline_catalogo_vendedor (id, nome, cpf, comissao, status) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoOffline.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, v.getId());
            ps.setString(2, v.getNome());
            ps.setString(3, v.getCpf());
            ps.setDouble(4, v.getComissao());
            ps.setInt(5, v.getStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar vendedor offline", e);
        }
    }

    public List<Vendedor> listarVendedores() {
        List<Vendedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM offline_catalogo_vendedor WHERE status = 1";
        try (Connection conn = ConexaoOffline.conectar();
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
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar vendedores offline", e);
        }
        return lista;
    }

    public void limparVendedores() {
        try (Connection conn = ConexaoOffline.conectar();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM offline_catalogo_vendedor");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao limpar catÃƒÂ¡logo de vendedores offline", e);
        }
    }

    /* =========================================================
       PRODUTOS
       ========================================================= */

    public void salvarProduto(Produto p) {
        String sql =
            "INSERT OR REPLACE INTO offline_catalogo_produto (id, nome, preco, estoque, status) " +
            "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoOffline.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, p.getId());
            ps.setString(2, p.getNome());
            ps.setDouble(3, p.getPreco());
            ps.setInt(4, p.getEstoque());
            ps.setInt(5, p.getStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar produto offline", e);
        }
    }

    public List<Produto> listarProdutos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM offline_catalogo_produto WHERE status = 1";
        try (Connection conn = ConexaoOffline.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setPreco(rs.getDouble("preco"));
                p.setEstoque(rs.getInt("estoque"));
                p.setStatus(rs.getInt("status"));
                lista.add(p);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar produtos offline", e);
        }
        return lista;
    }

    public void limparProdutos() {
        try (Connection conn = ConexaoOffline.conectar();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM offline_catalogo_produto");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao limpar catÃƒÂ¡logo de produtos offline", e);
        }
    }
}

