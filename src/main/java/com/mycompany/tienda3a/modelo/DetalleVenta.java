package com.mycompany.tienda3a.modelo;

/**
 * Línea de producto dentro de una venta.
 * Restricciones: cantidad > 0; precioUnitario <= precioLista;
 * importe = cantidad * precioUnitario.
 */
public class DetalleVenta {

    private int id;
    private Producto producto;
    private double cantidad;
    private double precioLista;
    private double precioUnitario;
    private Promocion promocionAplicada;

    public DetalleVenta(int id, Producto producto, double cantidad, double precioLista,
                         double precioUnitario, Promocion promocionAplicada) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (precioUnitario > precioLista) {
            throw new IllegalArgumentException("El precio unitario no puede superar el precio de lista.");
        }
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioLista = precioLista;
        this.precioUnitario = precioUnitario;
        this.promocionAplicada = promocionAplicada;
    }

    public double getImporte() {
        return cantidad * precioUnitario;
    }

    public Producto getProducto() { return producto; }
    public double getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public Promocion getPromocionAplicada() { return promocionAplicada; }

    @Override
    public String toString() {
        return String.format("%-20s x%-6.2f  S/ %.2f", producto.getNombre(), cantidad, getImporte());
    }
}
