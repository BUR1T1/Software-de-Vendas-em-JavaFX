package org.example.app.service;

import java.util.List;

import org.example.app.dao.ClienteDAO;
import org.example.app.model.Cliente;
import org.example.app.model.Venda;


import org.example.app.dao.offline.OfflineCatalogoDAO;
import org.example.app.dao.offline.OfflineVendaDAO;
import org.example.app.dao.postgres.PostgresCatalogoDAO;
import org.example.app.dao.postgres.PostgresVendaDAO;

public class SyncService {

    
    private static final OfflineCatalogoDAO catalogoDAO = new OfflineCatalogoDAO();
    private static final OfflineVendaDAO vendasOfflineDAO = new OfflineVendaDAO();
    private static final PostgresCatalogoDAO catalogoPostgres = new PostgresCatalogoDAO();
    private static final PostgresVendaDAO vendasPostgres = new PostgresVendaDAO();
    private static final ClienteDAO clienteDAO = new ClienteDAO();

    private SyncService() {
    }

    public static int sincronizar() {
        if (!ConnectivityService.estaOnline()) {
            throw new IllegalStateException("Sem conexão com o servidor. não foi possível sincronizar agora.");
        }

        int total = 0;

        total += sincronizarClientesPendentes();
        atualizarCatalogoOffline();
        total += sincronizarVendasPendentes();

        return total;
    }

    private static int sincronizarClientesPendentes() {
        List<Cliente> pendentes = clienteDAO.listarPendentes();
        int contador = 0;

        for (Cliente cliente : pendentes) {
            Long idLocal = cliente.getId();
            try {
                if (idLocal == null) {
                    catalogoPostgres.salvarCliente(cliente);
                } else {
                    Long idNuvem = catalogoPostgres.salvarClienteClienteExistente(cliente);
                    clienteDAO.atualizarIdAposSync(idLocal, idNuvem);
                }
                contador++;
            } catch (Exception e) {
                System.err.println("Erro ao sincronizar cliente offline #" + idLocal);
                e.printStackTrace();
            }
        }

        return contador;
    }

    private static void atualizarCatalogoOffline() {
        catalogoDAO.limparClientes();
        catalogoDAO.limparVendedores();
        catalogoDAO.limparProdutos();

        for (var c : catalogoPostgres.listarClientes()) {
            catalogoDAO.salvarCliente(c);
        }
        for (var v : catalogoPostgres.listarVendedores()) {
            catalogoDAO.salvarVendedor(v);
        }
        for (var p : catalogoPostgres.listarProdutos()) {
            catalogoDAO.salvarProduto(p);
        }
    }

    private static int sincronizarVendasPendentes() {
        List<Venda> pendentes = vendasOfflineDAO.listarPendentes();
        int contador = 0;

        for (Venda venda : pendentes) {
            try {
                vendasPostgres.salvarCompleta(venda);
                vendasOfflineDAO.marcarSincronizada(venda.getId());
                contador++;
            } catch (Exception e) {
                System.err.println("Erro ao sincronizar venda offline #" + venda.getId());
                e.printStackTrace();
            }
        }

        return contador;
    }

    public static int contarPendentes() {
        try {
            return vendasOfflineDAO.contarPendentes();
        } catch (Exception e) {
            return 0;
        }
    }
}
