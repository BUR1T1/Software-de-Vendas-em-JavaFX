package org.example.app.dao;

import org.example.app.database.ConexaoSQLite;
import org.example.app.model.Cliente;
import org.example.app.model.ItemPedido;
import org.example.app.model.Pedido;
import org.example.app.model.Produto;
import org.example.app.model.Vendedor;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PedidosDAO {

    // =========================================================
    // SALVAR PEDIDO (COM ITENS, EM TRANSAÇÃO)
    // =========================================================
    public void salvar(Pedido pedido) {

        String sqlPedido = """
            INSERT INTO pedido
            (cliente_id, vendedor_id, total, data_pedido, hora_pedido, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        String sqlItem = """
            INSERT INTO item_pedido
            (pedido_id, produto_id, quantidade, preco_unitario)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = ConexaoSQLite.conectar()) {

            conn.setAutoCommit(false);

            try (PreparedStatement psPedido =
                         conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {

                pedido.calcularTotal();

                psPedido.setLong(1, pedido.getCliente().getId());
                psPedido.setLong(2, pedido.getVendedor().getId());
                psPedido.setDouble(3, pedido.getTotal());
                psPedido.setString(4, pedido.getDataPedido().toString());
                psPedido.setString(5, pedido.getHoraPedido().toString());
                psPedido.setInt(6, pedido.getStatus());

                psPedido.executeUpdate();

                ResultSet rs = psPedido.getGeneratedKeys();

                if (rs.next()) {
                    Long pedidoId = rs.getLong(1);

                    for (ItemPedido item : pedido.getItens()) {
                        try (PreparedStatement psItem =
                                     conn.prepareStatement(sqlItem)) {

                            psItem.setLong(1, pedidoId);
                            psItem.setLong(2, item.getProduto().getId());
                            psItem.setInt(3, item.getQuantidade());
                            psItem.setDouble(4, item.getPrecoUnitario());
                            psItem.executeUpdate();
                        }
                    }
                }

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar pedido", e);
        }
    }

    // =========================================================
    // LISTAR HISTÓRICO DE PEDIDOS
    // =========================================================
    public List<Pedido> listarHistorico() {
        List<Pedido> lista = new ArrayList<>();

        String sql = """
            SELECT p.*, c.nome AS nome_cliente, v.nome AS nome_vendedor
            FROM pedido p
            JOIN cliente c ON p.cliente_id = c.id
            JOIN vendedor v ON p.vendedor_id = v.id
            ORDER BY p.id DESC
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getLong("id"));
                pedido.setTotal(rs.getDouble("total"));
                pedido.setDataPedido(LocalDate.parse(rs.getString("data_pedido")));
                pedido.setHoraPedido(LocalTime.parse(rs.getString("hora_pedido")));
                pedido.setStatus(rs.getInt("status"));

                Cliente c = new Cliente();
                c.setNome(rs.getString("nome_cliente"));
                pedido.setCliente(c);

                Vendedor vd = new Vendedor();
                vd.setNome(rs.getString("nome_vendedor"));
                pedido.setVendedor(vd);

                pedido.setItens(listarItens(pedido.getId()));

                lista.add(pedido);
            }

        } catch (Exception e) {
            System.err.println("Erro ao listar histórico de pedidos:");
            e.printStackTrace();
        }
        return lista;
    }

    // =========================================================
    // LISTAR PENDENTES (STATUS = 1 -> AINDA NÃO PROCESSADO)
    // =========================================================
    public List<Pedido> listarPendentes() {
        List<Pedido> lista = new ArrayList<>();

        String sql = """
            SELECT p.*, c.nome AS nome_cliente, v.nome AS nome_vendedor
            FROM pedido p
            JOIN cliente c ON p.cliente_id = c.id
            JOIN vendedor v ON p.vendedor_id = v.id
            WHERE p.status = 1
            ORDER BY p.id
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getLong("id"));
                pedido.setTotal(rs.getDouble("total"));
                pedido.setDataPedido(LocalDate.parse(rs.getString("data_pedido")));
                pedido.setHoraPedido(LocalTime.parse(rs.getString("hora_pedido")));
                pedido.setStatus(rs.getInt("status"));

                Cliente c = new Cliente();
                c.setId(rs.getLong("cliente_id"));
                c.setNome(rs.getString("nome_cliente"));
                pedido.setCliente(c);

                Vendedor vd = new Vendedor();
                vd.setId(rs.getLong("vendedor_id"));
                vd.setNome(rs.getString("nome_vendedor"));
                pedido.setVendedor(vd);

                pedido.setItens(listarItens(pedido.getId()));

                lista.add(pedido);
            }

        } catch (Exception e) {
            System.err.println("Erro ao listar pedidos pendentes:");
            e.printStackTrace();
        }
        return lista;
    }

    // =========================================================
    // LISTAR ITENS DE UM PEDIDO
    // =========================================================
    private List<ItemPedido> listarItens(Long pedidoId) {
        List<ItemPedido> itens = new ArrayList<>();

        String sql = """
            SELECT ip.*, pr.nome AS nome_produto, pr.preco AS preco_produto
            FROM item_pedido ip
            JOIN produto pr ON ip.produto_id = pr.id
            WHERE ip.pedido_id = ?
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, pedidoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Produto prod = new Produto();
                prod.setId(rs.getLong("produto_id"));
                prod.setNome(rs.getString("nome_produto"));
                prod.setPreco(rs.getDouble("preco_produto"));

                ItemPedido item = new ItemPedido(prod, rs.getInt("quantidade"));
                item.setPrecoUnitario(rs.getDouble("preco_unitario"));

                itens.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return itens;
    }

    // =========================================================
    // MARCAR PEDIDO COMO PROCESSADO / CONCLUÍDO (STATUS = 3)
    // =========================================================
    public void marcarConcluido(Long pedidoId) {
        String sql = "UPDATE pedido SET status = 3, updated_at = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setLong(2, pedidoId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao concluir pedido", e);
        }
    }

    // =========================================================
    // CANCELAR PEDIDO (STATUS = 2)
    // =========================================================
    public void cancelarPedido(Long pedidoId) {
        String sql = "UPDATE pedido SET status = 2, updated_at = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setLong(2, pedidoId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao cancelar pedido", e);
        }
    }
}

