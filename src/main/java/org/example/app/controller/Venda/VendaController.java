package org.example.app.controller.venda;


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
import javafx.scene.control.cell.PropertyValueFactory; 
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.app.consumer.PedidoEventBus;
import org.example.app.dao.PedidosDAO;
import org.example.app.dao.VendaDAO;
import org.example.app.util.Alerta;
import org.example.app.util.BuscaModalController;
import org.example.app.dao.ClienteDAO;
import org.example.app.dao.ProdutoDAO;
import org.example.app.dao.VendedorDAO; 
import org.example.app.dao.offline.OfflineCatalogoDAO;
import org.example.app.dao.offline.OfflineVendaDAO;
import org.example.app.dao.postgres.PostgresVendaDAO;
import org.example.app.service.ConnectivityService;
import org.example.app.model.*;
import java.util.ArrayList;

import java.util.List;



public class VendaController {

    /* =========================================================
       FXML - COMPONENTES
       ========================================================= */
    @FXML
    private TextField txtBuscaProduto;
    @FXML
    private TextField txtQuantidade;

    @FXML
    private TableView<ItemVenda> tblItens;
    @FXML
    private TableColumn<ItemVenda, String> colProduto;
    @FXML
    private TableColumn<ItemVenda, Integer> colQtd;
    @FXML
    private TableColumn<ItemVenda, Double> colPreco;
    @FXML
    private TableColumn<ItemVenda, Double> colTotal;

    @FXML
    private Label lblValorTotal;

    @FXML
    private TextField txtBuscaRapidaCliente;
    @FXML
    private Label lblClienteSelecionado;

    @FXML
    private TextField txtBuscaRapidaVendedor;
    @FXML
    private Label lblVendedorSelecionado;

    @FXML
    private ComboBox<String> cbFormaPagamento;
    @FXML
    private Spinner<Integer> spParcelas;
    @FXML
    private Label lblValorParcela;

    @FXML
    private Button btnPedidos;
    @FXML
    private Label lblBadgePedidos;

    private final PedidosDAO pedidosDAO = new PedidosDAO();
    private Pedido pedidoSelecionado;

    /* =========================================================
       ESTADO DA TELA
       ========================================================= */
    private Cliente clienteSelecionado;
    private Vendedor vendedorSelecionado;

    private final ObservableList<ItemVenda> itensVenda
            = FXCollections.observableArrayList();

    private final ObservableList<Produto> produtos
            = FXCollections.observableArrayList();


    /* =========================================================
       DAOs
       ========================================================= */
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VendedorDAO vendedorDAO = new VendedorDAO();
    private final VendaDAO vendaDAO = new VendaDAO();

    private final OfflineCatalogoDAO offlineCatalogoDAO = new OfflineCatalogoDAO();
    private final OfflineVendaDAO offlineVendaDAO = new OfflineVendaDAO();
    private final PostgresVendaDAO postgresVendaDAO = new PostgresVendaDAO();


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
                "DÃƒâ€°BITO",
                "CRÃƒâ€°DITO"
        );

        spParcelas.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1)
        );

        spParcelas.setDisable(true);

        spParcelas.valueProperty().addListener((obs, oldVal, newVal)
                -> atualizarParcelas()
        );

        atualizarTotal();
        configurarPedidos();
    }

    /* =========================================================
       PEDIDOS (INTEGRAÃƒâ€¡ÃƒÆ’O COM FILA / RABBITMQ)
       ========================================================= */
    private void configurarPedidos() {
        if (btnPedidos != null) {
            btnPedidos.setVisible(true);
        }
        atualizarBadgePedidos();

        PedidoEventBus.registrar(pedido -> {
            javafx.application.Platform.runLater(this::atualizarBadgePedidos);
        });
    }

    private void atualizarBadgePedidos() {
        if (lblBadgePedidos == null) {
            return;
        }

        // Considera pedidos pendentes em memÃƒÂ³ria + pendentes no banco
        int pendentes = PedidoEventBus.getPedidosPendentes().size();
        try {
            pendentes += pedidosDAO.listarPendentes().size();
        } catch (Exception ignored) {
        }

        int total = pendentes;
        lblBadgePedidos.setText(String.valueOf(total));

        boolean temPendente = total > 0;
        lblBadgePedidos.setVisible(temPendente);
        lblBadgePedidos.setManaged(temPendente);
    }

    @FXML
    private void abrirPedidos() {
        atualizarBadgePedidos();

        List<Pedido> pendentes = pedidosDAO.listarPendentes();

        if (pedidoSelecionado == null && !PedidoEventBus.getPedidosPendentes().isEmpty()) {
            pedidoSelecionado = PedidoEventBus.getPedidosPendentes().get(0);
        }

        if (pendentes.isEmpty() && pedidoSelecionado == null) {
            Alerta.info("Sem pedidos",
                    "Nenhum pedido pendente para ser aplicado como venda.");
            return;
        }

        List<Pedido> candidatos = pendentes.isEmpty()
                ? List.of(pedidoSelecionado)
                : pendentes;

        Pedido escolhido = selecionarPedidoModal(candidatos);
        if (escolhido != null) {
            aplicarPedidoNaVenda(escolhido);
        }
    }

    private Pedido selecionarPedidoModal(List<Pedido> pedidos) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/view/BuscaModal.fxml")
            );

            Parent root = loader.load();
            BuscaModalController<Pedido> controller = loader.getController();

            TableColumn<Pedido, String> colId = new TableColumn<>("#");
            colId.setCellValueFactory(d -> new SimpleStringProperty(
                    String.valueOf(d.getValue().getId())));

            TableColumn<Pedido, String> colCliente = new TableColumn<>("Cliente");
            colCliente.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().getCliente().getNome()));

            TableColumn<Pedido, String> colVendedor = new TableColumn<>("Vendedor");
            colVendedor.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().getVendedor().getNome()));

            TableColumn<Pedido, String> colTotal = new TableColumn<>("Total");
            colTotal.setCellValueFactory(d -> new SimpleStringProperty(
                    String.format("R$ %.2f", d.getValue().getTotal())));

            Pedido[] resultado = new Pedido[1];

            controller.configurar(
                    "SELECIONAR PEDIDO",
                    pedidos,
                    List.of(colId, colCliente, colVendedor, colTotal),
                    p -> resultado[0] = p
            );

            abrirModal(root, "Pedidos Pendentes");
            return resultado[0];

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void aplicarPedidoNaVenda(Pedido pedido) {
        // Carrega cliente e vendedor
        clienteSelecionado = pedido.getCliente();
        lblClienteSelecionado.setText(clienteSelecionado.getNome());

        vendedorSelecionado = pedido.getVendedor();
        lblVendedorSelecionado.setText(vendedorSelecionado.getNome());

        // Limpa e adiciona os itens
        itensVenda.clear();
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            if (produto == null) {
                continue;
            }
            ItemVenda iv = new ItemVenda(produto, item.getQuantidade());
            // Usa o preço unitário do item do pedido para preservar o valor original
            if (item.getPrecoUnitario() > 0) {
                iv.getProduto().setPreco(item.getPrecoUnitario());
            }
            itensVenda.add(iv);
        }

        atualizarTotal();

        // Remove p/ restante e marca como processado
        pedidoSelecionado = null;
        PedidoEventBus.removerPedido(pedido);
        try {
            pedidosDAO.marcarConcluido(pedido.getId());
        } catch (Exception ex) {
            // se nÃƒÂ£o tem id ainda, segue sem marcar
        }

        atualizarBadgePedidos();
        Alerta.info("Pedido aplicado",
                "Pedido carregado na venda com sucesso!");
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

            TableColumn<Cliente, String> colNome
                    = new TableColumn<>("Nome");

            colNome.setCellValueFactory(
                    new PropertyValueFactory<>("nome")
            );

            List<Cliente> clientes = ConnectivityService.estaOnline()
                    ? clienteDAO.listar()
                    : offlineCatalogoDAO.listarClientes();

            controller.configurar(
                    "BUSCAR CLIENTE",
                    clientes,
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

            TableColumn<Vendedor, String> colNome
                    = new TableColumn<>("Nome");

            colNome.setCellValueFactory(
                    new PropertyValueFactory<>("nome")
            );

            List<Vendedor> vendedores = ConnectivityService.estaOnline()
                    ? vendedorDAO.listar()
                    : offlineCatalogoDAO.listarVendedores();

            controller.configurar(
                    "BUSCAR VENDEDOR",
                    vendedores,
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
            BuscaModalController<Produto> controller
                    = loader.getController();

            TableColumn<Produto, String> colNome
                    = new TableColumn<>("Produto");

            colNome.setCellValueFactory(
                    new PropertyValueFactory<>("nome")
            );

            TableColumn<Produto, Integer> colEstoque
                    = new TableColumn<>("Estoque");

            colEstoque.setCellValueFactory(
                    new PropertyValueFactory<>("estoque")
            );

            TableColumn<Produto, Double> colPreco
                    = new TableColumn<>("Valor");
            colPreco.setCellValueFactory(
                    new PropertyValueFactory<>("preco")
            );

            controller.configurar(
                    "SELECIONAR PRODUTO",
                    produtoDAO.listar(),
                    List.of(colNome, colEstoque, colPreco),
                    produto -> selecionarProduto(produto)
            );

            // PrÃƒÂ©-filtra o modal com o termo digitado no campo de busca da tela de venda
            String termo = txtBuscaProduto.getText();
            if (termo != null && !termo.trim().isEmpty()) {
                controller.setFiltroInicial(termo.trim());
            }

            abrirModal(root, "SeleÃƒÂ§ÃƒÂ£o de Produto");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

@FXML
    private void pesquisarProdutoEnter() {

        String busca = txtBuscaProduto.getText();
        if (busca == null || busca.trim().isEmpty()) {
            return;
        }

        // Sempre abre a tela de seleÃƒÂ§ÃƒÂ£o de produtos para garantir que
        // o usuÃƒÂ¡rio visualize e escolha o produto (mesmo com 1 resultado).
        abrirBuscaProduto();
    }

    private void selecionarProduto(Produto p) {
        txtBuscaProduto.setUserData(p);
        txtBuscaProduto.setText(p.getNome());
    }

    @FXML
    private void adicionarItem() {

        Produto produto = (Produto) txtBuscaProduto.getUserData();
        if (produto == null) {
            return;
        }

        int quantidade;

        try {
            quantidade = Integer.parseInt(txtQuantidade.getText());
            if (quantidade <= 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            Alerta.error("Erro", "Quantidade invÃƒÂ¡lida.");
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

        if ("CRÃƒâ€°DITO".equalsIgnoreCase(cbFormaPagamento.getValue())) {
            spParcelas.setDisable(false);
        } else {
            spParcelas.getValueFactory().setValue(1);
            spParcelas.setDisable(true);
        }

        atualizarParcelas();
    }

    private void atualizarParcelas() {

        double total = calcularTotal();

        if ("CRÃƒâ€°DITO".equalsIgnoreCase(cbFormaPagamento.getValue())) {

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

        if (clienteSelecionado == null
                || vendedorSelecionado == null) {

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

        // Verifica conexÃƒÂ£o: ONLINE -> Postgres | OFFLINE -> SQLite offline
        boolean online = ConnectivityService.estaOnline();

        if (online) {
            try {
                postgresVendaDAO.salvarCompleta(venda);
                Alerta.info("Sucesso",
                        "Venda finalizada e registrada no servidor (online)!");
            } catch (Exception e) {
                Alerta.error("Erro",
                        "NÃƒÂ£o foi possÃƒÂ­vel registrar a venda no servidor. Verifique a conexÃƒÂ£o.");
                e.printStackTrace();
                return;
            }
        } else {
            try {
                offlineVendaDAO.salvarCompleta(venda);
                Alerta.info("Venda offline",
                        "Venda registrada localmente e aguardando sincronizaÃƒÂ§ÃƒÂ£o!");
            } catch (Exception e) {
                Alerta.error("Erro",
                        "NÃƒÂ£o foi possÃƒÂ­vel salvar a venda offline.");
                e.printStackTrace();
                return;
            }
        }

        limparVenda();
    }

    /* =========================================================
       MÃƒâ€°TODOS AUXILIARES
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
        if (ConnectivityService.estaOnline()) {
            produtos.setAll(produtoDAO.listar());
        } else {
            produtos.setAll(offlineCatalogoDAO.listarProdutos());
        }
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
        stage.showAndWait();
    }
}
