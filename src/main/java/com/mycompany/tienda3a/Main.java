package com.mycompany.tienda3a;

import com.mycompany.tienda3a.config.ConfiguracionVenta;
import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.vista.VentanaVenta;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal ejecutable (sección 6 de la EC2). Primero imprime una
 * demo por consola de cada concepto pedido en la sección 4 y luego abre la
 * interfaz de escritorio (Swing) de CU-02 Registrar venta.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== 1. OBJETOS Y CLASES (encapsulamiento, set/get, constructores) ===");
        Categoria categoria = new Categoria(1, "Abarrotes");
        Producto producto = new Producto(1, "P001", "Arroz Costeño 1kg", categoria, UnidadMedida.UNIDAD, 5.90, 50);
        System.out.println("Producto creado: " + producto);
        System.out.println();

        System.out.println("=== 2. VARIABLE ESTÁTICA (sin ser un contador de objetos) ===");
        System.out.println("IGV configurado para toda la tienda: " + (ConfiguracionVenta.TASA_IGV * 100) + "%");
        System.out.println("Fondo inicial de cada caja: S/ " + ConfiguracionVenta.FONDO_INICIAL_CAJA);
        System.out.println();

        System.out.println("=== 3. HERENCIA E INTERFAZ + POLIMORFISMO ===");
        Caja caja = new Caja(1, "Caja 1", "B001", "F001");
        Usuario cajero = new Usuario(1, "Ana Torres", "atorres", "hash-demo", Rol.CAJERO);
        Venta venta = new Venta(1, caja, cajero);
        venta.agregarDetalle(new DetalleVenta(1, producto, 2, producto.getPrecioVenta(), producto.getPrecioVenta(), null));

        Cliente clienteDni = new Cliente(1, TipoDocumento.DNI, "72111222", "Juan Pérez");
        Cliente clienteRuc = new Cliente(2, TipoDocumento.RUC, "20555555551", "Tech Solutions S.A.C.");

        // Tres subclases de Comprobante (Boleta, Factura, VentaSimpleComprobante)
        // tratadas de forma polimórfica a través de la interfaz Imprimible.
        List<Imprimible> bandejaDeImpresion = new ArrayList<>();
        bandejaDeImpresion.add(new Boleta(1, venta, caja.getSerieBoleta(), caja.siguienteCorrelativoBoleta(), clienteDni));
        bandejaDeImpresion.add(new Factura(2, venta, caja.getSerieFactura(), caja.siguienteCorrelativoFactura(), clienteRuc));
        bandejaDeImpresion.add(new VentaSimpleComprobante(3, venta, caja.getSerieBoleta(), caja.siguienteCorrelativoBoleta()));

        for (Imprimible documento : bandejaDeImpresion) {
            // El código no sabe si "documento" es Boleta, Factura o VentaSimpleComprobante;
            // cada una ejecuta su propia versión de generarImpresion() (polimorfismo dinámico).
            System.out.println(documento.generarImpresion());
        }

        System.out.println("=== 4. ABRIENDO LA INTERFAZ DE ESCRITORIO (Swing) ===");
        SwingUtilities.invokeLater(() -> new VentanaVenta().setVisible(true));
    }
}
