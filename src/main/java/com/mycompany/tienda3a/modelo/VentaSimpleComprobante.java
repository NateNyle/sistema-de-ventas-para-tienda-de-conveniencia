package com.mycompany.tienda3a.modelo;

/**
 * Comprobante sin datos del cliente, solo hasta el límite de RN-05.
 * 1ra subclase de Comprobante (relación de herencia #1).
 */
public class VentaSimpleComprobante extends Comprobante {

    public VentaSimpleComprobante(int id, Venta venta, String serie, int correlativo) {
        super(id, venta, serie, correlativo);
    }

    @Override
    public String generarImpresion() {
        return encabezado("VENTA SIMPLE") + "(sin datos del cliente)\n";
    }
}
