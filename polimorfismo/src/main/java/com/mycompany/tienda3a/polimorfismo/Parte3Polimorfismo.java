package com.mycompany.tienda3a.polimorfismo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Parte3Polimorfismo {
    public static void main(String[] args) {
        List<Imprimible> documentos = Arrays.asList(
                new Boleta(1, 101, "B001", "000001", "PENDIENTE", LocalDateTime.now()),
                new Factura(2, 102, "F001", "000002", "PENDIENTE", LocalDateTime.now())
        );
        for (Imprimible documento : documentos) {
            documento.imprimir(); // Misma interfaz, distinta implementacion.
        }
    }
}
