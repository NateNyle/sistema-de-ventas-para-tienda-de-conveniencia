package com.mycompany.tienda3a.polimorfismo;

import java.time.LocalDateTime;

/** Es-un Comprobante. Mantiene la misma estructura de la tabla COMPROBANTE. */
public class Boleta extends Comprobante {
    public Boleta(int idComprobante, int idVenta, String serie, String numero,
                  String estadoSunat, LocalDateTime fechaEmision) {
        super(idComprobante, idVenta, "BOLETA", serie, numero, estadoSunat, fechaEmision);
    }

    @Override
    public void imprimir() {
        System.out.println("--- BOLETA DE VENTA ---");
        System.out.println("Numero: " + getSerie() + "-" + getNumero());
        System.out.println("Venta: " + getIdVenta());
        System.out.println("Estado SUNAT: " + getEstadoSunat());
    }
}
