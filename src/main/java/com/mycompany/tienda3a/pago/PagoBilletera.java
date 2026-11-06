package com.mycompany.tienda3a.pago;

import com.mycompany.tienda3a.modelo.MedioDePago;
import com.mycompany.tienda3a.modelo.Pago;
import com.mycompany.tienda3a.modelo.Venta;

/** Yape, Plin u otra billetera digital (S-04: se registra el número de operación). */
public class PagoBilletera implements EstrategiaPago {

    @Override
    public Pago procesar(int idPago, Venta venta, double montoEntregado, String referenciaDigital) {
        double total = venta.getTotal();
        return new Pago(idPago, venta, MedioDePago.BILLETERA, total, total, referenciaDigital);
    }
}
