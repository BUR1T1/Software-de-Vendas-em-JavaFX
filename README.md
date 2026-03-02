# Sistema de Gestão de Vendas em JavaFX

Sistema desktop de gestão de vendas desenvolvido em **Java 17**, **JavaFX** e **SQLite**, com foco em organização arquitetural, boas práticas e modelagem relacional.

---

## 📌 Sobre o Projeto

A aplicação permite o gerenciamento completo de:

- Usuários (Admin e Vendedor)
- Vendedores
- Clientes
- Produtos
- Vendas
- Itens da venda
- Histórico de vendas com detalhamento

As vendas são registradas utilizando `LocalDateTime`, garantindo controle preciso de data e hora.  
O sistema possui auditoria com `created_at`, `updated_at` e controle de `status` (ativo/inativo).

O banco de dados é inicializado automaticamente na execução.

---

## 🛠 Tecnologias Utilizadas

- Java 17
- JavaFX 17
- SQLite
- JDBC
- Maven

---

## 🏗 Estrutura do Projeto

O projeto foi organizado com separação de responsabilidades:

- `model` → Entidades do sistema  
- `dao` → Acesso ao banco de dados  
- `controller` → Controle das telas JavaFX  
- `database` → Inicialização e configuração do banco  
- `BaseEntity` → Classe base com auditoria e controle de status  

---

## 📊 Funcionalidades

- Cadastro, edição e listagem de clientes
- Cadastro e controle de produtos com estoque
- Cadastro de vendedores com comissão
- Registro de vendas com:
  - Forma de pagamento
  - Parcelas
  - Valor total
  - Associação com cliente e vendedor
- Histórico de vendas com detalhamento
- Controle de integridade com chaves estrangeiras

---

## 🎯 Objetivo

Aplicar conceitos reais de desenvolvimento de sistemas comerciais, incluindo:

- Modelagem de banco relacional
- Manipulação de `LocalDateTime`
- Arquitetura em camadas
- Boas práticas de organização e padronização
- Evolução de schema no SQLite

