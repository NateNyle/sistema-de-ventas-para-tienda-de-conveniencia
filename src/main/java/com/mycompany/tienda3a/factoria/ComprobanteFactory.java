package com.mycompany.tienda3a.factoria;

import com.mycompany.tienda3a.config.ConfiguracionVenta;
import com.mycompany.tienda3a.modelo.*;

/**
 * Patrón Factory (CU-06). Encapsula la regla RN-05 (qué tipo de comprobante
 * corresponde según el monto y los datos del cliente) para que el resto del
 * sistema pida "un comprobante para esta venta" sin tener que saber construir
 * VentaSimpleComprobante, Boleta o Factura.
 *
 * SRP: esta clase solo decide y construye; no calcula totales (eso lo hace
 * Venta) ni aplica el IGV (eso lo hace Comprobante/ConfiguracionVenta).
 */
public class ComprobanteFactory {

    public enum TipoComprobante { VENTA_SIMPLE, BOLETA, FACTURA }

    public static Comprobante crear(int id, Venta venta, Caja caja, TipoComprobante tipo, Cliente cliente) {
        double total = venta.getTotal();

        switch (tipo) {
            case VENTA_SIMPLE -> {
                if (total > ConfiguracionVenta.LIMITE_VENTA_SIMPLE) {
                    throw new IllegalArgumentException(
                        "Una venta simple no puede superar S/ " + ConfiguracionVenta.LIMITE_VENTA_SIMPLE);
                }
                return new VentaSimpleComprobante(id, venta, caja.getSerieBoleta(), caja.siguienteCorrelativoBoleta());
            }
            case BOLETA -> {
                if (cliente == null || cliente.getTipoDocumento() != TipoDocumento.DNI) {
                    throw new IllegalArgumentException("La boleta exige el DNI del cliente.");
                }
                return new Boleta(id, venta, caja.getSerieBoleta(), caja.siguienteCorrelativoBoleta(), cliente);
            }
            case FACTURA -> {
                if (cliente == null || cliente.getTipoDocumento() != TipoDocumento.RUC) {
                    throw new IllegalArgumentException("La factura exige el RUC y la razón social del cliente.");
                }
                return new Factura(id, venta, caja.getSerieFactura(), caja.siguienteCorrelativoFactura(), cliente);
            }
            default -> throw new IllegalArgumentException("Tipo de comprobante no soportado.");
        }
    }
}
