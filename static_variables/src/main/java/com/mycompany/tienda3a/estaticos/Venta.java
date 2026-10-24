package com.mycompany.tienda3a.estaticos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Entidad del diagrama: VENTA. */
public class Venta {
    private int idVenta;
    private int idTienda;
    private int idCaja;
    private int idUsuario;
    private int idCliente;
    private LocalDateTime fecha;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String estado;

    public Venta(int idVenta, int idTienda, int idCaja, int idUsuario, int idCliente, LocalDateTime fecha, BigDecimal subtotal, BigDecimal igv, BigDecimal total, String estado) {
        this.idVenta = idVenta;
        this.idTienda = idTienda;
        this.idCaja = idCaja;
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.subtotal = subtotal;
        this.igv = igv;
        this.total = total;
        this.estado = estado;
    }

    public int getIdVenta() { return idVenta; }
    public int getIdTienda() { return idTienda; }
    public int getIdCaja() { return idCaja; }
    public int getIdUsuario() { return idUsuario; }
    public int getIdCliente() { return idCliente; }
    public LocalDateTime getFecha() { return fecha; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getIgv() { return igv; }
    public BigDecimal getTotal() { return total; }
    public String getEstado() { return estado; }

    @Override
    public String toString() {
        return "Venta{" + "idVenta=" + idVenta + ", " + "idTienda=" + idTienda + ", " + "idCaja=" + idCaja + ", " + "idUsuario=" + idUsuario + ", " + "idCliente=" + idCliente + ", " + "fecha=" + fecha + ", " + "subtotal=" + subtotal + ", " + "igv=" + igv + ", " + "total=" + total + ", " + "estado=" + estado + "}";
    }
}
