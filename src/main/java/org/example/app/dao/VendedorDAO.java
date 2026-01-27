package org.example.app.dao;

import org.example.app.database.ConexaoSQLite;
import org.example.app.model.Vendedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendedorDAO {

    public void salvar(Vendedor vendedor) {
        String sql = "INSERT INTO vendedor (nome, comissao, CPF,status) VALUES (?, ?, ?, 1)";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vendedor.getNome());
            ps.setDouble(2, vendedor.getComissao());
            ps.setInt(3,vendedor.getCPF());
            ps.executeUpdate();

        } catch (Exception e) {
            throw  new RuntimeException("CPF de vendedor já cadastrado no sistema");
        }
    }

    public void inativar(int id){
        String sql = "UPDATE vendedor SET status = 2 WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
        PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1,id);
            ps.executeUpdate();
        } catch (Exception e){
            throw new RuntimeException("Erro ao inativar vendedor", e);        }
    }

    public void reativar(List<Integer> ids) {
        String sql = "UPDATE vendedor SET status = 1 WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Integer id : ids) {
                ps.setInt(1, id);
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao ativar vendedor", e);        }
    }



    public List<Vendedor> listar() {
        List<Vendedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM vendedor";

        try (Connection conn = ConexaoSQLite.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Vendedor v = new Vendedor();
                v.setId(rs.getInt("id"));
                v.setNome(rs.getString("nome"));
                v.setCPF(rs.getInt("CPF"));
                v.setComissao(rs.getDouble("comissao"));
                lista.add(v);
            }

        } catch (Exception e) {
            throw new RuntimeException(" Nenhum vendedor foi cadastrado !!!");
        }

        return lista;
    }

    public void atualizar(Vendedor vendedor) {
        String sql = "UPDATE vendedor SET nome = ?, comissao = ?, CPF = ? WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vendedor.getNome());
            ps.setDouble(2, vendedor.getComissao());
            ps.setInt(3,vendedor.getCPF());
            ps.setInt(4, vendedor.getId());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Vendedor> ListarAtivos() {
        String sql = "SELECT * FROM vendedor WHERE status = 1";
        List<Vendedor> list = new ArrayList<>();

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Vendedor(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("comissao"),
                        rs.getInt("CPF"),
                        rs.getInt("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
