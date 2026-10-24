package com.mycompany.tienda3a.estrategia;

import java.math.BigDecimal;
import java.util.Objects;

/** Contexto del patron Estrategia: delega al medio de pago seleccionado. */
public class ProcesadorPago {
    private EstrategiaPago estrategia;

    public ProcesadorPago(EstrategiaPago estrategia) {
        cambiarEstrategia(estrategia);
    }

    public void cambiarEstrategia(EstrategiaPago estrategia) {
        this.estrategia = Objects.requireNonNull(estrategia, "Se requiere estrategia");
    }

    public Pago cobrar(int idPago, int idVenta, BigDecimal total, BigDecimal recibido) {
        return estrategia.procesar(idPago, idVenta, total, recibido);
    }
}
