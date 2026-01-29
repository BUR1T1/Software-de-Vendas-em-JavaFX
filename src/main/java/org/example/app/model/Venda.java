package org.example.app.model;

import org.example.app.BaseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Venda extends BaseEntity {

    private LocalDate dataVenda;
    private LocalTime horaVenda;

    private Vendedor vendedor;
    private Cliente cliente;

    private double valorBruto;
    private double desconto;
    private double acrescimo;
    private double total;

    private List<ItemVenda> itens = new ArrayList<>();

    public Venda() {
        super();
        this.dataVenda = LocalDate.now();
        this.horaVenda = LocalTime.now();
    }

    // Método para calcular o total geral da venda automaticamente
    public double getTotal() {
        return itens.stream().mapToDouble(ItemVenda::getTotal).sum();
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDate getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDate dataVenda) { this.dataVenda = dataVenda; }

    public LocalTime getHoraVenda() { return horaVenda; }
    public void setHoraVenda(LocalTime horaVenda) { this.horaVenda = horaVenda; }

    public Vendedor getVendedor() { return vendedor; }
    public void setVendedor(Vendedor vendedor) { this.vendedor = vendedor; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }

    public double getValorBruto() { return valorBruto; }
    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }
    public double getAcrescimo() { return acrescimo; }
    public void setAcrescimo(double acrescimo) { this.acrescimo = acrescimo; }

    public void calcularTotalFinal() {
        this.valorBruto = itens.stream().mapToDouble(ItemVenda::getTotal).sum();
        this.total = this.valorBruto - this.desconto + this.acrescimo;
    }
}
