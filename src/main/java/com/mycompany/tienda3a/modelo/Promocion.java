package com.mycompany.tienda3a.modelo;

import java.time.LocalDate;

/**
 * Precio promocional de un producto durante un periodo (RN-13).
 * Restricciones: precioPromocional > 0 y menor que el precio de venta;
 * fechaFin >= fechaInicio.
 */
public class Promocion {

    private int id;
    private Producto producto;
    private String nombre;
    private double precioPromocional;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Promocion(int id, Producto producto, String nombre, double precioPromocional,
                      LocalDate fechaInicio, LocalDate fechaFin) {
        if (precioPromocional <= 0 || precioPromocional >= producto.getPrecioVenta()) {
            throw new IllegalArgumentException(
                "El precio promocional debe ser mayor que cero y menor que el precio de venta.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial.");
        }
        this.id = id;
        this.producto = producto;
        this.nombre = nombre;
        this.precioPromocional = precioPromocional;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public int getId() { return id; }
    public Producto getProducto() { return producto; }
    public String getNombre() { return nombre; }
    public double getPrecioPromocional() { return precioPromocional; }
}
