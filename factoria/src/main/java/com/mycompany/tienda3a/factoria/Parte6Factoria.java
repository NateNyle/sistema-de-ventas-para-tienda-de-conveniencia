package com.mycompany.tienda3a.factoria;

import java.time.LocalDateTime;

public class Parte6Factoria {
    public static void main(String[] args) {
        LocalDateTime fecha = LocalDateTime.now();

        // No es necesario escribir new Boleta() ni new Factura() aqui.
        Comprobante boleta = ComprobanteFactory.crearComprobante(
                "BOLETA", 1, 101, "B001", "000001", "PENDIENTE", fecha);

        Comprobante factura = ComprobanteFactory.crearComprobante(
                "FACTURA", 2, 102, "F001", "000002", "PENDIENTE", fecha);

        System.out.println("--- PATRON FACTORIA ---");
        System.out.println("Objeto creado: " + boleta.getClass().getSimpleName());
        boleta.imprimir();
        System.out.println();
        System.out.println("Objeto creado: " + factura.getClass().getSimpleName());
        factura.imprimir();
    }
}
