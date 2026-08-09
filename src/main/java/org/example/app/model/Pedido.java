package org.example.app.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido extends BaseEntity {

    private Cliente cliente;
    private Vendedor vendedor;
    private double total;
    private LocalDate dataPedido;
    private LocalTime horaPedido;
    private int status;

    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
        super();
        this.dataPedido = LocalDate.now();
        this.horaPedido = LocalTime.now();
        this.status = 1;
    }

    public void calcularTotal() {
        this.total = itens.stream()
                .mapToDouble(ItemPedido::getTotal)
                .sum();
    }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Vendedor getVendedor() { return vendedor; }
    public void setVendedor(Vendedor vendedor) { this.vendedor = vendedor; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public LocalDate getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDate dataPedido) { this.dataPedido = dataPedido; }

    public LocalTime getHoraPedido() { return horaPedido; }
    public void setHoraPedido(LocalTime horaPedido) { this.horaPedido = horaPedido; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
}
