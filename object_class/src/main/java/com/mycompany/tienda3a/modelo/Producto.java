package com.mycompany.tienda3a.modelo;

import java.math.BigDecimal;

/** Entidad del diagrama: PRODUCTO. */
public class Producto {
    private int idProducto;
    private String codigoBarras;
    private String nombre;
    private BigDecimal precio;
    private boolean activo;

    public Producto(int idProducto, String codigoBarras, String nombre, BigDecimal precio, boolean activo) {
        this.idProducto = idProducto;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        if (precio == null || precio.signum() < 0) throw new IllegalArgumentException("Precio no valido");
        this.precio = precio;
        this.activo = activo;
    }

    public int getIdProducto() { return idProducto; }
    public String getCodigoBarras() { return codigoBarras; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public boolean isActivo() { return activo; }

    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        if (nuevoPrecio == null || nuevoPrecio.signum() < 0)
            throw new IllegalArgumentException("El precio debe ser mayor o igual a cero");
        this.precio = nuevoPrecio;
    }

    public void desactivar() { this.activo = false; }

    @Override
    public String toString() {
        return "Producto{" + "idProducto=" + idProducto + ", " + "codigoBarras=" + codigoBarras + ", " + "nombre=" + nombre + ", " + "precio=" + precio + ", " + "activo=" + activo + "}";
    }
}
