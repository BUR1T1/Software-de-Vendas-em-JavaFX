package org.example.app.controller.usuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.app.dao.UsuarioDAO;
import org.example.app.model.Usuario;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UsuarioController implements Initializable {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtLogin;
    @FXML
    private PasswordField txtSenha;
    @FXML
    private ComboBox<String> cmbPerfil;
    @FXML
    private Label lblMensagem;

    @FXML
    private ComboBox<String> cmbFiltro;
    @FXML
    private TextField txtPesquisar;

    @FXML
    private TableView<Usuario> tblUsuarios;
    @FXML
    private TableColumn<Usuario, Long> colId;
    @FXML
    private TableColumn<Usuario, String> colNome;
    @FXML
    private TableColumn<Usuario, String> colLogin;
    @FXML
    private TableColumn<Usuario, String> colPerfil;

    private final ObservableList<Usuario> dados = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbPerfil.getItems().addAll("ADMIN", "VENDEDOR");
        cmbPerfil.setValue("VENDEDOR");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPerfil.setCellValueFactory(new PropertyValueFactory<>("perfil"));

        cmbFiltro.getItems().addAll("ID", "NOME");
        cmbFiltro.setValue("NOME");

        tblUsuarios.setItems(dados);
        carregarUsuarios();
    }

    @FXML
    public void buscarRelatorio() {
        String filtro = cmbFiltro.getValue();
        String termo = txtPesquisar.getText() == null ? "" : txtPesquisar.getText().trim();

        if (termo.isEmpty()) {
            carregarUsuarios();
            return;
        }

        List<Usuario> todos = usuarioDAO.listarTodos();
        List<Usuario> res = new java.util.ArrayList<>();

        for (Usuario u : todos) {
            if (matches(u, filtro, termo)) {
                res.add(u);
            }
        }

        dados.clear();
        dados.addAll(res);
    }

    private boolean matches(Usuario u, String filtro, String termo) {
        if ("NOME".equals(filtro)) {
            return u.getNome() != null && u.getNome().toLowerCase().contains(termo.toLowerCase());
        }
        return String.valueOf(u.getId()).equals(termo);
    }

    private void carregarUsuarios() {
        dados.clear();
        List<Usuario> lista = usuarioDAO.listarTodos();
        dados.addAll(lista);
    }

    @FXML
    public void salvar() {

        if (txtNome.getText().isEmpty()
                || txtLogin.getText().isEmpty()
                || txtSenha.getText().isEmpty()
                || cmbPerfil.getValue() == null) {

            lblMensagem.setText("Preencha todos os campos.");
            return;
        }

        // Verifica se o login jÃƒÆ’Ã‚Â¡ existe
        if (usuarioDAO.buscarPorLogin(txtLogin.getText().trim()) != null) {
            lblMensagem.setText("já existe um usuário com este login.");
            return;
        }

        Usuario usuario = new Usuario(
                txtNome.getText().trim(),
                txtLogin.getText().trim(),
                txtSenha.getText(),
                cmbPerfil.getValue()
        );

        usuarioDAO.salvar(usuario);

        lblMensagem.setText("Usuário salvo com sucesso.");

        txtNome.clear();
        txtLogin.clear();
        txtSenha.clear();
        cmbPerfil.setValue("VENDEDOR");

        carregarUsuarios();
    }

    @FXML
    public void limpar() {
        txtNome.clear();
        txtLogin.clear();
        txtSenha.clear();
        cmbPerfil.setValue("VENDEDOR");
        lblMensagem.setText("");
    }
}
