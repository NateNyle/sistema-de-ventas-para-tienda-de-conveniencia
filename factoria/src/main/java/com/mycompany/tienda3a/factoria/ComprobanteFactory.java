package com.mycompany.tienda3a.factoria;

import java.time.LocalDateTime;
import java.util.Locale;

/** Patron Factoria: centraliza la creacion de boletas y facturas. */
public final class ComprobanteFactory {

    // Evita crear instancias de esta clase de utilidad.
    private ComprobanteFactory() {
    }

    public static Comprobante crearComprobante(
            String tipo,
            int idComprobante,
            int idVenta,
            String serie,
            String numero,
            String estadoSunat,
            LocalDateTime fechaEmision) {

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Debe indicar BOLETA o FACTURA.");
        }
        if (fechaEmision == null) {
            throw new IllegalArgumentException("La fecha de emision es obligatoria.");
        }

        switch (tipo.trim().toUpperCase(Locale.ROOT)) {
            case "BOLETA":
                return new Boleta(idComprobante, idVenta,
                        serie, numero, estadoSunat, fechaEmision);
            case "FACTURA":
                return new Factura(idComprobante, idVenta,
                        serie, numero, estadoSunat, fechaEmision);
            default:
                throw new IllegalArgumentException("Tipo no permitido: " + tipo);
        }
    }
}
