# TODO - Preparar ambiente para rodar o código

- [x] 1. Corrigir `pom.xml` (mainClass do plugin JavaFX -> `org.example.app.MainApp`)
- [x] 2. Compilar o projeto com Maven (`mvn clean compile`)
- [x] 3. Rodar a aplicação JavaFX (`mvn javafx:run`)
- [x] 4. Verificar inicialização do banco SQLite (`loja.db`)

# TODO - Fluxo de Pedidos

- [x] 5. Dependência Gson + `requires com.google.gson` no module-info
- [x] 6. Modelos `ItemPedido` e `Pedido`
- [x] 7. Correção do `DatabaseInit` (tabelas `pedido`/`item_pedido`)
- [x] 8. `PedidosDAO` (salvar, listar, concluir, cancelar)
- [x] 9. `PedidoEventBus` (notifica interface)
- [x] 10. `PedidoConsumer` com parse Gson -> salvar -> notificar

# TODO - Feedback: Consumer automático + Fluxo de Usuário

- [x] 11. Reativar acionamento automático do consumer no `MainApp.start()`
- [x] 12. Remover botão manual "Iniciar Consumidor" do menu
- [x] 13. Adicionar campo `nome` ao modelo `Usuario`
- [x] 14. Corrigir `UsuarioDAO` (salvar com nome, autenticar completo, listarTodos)
- [x] 15. Integrar `UsuarioController` ao banco (persistência + TableView)
- [x] 16. Criar usuário admin padrão (`admin`/`123`) automaticamente no `DatabaseInit`
- [x] 17. `LoginController` validar usando `UsuarioDAO` (remover login mock)

# TODO - Aplicar tema escuro (branco + laranja)

- [x] login.fxml
- [x] principal.fxml
- [x] main_shell.fxml
- [x] Usuario.fxml
- [x] BuscaModal.fxml
- [x] Cliente.fxml / ClienteForm.fxml
- [x] Produto.fxml / ProdutoForm.fxml
- [x] Venda.fxml / venda-historico.fxml / pedido-historico.fxml
- [x] Vendedor.fxml / VendedorForm.fxml
- [x] Compilar e verificar

