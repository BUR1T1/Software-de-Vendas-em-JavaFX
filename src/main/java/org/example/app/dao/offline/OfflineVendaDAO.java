package org.example.app.dao.offline;

import org.example.app.database.ConexaoOffline;
import org.example.app.model.Cliente;
import org.example.app.model.ItemVenda;
import org.example.app.model.Produto;
import org.example.app.model.Venda;
import org.example.app.model.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsÃƒÂ¡vel por persistir VENDAS OFFLINE no SQLite local (offline.db).
 * As vendas ficam marcadas como "sincronizado = 0" atÃƒÂ© serem enviadas ao Postgres.
 */
public class OfflineVendaDAO {

    /* =========================================================
       SALVAR VENDA OFFLINE (COM ITENS, EM TRANSAÃƒâ€¡ÃƒÆ’O)
       ========================================================= */
    public void salvarCompleta(Venda venda) {

        String sqlVenda =
            "INSERT INTO offline_venda (cliente_id, vendedor_id, total, forma_pagamento, " +
            "parcelas, valor_parcela, data_venda, hora_venda, sincronizado) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)";

        String sqlItem =
            "INSERT INTO offline_item_venda (venda_id, produto_id, quantidade, preco_unitario) " +
            "VALUES (?, ?, ?, ?)";

        String sqlEstoque =
            "UPDATE offline_catalogo_produto SET estoque = estoque - ? WHERE id = ?";

        try (Connection conn = ConexaoOffline.conectar()) {

            conn.setAutoCommit(false);

            try (PreparedStatement psVenda =
                         conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {

                venda.calcularTotalFinal();

                psVenda.setLong(1, venda.getCliente().getId());
                psVenda.setLong(2, venda.getVendedor().getId());
                psVenda.setDouble(3, venda.getTotal());
                psVenda.setString(4, venda.getFormaPagamento());
                psVenda.setInt(5, venda.getParcelas());
                psVenda.setDouble(6, venda.getValorParcela());
                psVenda.setString(7, venda.getDataVenda().toString());
                psVenda.setString(8, venda.getHoraVenda().toString());

                psVenda.executeUpdate();

                ResultSet rs = psVenda.getGeneratedKeys();

                if (rs.next()) {
                    Long vendaId = rs.getLong(1);

                    for (ItemVenda item : venda.getItens()) {
                        try (PreparedStatement psItem =
                                     conn.prepareStatement(sqlItem)) {
                            psItem.setLong(1, vendaId);
                            psItem.setLong(2, item.getProduto().getId());
                            psItem.setInt(3, item.getQuantidade());
                            psItem.setDouble(4, item.getProduto().getPreco());
                            psItem.executeUpdate();
                        }

                        try (PreparedStatement psEstoque =
                                     conn.prepareStatement(sqlEstoque)) {
                            psEstoque.setInt(1, item.getQuantidade());
                            psEstoque.setLong(2, item.getProduto().getId());
                            psEstoque.executeUpdate();
                        }
                    }
                }

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar venda offline", e);
        }
    }

    /* =========================================================
       LISTAR PENDENTES (SINCRONIZADO = 0)
       ========================================================= */
    public List<Venda> listarPendentes() {
        List<Venda> lista = new ArrayList<>();

        String sql =
            "SELECT v.*, c.nome AS nome_cliente, c.cpf AS cpf_cliente, " +
            "       ve.nome AS nome_vendedor, ve.cpf AS cpf_vendedor " +
            "FROM offline_venda v " +
            "JOIN offline_catalogo_cliente c ON v.cliente_id = c.id " +
            "JOIN offline_catalogo_vendedor ve ON v.vendedor_id = ve.id " +
            "WHERE v.sincronizado = 0 " +
            "ORDER BY v.id";

        try (Connection conn = ConexaoOffline.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venda v = new Venda();
                v.setId(rs.getLong("id"));
                v.setTotal(rs.getDouble("total"));
                v.setFormaPagamento(rs.getString("forma_pagamento"));
                v.setParcelas(rs.getInt("parcelas"));
                v.setValorParcela(rs.getDouble("valor_parcela"));
                v.setDataVenda(LocalDate.parse(rs.getString("data_venda")));
                v.setHoraVenda(LocalTime.parse(rs.getString("hora_venda")));

                Cliente c = new Cliente();
                c.setId(rs.getLong("cliente_id"));
                c.setNome(rs.getString("nome_cliente"));
                c.setCpf(rs.getString("cpf_cliente"));
                v.setCliente(c);

                Vendedor vendedor = new Vendedor();
                vendedor.setId(rs.getLong("vendedor_id"));
                vendedor.setNome(rs.getString("nome_vendedor"));
                vendedor.setCpf(rs.getString("cpf_vendedor"));
                v.setVendedor(vendedor);

                v.setItens(listarItens(rs.getLong("id")));

                lista.add(v);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar vendas offline pendentes", e);
        }
        return lista;
    }

    /* =========================================================
       LISTAR ITENS DE UMA VENDA OFFLINE
       ========================================================= */
    private List<ItemVenda> listarItens(Long vendaId) {
        List<ItemVenda> itens = new ArrayList<>();

        String sql =
            "SELECT iv.*, p.nome AS nome_produto " +
            "FROM offline_item_venda iv " +
            "JOIN offline_catalogo_produto p ON iv.produto_id = p.id " +
            "WHERE iv.venda_id = ?";

        try (Connection conn = ConexaoOffline.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, vendaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Produto prod = new Produto();
                prod.setId(rs.getLong("produto_id"));
                prod.setNome(rs.getString("nome_produto"));
                prod.setPreco(rs.getDouble("preco_unitario"));

                itens.add(new ItemVenda(prod, rs.getInt("quantidade")));
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar itens da venda offline", e);
        }
        return itens;
    }

    /* =========================================================
       MARCAR VENDA COMO SINCRONIZADA
       ========================================================= */
    public void marcarSincronizada(Long vendaId) {
        String sql = "UPDATE offline_venda SET sincronizado = 1 WHERE id = ?";
        try (Connection conn = ConexaoOffline.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, vendaId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao marcar venda como sincronizada", e);
        }
    }

    /* =========================================================
       CONTAR PENDENTES
       ========================================================= */
    public int contarPendentes() {
        String sql = "SELECT COUNT(*) FROM offline_venda WHERE sincronizado = 0";
        try (Connection conn = ConexaoOffline.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao contar vendas offline pendentes", e);
        }
        return 0;
    }
}
