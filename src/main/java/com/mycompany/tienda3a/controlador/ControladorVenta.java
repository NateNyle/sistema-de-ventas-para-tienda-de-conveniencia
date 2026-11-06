package com.mycompany.tienda3a.controlador;

import com.mycompany.tienda3a.config.ConfiguracionVenta;
import com.mycompany.tienda3a.factoria.ComprobanteFactory;
import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.pago.EstrategiaPago;
import com.mycompany.tienda3a.pago.ProcesadorPago;
import com.mycompany.tienda3a.sunat.AdaptadorSunat;
import java.util.List;

/**
 * Aplica las reglas de negocio y coordina las operaciones (sección 8.3,
 * "los controladores aplican las reglas de negocio"); por ejemplo, esta
 * clase ejecuta la confirmación única de CU-02.
 *
 * Los 3 principios SOLID que pide la EC2, explicados sobre esta clase:
 *
 * 1) SRP (responsabilidad única). ControladorVenta solo orquesta el flujo
 *    de una venta (agregar líneas, confirmar, anular). No calcula el IGV
 *    (lo hace Venta/Comprobante), no decide qué tipo de comprobante emitir
 *    (lo hace ComprobanteFactory) y no sabe cobrar con tarjeta o efectivo
 *    (lo hace EstrategiaPago). Si cambia la forma de calcular el IGV o de
 *    construir un comprobante, esta clase no se modifica: cada motivo de
 *    cambio vive en una sola clase.
 *
 * 2) OCP (abierto/cerrado). Para aceptar un nuevo medio de pago (por
 *    ejemplo, criptomonedas) solo se agrega una clase que implemente
 *    EstrategiaPago; para un comprobante nuevo, se extiende Comprobante y
 *    ComprobanteFactory. Este controlador no necesita tocarse: está
 *    cerrado a la modificación pero abierto a la extensión.
 *
 * 3) DIP (inversión de dependencias). Este controlador depende de las
 *    abstracciones EstrategiaPago y AdaptadorSunat (interfaces), nunca de
 *    PagoEfectivo, PagoTarjeta o SimuladorSunat directamente. Esas
 *    implementaciones concretas se le inyectan por el constructor, así que
 *    el módulo de alto nivel (el flujo de venta) no depende de los módulos
 *    de bajo nivel (cómo se cobra o cómo se envía a SUNAT), sino de sus
 *    contratos.
 */
public class ControladorVenta {

    private int siguienteIdDetalle = 1;
    private int siguienteIdPago = 1;
    private int siguienteIdComprobante = 1;
    private int siguienteIdMovimiento = 1;

    private final AdaptadorSunat adaptadorSunat;

    public ControladorVenta(AdaptadorSunat adaptadorSunat) {
        this.adaptadorSunat = adaptadorSunat;
    }

    /** RF-03, RF-04, RF-06: agrega una línea validando stock y aplicando promoción vigente (RN-03). */
    public void agregarProducto(Venta venta, Producto producto, double cantidad, Promocion promocionVigente) {
        if (!producto.hayStockPara(cantidad)) {
            throw new IllegalStateException(
                "Stock insuficiente. Disponible: " + producto.getStockActual());
        }
        double precioUnitario = (promocionVigente != null)
            ? promocionVigente.getPrecioPromocional()
            : producto.getPrecioVenta();

        DetalleVenta detalle = new DetalleVenta(
            siguienteIdDetalle++, producto, cantidad, producto.getPrecioVenta(), precioUnitario, promocionVigente);
        venta.agregarDetalle(detalle);
    }

    /**
     * CU-02 paso 9: confirma la venta en una sola operación (RF-10): cobra
     * con la estrategia de pago elegida, descuenta stock y emite el
     * comprobante con ComprobanteFactory (RN-05). Si algo falla, no se
     * aplica ningún cambio parcial (RNF-11): por eso se valida todo antes
     * de tocar el estado de la venta o el stock.
     */
    public ResultadoVenta confirmarVenta(Venta venta, Caja caja, ComprobanteFactory.TipoComprobante tipo,
                                          Cliente cliente, ProcesadorPago procesadorPago,
                                          double montoEntregado, String referenciaDigital) {
        List<DetalleVenta> detalles = venta.getDetalles();
        if (detalles.isEmpty()) {
            throw new IllegalStateException("La venta no tiene productos.");
        }
        for (DetalleVenta d : detalles) {
            if (!d.getProducto().hayStockPara(d.getCantidad())) {
                throw new IllegalStateException("El stock cambió para " + d.getProducto().getNombre() + ".");
            }
        }

        // 1. Cobro (RN-01: solo se confirma si el pago cubre el total).
        Pago pago = procesadorPago.cobrar(siguienteIdPago++, venta, montoEntregado, referenciaDigital);

        // 2. Confirmación única: descuenta stock y registra movimientos (RN-08).
        venta.confirmar();
        for (DetalleVenta d : detalles) {
            new MovimientoInventario(siguienteIdMovimiento++, d.getProducto(),
                MovimientoInventario.Tipo.SALIDA_VENTA, d.getCantidad());
        }

        // 3. Emisión del comprobante (CU-06) con la factoría.
        Comprobante comprobante = ComprobanteFactory.crear(siguienteIdComprobante++, venta, caja, tipo, cliente);

        // 4. Envío al simulador de SUNAT, con el adaptador inyectado (DIP).
        boolean aceptado = adaptadorSunat.enviar(comprobante);

        return new ResultadoVenta(pago, comprobante, aceptado);
    }

    /** CU-08: anula una venta confirmada y reingresa el stock (RN-07, RN-08). */
    public void anularVenta(Venta venta, Comprobante comprobante, Pago pago, String motivo) {
        venta.anular(motivo);
        for (DetalleVenta d : venta.getDetalles()) {
            new MovimientoInventario(siguienteIdMovimiento++, d.getProducto(),
                MovimientoInventario.Tipo.REINGRESO_ANULACION, d.getCantidad());
        }
        pago.revertir();
        comprobante.anular();
    }

    /** Resultado de confirmarVenta(): agrupa pago, comprobante y estado SUNAT. */
    public static class ResultadoVenta {
        public final Pago pago;
        public final Comprobante comprobante;
        public final boolean aceptadoPorSunat;

        public ResultadoVenta(Pago pago, Comprobante comprobante, boolean aceptadoPorSunat) {
            this.pago = pago;
            this.comprobante = comprobante;
            this.aceptadoPorSunat = aceptadoPorSunat;
        }
    }
}
