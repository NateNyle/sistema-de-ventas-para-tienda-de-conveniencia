package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.config.Configuracion;
import com.mycompany.tienda3a.excepcion.ExcepcionNegocio;
import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class PanelVenta extends PanelBase {
    private Venta venta;
    private final Campo busqueda = new Campo("Escanea o escribe el código o el nombre del producto");
    private final Campo cantidad = new Campo(null, 4);
    private final DefaultListModel<Producto> modeloSugerencias = new DefaultListModel<>();
    private final JList<Producto> sugerencias = new JList<>(modeloSugerencias);
    private final JComponent panelSugerencias;
    private final JTable tabla = Tablas.crear("#", "Producto", "Cantidad", "P. unitario", "Importe");
    private final CardLayout cartasLineas = new CardLayout();
    private final JPanel lineas = new JPanel(cartasLineas);
    private final JLabel resumenLineas = Textos.ayuda("");
    private final Insignia ahorro = new Insignia("", Insignia.Tipo.PROMO);

    private final Selector<TipoComprobante> tipo = new Selector<>(
            List.of(TipoComprobante.VENTA_SIMPLE, TipoComprobante.BOLETA, TipoComprobante.FACTURA),
            List.of("Venta simple", "Boleta", "Factura"), null);
    private final CardLayout cartasCliente = new CardLayout();
    private final JPanel datosCliente = new JPanel(cartasCliente);
    private final Campo dni = new Campo("8 dígitos");
    private final Campo nombre = new Campo("Nombre y apellidos");
    private final Campo ruc = new Campo("11 dígitos");
    private final Campo razon = new Campo("Razón social");
    private final Campo direccion = new Campo("Dirección (opcional)");
    private final JLabel notaSimple = Textos.ayuda("");

    private final Selector<MedioPago> medio = new Selector<>(
            List.of(MedioPago.EFECTIVO, MedioPago.TARJETA, MedioPago.BILLETERA),
            List.of("Efectivo", "Tarjeta", "Billetera"),
            List.of(Icono.Tipo.EFECTIVO, Icono.Tipo.TARJETA, Icono.Tipo.BILLETERA));
    private final CardLayout cartasPago = new CardLayout();
    private final JPanel datosPago = new JPanel(cartasPago);
    private final Campo recibido = new Campo("0.00");
    private final JPanel rapidos = Textos.transparente(new GridLayout(1, 4, 6, 0));
    private final JLabel vuelto = Textos.monto("S/ 0.00", 16, Tema.TEXTO);
    private final Campo operacion = new Campo("Escribe el número");
    private final JLabel etiquetaOperacion = Textos.etiqueta("Número de operación");
    private final JLabel subtotal = Textos.monto("S/ 0.00", 13.5f, Tema.TEXTO);
    private final JLabel igv = Textos.monto("S/ 0.00", 13.5f, Tema.TEXTO);
    private final JLabel ahorroTotal = Textos.monto("S/ 0.00", 13.5f, Tema.AMARILLO_TEXTO);
    private final JLabel total = Textos.monto("S/ 0.00", 30, Color.WHITE);
    private final Boton cobrar = new Boton("Cobrar", Boton.Estilo.EXITO, Icono.Tipo.CHECK).alto(54);

    PanelVenta(Contexto ctx) {
        super(ctx, "Nueva venta", "Escanea los productos, elige el comprobante y cobra.", Icono.Tipo.CARRITO, "CU-02");
        panelSugerencias = construirSugerencias();
        JPanel centro = Textos.transparente(new BorderLayout(16, 0));
        centro.add(columnaIzquierda(), BorderLayout.CENTER);
        centro.add(columnaDerecha(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);
        atajos();
        nuevaVenta();
    }

    @Override
    public void alMostrar() {
        SwingUtilities.invokeLater(busqueda::requestFocusInWindow);
    }

    private JComponent columnaIzquierda() {
        JPanel col = Textos.transparente(new BorderLayout(0, 14));

        Tarjeta agregar = new Tarjeta();
        agregar.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        JPanel fila = Textos.transparente(new BorderLayout(10, 0));
        busqueda.conIcono(Icono.Tipo.BUSCAR);
        busqueda.setFont(Tema.texto(14));
        fila.add(busqueda, BorderLayout.CENTER);
        JPanel der = Textos.transparente(new BorderLayout(10, 0));
        cantidad.setText("1");
        cantidad.setHorizontalAlignment(JTextField.CENTER);
        cantidad.setFont(Tema.cifra(14));
        cantidad.setToolTipText("Cantidad, o peso en kg para productos a granel (por ejemplo 0.750)");
        cantidad.setPreferredSize(new Dimension(96, 40));
        der.add(cantidad, BorderLayout.WEST);
        Boton btnAgregar = new Boton("Agregar", Boton.Estilo.PRIMARIO, Icono.Tipo.MAS);
        btnAgregar.addActionListener(e -> agregar());
        der.add(btnAgregar, BorderLayout.EAST);
        fila.add(der, BorderLayout.EAST);
        JPanel cuerpo = Textos.transparente(new BorderLayout(0, 8));
        cuerpo.add(fila, BorderLayout.NORTH);
        cuerpo.add(panelSugerencias, BorderLayout.CENTER);
        JLabel tecla = Textos.texto("F2 buscar  ·  Enter agregar  ·  flechas: elegir sugerencia  ·  Supr: quitar línea  ·  F9 cobrar",
                11.5f, Tema.TEXTO_TENUE);
        cuerpo.add(tecla, BorderLayout.SOUTH);
        agregar.con(cuerpo);
        col.add(agregar, BorderLayout.NORTH);

        Tarjeta enCurso = new Tarjeta();
        JPanel cab = Textos.transparente(new BorderLayout());
        JPanel cabIzq = Textos.transparente(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel t = new JLabel("Venta en curso");
        t.setFont(Tema.medio(15));
        cabIzq.add(t);
        cabIzq.add(Box.createHorizontalStrut(10));
        cabIzq.add(resumenLineas);
        cab.add(cabIzq, BorderLayout.WEST);
        cab.add(ahorro, BorderLayout.EAST);
        cab.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        enCurso.cuerpo().add(cab, BorderLayout.NORTH);

        tabla.setRowHeight(48);
        Tablas.ancho(tabla, 0, 44, true);
        Tablas.ancho(tabla, 2, 110, true);
        Tablas.ancho(tabla, 3, 110, true);
        Tablas.ancho(tabla, 4, 116, true);
        Tablas.cifra(tabla, 0, 2, 3, 4);
        tabla.getColumnModel().getColumn(1).setCellRenderer(new Tablas.RenderTexto());
        lineas.setOpaque(false);
        lineas.add(Tablas.envolver(tabla), "tabla");
        lineas.add(estadoVacio(), "vacio");
        enCurso.cuerpo().add(lineas, BorderLayout.CENTER);

        JPanel acciones = Textos.transparente(new BorderLayout());
        acciones.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JPanel accIzq = Textos.transparente(new FlowLayout(FlowLayout.LEFT, 0, 0));
        Boton cambiar = new Boton("Cambiar cantidad", Boton.Estilo.SECUNDARIO, Icono.Tipo.EDITAR);
        cambiar.addActionListener(e -> cambiarCantidad());
        Boton quitar = new Boton("Quitar", Boton.Estilo.PELIGRO_SUAVE, Icono.Tipo.BASURA);
        quitar.addActionListener(e -> quitarLinea());
        accIzq.add(cambiar);
        accIzq.add(Box.createHorizontalStrut(8));
        accIzq.add(quitar);
        acciones.add(accIzq, BorderLayout.WEST);
        Boton cancelar = new Boton("Cancelar venta", Boton.Estilo.FANTASMA, Icono.Tipo.CERRAR);
        cancelar.addActionListener(e -> cancelarVenta());
        acciones.add(cancelar, BorderLayout.EAST);
        enCurso.cuerpo().add(acciones, BorderLayout.SOUTH);
        col.add(enCurso, BorderLayout.CENTER);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    cambiarCantidad();
                }
            }
        });
        return col;
    }

    private JComponent estadoVacio() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Tema.SUPERFICIE);
        p.setBorder(BorderFactory.createLineBorder(Tema.BORDE));
        JPanel c = Textos.transparente(null);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        JLabel ic = new JLabel(new Icono(Icono.Tipo.CARRITO, 46, Tema.BORDE_FUERTE));
        ic.setAlignmentX(Component.CENTER_ALIGNMENT);
        c.add(ic);
        c.add(Box.createVerticalStrut(10));
        JLabel a = new JLabel("La venta está vacía");
        a.setFont(Tema.medio(15));
        a.setForeground(Tema.TEXTO_SUAVE);
        a.setAlignmentX(Component.CENTER_ALIGNMENT);
        c.add(a);
        JLabel b = Textos.texto("Escanea un producto o búscalo por su nombre para empezar.", 13, Tema.TEXTO_TENUE);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        c.add(b);
        p.add(c);
        return p;
    }

    private JComponent construirSugerencias() {
        sugerencias.setCellRenderer(new RenderSugerencia());
        sugerencias.setFixedCellHeight(46);
        sugerencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sugerencias.setBackground(Tema.SUPERFICIE);
        sugerencias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (sugerencias.getSelectedValue() != null) {
                    agregarProducto(sugerencias.getSelectedValue());
                }
            }
        });
        JScrollPane sp = Tablas.desplazable(sugerencias);
        sp.setBorder(BorderFactory.createLineBorder(Tema.BORDE));
        sp.setVisible(false);
        busqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { actualizarSugerencias(); }

            @Override
            public void removeUpdate(DocumentEvent e) { actualizarSugerencias(); }

            @Override
            public void changedUpdate(DocumentEvent e) { actualizarSugerencias(); }
        });
        busqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int n = modeloSugerencias.size();
                if (n == 0) {
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    sugerencias.setSelectedIndex(Math.min(n - 1, sugerencias.getSelectedIndex() + 1));
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    sugerencias.setSelectedIndex(Math.max(0, sugerencias.getSelectedIndex() - 1));
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    busqueda.setText("");
                }
            }
        });
        busqueda.addActionListener(e -> agregar());
        cantidad.addActionListener(e -> agregar());
        return sp;
    }

    private JComponent columnaDerecha() {
        PanelDesplazable col = new PanelDesplazable(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.insets = new Insets(0, 0, 10, 0);

        Tarjeta comp = new Tarjeta("Comprobante", null);
        comp.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JPanel cc = Textos.transparente(new BorderLayout(0, 10));
        cc.add(tipo, BorderLayout.NORTH);
        datosCliente.setOpaque(false);
        JPanel simple = Textos.transparente(new BorderLayout());
        notaSimple.setIcon(new Icono(Icono.Tipo.INFO, 16, Tema.TEXTO_SUAVE));
        notaSimple.setIconTextGap(8);
        simple.add(notaSimple, BorderLayout.NORTH);
        datosCliente.add(simple, "VENTA_SIMPLE");
        JPanel boleta = Textos.transparente(new BorderLayout(10, 0));
        dni.setPreferredSize(new Dimension(118, 40));
        boleta.add(Textos.conEtiqueta("DNI", dni), BorderLayout.WEST);
        boleta.add(Textos.conEtiqueta("Nombre del cliente", nombre), BorderLayout.CENTER);
        datosCliente.add(boleta, "BOLETA");
        JPanel factura = Textos.transparente(new BorderLayout(0, 10));
        JPanel f1 = Textos.transparente(new BorderLayout(10, 0));
        ruc.setPreferredSize(new Dimension(130, 40));
        f1.add(Textos.conEtiqueta("RUC", ruc), BorderLayout.WEST);
        f1.add(Textos.conEtiqueta("Razón social", razon), BorderLayout.CENTER);
        factura.add(f1, BorderLayout.NORTH);
        factura.add(direccion, BorderLayout.CENTER);
        datosCliente.add(factura, "FACTURA");
        cc.add(datosCliente, BorderLayout.CENTER);
        comp.con(cc);
        tipo.alCambiar(t -> {
            cartasCliente.show(datosCliente, t.name());
            datosCliente.setPreferredSize(new Dimension(10, t == TipoComprobante.FACTURA ? 112 : t == TipoComprobante.BOLETA ? 62 : 22));
            datosCliente.revalidate();
        });
        tipo.seleccionar(TipoComprobante.VENTA_SIMPLE);
                FiltrosTexto.soloDni(dni);
                FiltrosTexto.soloCantidad(cantidad);
                FiltrosTexto.soloRuc(ruc);
        FiltrosTexto.soloNombre(nombre, Validaciones.MAX_NOMBRE_CLIENTE);
        FiltrosTexto.quitarErrorAlEscribir(nombre);
        dni.getDocument().addDocumentListener(alEscribir(() -> autocompletar(TipoDocumento.DNI, dni, nombre)));
        ruc.getDocument().addDocumentListener(alEscribir(() -> autocompletar(TipoDocumento.RUC, ruc, razon)));
        col.add(comp, g);

        Tarjeta pago = new Tarjeta("Pago", null);
        pago.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JPanel pc = Textos.transparente(new BorderLayout(0, 10));
        pc.add(medio, BorderLayout.NORTH);
        datosPago.setOpaque(false);
        JPanel efectivo = Textos.transparente(new BorderLayout(0, 8));
        JPanel ef1 = Textos.transparente(new BorderLayout(12, 0));
        recibido.setFont(Tema.cifra(15));
        recibido.setHorizontalAlignment(JTextField.RIGHT);
        ef1.add(Textos.conEtiqueta("Monto recibido (S/)", recibido), BorderLayout.CENTER);
        JPanel vu = Textos.transparente(new BorderLayout(0, 6));
        vu.add(Textos.etiqueta("Vuelto"), BorderLayout.NORTH);
        vuelto.setPreferredSize(new Dimension(120, 40));
        vuelto.setHorizontalAlignment(SwingConstants.RIGHT);
        vu.add(vuelto, BorderLayout.CENTER);
        ef1.add(vu, BorderLayout.EAST);
        efectivo.add(ef1, BorderLayout.NORTH);
        efectivo.add(rapidos, BorderLayout.CENTER);
        datosPago.add(efectivo, "EFECTIVO");
        JPanel electronico = Textos.transparente(new BorderLayout(0, 6));
        JPanel filaOperacion = Textos.transparente(new BorderLayout(0, 6));
        filaOperacion.add(etiquetaOperacion, BorderLayout.NORTH);
        filaOperacion.add(operacion, BorderLayout.CENTER);
        electronico.add(filaOperacion, BorderLayout.NORTH);
        electronico.add(Textos.texto("Lo informa el POS o la app del cliente (RN-09).", 11.5f, Tema.TEXTO_TENUE), BorderLayout.CENTER);
        datosPago.add(electronico, "ELECTRONICO");
        pc.add(datosPago, BorderLayout.CENTER);
        pago.con(pc);

        medio.alCambiar(m -> {
            cartasPago.show(datosPago, m == MedioPago.EFECTIVO ? "EFECTIVO" : "ELECTRONICO");
            etiquetaOperacion.setText(m == MedioPago.TARJETA ? "Número de tarjeta" : "Número de operación");
            operacion.setText("");
            operacion.setError(false);
            recibido.setError(false);
        });
            FiltrosNumericos.soloMonto(recibido);
        FiltrosNumericos.soloDigitos(operacion, ValidacionesPago.MAX_DIGITOS_OPERACION);
        recibido.getDocument().addDocumentListener(alEscribir(this::actualizarVuelto));
        col.add(pago, g);

        JPanel totales = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g2 = (Graphics2D) g0.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, Tema.PETROLEO, getWidth(), getHeight(), Tema.PETROLEO_OSCURO));
                g2.fill(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        totales.setOpaque(false);
        totales.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        GridBagConstraints t = new GridBagConstraints();
        t.gridy = 0;
        filaTotal(totales, t, "Op. gravada", subtotal);
        filaTotal(totales, t, "IGV " + Math.round(Configuracion.getTasaIgv() * 100) + "%", igv);
        filaTotal(totales, t, "Ahorro en promociones", ahorroTotal);
        ahorroTotal.setForeground(Tema.AMARILLO);
        t.gridx = 0;
        t.gridy++;
        t.weightx = 1;
        t.anchor = GridBagConstraints.WEST;
        t.insets = new Insets(8, 0, 0, 0);
        JLabel lt = new JLabel("TOTAL");
        lt.setFont(Tema.medio(14));
        lt.setForeground(new Color(0xCFE2E6));
        totales.add(lt, t);
        t.gridx = 1;
        t.anchor = GridBagConstraints.EAST;
        totales.add(total, t);
        g.insets = new Insets(0, 0, 10, 0);
        col.add(totales, g);
        cobrar.addActionListener(e -> cobrar());
        g.insets = new Insets(0, 0, 2, 0);
        col.add(cobrar, g);
        g.weighty = 1;
        col.add(Box.createVerticalGlue(), g);
        javax.swing.JScrollPane sp = col.envolver();
        sp.setPreferredSize(new Dimension(384, 10));
        return sp;
    }

    private void filaTotal(JPanel p, GridBagConstraints t, String etiqueta, JLabel valor) {
        t.gridx = 0;
        t.weightx = 1;
        t.anchor = GridBagConstraints.WEST;
        t.insets = new Insets(2, 0, 2, 0);
        JLabel l = new JLabel(etiqueta);
        l.setFont(Tema.texto(13));
        l.setForeground(new Color(0xCFE2E6));
        p.add(l, t);
        t.gridx = 1;
        t.anchor = GridBagConstraints.EAST;
        valor.setForeground(Color.WHITE);
        p.add(valor, t);
        t.gridy++;
    }

    private void atajos() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "buscar");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "cobrar");
        am.put("buscar", accion(busqueda::requestFocusInWindow));
        am.put("cobrar", accion(this::cobrar));
        tabla.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "quitar");
        tabla.getActionMap().put("quitar", accion(this::quitarLinea));
    }

    private static Action accion(Runnable r) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                r.run();
            }
        };
    }

    private static DocumentListener alEscribir(Runnable r) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { r.run(); }

            @Override
            public void removeUpdate(DocumentEvent e) { r.run(); }

            @Override
            public void changedUpdate(DocumentEvent e) { r.run(); }
        };
    }

    private void nuevaVenta() {
        venta = ctx.venta.iniciarVenta(ctx.getCaja(), (Cajero) ctx.getUsuario());
        busqueda.setText("");
        cantidad.setText("1");
        tipo.seleccionar(TipoComprobante.VENTA_SIMPLE);
        medio.seleccionar(MedioPago.EFECTIVO);
        for (Campo c : new Campo[]{dni, nombre, ruc, razon, direccion, recibido, operacion}) {
            c.setText("");
            c.setError(false);
        }
        refrescar();
        if (!venta.getDetalles().isEmpty()) {
            aviso.mostrar(Aviso.Tipo.INFO, "Retomaste la venta que dejaste en curso antes de cerrar sesión (RNF-16).");
        }
    }

    private void actualizarSugerencias() {
        String t = busqueda.getText().trim();
        modeloSugerencias.clear();
        if (t.length() >= 2) {
            List<Producto> lista = ctx.producto.buscarProductos(t);
            for (int i = 0; i < Math.min(6, lista.size()); i++) {
                modeloSugerencias.addElement(lista.get(i));
            }
        }
        boolean hay = !modeloSugerencias.isEmpty();
        if (hay) {
            sugerencias.setSelectedIndex(0);
        }
        panelSugerencias.setPreferredSize(new Dimension(10, Math.min(6, modeloSugerencias.size()) * 46 + 2));
        panelSugerencias.setVisible(hay);
        panelSugerencias.revalidate();
        revalidate();
    }

    private void agregar() {
        Producto elegido = panelSugerencias.isVisible() ? sugerencias.getSelectedValue() : null;
        if (elegido != null) {
            agregarProducto(elegido);
        } else {
            agregarTexto(busqueda.getText());
        }
    }

    private void agregarProducto(Producto p) {
        agregarTexto(p.getCodigo());
    }

    private boolean cajaCerrada() {
        if (!ctx.caja.cajaAbierta((Cajero) ctx.getUsuario(), ctx.getCaja())) {
            aviso.mostrar(Aviso.Tipo.ADVERTENCIA, "Ya registraste el cierre de " + ctx.getCaja().getCodigo()
                    + " hoy: no se pueden registrar más ventas en esta caja (RN-10).");
            return true;
        }
        return false;
    }

    private void agregarTexto(String texto) {
        aviso.limpiar();
        if (cajaCerrada()) {
            return;
        }
        double cant;
        try {
            cant = Double.parseDouble(cantidad.getText().trim().replace(',', '.'));
            cantidad.setError(false);
        } catch (NumberFormatException e) {
            cantidad.setError(true);
            aviso.mostrar(Aviso.Tipo.ERROR, "Escribe la cantidad con números, por ejemplo 2 o 0.750 kg.");
            return;
        }
        DetalleVenta[] d = new DetalleVenta[1];
        if (ejecutar(() -> d[0] = ctx.venta.agregarProducto(venta, texto, cant))) {
            busqueda.setText("");
            cantidad.setText("1");
            refrescar();
            int fila = venta.getDetalles().indexOf(d[0]);
            tabla.setRowSelectionInterval(fila, fila);
            tabla.scrollRectToVisible(tabla.getCellRect(fila, 0, true));
            busqueda.requestFocusInWindow();
        }
    }

    private DetalleVenta lineaSeleccionada() {
        int f = tabla.getSelectedRow();
        if (f < 0) {
            aviso.mostrar(Aviso.Tipo.ADVERTENCIA, "Primero selecciona una línea de la venta.");
            return null;
        }
        return venta.getDetalles().get(f);
    }

    private void cambiarCantidad() {
        DetalleVenta d = lineaSeleccionada();
        if (d == null) {
            return;
        }
        String unidad = d.getProducto().getUnidadMedida() == UnidadMedida.KG ? "Peso en kg" : "Cantidad";
        String r = Dialogos.pedirTexto(this, "Cambiar cantidad · " + d.getProducto().getNombre(), unidad,
                d.getProducto().getUnidadMedida() == UnidadMedida.KG ? String.format("%.3f", d.getCantidad())
                        : String.format("%.0f", d.getCantidad()), "Cambiar");
        if (r == null) {
            return;
        }
        try {
            double c = Double.parseDouble(r.trim().replace(',', '.'));
            if (ejecutar(() -> ctx.venta.modificarLinea(venta, d, c))) {
                refrescar();
            }
        } catch (NumberFormatException e) {
            aviso.mostrar(Aviso.Tipo.ERROR, "Escribe la cantidad con números.");
        }
    }

    private void quitarLinea() {
        DetalleVenta d = lineaSeleccionada();
        if (d != null && ejecutar(() -> ctx.venta.retirarLinea(venta, d))) {
            refrescar();
        }
    }

    private void cancelarVenta() {
        if (venta.getDetalles().isEmpty()) {
            return;
        }
        if (Dialogos.confirmar(this, "¿Cancelar la venta?", "Se descartan las " + venta.getDetalles().size()
                + " líneas. No se cobra nada ni cambia el stock (RF-07).", "Cancelar venta", Boton.Estilo.PELIGRO)) {
            ctx.venta.cancelarVenta(venta);
            nuevaVenta();
            aviso.mostrar(Aviso.Tipo.INFO, "Venta cancelada. Puedes empezar una nueva.");
        }
    }

    private void autocompletar(TipoDocumento td, Campo doc, Campo nom) {
        String n = doc.getText().trim();
        doc.setError(false);
        if ((td == TipoDocumento.DNI && n.length() == 8) || (td == TipoDocumento.RUC && n.length() == 11)) {
            Cliente c = ctx.venta.buscarCliente(td, n);
            if (c != null && nom.getText().isBlank()) {
                FiltrosTexto.asignar(nom, c.getNombreRazonSocial());
                if (td == TipoDocumento.RUC && direccion.getText().isBlank()) {
                    direccion.setText(c.getDireccion());
                }
            }
        }
    }

    private void actualizarVuelto() {
        double r = leerMonto(recibido.getText());
        double v = r - venta.getTotal();
        recibido.setError(false);
        vuelto.setText(Tema.soles(Math.max(0, v)));
        vuelto.setForeground(r <= 0 ? Tema.TEXTO_TENUE : v >= -0.001 ? Tema.VERDE : Tema.ROJO);
        if (r > 0 && v < -0.001) {
            vuelto.setText("Falta " + Tema.numero(-v));
        }
    }

    private static double leerMonto(String t) {
        try {
            return Double.parseDouble(t.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void cobrar() {
        aviso.limpiar();
        if (cajaCerrada()) {
            return;
        }
        if (venta.getDetalles().isEmpty()) {
            aviso.mostrar(Aviso.Tipo.ADVERTENCIA, "Agrega al menos un producto antes de cobrar.");
            return;
        }
        TipoComprobante t = tipo.getSeleccionado();
        MedioPago m = medio.getSeleccionado();
        if (m == MedioPago.EFECTIVO && ValidacionesPago.errorMonto(recibido.getText()) != null) {
            recibido.setError(true);
            aviso.mostrar(Aviso.Tipo.ERROR, ValidacionesPago.errorMonto(recibido.getText()));
            recibido.requestFocusInWindow();
            return;
        }
        if (m != MedioPago.EFECTIVO && ValidacionesPago.errorOperacion(operacion.getText()) != null) {
            operacion.setError(true);
            aviso.mostrar(Aviso.Tipo.ERROR, ValidacionesPago.errorOperacion(operacion.getText()));
            operacion.requestFocusInWindow();
            return;
        }
                if (t == TipoComprobante.BOLETA) {
            String errorDni = Validaciones.errorDni(dni.getText());
            String errorNombre = Validaciones.errorNombre("nombre del cliente", nombre.getText(), Validaciones.MAX_NOMBRE_CLIENTE);
            if (errorDni != null || errorNombre != null) {
                dni.setError(errorDni != null);
                nombre.setError(errorDni == null);
                aviso.mostrar(Aviso.Tipo.ERROR, errorDni != null ? errorDni : errorNombre);
                (errorDni != null ? dni : nombre).requestFocusInWindow();
                return;
            }
        }
                if (t == TipoComprobante.FACTURA) {
            String errorRuc = Validaciones.errorRuc(ruc.getText());
            if (errorRuc != null) {
                ruc.setError(true);
                aviso.mostrar(Aviso.Tipo.ERROR, errorRuc);
                ruc.requestFocusInWindow();
                return;
            }
        }
        Comprobante[] comp = new Comprobante[1];
        boolean ok = ejecutar(() -> {
            Cliente cliente = null;
            try {
                if (t == TipoComprobante.BOLETA) {
                    cliente = ctx.venta.obtenerCliente(TipoDocumento.DNI, dni.getText(), nombre.getText(), null);
                } else if (t == TipoComprobante.FACTURA) {
                    cliente = ctx.venta.obtenerCliente(TipoDocumento.RUC, ruc.getText(), razon.getText(), direccion.getText());
                }
            } catch (ExcepcionNegocio e) {
                if (t == TipoComprobante.BOLETA) {
                    boolean dniIncorrecto = Validaciones.errorDni(dni.getText()) != null;
                    dni.setError(dniIncorrecto);
                    nombre.setError(!dniIncorrecto);
                } else {
                    ruc.setError(true);
                }
                throw e;
            }
            ctx.venta.elegirComprobante(venta, t, cliente);
            Pago p = ctx.venta.nuevoPago(venta, m, leerMonto(recibido.getText()), operacion.getText());
            try {
                ctx.venta.registrarPago(venta, p);
            } catch (ExcepcionNegocio e) {
                (m == MedioPago.EFECTIVO ? recibido : operacion).setError(true);
                throw e;
            }
            comp[0] = ctx.venta.confirmarVenta(venta, p, t);
        });
        if (!ok) {
            refrescar();
            return;
        }
        mostrarComprobante(comp[0]);
        nuevaVenta();
        exito("Venta registrada con " + comp[0].getTipo().getEtiqueta().toLowerCase() + " " + comp[0].getNumero() + ".");
    }

    private void mostrarComprobante(Comprobante c) {
        JPanel p = Textos.transparente(new BorderLayout(0, 12));
        JPanel cab = Textos.transparente(new FlowLayout(FlowLayout.LEFT, 0, 0));
        Insignia sunat = new Insignia("SUNAT: " + c.getEstado().getEtiqueta(), switch (c.getEstado()) {
            case ACEPTADO -> Insignia.Tipo.EXITO;
            case PENDIENTE -> Insignia.Tipo.ADVERTENCIA;
            default -> Insignia.Tipo.PELIGRO;
        });
        cab.add(sunat);
        if (c.getVenta().getPago() instanceof PagoEfectivo e && e.getVuelto() > 0) {
            cab.add(Box.createHorizontalStrut(10));
            cab.add(new Insignia("Entregar vuelto: " + Tema.soles(e.getVuelto()), Insignia.Tipo.MARCA));
        }
        p.add(cab, BorderLayout.NORTH);
        p.add(Dialogos.ticket(c.generarImpresion()), BorderLayout.CENTER);
        if (c.getEstado() != EstadoComprobante.ACEPTADO) {
            p.add(Textos.texto("<html><div style='width:400px'>" + (c.getEstado() == EstadoComprobante.PENDIENTE
                    ? "El simulador no respondió: el sistema lo reenviará cada 5 minutos, hasta 3 veces (RN-15)."
                    : "El simulador rechazó el comprobante. La venta sigue confirmada y el administrador lo revisará (CU-13).")
                    + "</div></html>", 12, Tema.TEXTO_SUAVE), BorderLayout.SOUTH);
        }
        Dialogos.mostrarContenido(this, "Venta registrada · " + c.getNumero(), p);
    }

    private void refrescar() {
        venta.calcularTotales();
        var modelo = Tablas.modelo(tabla);
        modelo.setRowCount(0);
        int i = 1;
        for (DetalleVenta d : venta.getDetalles()) {
            Producto p = d.getProducto();
            String sub = d.tienePromocion()
                    ? "<span style='color:#7A5A00'>Promoción · antes S/ " + Tema.numero(d.getPrecioLista()) + "</span>"
                    : "<span style='color:#8A989C'>" + p.getCodigo() + " · " + p.getCategoria().getNombre() + "</span>";
            modelo.addRow(new Object[]{i++, "<html>" + p.getNombre() + "<br><span style='font-size:9.5px'>" + sub + "</span></html>",
                    p.formatearCantidad(d.getCantidad()), Tema.numero(d.getPrecioUnitario()), Tema.numero(d.getImporte())});
        }
        cartasLineas.show(lineas, venta.getDetalles().isEmpty() ? "vacio" : "tabla");
        int n = venta.getCantidadArticulos();
        resumenLineas.setText(venta.getDetalles().isEmpty() ? "" : venta.getDetalles().size() + " líneas · " + n
                + (n == 1 ? " artículo" : " artículos"));
        double a = venta.getAhorroTotal();
        ahorro.setText("Ahorro " + Tema.soles(a));
        ahorro.setVisible(a > 0);
        subtotal.setText(Tema.soles(venta.getSubtotal()));
        igv.setText(Tema.soles(venta.getIgv()));
        ahorroTotal.setText(a > 0 ? "− " + Tema.soles(a) : Tema.soles(0));
        total.setText(Tema.soles(venta.getTotal()));
        cobrar.setText(venta.getTotal() > 0 ? "Cobrar " + Tema.soles(venta.getTotal()) + "   (F9)" : "Cobrar");
        cobrar.setEnabled(!venta.getDetalles().isEmpty());
        double max = Configuracion.getMontoMaximoVentaSimple();
        notaSimple.setText(venta.getTotal() > max
                ? "<html>La venta supera S/ " + Tema.numero(max) + ": elige boleta o factura (RN-05).</html>"
                : "<html>Sin datos del cliente · hasta S/ " + Tema.numero(max) + "</html>");
        notaSimple.setForeground(venta.getTotal() > max ? Tema.ROJO : Tema.TEXTO_SUAVE);
        actualizarRapidos();
        actualizarVuelto();
    }

    private void actualizarRapidos() {
        rapidos.removeAll();
        double t = venta.getTotal();
        Boton exacto = rapido("Exacto", t);
        rapidos.add(exacto);
        int puestos = 0;
        for (int b : new int[]{10, 20, 50, 100, 200}) {
            if (b > t && puestos < 3) {
                rapidos.add(rapido("S/ " + b, b));
                puestos++;
            }
        }
        while (puestos++ < 3) {
            rapidos.add(new JLabel());
        }
        rapidos.revalidate();
        rapidos.repaint();
    }

    private Boton rapido(String texto, double monto) {
        Boton b = new Boton(texto, Boton.Estilo.SECUNDARIO);
        b.alto(34);
        b.setFont(Tema.medio(12));
        b.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        b.addActionListener(e -> recibido.setText(String.format(java.util.Locale.US, "%.2f", monto)));
        b.setEnabled(monto > 0);
        return b;
    }

    private static class RenderSugerencia extends JPanel implements ListCellRenderer<Producto> {
        private final JLabel nombre = new JLabel();
        private final JLabel detalle = new JLabel();
        private final JLabel precio = new JLabel();
        private final JLabel stock = new JLabel();

        RenderSugerencia() {
            super(new BorderLayout(10, 0));
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            JPanel izq = Textos.transparente(new GridLayout(2, 1));
            nombre.setFont(Tema.medio(13));
            detalle.setFont(Tema.texto(11.5f));
            detalle.setForeground(Tema.TEXTO_TENUE);
            izq.add(nombre);
            izq.add(detalle);
            add(izq, BorderLayout.CENTER);
            JPanel der = Textos.transparente(new GridLayout(2, 1));
            precio.setFont(Tema.cifraNegrita(13));
            precio.setHorizontalAlignment(SwingConstants.RIGHT);
            stock.setFont(Tema.texto(11.5f));
            stock.setHorizontalAlignment(SwingConstants.RIGHT);
            der.add(precio);
            der.add(stock);
            add(der, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Producto> l, Producto p, int i, boolean sel, boolean foco) {
            setBackground(sel ? Tema.PETROLEO_SUAVE : Tema.SUPERFICIE);
            nombre.setText(p.getNombre());
            detalle.setText(p.getCodigo() + " · " + p.getCategoria().getNombre());
            Promocion promo = p.promocionVigente(LocalDate.now());
            if (promo != null) {
                precio.setText("<html><span style='color:#8A989C'><s>" + Tema.numero(p.getPrecioVenta())
                        + "</s></span> <span style='color:#7A5A00'>S/ " + Tema.numero(promo.getPrecioPromocional()) + "</span></html>");
            } else {
                precio.setText("S/ " + Tema.numero(p.getPrecioVenta()));
                precio.setForeground(Tema.TEXTO);
            }
            boolean bajo = p.getStockActual() < 10;
            stock.setText("Stock: " + p.formatearCantidad(p.getStockActual()));
            stock.setForeground(p.getStockActual() <= 0 ? Tema.ROJO : bajo ? Tema.AMBAR : Tema.TEXTO_SUAVE);
            return this;
        }
    }
}
