package org.example.app.dao;

import org.example.app.database.ConnectionManager;
import org.example.app.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Produtos.
 *
 * Consome o {@link ConnectionManager} de forma TRANSPARENTE: o mÃƒÆ’Ã‚Â©todo
 * {@code ConnectionManager.getConnection()} decide automaticamente se a
 * operaÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o irÃƒÆ’Ã‚Â¡ para o banco PRINCIPAL (PostgreSQL / online) ou para o banco
 * de CONTINGÃƒÆ’Ã…Â NCIA (loja.db / SQLite), conforme o estado da rede.
 */
public class ProdutoDAO {

    public void salvar(Produto produto) {
        String sql = """
            INSERT INTO produto (nome, preco, estoque, status)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produto.getNome());
            ps.setDouble(2, produto.getPreco());
            ps.setInt(3, produto.getEstoque());
            ps.setInt(4, produto.getStatus()); // ativo por padrÃƒÆ’Ã‚Â£o
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

public Produto buscarPorId(Long id) {
        String sql = "SELECT * FROM produto WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getLong("id"));
                    p.setNome(rs.getString("nome"));
                    p.setPreco(rs.getDouble("preco"));
                    p.setEstoque(rs.getInt("estoque"));
                    p.setStatus(rs.getInt("status"));
                    return p;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por id", e);
        }
        return null;
    }

    public List<Produto> listar() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto WHERE status = 1" ;

        try (Connection conn = ConnectionManager.getConnection();
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

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        }

        return lista;
    }


    public List<Produto> listarInativos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto WHERE status = 2";

        try (Connection conn = ConnectionManager.getConnection();
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
            e.printStackTrace();
        }
        return lista;
    }


    public void atualizar(Produto produto) {
        String sql = """
            UPDATE produto
            SET nome = ?, preco = ?, estoque = ?, status = ?
            WHERE id = ?
        """;

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produto.getNome());
            ps.setDouble(2, produto.getPreco());
            ps.setInt(3, produto.getEstoque());
            ps.setInt(4, produto.getStatus());
            ps.setLong(5, produto.getId());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alterarStatus(Long id, int status) {
        String sql = "UPDATE produto SET status = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, status); // 1 = ativo, 2 = inativo
            ps.setLong(2, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void reativar(List<Long> ids) {
        String sql = "UPDATE produto SET status = 1 WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Long id : ids) {
                ps.setLong(1, id);
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao reativar produtos", e);
        }
    }

    //=======================================
    // METHODO PARA VALIDAR A UTILIZAÃƒÆ’Ã¢â‚¬Â¡ÃƒÆ’Ã†â€™O DO NOME;
    //=======================================
    public boolean buscarNome(String nome){
        String sql = "SELECT * FROM produto WHERE nome = ? LIMIT 1";
         try(Connection conn = ConnectionManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)){

             ps.setString(1,nome);

             try(ResultSet rs = ps.executeQuery()){
                 return rs.next();
             }

         }catch (SQLException e){
             throw new IllegalArgumentException("Erro imprevisto na execuÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â£o da query", e);
         }
    }

    //=======================================
    // METHODO PARA VALIDAR EXIATENCIA QUNATIDADE DE ESTOQUE
    //=======================================

}
