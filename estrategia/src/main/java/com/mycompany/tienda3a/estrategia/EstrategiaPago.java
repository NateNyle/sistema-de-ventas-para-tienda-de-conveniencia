package com.mycompany.tienda3a.estrategia;

import java.math.BigDecimal;

/** Contrato para intercambiar metodos de pago sin cambiar el controlador. */
public interface EstrategiaPago {
    Pago procesar(int idPago, int idVenta, BigDecimal total, BigDecimal recibido);
}
