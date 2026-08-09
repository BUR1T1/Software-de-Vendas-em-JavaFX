package org.example.app.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.example.app.service.ConnectivityService;
import org.example.app.service.SyncService;
import org.example.app.util.Alerta;

import javafx.concurrent.Task;

/**
 * Controlador do bloco de sincronização exibido no canto do menu.
 * Permite:
 *  - Exibir o indicador de conexão (ONLINE / OFFLINE)
 *  - Exibir o número de vendas pendentes de sincronização
 *  - Disparar a sincronização manualmente
 */
public class SyncController {

    @FXML private Label lblStatusConexao;
    @FXML private Label lblPendentes;
    @FXML private Button btnSincronizar;

    public void initialize() {
        atualizarIndicadores();
    }

    /**
     * Atualiza o indicador de conexão e o contador de pendências.
     */
    public void atualizarIndicadores() {
        boolean online = ConnectivityService.estaOnline();
        int pendentes = SyncService.contarPendentes();

        if (lblStatusConexao != null) {
            if (online) {
                lblStatusConexao.setText("● ONLINE");
                lblStatusConexao.setStyle("-fx-text-fill: #4ADE80; -fx-font-weight: bold;");
            } else {
                lblStatusConexao.setText("● OFFLINE");
                lblStatusConexao.setStyle("-fx-text-fill: #F87171; -fx-font-weight: bold;");
            }
        }

        if (lblPendentes != null) {
            if (pendentes > 0) {
                lblPendentes.setText("Sincronizações pendentes: " + pendentes);
                lblPendentes.setStyle("-fx-text-fill: #FACC15; -fx-font-weight: bold;");
            } else {
                lblPendentes.setText("Sem pendências");
                lblPendentes.setStyle("-fx-text-fill: #94A3B8;");
            }
        }

        if (btnSincronizar != null) {
            boolean temPendencia = pendentes > 0;
            btnSincronizar.setDisable(!online || !temPendencia);
        }
    }

    /**
     * Ação do botão de sincronização.
     * Executa a sincronização em background para não travar a interface.
     */
    @FXML
    private void sincronizar() {
        if (!ConnectivityService.estaOnline()) {
            Alerta.error("Sem conexão",
                    "O sistema está offline. Conecte-se à internet para sincronizar.");
            return;
        }

        btnSincronizar.setDisable(true);
        btnSincronizar.setText("Sincronizando...");

        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() {
                return SyncService.sincronizar();
            }
        };

        task.setOnSucceeded(e -> {
            int sincronizadas = task.getValue();
            btnSincronizar.setText("↻ Sincronizar");
            atualizarIndicadores();
            Alerta.info("Sincronização concluída",
                    sincronizadas + " venda(s) sincronizada(s) com sucesso.");
        });

        task.setOnFailed(e -> {
            btnSincronizar.setText("↻ Sincronizar");
            btnSincronizar.setDisable(false);
            Alerta.error("Erro na sincronização",
                    "Ocorreu um erro durante a sincronização. Tente novamente mais tarde.");
        });

        new Thread(task).start();
    }
}