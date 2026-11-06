package com.mycompany.tienda3a.modelo;

/**
 * Contrato de polimorfismo: cualquier comprobante debe poder generar su
 * propia representación impresa, sin que quien lo use sepa de qué tipo es.
 */
public interface Imprimible {
    String generarImpresion();
}
