package com.mycompany.tienda3a.sunat;

import com.mycompany.tienda3a.modelo.Comprobante;
import java.util.Random;

/** PV-23: simulador interno que devuelve ACEPTADO o RECHAZADO (no se conecta a SUNAT). */
public class SimuladorSunat implements AdaptadorSunat {

    private final Random azar = new Random();

    @Override
    public boolean enviar(Comprobante comprobante) {
        boolean aceptado = azar.nextInt(100) < 90; // ~90% aceptado, para la demo
        comprobante.registrarIntentoEnvio(aceptado, aceptado ? null : "Rechazado por el simulador de SUNAT.");
        return aceptado;
    }
}
