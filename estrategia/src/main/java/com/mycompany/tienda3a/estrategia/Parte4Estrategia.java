package com.mycompany.tienda3a.estrategia;

import java.math.BigDecimal;

public class Parte4Estrategia {
    public static void main(String[] args) {
        BigDecimal total = new BigDecimal("11.80");
        ProcesadorPago procesador = new ProcesadorPago(new PagoEfectivo());
        Pago efectivo = procesador.cobrar(1, 1, total, new BigDecimal("20.00"));
        System.out.println("Efectivo: " + efectivo);

        procesador.cambiarEstrategia(new PagoYape());
        Pago yape = procesador.cobrar(2, 2, total, total);
        System.out.println("Yape: " + yape);

        procesador.cambiarEstrategia(new PagoTarjeta());
        Pago tarjeta = procesador.cobrar(3, 3, total, total);
        System.out.println("Tarjeta: " + tarjeta);

        procesador.cambiarEstrategia(new PagoPlin());
        Pago plin = procesador.cobrar(4, 4, total, total);
        System.out.println("Plin: " + plin);

        procesador.cambiarEstrategia(new PagoTransferencia());
        Pago transferencia = procesador.cobrar(5, 5, total, total);
        System.out.println("Transferencia: " + transferencia);
    }
}
