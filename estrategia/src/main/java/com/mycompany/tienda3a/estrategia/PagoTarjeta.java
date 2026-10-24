package com.mycompany.tienda3a.estrategia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoTarjeta implements EstrategiaPago {
    @Override
    public Pago procesar(int idPago, int idVenta, BigDecimal total, BigDecimal recibido) {
        if (total == null || recibido == null || total.signum() <= 0
                || recibido.compareTo(total) != 0)
            throw new IllegalArgumentException("El cargo a tarjeta debe igualar el total");
        // Demostracion POO: no procesa tarjetas ni contacta una pasarela real.
        return new Pago(idPago, idVenta, "TARJETA", total, BigDecimal.ZERO, LocalDateTime.now());
    }
}
