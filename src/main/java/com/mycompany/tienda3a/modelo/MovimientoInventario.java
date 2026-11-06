package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

/**
 * Salida o reingreso de stock causado por una venta o su anulación (RN-08).
 * Restricciones: cantidad > 0.
 */
public class MovimientoInventario {

    public enum Tipo { SALIDA_VENTA, REINGRESO_ANULACION }

    private int id;
    private Producto producto;
    private Tipo tipo;
    private double cantidad;
    private double saldoResultante;
    private LocalDateTime fecha;

    public MovimientoInventario(int id, Producto producto, Tipo tipo, double cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        this.id = id;
        this.producto = producto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
        double delta = (tipo == Tipo.SALIDA_VENTA) ? -cantidad : cantidad;
        producto.ajustarStock(delta);
        this.saldoResultante = producto.getStockActual();
    }

    public double getSaldoResultante() { return saldoResultante; }
    public Tipo getTipo() { return tipo; }
}
