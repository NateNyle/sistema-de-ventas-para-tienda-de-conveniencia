package com.mycompany.tienda3a.modelo;

/**
 * Artículo del catálogo, con su precio vigente y su stock (CU-11).
 * Restricciones: precioVenta > 0; stockActual >= 0.
 */
public class Producto {

    private int id;
    private String codigo;
    private String nombre;
    private Categoria categoria;
    private UnidadMedida unidadMedida;
    private double precioVenta;
    private double stockActual;
    private boolean activo;

    public Producto(int id, String codigo, String nombre, Categoria categoria,
                     UnidadMedida unidadMedida, double precioVenta, double stockActual) {
        if (precioVenta <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor que cero.");
        }
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.unidadMedida = unidadMedida;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.activo = true;
    }

    /** RF-04: verificar que el stock cubra la cantidad solicitada. */
    public boolean hayStockPara(double cantidad) {
        return activo && this.stockActual >= cantidad;
    }

    /** Aplicado por confirmarVenta / anularVenta a través de MovimientoInventario. */
    void ajustarStock(double delta) {
        this.stockActual += delta;
        if (this.stockActual < 0) {
            this.stockActual = 0;
        }
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public Categoria getCategoria() { return categoria; }
    public UnidadMedida getUnidadMedida() { return unidadMedida; }
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
    public double getStockActual() { return stockActual; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (S/ " + String.format("%.2f", precioVenta) + ")";
    }
}
