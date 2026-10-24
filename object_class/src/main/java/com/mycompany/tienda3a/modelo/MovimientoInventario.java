package com.mycompany.tienda3a.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Entidad del diagrama: MOVIMIENTOINVENTARIO. */
public class MovimientoInventario {
    private int idMovimiento;
    private int idInventario;
    private String tipo;
    private BigDecimal cantidad;
    private LocalDateTime fecha;
    private String motivo;

    public MovimientoInventario(int idMovimiento, int idInventario, String tipo, BigDecimal cantidad, LocalDateTime fecha, String motivo) {
        this.idMovimiento = idMovimiento;
        this.idInventario = idInventario;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.motivo = motivo;
    }

    public int getIdMovimiento() { return idMovimiento; }
    public int getIdInventario() { return idInventario; }
    public String getTipo() { return tipo; }
    public BigDecimal getCantidad() { return cantidad; }
    public LocalDateTime getFecha() { return fecha; }
    public String getMotivo() { return motivo; }

    @Override
    public String toString() {
        return "MovimientoInventario{" + "idMovimiento=" + idMovimiento + ", " + "idInventario=" + idInventario + ", " + "tipo=" + tipo + ", " + "cantidad=" + cantidad + ", " + "fecha=" + fecha + ", " + "motivo=" + motivo + "}";
    }
}
