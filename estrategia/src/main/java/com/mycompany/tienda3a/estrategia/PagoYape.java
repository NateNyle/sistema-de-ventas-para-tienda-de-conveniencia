package com.mycompany.tienda3a.estrategia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoYape implements EstrategiaPago {
    @Override
    public Pago procesar(int idPago, int idVenta, BigDecimal total, BigDecimal recibido) {
        if (total == null || recibido == null || total.signum() <= 0
                || recibido.compareTo(total) != 0)
            throw new IllegalArgumentException("Yape debe cubrir exactamente el total");
        // Demostracion POO: la confirmacion bancaria real requiere integracion.
        return new Pago(idPago, idVenta, "YAPE", total, BigDecimal.ZERO, LocalDateTime.now());
    }
}
