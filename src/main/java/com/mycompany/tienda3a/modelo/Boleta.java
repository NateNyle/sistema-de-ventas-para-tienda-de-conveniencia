package com.mycompany.tienda3a.modelo;

/**
 * Comprobante con el DNI del cliente (RN-05).
 * 2da subclase de Comprobante (relación de herencia #2), usa super(...)
 * para reutilizar la inicialización común.
 */
public class Boleta extends Comprobante {

    private Cliente cliente;

    public Boleta(int id, Venta venta, String serie, int correlativo, Cliente cliente) {
        super(id, venta, serie, correlativo);
        if (cliente.getTipoDocumento() != TipoDocumento.DNI) {
            throw new IllegalArgumentException("La boleta requiere un cliente con DNI.");
        }
        this.cliente = cliente;
    }

    @Override
    public String generarImpresion() {
        return encabezado("BOLETA")
             + "Cliente: " + cliente.getNombreORazonSocial()
             + " | DNI: " + cliente.getNumeroDocumento() + "\n";
    }
}
