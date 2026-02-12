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
import org.example.app.dao.VendaDAO;
import org.example.app.util.Alerta;
import org.example.app.util.BuscaModalController;
import org.example.app.dao.ClienteDAO;
import org.example.app.dao.ProdutoDAO;
import org.example.app.dao.VendedorDAO; // Adicionado
import org.example.app.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VendaController {

    /* =========================================================
       FXML - COMPONENTES
       ========================================================= */

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

    @FXML private ComboBox<String> cbFormaPagamento;
    @FXML private Spinner<Integer> spParcelas;
    @FXML private Label lblValorParcela;


    /* =========================================================
       ESTADO DA TELA
       ========================================================= */

    private Cliente clienteSelecionado;
    private Vendedor vendedorSelecionado;

    private final ObservableList<ItemVenda> itensVenda =
            FXCollections.observableArrayList();

    private final ObservableList<Produto> produtos =
            FXCollections.observableArrayList();


    /* =========================================================
       DAOs
       ========================================================= */

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VendedorDAO vendedorDAO = new VendedorDAO();
    private final VendaDAO vendaDAO = new VendaDAO();


    /* =========================================================
       INITIALIZE
       ========================================================= */

    @FXML
    public void initialize() {

        configurarTabela();
        carregarProdutos();

        tblItens.setItems(itensVenda);

        cbFormaPagamento.getItems().addAll(
                "DINHEIRO",
                "PIX",
                "DÉBITO",
                "CRÉDITO"
        );

        spParcelas.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1)
        );

        spParcelas.setDisable(true);

        spParcelas.valueProperty().addListener((obs, oldVal, newVal) ->
                atualizarParcelas()
        );

        atualizarTotal();
    }


    /* =========================================================
       CLIENTE
       ========================================================= */

    @FXML
    private void pesquisarClienteEnter() {
        abrirGridBuscaCliente();
    }

    @FXML
    private void abrirBuscaCliente() {
        abrirGridBuscaCliente();
    }

    private void abrirGridBuscaCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/BuscaModal.fxml")
            );

            Parent root = loader.load();
            BuscaModalController<Cliente> controller = loader.getController();

            TableColumn<Cliente, String> colNome =
                    new TableColumn<>("Nome");

            colNome.setCellValueFactory(
                    new PropertyValueFactory<>("nome")
            );

            controller.configurar(
                    "BUSCAR CLIENTE",
                    clienteDAO.listar(),
                    List.of(colNome),
                    this::setClienteSelecionado
            );

            abrirModal(root, "Pesquisa de Clientes");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setClienteSelecionado(Cliente cliente) {
        clienteSelecionado = cliente;
        lblClienteSelecionado.setText(cliente.getNome());
        txtBuscaRapidaCliente.clear();
    }


    /* =========================================================
       VENDEDOR
       ========================================================= */

    @FXML
    private void pesquisarVendedorEnter() {
        abrirGridBuscaVendedor();
    }

    @FXML
    private void abrirBuscaVendedor() {
        abrirGridBuscaVendedor();
    }

    private void abrirGridBuscaVendedor() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/BuscaModal.fxml")
            );

            Parent root = loader.load();
            BuscaModalController<Vendedor> controller = loader.getController();

            TableColumn<Vendedor, String> colNome =
                    new TableColumn<>("Nome");

            colNome.setCellValueFactory(
                    new PropertyValueFactory<>("nome")
            );

            controller.configurar(
                    "BUSCAR VENDEDOR",
                    vendedorDAO.listar(),
                    List.of(colNome),
                    this::setVendedorSelecionado
            );

            abrirModal(root, "Pesquisa de Vendedores");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setVendedorSelecionado(Vendedor vendedor) {
        vendedorSelecionado = vendedor;
        lblVendedorSelecionado.setText(vendedor.getNome());
        txtBuscaRapidaVendedor.clear();
    }


    /* =========================================================
       PRODUTO
       ========================================================= */
    @FXML
    private void abrirBuscaProduto() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/BuscaModal.fxml")
            );

            Parent root = loader.load();
            BuscaModalController<Produto> controller =
                    loader.getController();

            TableColumn<Produto, String> colNome =
                    new TableColumn<>("Produto");

            colNome.setCellValueFactory(
                    new PropertyValueFactory<>("nome")
            );

            controller.configurar(
                    "SELECIONAR PRODUTO",
                    produtoDAO.listar(),
                    List.of(colNome),
                    produto -> selecionarProduto(produto)
            );

            abrirModal(root, "Seleção de Produto");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void pesquisarProdutoEnter() {

        String busca = txtBuscaProduto.getText().trim();
        if (busca.isEmpty()) return;

        List<Produto> encontrados = produtos.stream()
                .filter(p -> p.getNome().toLowerCase().contains(busca.toLowerCase())
                        || String.valueOf(p.getId()).equals(busca))
                .toList();

        if (encontrados.size() == 1) {
            selecionarProduto(encontrados.get(0));
        } else {
            Alerta.info("Não encontrado",
                    "Produto não localizado.");
        }
    }

    private void selecionarProduto(Produto p) {
        txtBuscaProduto.setUserData(p);
        txtBuscaProduto.setText(p.getNome());
    }

    @FXML
    private void adicionarItem() {

        Produto produto = (Produto) txtBuscaProduto.getUserData();
        if (produto == null) return;

        int quantidade;

        try {
            quantidade = Integer.parseInt(txtQuantidade.getText());
            if (quantidade <= 0) throw new Exception();
        } catch (Exception e) {
            Alerta.error("Erro", "Quantidade inválida.");
            return;
        }

        itensVenda.add(new ItemVenda(produto, quantidade));

        atualizarTotal();
        limparCamposProduto();
    }


    /* =========================================================
       PAGAMENTO
       ========================================================= */

    @FXML
    private void alterarFormaPagamento() {

        if ("CRÉDITO".equalsIgnoreCase(cbFormaPagamento.getValue())) {
            spParcelas.setDisable(false);
        } else {
            spParcelas.getValueFactory().setValue(1);
            spParcelas.setDisable(true);
        }

        atualizarParcelas();
    }

    private void atualizarParcelas() {

        double total = calcularTotal();

        if ("CRÉDITO".equalsIgnoreCase(cbFormaPagamento.getValue())) {

            int parcelas = spParcelas.getValue();
            double valorParcela = total / parcelas;

            lblValorParcela.setText(
                    String.format("R$ %.2f", valorParcela)
            );

        } else {

            lblValorParcela.setText(
                    String.format("R$ %.2f", total)
            );
        }
    }


    /* =========================================================
       FINALIZAR VENDA
       ========================================================= */

    @FXML
    private void finalizarVenda() {

        if (clienteSelecionado == null ||
                vendedorSelecionado == null) {

            Alerta.info("Venda incompleta",
                    "Selecione cliente e vendedor.");
            return;
        }

        if (itensVenda.isEmpty()) {
            Alerta.info("Venda vazia",
                    "Adicione produtos.");
            return;
        }

        Venda venda = new Venda();
        venda.setCliente(clienteSelecionado);
        venda.setVendedor(vendedorSelecionado);
        venda.setItens(new ArrayList<>(itensVenda));
        venda.setFormaPagamento(cbFormaPagamento.getValue());

        vendaDAO.salvarCompleta(venda);

        Alerta.info("Sucesso",
                "Venda finalizada com sucesso!");

        limparVenda();
    }


    /* =========================================================
       MÉTODOS AUXILIARES
       ========================================================= */

    private void configurarTabela() {

        colProduto.setCellValueFactory(
                d -> new SimpleStringProperty(
                        d.getValue().getProduto().getNome()
                )
        );

        colQtd.setCellValueFactory(
                d -> new SimpleIntegerProperty(
                        d.getValue().getQuantidade()
                ).asObject()
        );

        colPreco.setCellValueFactory(
                d -> new SimpleDoubleProperty(
                        d.getValue().getProduto().getPreco()
                ).asObject()
        );

        colTotal.setCellValueFactory(
                d -> new SimpleDoubleProperty(
                        d.getValue().getTotal()
                ).asObject()
        );
    }

    private void carregarProdutos() {
        produtos.setAll(produtoDAO.listar());
    }

    private double calcularTotal() {
        return itensVenda.stream()
                .mapToDouble(ItemVenda::getTotal)
                .sum();
    }

    private void atualizarTotal() {
        lblValorTotal.setText(
                String.format("R$ %.2f", calcularTotal())
        );
        atualizarParcelas();
    }

    private void limparCamposProduto() {
        txtBuscaProduto.clear();
        txtBuscaProduto.setUserData(null);
        txtQuantidade.setText("1");
    }

    private void limparVenda() {

        itensVenda.clear();
        clienteSelecionado = null;
        vendedorSelecionado = null;

        lblClienteSelecionado.setText("Nenhum selecionado");
        lblVendedorSelecionado.setText("Nenhum selecionado");

        cbFormaPagamento.getSelectionModel().clearSelection();
        spParcelas.getValueFactory().setValue(1);
        spParcelas.setDisable(true);
        lblValorParcela.setText("R$ 0,00");

        atualizarTotal();
    }

    private void abrirModal(Parent root, String titulo) {
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}
