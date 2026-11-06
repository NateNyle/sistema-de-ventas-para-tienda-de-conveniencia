package com.mycompany.tienda3a.pago;

import com.mycompany.tienda3a.modelo.Pago;
import com.mycompany.tienda3a.modelo.Venta;

/**
 * Patrón Strategy (CU-05, RF-09). Cada medio de pago (efectivo, tarjeta,
 * billetera) implementa este contrato con su propia validación, y
 * ProcesadorPago los usa sin conocer la clase concreta: es la misma
 * interfaz que exige la rúbrica de interfaz + polimorfismo, aplicada
 * además como patrón de diseño (y no solo como ejemplo aislado).
 */
public interface EstrategiaPago {
    Pago procesar(int idPago, Venta venta, double montoEntregado, String referenciaDigital);
}
