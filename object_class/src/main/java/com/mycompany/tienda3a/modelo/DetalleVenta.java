package com.mycompany.tienda3a.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Entidad del diagrama: DETALLEVENTA. */
public class DetalleVenta {
    private int idDetalle;
    private int idVenta;
    private int idProducto;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public DetalleVenta(int idDetalle, int idVenta, int idProducto, BigDecimal cantidad, BigDecimal precioUnitario) {
        this.idDetalle = idDetalle;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        if (cantidad == null || cantidad.signum() <= 0) throw new IllegalArgumentException("cantidad debe ser positivo");
        this.cantidad = cantidad;
        if (precioUnitario == null || precioUnitario.signum() <= 0) throw new IllegalArgumentException("precioUnitario debe ser positivo");
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad.multiply(precioUnitario).setScale(2, RoundingMode.HALF_UP);
    }

    public int getIdDetalle() { return idDetalle; }
    public int getIdVenta() { return idVenta; }
    public int getIdProducto() { return idProducto; }
    public BigDecimal getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }

    @Override
    public String toString() {
        return "DetalleVenta{" + "idDetalle=" + idDetalle + ", " + "idVenta=" + idVenta + ", " + "idProducto=" + idProducto + ", " + "cantidad=" + cantidad + ", " + "precioUnitario=" + precioUnitario + ", " + "subtotal=" + subtotal + "}";
    }
}
