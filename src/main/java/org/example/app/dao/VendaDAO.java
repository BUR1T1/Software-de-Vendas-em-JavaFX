package org.example.app.dao;

import org.example.app.database.ConexaoSQLite;
import org.example.app.model.Cliente;
import org.example.app.model.Venda;
import org.example.app.model.ItemVenda;
import org.example.app.model.Vendedor;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    public void atualizarCliente(Long vendaId, Long clienteId) {
        String sql = "UPDATE venda SET cliente_id = ? WHERE id = ?";
        executarUpdate(sql, clienteId, vendaId);
    }

    public void atualizarVendedor(Long vendaId, Long vendedorId) {
        String sql = "UPDATE venda SET vendedor_id = ? WHERE id = ?";
        executarUpdate(sql, vendedorId, vendaId);
    }

    private void executarUpdate(String sql, Long fkId, Long vendaId) {
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, fkId);
            ps.setLong(2, vendaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void salvarCompleta(Venda venda) {

        String sqlVenda = """
        INSERT INTO venda 
        (cliente_id, vendedor_id, total, forma_pagamento, parcelas, valor_parcela,
         data_venda, hora_venda, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        String sqlItem = """
        INSERT INTO item_venda 
        (venda_id, produto_id, quantidade, preco_unitario) 
        VALUES (?, ?, ?, ?)
    """;

        String sqlEstoque = """
        UPDATE produto 
        SET estoque = estoque - ? 
        WHERE id = ?
    """;

        try (Connection conn = ConexaoSQLite.conectar()) {

            conn.setAutoCommit(false);

            try (PreparedStatement psVenda =
                         conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {

                venda.calcularTotalFinal(); // 🔥 garante cálculo correto

                psVenda.setLong(1, venda.getCliente().getId());
                psVenda.setLong(2, venda.getVendedor().getId());
                psVenda.setDouble(3, venda.getTotal());
                psVenda.setString(4, venda.getFormaPagamento());
                psVenda.setInt(5, venda.getParcelas());
                psVenda.setDouble(6, venda.getValorParcela());
                psVenda.setString(7, venda.getDataVenda().toString());
                psVenda.setString(8, venda.getHoraVenda().toString());
                psVenda.setInt(9, 1); // status efetivada

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
            throw new RuntimeException("Erro ao salvar venda completa", e);
        }
    }

    public List<Venda> listarHistorico() {

        List<Venda> lista = new ArrayList<>();

        String sql = """
        SELECT v.*, 
               c.nome AS nome_cliente, 
               u.nome AS nome_vendedor
        FROM venda v
        JOIN cliente c ON v.cliente_id = c.id
        JOIN vendedor ven ON v.vendedor_id = ven.id
        JOIN usuario u ON ven.usuario_id = u.id
        WHERE v.status = 1
        ORDER BY v.id DESC
    """;

        try (Connection conn = ConexaoSQLite.conectar();
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
                c.setNome(rs.getString("nome_cliente"));
                v.setCliente(c);

                Vendedor vendedor = new Vendedor();
                vendedor.setNome(rs.getString("nome_vendedor"));
                v.setVendedor(vendedor);

                lista.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }


    public void cancelarVenda(Long vendaId) {
        String sqlCheck = "SELECT status FROM venda WHERE id = ?";
        String sqlUpdateVenda = "UPDATE venda SET status = 2 WHERE id = ?";
        String sqlItens = "SELECT produto_id, quantidade FROM item_venda WHERE venda_id = ?";
        String sqlUpdateProduto = "UPDATE produto SET estoque = estoque + ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar()) {
            conn.setAutoCommit(false);

            // 1. Atualiza status da venda
            try (PreparedStatement psVenda = conn.prepareStatement(sqlUpdateVenda)) {
                psVenda.setLong(1, vendaId);
                psVenda.executeUpdate();
            }

            // 2. Repor estoque dos produtos
            try (PreparedStatement psItens = conn.prepareStatement(sqlItens);
                 PreparedStatement psProduto = conn.prepareStatement(sqlUpdateProduto)) {

                psItens.setLong(1, vendaId);
                ResultSet rs = psItens.executeQuery();

                while (rs.next()) {
                    Long produtoId = rs.getLong("produto_id");
                    int quantidade = rs.getInt("quantidade");

                    psProduto.setInt(1, quantidade);
                    psProduto.setLong(2, produtoId);
                    psProduto.executeUpdate();
                }
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
