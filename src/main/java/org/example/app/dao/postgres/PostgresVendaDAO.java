package org.example.app.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.example.app.database.ConexaoPostgres;
import org.example.app.model.ItemVenda;
import org.example.app.model.Venda;

/**
 * DAO responsÃƒÂ¡vel por persistir VENDAS no banco ONLINE (Postgres).
 * Usado quando o sistema estÃƒÂ¡ conectado ÃƒÂ  internet.
 */
public class PostgresVendaDAO {

    public void salvarCompleta(Venda venda) {

        String sqlVenda = "INSERT INTO venda (cliente_id, vendedor_id, total, forma_pagamento, " +
                "parcelas, valor_parcela, data_venda, hora_venda, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";

        String sqlItem = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario) " +
                "VALUES (?, ?, ?, ?)";

        String sqlEstoque = "UPDATE produto SET estoque = estoque - ? WHERE id = ?";

        try (Connection conn = ConexaoPostgres.conectar()) {

            conn.setAutoCommit(false);

            try (PreparedStatement psVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {

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
                        try (PreparedStatement psItem = conn.prepareStatement(sqlItem)) {
                            psItem.setLong(1, vendaId);
                            psItem.setLong(2, item.getProduto().getId());
                            psItem.setInt(3, item.getQuantidade());
                            psItem.setDouble(4, item.getProduto().getPreco());
                            psItem.executeUpdate();
                        }

                        try (PreparedStatement psEstoque = conn.prepareStatement(sqlEstoque)) {
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
            throw new RuntimeException("Erro ao salvar venda no Postgres", e);
        }
    }
}
