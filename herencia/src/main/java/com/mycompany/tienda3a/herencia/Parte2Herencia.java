package com.mycompany.tienda3a.herencia;

import java.time.LocalDateTime;

public class Parte2Herencia {
    public static void main(String[] args) {
        Boleta boleta = new Boleta(1, 101, "B001", "000001", "PENDIENTE", LocalDateTime.now());
        Factura factura = new Factura(2, 102, "F001", "000002", "PENDIENTE", LocalDateTime.now());
        System.out.println("Ambas clases heredan de Comprobante:");
        System.out.println("Tipo del padre heredado por boleta: " + boleta.getTipo());
        System.out.println("ID venta heredado por factura: " + factura.getIdVenta());
        Comprobante documento = boleta; // Una Boleta ES un Comprobante.
        documento.imprimir();            // Metodo sobrescrito de la hija.
        factura.imprimir();
    }
}
