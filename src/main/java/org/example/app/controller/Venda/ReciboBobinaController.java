package org.example.app.controller.venda;

import javafx.fxml.FXML;
import javafx.print.*;
import javafx.scene.control.TextArea;
import org.example.app.model.Venda;
import org.example.app.model.ItemVenda;

public class ReciboBobinaController {

    @FXML private TextArea txtRecibo;

    public void carregar(Venda venda) {
        txtRecibo.setText(montarTexto(venda));
    }

    private String montarTexto(Venda venda) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           LOJA XYZ - COMPROVANTE\n");
        sb.append("       (documento não fiscal)\n");
        sb.append("========================================\n");
        sb.append("Venda: #").append(venda.getId()).append("\n");
        sb.append("Data: ").append(venda.getDataVenda()).append(" ").append(venda.getHoraVenda()).append("\n");
        sb.append("Cliente: ").append(venda.getCliente() != null ? venda.getCliente().getNome() : "N/A").append("\n");
        sb.append("Vendedor: ").append(venda.getVendedor() != null ? venda.getVendedor().getNome() : "N/A").append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-20s %5s %8s %8s\n", "ITEM", "QTD", "UNIT", "TOTAL"));

        if (venda.getItens() != null) {
            for (ItemVenda item : venda.getItens()) {
                double precoUnit = item.getProduto() != null ? item.getProduto().getPreco() : 0.0;
                double totalItem = item.getQuantidade() * precoUnit;
                String nome = item.getProduto() != null ? item.getProduto().getNome() : "?";
                sb.append(String.format("%-20s %5d %8.2f %8.2f\n",
                        truncate(nome, 20), item.getQuantidade(), precoUnit, totalItem));
            }
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("TOTAL: R$ %.2f\n", venda.getTotal()));
        sb.append("Forma de pagamento: ").append(venda.getFormaPagamento()).append("\n");
        if (venda.getParcelas() > 1) {
            sb.append(venda.getParcelas()).append("x de R$ ")
              .append(String.format("%.2f", venda.getValorParcela())).append("\n");
        }
        sb.append("========================================\n");
        sb.append("        Obrigado pela preferencia!\n");
        return sb.toString();
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max);
    }

    @FXML
    private void imprimir() {
        Printer impressora = Printer.getDefaultPrinter();
        if (impressora == null) return;

        PrinterJob job = PrinterJob.createPrinterJob(impressora);
        if (job == null) return;

        // Tenta achar um papel de bobina (~80mm) jÃƒÆ’Ã‚Â¡ registrado no driver
        Paper papelBobina = impressora.getPrinterAttributes().getSupportedPapers().stream()
                .filter(p -> p.getWidth() > 75 && p.getWidth() < 85) // largura em pontos ~ mm no driver
                .findFirst()
                .orElse(impressora.getDefaultPageLayout().getPaper());

        PageLayout layout = impressora.createPageLayout(papelBobina, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
        job.getJobSettings().setPageLayout(layout);

        if (job.showPrintDialog(txtRecibo.getScene().getWindow())) {
            boolean sucesso = job.printPage(layout, txtRecibo);
            if (sucesso) job.endJob();
        }
    }
}
