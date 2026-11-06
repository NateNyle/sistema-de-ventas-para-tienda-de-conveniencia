package com.mycompany.tienda3a.modelo;

/**
 * Cobro de una venta con un medio de pago (CU-05).
 * Restricciones: EFECTIVO exige montoEntregado >= monto; otro medio exige
 * referenciaDigital.
 */
public class Pago {

    private int id;
    private Venta venta;
    private MedioDePago medio;
    private double monto;
    private double montoEntregado;
    private String referenciaDigital;
    private boolean revertido;

    public Pago(int id, Venta venta, MedioDePago medio, double monto,
                double montoEntregado, String referenciaDigital) {
        if (medio == MedioDePago.EFECTIVO && montoEntregado < monto) {
            throw new IllegalArgumentException("El monto entregado no cubre el total.");
        }
        if (medio != MedioDePago.EFECTIVO && (referenciaDigital == null || referenciaDigital.isBlank())) {
            throw new IllegalArgumentException("Falta el número de operación.");
        }
        this.id = id;
        this.venta = venta;
        this.medio = medio;
        this.monto = monto;
        this.montoEntregado = montoEntregado;
        this.referenciaDigital = referenciaDigital;
    }

    public double getVuelto() {
        return medio == MedioDePago.EFECTIVO ? montoEntregado - monto : 0.0;
    }

    public void revertir() { this.revertido = true; }

    public MedioDePago getMedio() { return medio; }
    public double getMonto() { return monto; }
    public boolean isRevertido() { return revertido; }
}
