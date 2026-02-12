package org.example.app.controller.Cliente;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.app.dao.ClienteDAO;
import org.example.app.model.Cliente;
import org.example.app.util.Alerta;

import java.util.List;

public class ClienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtTelefone;

    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, Long> colId;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, String> colCpf;
    @FXML private TableColumn<Cliente, String> colTelefone;

    @FXML private TableView<Cliente> tabelaInativos;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private Cliente clienteSelecionado;

    @FXML public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        carregarTabelas();
    }



    @FXML
    private void salvar() {
        if (txtNome.getText().isEmpty() || txtCpf.getText().isEmpty()) {
            Alerta.info("Validação", "Nome e CPF são obrigatórios.");
            return;
        }

        if (clienteSelecionado == null) {
            // NOVO CLIENTE
            Cliente c = new Cliente(
                    txtNome.getText(),
                    txtCpf.getText(),
                    txtTelefone.getText(),
                    1
            );
            // O createdAt já é gerado no construtor da BaseEntity automaticamente
            clienteDAO.salvar(c);
        } else {
            // ATUALIZAÇÃO DE CLIENTE EXISTENTE
            clienteSelecionado.setNome(txtNome.getText());
            clienteSelecionado.setCpf(txtCpf.getText());
            clienteSelecionado.setTelefone(txtTelefone.getText());

            // REGRA NOVA: Atualiza o timestamp de updatedAt
            clienteSelecionado.markAsUpdated();

            clienteDAO.atualizar(clienteSelecionado);
        }

        limpar();
        carregarTabelas();
    }

    @FXML
    private void fecharFormulario() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void novoCadastro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/Cliente-Views/ClienteForm.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Novo Cliente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            carregarTabelas();
        } catch (Exception e) {
            e.printStackTrace();
            Alerta.error("Erro", "Não foi possível abrir o formulário.");
        }
    }

    @FXML
    private void editar() {
        Cliente c = tabelaClientes.getSelectionModel().getSelectedItem();
        if (c == null) {
            Alerta.info("Seleção", "Selecione um cliente para editar.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/Cliente-Views/ClienteForm.fxml")
            );
            Parent root = loader.load();
            ClienteFormController controller = loader.getController();
            controller.setCliente(c);

            Stage stage = new Stage();
            stage.setTitle("Editar Cliente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            carregarTabelas();
        } catch (Exception e) {
            e.printStackTrace();
            Alerta.error("Erro", "Não foi possível abrir o formulário.");
        }
    }


    @FXML
    private void inativarCliente() {
        Cliente c = tabelaClientes.getSelectionModel().getSelectedItem();
        if (c != null) {
            clienteDAO.inativar(c.getId());
            carregarTabelas();
        } else {
            Alerta.info("Seleção", "Selecione um cliente para inativar.");
        }
    }

    private void carregarTabelas() {
        tabelaClientes.setItems(FXCollections.observableArrayList(clienteDAO.listarAtivos()));
        tabelaInativos.setItems(FXCollections.observableArrayList(clienteDAO.listarInativos())); }

    @FXML
    private void reativar() {
        List<Long> ids = tabelaInativos.getSelectionModel()
                .getSelectedItems()
                .stream()
                .map(Cliente::getId)
                .toList();

        if (!ids.isEmpty()) {
            clienteDAO.reativar(ids);
            carregarTabelas(); // Recarrega para ver os reativados
        } else {
            Alerta.info("Seleção", "Selecione ao menos um cliente para reativar.");
        }
    }

    private void selecionar(Cliente c) {
        if (c != null) {
            clienteSelecionado = c;
            txtNome.setText(c.getNome());
            txtCpf.setText(c.getCpf());
            txtTelefone.setText(c.getTelefone());
        }
    }

    private void limpar() {
        txtNome.clear();
        txtCpf.clear();
        txtTelefone.clear();
        clienteSelecionado = null;
        tabelaClientes.getSelectionModel().clearSelection();
    }
}
