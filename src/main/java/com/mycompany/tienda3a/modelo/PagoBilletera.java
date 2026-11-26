package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

public class PagoBilletera extends Pago implements PagoElectronico {
    private String referenciaDigital;
    private String referenciaReversion;

    public PagoBilletera(int idPago, Venta venta, Cajero cajero, double monto, LocalDateTime fechaHora,
                  String referenciaDigital) {
        super(idPago, venta, cajero, monto, fechaHora);
        this.referenciaDigital = referenciaDigital == null ? "" : referenciaDigital.trim();
    }

    @Override
    public boolean validar() {
        return ValidacionesPago.esOperacionValida(referenciaDigital);   
    }

    @Override
    public MedioPago getMedio() {
        return MedioPago.BILLETERA;
    }

    @Override
    public String getReferenciaDigital() {
        return referenciaDigital;
    }

    @Override
    public void registrarReferenciaReversion(String referencia) {
        this.referenciaReversion = referencia;
    }

    public String getReferenciaReversion() { return referenciaReversion; }
}
