package org.example.app.controller.Venda;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // Adicionado
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.app.controller.BuscaModalController;
import org.example.app.dao.ClienteDAO;
import org.example.app.dao.ProdutoDAO;
import org.example.app.dao.VendedorDAO; // Adicionado
import org.example.app.model.*;

import java.io.IOException;
import java.util.List;

public class VendaController {

    @FXML private TextField txtBuscaProduto;
    @FXML private TextField txtQuantidade;
    @FXML private TableView<ItemVenda> tblItens;
    @FXML private TableColumn<ItemVenda, String> colProduto;
    @FXML private TableColumn<ItemVenda, Integer> colQtd;
    @FXML private TableColumn<ItemVenda, Double> colPreco;
    @FXML private TableColumn<ItemVenda, Double> colTotal;

    @FXML private Label lblValorTotal;
    @FXML private TextField txtBuscaRapidaCliente;
    @FXML private Label lblClienteSelecionado;
    @FXML private TextField txtBuscaRapidaVendedor;
    @FXML private Label lblVendedorSelecionado;

    // ===== ESTADO DA TELA =====
    private Cliente clienteSelecionado;
    private Vendedor vendedorSelecionado;
    private final ObservableList<ItemVenda> itensVenda = FXCollections.observableArrayList();
    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();

    // Instâncias dos DAOs (Corrigem o erro de static)
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VendedorDAO vendedorDAO = new VendedorDAO();

    @FXML
    public void initialize() {
        configurarTabela();
        carregarProdutos();
        tblItens.setItems(itensVenda);
        atualizarTotal();
    }

    // ===== LÓGICA DE CLIENTE =====
    @FXML
    private void pesquisarClienteEnter() {
        abrirGridBuscaCliente(txtBuscaRapidaCliente.getText());
    }

    @FXML
    private void abrirBuscaCliente() {
        abrirGridBuscaCliente("");
    }

    private void abrirGridBuscaCliente(String filtro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/BuscaModal.fxml"));
            Parent root = loader.load();

            BuscaModalController<Cliente> controller = loader.getController();

            // Criando colunas para o Grid de Clientes
            TableColumn<Cliente, String> colNome = new TableColumn<>("Nome");
            colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

            // Usando a instância 'clienteDAO' para listar (Resolve o erro static)
            List<Cliente> clientes = clienteDAO.listar();

            controller.configurar("BUSCAR CLIENTE", clientes, List.of(colNome), cliente -> {
                setClienteSelecionado(cliente);
            });

            abrirStageModal(root, "Pesquisa de Clientes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setClienteSelecionado(Cliente cliente) {
        this.clienteSelecionado = cliente;
        lblClienteSelecionado.setText(cliente.getNome());
        lblClienteSelecionado.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");
        txtBuscaRapidaCliente.clear();
    }

    // ===== LÓGICA DE VENDEDOR =====
    @FXML
    private void pesquisarVendedorEnter() {
        abrirGridBuscaVendedor(txtBuscaRapidaVendedor.getText());
    }

    @FXML
    private void abrirBuscaVendedor() {
        abrirGridBuscaVendedor("");
    }

    private void abrirGridBuscaVendedor(String filtro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/BuscaModal.fxml"));
            Parent root = loader.load();

            BuscaModalController<Vendedor> controller = loader.getController();

            TableColumn<Vendedor, String> colNome = new TableColumn<>("Nome");
            colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

            List<Vendedor> vendedores = vendedorDAO.listar();

            controller.configurar("BUSCAR VENDEDOR", vendedores, List.of(colNome), vendedor -> {
                setVendedorSelecionado(vendedor);
            });

            abrirStageModal(root, "Pesquisa de Vendedores");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVendedorSelecionado(Vendedor vendedor) {
        this.vendedorSelecionado = vendedor;
        lblVendedorSelecionado.setText(vendedor.getNome());
        lblVendedorSelecionado.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");
        txtBuscaRapidaVendedor.clear();
    }

    // ===== MÉTODO AUXILIAR PARA ABRIR JANELAS =====
    private void abrirStageModal(Parent root, String titulo) {
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    // [Restante dos métodos: pesquisarProdutoEnter, adicionarItem, finalizarVenda, etc, permanecem iguais]

    @FXML
    private void pesquisarProdutoEnter() {
        String busca = txtBuscaProduto.getText().trim();
        if (busca.isEmpty()) return;

        List<Produto> encontrados = produtos.stream()
                .filter(p -> p.getNome().toLowerCase().contains(busca.toLowerCase()) ||
                        String.valueOf(p.getId()).equals(busca))
                .toList();

        if (encontrados.size() == 1) {
            selecionarProduto(encontrados.get(0));
            txtQuantidade.requestFocus();
        } else if (encontrados.size() > 1) {
            abrirGridBuscaProduto(encontrados);
        } else {
            alerta("Não encontrado", "Nenhum produto com o termo: " + busca, Alert.AlertType.WARNING);
            txtBuscaProduto.selectAll();
        }
    }

    private void selecionarProduto(Produto p) {
        txtBuscaProduto.setUserData(p);
        txtBuscaProduto.setText(p.getNome());
        txtBuscaProduto.setStyle("-fx-border-color: #22c55e; -fx-border-width: 2; -fx-background-radius: 8;");
    }

    private void abrirGridBuscaProduto(List<Produto> lista) {
        // Implementação futura similar à de clientes/vendedores
        System.out.println("Abrindo grid para escolher entre " + lista.size() + " produtos.");
    }

    @FXML
    private void adicionarItem() {
        Produto produto = (Produto) txtBuscaProduto.getUserData();
        if (produto == null) {
            pesquisarProdutoEnter();
            produto = (Produto) txtBuscaProduto.getUserData();
            if (produto == null) return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(txtQuantidade.getText());
            if (quantidade <= 0) throw new Exception();
        } catch (Exception e) {
            alerta("Erro", "Quantidade inválida.", Alert.AlertType.ERROR);
            return;
        }

        boolean existe = false;
        for (ItemVenda item : itensVenda) {
            if (item.getProduto().getId() == produto.getId()) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                existe = true;
                break;
            }
        }

        if (!existe) {
            itensVenda.add(new ItemVenda(produto, quantidade));
        }

        tblItens.refresh();
        atualizarTotal();
        limparCamposProduto();
        txtBuscaProduto.requestFocus();
    }

    @FXML
    private void finalizarVenda() {
        if (clienteSelecionado == null || vendedorSelecionado == null) {
            alerta("Venda incompleta", "Selecione cliente e vendedor.", Alert.AlertType.WARNING);
            return;
        }
        if (itensVenda.isEmpty()) {
            alerta("Venda vazia", "Adicione produtos.", Alert.AlertType.WARNING);
            return;
        }

        alerta("Sucesso", "Venda finalizada para " + clienteSelecionado.getNome(), Alert.AlertType.INFORMATION);
        itensVenda.clear();
        clienteSelecionado = null;
        vendedorSelecionado = null;
        lblClienteSelecionado.setText("Nenhum selecionado");
        lblVendedorSelecionado.setText("Nenhum selecionado");
        atualizarTotal();
    }

    private void configurarTabela() {
        colProduto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProduto().getNome()));
        colQtd.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantidade()).asObject());
        colPreco.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getProduto().getPreco()).asObject());
        colTotal.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getTotal()).asObject());
    }

    private void carregarProdutos() {
        produtos.setAll(produtoDAO.listar());
    }

    private void atualizarTotal() {
        double total = itensVenda.stream().mapToDouble(ItemVenda::getTotal).sum();
        lblValorTotal.setText(String.format("R$ %.2f", total));
    }

    private void limparCamposProduto() {
        txtBuscaProduto.clear();
        txtBuscaProduto.setUserData(null);
        txtBuscaProduto.setStyle("");
        txtQuantidade.setText("1");
    }

    private void alerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensagem);
        a.showAndWait();
    }
}