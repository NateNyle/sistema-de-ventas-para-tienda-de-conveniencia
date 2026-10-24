package com.mycompany.tienda3a.estrategia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoEfectivo implements EstrategiaPago {
    @Override
    public Pago procesar(int idPago, int idVenta, BigDecimal total, BigDecimal recibido) {
        if (total == null || recibido == null || total.signum() <= 0
                || recibido.compareTo(total) < 0)
            throw new IllegalArgumentException("Efectivo insuficiente o importe invalido");
        BigDecimal vuelto = recibido.subtract(total).setScale(2);
        // En MySQL pago.monto = efectivo recibido; pago.vuelto = cambio entregado.
        return new Pago(idPago, idVenta, "EFECTIVO", recibido.setScale(2), vuelto, LocalDateTime.now());
    }
}
