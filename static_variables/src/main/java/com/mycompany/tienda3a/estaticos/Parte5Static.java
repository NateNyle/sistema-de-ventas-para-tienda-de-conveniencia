package com.mycompany.tienda3a.estaticos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Parte5Static {
    public static void main(String[] args) {
        BigDecimal subtotal1 = new BigDecimal("100.00");
        BigDecimal subtotal2 = new BigDecimal("50.00");

        Venta venta1 = new Venta(1, 1, 1, 1, 1, LocalDateTime.now(), subtotal1,
                ConfiguracionVenta.calcularIgv(subtotal1),
                ConfiguracionVenta.calcularTotal(subtotal1), "PAGADA");
        Venta venta2 = new Venta(2, 1, 1, 1, 2, LocalDateTime.now(), subtotal2,
                ConfiguracionVenta.calcularIgv(subtotal2),
                ConfiguracionVenta.calcularTotal(subtotal2), "PAGADA");

        System.out.println("IGV compartido: " + ConfiguracionVenta.TASA_IGV.multiply(new BigDecimal("100")) + "%");
        System.out.println("Total venta 1: S/ " + venta1.getTotal());
        System.out.println("Total venta 2: S/ " + venta2.getTotal());
        ConfiguracionVenta.registrarVenta(venta1);
        ConfiguracionVenta.registrarVenta(venta2);
        System.out.println("Ventas registradas en esta ejecucion: " + ConfiguracionVenta.getVentasRegistradas());
    }
}
