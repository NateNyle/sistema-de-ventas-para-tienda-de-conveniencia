package com.mycompany.tienda3a.pago;

import com.mycompany.tienda3a.modelo.MedioDePago;
import com.mycompany.tienda3a.modelo.Pago;
import com.mycompany.tienda3a.modelo.Venta;

public class PagoTarjeta implements EstrategiaPago {

    @Override
    public Pago procesar(int idPago, Venta venta, double montoEntregado, String referenciaDigital) {
        double total = venta.getTotal();
        return new Pago(idPago, venta, MedioDePago.TARJETA, total, total, referenciaDigital);
    }
}
