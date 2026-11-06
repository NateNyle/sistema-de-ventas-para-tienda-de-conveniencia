package com.mycompany.tienda3a.pago;

import com.mycompany.tienda3a.modelo.Pago;
import com.mycompany.tienda3a.modelo.Venta;

/**
 * Contexto del patrón Strategy. No conoce PagoEfectivo, PagoTarjeta ni
 * PagoBilletera: solo conoce la interfaz EstrategiaPago (DIP - principio
 * de inversión de dependencias). Si Tiendas 3A agrega un nuevo medio de
 * pago, se crea una clase nueva que implemente EstrategiaPago y esta
 * clase no se modifica (OCP - abierto/cerrado).
 */
public class ProcesadorPago {

    private EstrategiaPago estrategia;

    public ProcesadorPago(EstrategiaPago estrategia) {
        this.estrategia = estrategia;
    }

    public void cambiarEstrategia(EstrategiaPago estrategia) {
        this.estrategia = estrategia;
    }

    public Pago cobrar(int idPago, Venta venta, double montoEntregado, String referenciaDigital) {
        return estrategia.procesar(idPago, venta, montoEntregado, referenciaDigital);
    }
}
