package com.mycompany.tienda3a.sunat;

import com.mycompany.tienda3a.modelo.Comprobante;

/**
 * Aísla la comunicación con SUNAT (sección 8.3 del documento: "el adaptador
 * SUNAT aísla la comunicación con ese sistema... y puede reemplazarse por la
 * conexión real sin cambiar el resto del sistema"). Hoy lo implementa
 * SimuladorSunat; el día de mañana podría implementarlo un cliente real del
 * OSE sin tocar ControladorVenta (DIP).
 */
public interface AdaptadorSunat {
    boolean enviar(Comprobante comprobante);
}
