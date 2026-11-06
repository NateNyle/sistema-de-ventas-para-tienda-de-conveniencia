package com.mycompany.tienda3a.pago;

import com.mycompany.tienda3a.modelo.MedioDePago;
import com.mycompany.tienda3a.modelo.Pago;
import com.mycompany.tienda3a.modelo.Venta;

public class PagoEfectivo implements EstrategiaPago {

    @Override
    public Pago procesar(int idPago, Venta venta, double montoEntregado, String referenciaDigital) {
        return new Pago(idPago, venta, MedioDePago.EFECTIVO, venta.getTotal(), montoEntregado, null);
    }
}
