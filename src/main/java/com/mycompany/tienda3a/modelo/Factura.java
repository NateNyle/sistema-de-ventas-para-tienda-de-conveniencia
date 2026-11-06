package com.mycompany.tienda3a.modelo;

/**
 * Comprobante con RUC y razón social del cliente (RN-05).
 * 3ra subclase de Comprobante (relación de herencia #3).
 */
public class Factura extends Comprobante {

    private Cliente cliente;

    public Factura(int id, Venta venta, String serie, int correlativo, Cliente cliente) {
        super(id, venta, serie, correlativo);
        if (cliente.getTipoDocumento() != TipoDocumento.RUC) {
            throw new IllegalArgumentException("La factura requiere un cliente con RUC.");
        }
        this.cliente = cliente;
    }

    @Override
    public String generarImpresion() {
        return encabezado("FACTURA")
             + "Razón social: " + cliente.getNombreORazonSocial()
             + " | RUC: " + cliente.getNumeroDocumento() + "\n";
    }
}
