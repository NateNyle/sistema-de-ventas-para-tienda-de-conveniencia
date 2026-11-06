package com.mycompany.tienda3a.modelo;

import com.mycompany.tienda3a.config.ConfiguracionVenta;

/**
 * Cierre diario de la caja de un cajero (CU-09, RN-10).
 * efectivoEsperado = fondoInicial + cobrosEfectivo - efectivoDevuelto.
 */
public class CierreCaja {

    private int id;
    private Caja caja;
    private Usuario cajero;
    private double cobrosEfectivo;
    private double efectivoDevuelto;
    private double efectivoContado;

    public CierreCaja(int id, Caja caja, Usuario cajero, double cobrosEfectivo, double efectivoDevuelto) {
        this.id = id;
        this.caja = caja;
        this.cajero = cajero;
        this.cobrosEfectivo = cobrosEfectivo;
        this.efectivoDevuelto = efectivoDevuelto;
    }

    public double getEfectivoEsperado() {
        return ConfiguracionVenta.FONDO_INICIAL_CAJA + cobrosEfectivo - efectivoDevuelto;
    }

    public double registrarEfectivoContado(double contado) {
        this.efectivoContado = contado;
        return contado - getEfectivoEsperado();
    }

    public double getEfectivoContado() { return efectivoContado; }
}
