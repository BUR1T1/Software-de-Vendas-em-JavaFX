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

    public void atualizarCliente(int vendaId, int clienteId) {
        String sql = "UPDATE venda SET cliente_id = ? WHERE id = ?";
        executarUpdate(sql, clienteId, vendaId);
    }

    public void atualizarVendedor(int vendaId, int vendedorId) {
        String sql = "UPDATE venda SET vendedor_id = ? WHERE id = ?";
        executarUpdate(sql, vendedorId, vendaId);
    }

    private void executarUpdate(String sql, int fkId, int vendaId) {
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fkId);
            ps.setInt(2, vendaId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void salvarCompleta(Venda venda) {
        // SQLs alinhados com seu DatabaseInit
        String sqlVenda = "INSERT INTO venda (cliente_id, vendedor_id, data, total) VALUES (?, ?, ?, ?)";
        String sqlItem = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        String sqlEstoque = "UPDATE produto SET estoque = estoque - ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar()) {
            conn.setAutoCommit(false); // Inicia Transação para segurança dos dados

            try (PreparedStatement psVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {

                // Formatação de data conforme seu padrão TEXT no banco
                String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String horaAtual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String dataHoraCompleta = dataAtual + " " + horaAtual;

                psVenda.setInt(1, venda.getCliente().getId());
                psVenda.setInt(2, venda.getVendedor().getId());
                psVenda.setString(3, dataHoraCompleta);
                psVenda.setDouble(4, venda.getTotal());
                psVenda.executeUpdate();

                // Recupera o ID da venda gerado agora
                ResultSet rs = psVenda.getGeneratedKeys();
                if (rs.next()) {
                    int vendaId = rs.getInt(1);

                    // Salva os itens e baixa o estoque
                    for (ItemVenda item : venda.getItens()) {
                        // 1. Salva Item
                        try (PreparedStatement psItem = conn.prepareStatement(sqlItem)) {
                            psItem.setInt(1, vendaId);
                            psItem.setInt(2, item.getProduto().getId());
                            psItem.setInt(3, item.getQuantidade());
                            psItem.setDouble(4, item.getProduto().getPreco());
                            psItem.executeUpdate();
                        }

                        // 2. Baixa Estoque
                        try (PreparedStatement psEstoque = conn.prepareStatement(sqlEstoque)) {
                            psEstoque.setInt(1, item.getQuantidade());
                            psEstoque.setInt(2, item.getProduto().getId());
                            psEstoque.executeUpdate();
                        }
                    }
                }
                conn.commit(); // Finaliza tudo com sucesso
            } catch (SQLException e) {
                conn.rollback(); // Se der erro em qualquer parte, desfaz tudo
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao processar transação de venda", e);
        }
    }

    // No seu VendaDAO.java
    public List<Venda> listarHistorico() {
        List<Venda> lista = new ArrayList<>();
        // SQL com JOIN para trazer os nomes de cliente e vendedor de uma vez
        String sql = """
        SELECT v.*, c.nome as nome_cliente, ven.usuario_id, u.nome as nome_vendedor
        FROM venda v
        JOIN cliente c ON v.cliente_id = c.id
        JOIN vendedor ven ON v.vendedor_id = ven.id
        JOIN usuario u ON ven.usuario_id = u.id
        ORDER BY v.id DESC
    """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venda v = new Venda();
                v.setId(rs.getInt("id"));
                v.setTotal(rs.getDouble("total"));

                // Aqui você precisará tratar a String data para LocalDate/LocalTime se desejar
                // Ou apenas ler como String se o seu Model suportar

                Cliente c = new Cliente();
                c.setId(rs.getInt("cliente_id"));
                c.setNome(rs.getString("nome_cliente"));
                v.setCliente(c);

                Vendedor ven = new Vendedor();
                ven.setId(rs.getInt("vendedor_id"));
                // Lógica para setar o nome do vendedor...
                v.setVendedor(ven);

                lista.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void atualizarCadastroVenda(int vendaId, int novoClienteId, int novoVendedorId) {
        String sql = "UPDATE venda SET cliente_id = ?, vendedor_id = ? WHERE id = ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, novoClienteId);
            ps.setInt(2, novoVendedorId);
            ps.setInt(3, vendaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}