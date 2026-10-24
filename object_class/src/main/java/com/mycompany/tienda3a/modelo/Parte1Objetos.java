package com.mycompany.tienda3a.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Ejecutar con clic derecho > Run File en NetBeans. */
public class Parte1Objetos {
    public static void main(String[] args) {
        Tienda tienda = new Tienda(1, "3A-001", "Tienda 3A", "Surco", "Av. Ejemplo 100", true);
        Rol rol = new Rol(1, "CAJERO");
        Usuario usuario = new Usuario(1, 1, 1, "cajero01", "Ana Perez", "hash-demo", true);
        Cliente cliente = new Cliente(1, "71234567", "Luis Soto", "BOLETA");
        Caja caja = new Caja(1, 1, "CAJA-01", true);
        Producto producto = new Producto(1, "775000000001", "Cuaderno", new BigDecimal("5.00"), true);
        DetalleVenta detalle = new DetalleVenta(1, 1, 1, new BigDecimal("2.000"), producto.getPrecio());
        Venta venta = new Venta(1, 1, 1, 1, 1, LocalDateTime.now(),
                detalle.getSubtotal(), new BigDecimal("1.80"), new BigDecimal("11.80"), "PAGADA");
        Pago pago = new Pago(1, 1, "EFECTIVO", new BigDecimal("11.80"),
                new BigDecimal("8.20"), LocalDateTime.now());
        Inventario inventario = new Inventario(1, 1, 1, new BigDecimal("20.000"), new BigDecimal("5.000"));
        inventario.retirar(detalle.getCantidad());
        MovimientoInventario movimiento = new MovimientoInventario(1, 1, "SALIDA",
                detalle.getCantidad(), LocalDateTime.now(), "Venta numero 1");
        Comprobante comprobante = new Comprobante(1, 1, "BOLETA", "B001", "00000001",
                "PENDIENTE", LocalDateTime.now());

        // Las 12 entidades del diagrama estan representadas por objetos.
        System.out.println(tienda);
        System.out.println(rol);
        System.out.println(usuario); // No muestra la clave por seguridad.
        System.out.println(cliente);
        System.out.println(caja);
        System.out.println(producto);
        System.out.println(venta);
        System.out.println(detalle);
        System.out.println(pago);
        System.out.println(inventario);
        System.out.println(movimiento);
        System.out.println(comprobante);
    }
}
