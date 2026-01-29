package org.example.app.dao;

import org.example.app.database.ConexaoSQLite;
import org.example.app.model.Usuario;

import java.sql.*;

public class UsuarioDAO {

    public void salvar(Usuario usuario) {
        String sql = """
            INSERT INTO usuario (login, senha, perfil)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getLogin());
            ps.setString(2, usuario.getSenha());
            ps.setString(3, usuario.getPerfil());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Usuario autenticar(String login, String senha) {
        String sql = """
            SELECT * FROM usuario
            WHERE login = ? AND senha = ?
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, senha);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario(null,null,null);

                u.setLogin(rs.getString("login"));
                u.setPerfil(rs.getString("perfil"));
                return u;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

