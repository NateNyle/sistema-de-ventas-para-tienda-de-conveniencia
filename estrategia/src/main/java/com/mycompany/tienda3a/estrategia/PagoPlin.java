package com.mycompany.tienda3a.estrategia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Estrategia de demostracion; no llama a una pasarela bancaria real. */
public class PagoPlin implements EstrategiaPago {
    @Override
    public Pago procesar(int idPago, int idVenta, BigDecimal total, BigDecimal recibido) {
        if (total == null || recibido == null || total.signum() <= 0
                || recibido.compareTo(total) != 0) {
            throw new IllegalArgumentException("Plin debe cubrir exactamente el total");
        }
        return new Pago(idPago, idVenta, "PLIN", total, BigDecimal.ZERO, LocalDateTime.now());
    }
}
