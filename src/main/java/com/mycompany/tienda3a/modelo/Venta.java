package com.mycompany.tienda3a.modelo;

import com.mycompany.tienda3a.config.ConfiguracionVenta;
import java.util.ArrayList;
import java.util.List;

/**
 * Operación de venta en caja, desde que se inicia hasta que se confirma,
 * cancela o anula (CU-02). RN-04: los precios incluyen IGV, por lo que
 * subtotal = total / (1 + tasa) e IGV = total - subtotal.
 */
public class Venta {

    private int id;
    private Caja caja;
    private Usuario cajero;
    private Cliente cliente;
    private List<DetalleVenta> detalles;
    private EstadoVenta estado;
    private String motivoAnulacion;

    public Venta(int id, Caja caja, Usuario cajero) {
        this.id = id;
        this.caja = caja;
        this.cajero = cajero;
        this.detalles = new ArrayList<>();
        this.estado = EstadoVenta.EN_CURSO;
    }

    /** RF-05: permite agregar una línea mientras la venta está EN_CURSO. */
    public void agregarDetalle(DetalleVenta detalle) {
        if (estado != EstadoVenta.EN_CURSO) {
            throw new IllegalStateException("Solo se puede modificar una venta EN_CURSO.");
        }
        detalles.add(detalle);
    }

    public void quitarDetalle(DetalleVenta detalle) {
        if (estado != EstadoVenta.EN_CURSO) {
            throw new IllegalStateException("Solo se puede modificar una venta EN_CURSO.");
        }
        detalles.remove(detalle);
    }

    /** RF-06: total de la venta (importes con IGV incluido). */
    public double getTotal() {
        return detalles.stream().mapToDouble(DetalleVenta::getImporte).sum();
    }

    public double getSubtotal() {
        return getTotal() / (1 + ConfiguracionVenta.TASA_IGV);
    }

    public double getIgv() {
        return getTotal() - getSubtotal();
    }

    public void confirmar() {
        this.estado = EstadoVenta.CONFIRMADA;
    }

    /** CU-02/RF-07: cancelar antes del cobro, sin afectar stock ni caja. */
    public void cancelar() {
        if (estado != EstadoVenta.EN_CURSO) {
            throw new IllegalStateException("Solo se puede cancelar una venta EN_CURSO.");
        }
        this.estado = EstadoVenta.CANCELADA;
    }

    /** CU-08: anular una venta confirmada, sin límite de plazo. */
    public void anular(String motivo) {
        if (estado != EstadoVenta.CONFIRMADA) {
            throw new IllegalStateException("Solo se puede anular una venta CONFIRMADA.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("La anulación exige un motivo.");
        }
        this.estado = EstadoVenta.ANULADA;
        this.motivoAnulacion = motivo;
    }

    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public int getId() { return id; }
    public Caja getCaja() { return caja; }
    public Usuario getCajero() { return cajero; }
    public Cliente getCliente() { return cliente; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public EstadoVenta getEstado() { return estado; }
}
