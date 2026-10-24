package com.mycompany.tienda3a.estrategia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Entidad del diagrama: PAGO. */
public class Pago {
    private int idPago;
    private int idVenta;
    private String medioPago;
    private BigDecimal monto;
    private BigDecimal vuelto;
    private LocalDateTime fecha;

    public Pago(int idPago, int idVenta, String medioPago, BigDecimal monto, BigDecimal vuelto, LocalDateTime fecha) {
        this.idPago = idPago;
        this.idVenta = idVenta;
        this.medioPago = medioPago;
        this.monto = monto;
        this.vuelto = vuelto;
        this.fecha = fecha;
    }

    public int getIdPago() { return idPago; }
    public int getIdVenta() { return idVenta; }
    public String getMedioPago() { return medioPago; }
    public BigDecimal getMonto() { return monto; }
    public BigDecimal getVuelto() { return vuelto; }
    public LocalDateTime getFecha() { return fecha; }

    @Override
    public String toString() {
        return "Pago{" + "idPago=" + idPago + ", " + "idVenta=" + idVenta + ", " + "medioPago=" + medioPago + ", " + "monto=" + monto + ", " + "vuelto=" + vuelto + ", " + "fecha=" + fecha + "}";
    }
}
