package com.mycompany.tienda3a.estaticos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Ejemplo de constante static y contador static compartidos. */
public final class ConfiguracionVenta {
    public static final BigDecimal TASA_IGV = new BigDecimal("0.18");
    private static int ventasRegistradas = 0;

    private ConfiguracionVenta() { }

    /** El subtotal en esta demostracion es el monto antes de IGV. */
    public static BigDecimal calcularIgv(BigDecimal subtotalSinIgv) {
        Objects.requireNonNull(subtotalSinIgv);
        if (subtotalSinIgv.signum() < 0) throw new IllegalArgumentException("Subtotal negativo");
        return subtotalSinIgv.multiply(TASA_IGV).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularTotal(BigDecimal subtotalSinIgv) {
        return subtotalSinIgv.add(calcularIgv(subtotalSinIgv)).setScale(2, RoundingMode.HALF_UP);
    }

    /** Contador de demostracion: solo vive mientras se ejecuta este programa. */
    public static synchronized int registrarVenta(Venta venta) {
        Objects.requireNonNull(venta, "La venta no puede ser nula");
        return ++ventasRegistradas;
    }

    public static synchronized int getVentasRegistradas() { return ventasRegistradas; }
}
