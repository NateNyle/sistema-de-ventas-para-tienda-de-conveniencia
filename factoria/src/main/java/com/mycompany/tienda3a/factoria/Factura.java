package com.mycompany.tienda3a.factoria;

import java.time.LocalDateTime;

/** Es-un Comprobante, pero con impresion propia para facturas. */
public class Factura extends Comprobante {
    public Factura(int idComprobante, int idVenta, String serie, String numero,
                   String estadoSunat, LocalDateTime fechaEmision) {
        super(idComprobante, idVenta, "FACTURA", serie, numero, estadoSunat, fechaEmision);
    }

    @Override
    public void imprimir() {
        System.out.println("=== FACTURA ===");
        System.out.println("Numero: " + getSerie() + "-" + getNumero());
        System.out.println("Venta: " + getIdVenta());
        System.out.println("Estado SUNAT: " + getEstadoSunat());
    }
}
