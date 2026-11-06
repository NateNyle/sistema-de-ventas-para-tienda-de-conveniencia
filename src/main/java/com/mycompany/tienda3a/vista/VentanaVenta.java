package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.config.ConfiguracionVenta;
import com.mycompany.tienda3a.controlador.ControladorVenta;
import com.mycompany.tienda3a.factoria.ComprobanteFactory;
import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.pago.*;
import com.mycompany.tienda3a.sunat.SimuladorSunat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista de escritorio (Swing) de CU-02 Registrar venta. Sección 8.3: "las
 * vistas solo capturan datos y muestran resultados"; toda la lógica de
 * negocio vive en ControladorVenta, ComprobanteFactory y las estrategias
 * de pago. Esta clase es la "interfaz de programa" (JFrame), no una
 * página web.
 */
public class VentanaVenta extends JFrame {

    private final ControladorVenta controladorVenta = new ControladorVenta(new SimuladorSunat());
    private final List<Producto> catalogo = new ArrayList<>();
    private static final Caja CAJA_DEMO = new Caja(1, "Caja 1", "B001", "F001");
    private final Caja caja = CAJA_DEMO;
    private final Usuario cajero = new Usuario(1, "Ana Torres", "atorres", "hash-demo", Rol.CAJERO);
    private Venta ventaActual;

    private final JComboBox<Producto> comboProductos = new JComboBox<>();
    private final JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 999.0, 1.0));
    private final DefaultTableModel modeloTabla =
        new DefaultTableModel(new Object[]{"Producto", "Cantidad", "P. Unit.", "Importe"}, 0);
    private final JTable tablaDetalle = new JTable(modeloTabla);

    private final JLabel lblSubtotal = new JLabel("S/ 0.00");
    private final JLabel lblIgv = new JLabel("S/ 0.00");
    private final JLabel lblTotal = new JLabel("S/ 0.00");

    private final JComboBox<String> comboComprobante =
        new JComboBox<>(new String[]{"VENTA_SIMPLE", "BOLETA", "FACTURA"});
    private final JTextField txtDocumentoCliente = new JTextField(12);
    private final JTextField txtNombreCliente = new JTextField(16);

    private final JComboBox<String> comboMedioPago =
        new JComboBox<>(new String[]{"EFECTIVO", "TARJETA", "BILLETERA"});
    private final JTextField txtMontoEntregado = new JTextField(8);
    private final JTextField txtReferencia = new JTextField(12);

    private final JTextArea areaComprobante = new JTextArea(10, 30);

    public VentanaVenta() {
        super("Tiendas 3A — Venta en caja (" + CAJA_DEMO.getNombre() + ")");
        cargarCatalogoDemo();
        iniciarVentaNueva();
        construirInterfaz();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 560);
        setLocationRelativeTo(null);
    }

    private void cargarCatalogoDemo() {
        Categoria abarrotes = new Categoria(1, "Abarrotes");
        Categoria bebidas = new Categoria(2, "Bebidas");
        Categoria granel = new Categoria(3, "A granel");
        catalogo.add(new Producto(1, "7501234567890", "Arroz Costeño 1kg", abarrotes, UnidadMedida.UNIDAD, 5.90, 50));
        catalogo.add(new Producto(2, "7501234567891", "Gaseosa Big Cola 1.5L", bebidas, UnidadMedida.UNIDAD, 4.50, 40));
        catalogo.add(new Producto(3, "7501234567892", "Manzana (granel)", granel, UnidadMedida.KG, 6.90, 25));
    }

    private void iniciarVentaNueva() {
        this.ventaActual = new Venta(1, caja, cajero);
        modeloTabla.setRowCount(0);
        actualizarTotales();
        areaComprobante.setText("");
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(8, 8));

        // --- Panel superior: agregar producto ---
        JPanel panelAgregar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Producto p : catalogo) comboProductos.addItem(p);
        panelAgregar.add(new JLabel("Producto:"));
        panelAgregar.add(comboProductos);
        panelAgregar.add(new JLabel("Cantidad:"));
        panelAgregar.add(spinnerCantidad);
        JButton btnAgregar = new JButton("Agregar a la venta");
        btnAgregar.addActionListener(e -> agregarProducto());
        panelAgregar.add(btnAgregar);

        // --- Centro: tabla + totales ---
        JPanel panelCentro = new JPanel(new BorderLayout(4, 4));
        panelCentro.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);

        JPanel panelTotales = new JPanel(new GridLayout(3, 2, 4, 2));
        panelTotales.add(new JLabel("Subtotal:")); panelTotales.add(lblSubtotal);
        panelTotales.add(new JLabel("IGV (18%):")); panelTotales.add(lblIgv);
        panelTotales.add(new JLabel("TOTAL:")); panelTotales.add(lblTotal);
        panelCentro.add(panelTotales, BorderLayout.SOUTH);

        // --- Panel de cobro ---
        JPanel panelCobro = new JPanel();
        panelCobro.setLayout(new BoxLayout(panelCobro, BoxLayout.Y_AXIS));
        panelCobro.setBorder(BorderFactory.createTitledBorder("Comprobante y pago"));

        JPanel filaComprobante = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaComprobante.add(new JLabel("Comprobante:"));
        filaComprobante.add(comboComprobante);
        filaComprobante.add(new JLabel("Documento (DNI/RUC):"));
        filaComprobante.add(txtDocumentoCliente);
        filaComprobante.add(new JLabel("Nombre/Razón social:"));
        filaComprobante.add(txtNombreCliente);
        panelCobro.add(filaComprobante);

        JPanel filaPago = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaPago.add(new JLabel("Medio de pago:"));
        filaPago.add(comboMedioPago);
        filaPago.add(new JLabel("Monto entregado (efectivo):"));
        filaPago.add(txtMontoEntregado);
        filaPago.add(new JLabel("N° operación (tarjeta/billetera):"));
        filaPago.add(txtReferencia);
        panelCobro.add(filaPago);

        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnConfirmar = new JButton("Confirmar venta");
        btnConfirmar.addActionListener(e -> confirmarVenta());
        JButton btnNueva = new JButton("Nueva venta");
        btnNueva.addActionListener(e -> iniciarVentaNueva());
        filaBotones.add(btnConfirmar);
        filaBotones.add(btnNueva);
        panelCobro.add(filaBotones);

        panelCobro.add(new JLabel("Comprobante emitido:"));
        areaComprobante.setEditable(false);
        areaComprobante.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        panelCobro.add(new JScrollPane(areaComprobante));

        add(panelAgregar, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);
        add(panelCobro, BorderLayout.SOUTH);
    }

    private void agregarProducto() {
        try {
            Producto producto = (Producto) comboProductos.getSelectedItem();
            double cantidad = (double) spinnerCantidad.getValue();
            controladorVenta.agregarProducto(ventaActual, producto, cantidad, null);
            modeloTabla.addRow(new Object[]{
                producto.getNombre(), cantidad,
                String.format("S/ %.2f", producto.getPrecioVenta()),
                String.format("S/ %.2f", producto.getPrecioVenta() * cantidad)
            });
            actualizarTotales();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo agregar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarTotales() {
        lblSubtotal.setText(String.format("S/ %.2f", ventaActual.getSubtotal()));
        lblIgv.setText(String.format("S/ %.2f", ventaActual.getIgv()));
        lblTotal.setText(String.format("S/ %.2f", ventaActual.getTotal()));
    }

    private void confirmarVenta() {
        try {
            String tipoTexto = (String) comboComprobante.getSelectedItem();
            ComprobanteFactory.TipoComprobante tipo = ComprobanteFactory.TipoComprobante.valueOf(tipoTexto);

            Cliente cliente = null;
            if (tipo == ComprobanteFactory.TipoComprobante.BOLETA) {
                cliente = new Cliente(1, TipoDocumento.DNI, txtDocumentoCliente.getText().trim(), txtNombreCliente.getText().trim());
            } else if (tipo == ComprobanteFactory.TipoComprobante.FACTURA) {
                cliente = new Cliente(1, TipoDocumento.RUC, txtDocumentoCliente.getText().trim(), txtNombreCliente.getText().trim());
            }

            String medioTexto = (String) comboMedioPago.getSelectedItem();
            EstrategiaPago estrategia = switch (medioTexto) {
                case "EFECTIVO" -> new PagoEfectivo();
                case "TARJETA" -> new PagoTarjeta();
                default -> new PagoBilletera();
            };
            ProcesadorPago procesadorPago = new ProcesadorPago(estrategia);

            double montoEntregado = medioTexto.equals("EFECTIVO")
                ? Double.parseDouble(txtMontoEntregado.getText().trim())
                : ventaActual.getTotal();
            String referencia = medioTexto.equals("EFECTIVO") ? null : txtReferencia.getText().trim();

            ControladorVenta.ResultadoVenta resultado = controladorVenta.confirmarVenta(
                ventaActual, caja, tipo, cliente, procesadorPago, montoEntregado, referencia);

            StringBuilder salida = new StringBuilder();
            salida.append(resultado.comprobante.generarImpresion());
            if (resultado.pago.getMedio() == MedioDePago.EFECTIVO) {
                salida.append(String.format("Vuelto: S/ %.2f%n", resultado.pago.getVuelto()));
            }
            salida.append("Estado SUNAT: ").append(resultado.aceptadoPorSunat ? "ACEPTADO" : "PENDIENTE/RECHAZADO");
            areaComprobante.setText(salida.toString());

            JOptionPane.showMessageDialog(this, "Venta confirmada correctamente.");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo confirmar la venta", JOptionPane.ERROR_MESSAGE);
        }
    }
}
