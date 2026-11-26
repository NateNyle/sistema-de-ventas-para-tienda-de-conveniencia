package com.mycompany.tienda3a.vista;

import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.vista.componentes.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class PanelUsuarios extends PanelBase {
    private final JTable tabla = Tablas.crear("Nombre", "Usuario", "Rol", "Estado", "Registrado");
    private final List<Usuario> lista = new ArrayList<>();
    private Usuario editando;
    private final JLabel tituloForm = new JLabel();
    private final Campo nombre = new Campo("Nombre y apellidos");
    private final Campo acceso = new Campo("Ej.: mrojas");
    private final Combo<Rol> rol = new Combo<>(List.of(Rol.CAJERO, Rol.ADMINISTRADOR), Rol::getEtiqueta);
    private final Campo.CampoClave clave = new Campo.CampoClave("Mínimo 8 caracteres");
    private final JPanel filaClave;
    private final Boton guardar = new Boton("Registrar usuario", Boton.Estilo.PRIMARIO, Icono.Tipo.CHECK);
    private final Boton desbloquear = new Boton("Desbloquear", Boton.Estilo.SECUNDARIO, Icono.Tipo.CANDADO);
    private final Boton restablecer = new Boton("Nueva contraseña", Boton.Estilo.SECUNDARIO, Icono.Tipo.EDITAR);
    private final Boton estado = new Boton("Desactivar usuario", Boton.Estilo.PELIGRO_SUAVE);

    PanelUsuarios(Contexto ctx) {
        super(ctx, "Usuarios", "Cajeros y administradores. Los usuarios no se borran: se desactivan (RN-11).",
                Icono.Tipo.USUARIOS, "CU-12");
        filaClave = Textos.conEtiqueta("Contraseña inicial", clave);
        FiltrosTexto.soloNombre(nombre, Validaciones.MAX_NOMBRE_USUARIO);
        FiltrosTexto.quitarErrorAlEscribir(nombre);
        JPanel centro = Textos.transparente(new BorderLayout(16, 0));
        Tarjeta t = new Tarjeta();
        Tablas.ancho(tabla, 1, 120, true);
        Tablas.ancho(tabla, 2, 150, true);
        Tablas.ancho(tabla, 3, 130, true);
        Tablas.ancho(tabla, 4, 110, true);
        Tablas.insignia(tabla, 2, v -> "Administrador".equals(v) ? Insignia.Tipo.MARCA : Insignia.Tipo.NEUTRO);
        Tablas.insignia(tabla, 3, v -> switch (String.valueOf(v)) {
            case "Activo" -> Insignia.Tipo.EXITO;
            case "Bloqueado" -> Insignia.Tipo.ADVERTENCIA;
            default -> Insignia.Tipo.PELIGRO;
        });
        t.con(Tablas.envolver(tabla));
        centro.add(t, BorderLayout.CENTER);
        centro.add(formulario(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                editar(lista.get(tabla.getSelectedRow()));
            }
        });
    }

    private JComponent formulario() {
        Tarjeta t = new Tarjeta();
        JPanel p = Textos.transparente(null);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JPanel cab = Textos.transparente(new BorderLayout());
        tituloForm.setFont(Tema.negrita(17));
        cab.add(tituloForm, BorderLayout.WEST);
        Boton nuevo = new Boton("Nuevo", Boton.Estilo.FANTASMA, Icono.Tipo.MAS);
        nuevo.addActionListener(e -> limpiar());
        cab.add(nuevo, BorderLayout.EAST);
        p.add(fila(cab));
        p.add(Box.createVerticalStrut(14));
        p.add(fila(Textos.conEtiqueta("Nombre completo", nombre)));
        p.add(Box.createVerticalStrut(12));
        p.add(fila(Textos.conEtiqueta("Nombre de acceso", acceso)));
        p.add(Box.createVerticalStrut(12));
        p.add(fila(Textos.conEtiqueta("Rol", rol)));
        p.add(Box.createVerticalStrut(12));
        p.add(fila(filaClave));
        p.add(Box.createVerticalStrut(16));
        guardar.addActionListener(e -> guardar());
        p.add(fila(guardar));
        p.add(Box.createVerticalStrut(8));
        JPanel dos = Textos.transparente(new GridLayout(1, 2, 8, 0));
        dos.add(desbloquear);
        dos.add(restablecer);
        p.add(fila(dos));
        p.add(Box.createVerticalStrut(8));
        p.add(fila(estado));
        desbloquear.addActionListener(e -> {
            Usuario u = editando;
            if (u != null && ejecutar(() -> ctx.usuarios.desbloquearUsuario(u, admin()))) {
                cargar();
                seleccionar(u);
                exito(u.getNombre() + " ya puede iniciar sesión.");
            }
        });
        restablecer.addActionListener(e -> restablecer());
        estado.addActionListener(e -> cambiarEstado());
        t.con(p);
        limpiar();
        return Textos.ancho(t, 350);
    }

    private static JComponent fila(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        return c;
    }

    private Administrador admin() {
        return (Administrador) ctx.getUsuario();
    }

    @Override
    public void alMostrar() {
        cargar();
    }

    private void cargar() {
        lista.clear();
        lista.addAll(ctx.usuarios.listarUsuarios());
        var m = Tablas.modelo(tabla);
        m.setRowCount(0);
        for (Usuario u : lista) {
            String est = !u.isActivo() ? "Inactivo" : u.estaBloqueado() ? "Bloqueado" : "Activo";
            m.addRow(new Object[]{u.getNombre() + (u.getIdUsuario() == ctx.getUsuario().getIdUsuario() ? "  (tú)" : ""),
                    u.getNombreAcceso(), u.getRol().getEtiqueta(), est, u.getCreadoEn().format(Tema.FECHA)});
        }
    }

    private void limpiar() {
        editando = null;
        tabla.clearSelection();
        tituloForm.setText("Nuevo usuario");
        nombre.setText("");
        nombre.setError(false);
        acceso.setText("");
        acceso.setEditable(true);
        clave.setText("");
        rol.setSelectedItem(Rol.CAJERO);
        filaClave.setVisible(true);
        guardar.setText("Registrar usuario");
        desbloquear.setVisible(false);
        restablecer.setVisible(false);
        estado.setVisible(false);
    }

    private void editar(Usuario u) {
        editando = u;
        aviso.limpiar();
        tituloForm.setText("Editar usuario");
        FiltrosTexto.asignar(nombre, u.getNombre());
        nombre.setError(false);
        acceso.setText(u.getNombreAcceso());
        acceso.setEditable(false);
        rol.setSelectedItem(u.getRol());
        filaClave.setVisible(false);
        guardar.setText("Guardar cambios");
        desbloquear.setVisible(true);
        desbloquear.setEnabled(u.estaBloqueado() || u.getIntentosFallidos() > 0);
        restablecer.setVisible(true);
        estado.setVisible(true);
        estado.setText(u.isActivo() ? "Desactivar usuario" : "Activar usuario");
        estado.setEstilo(u.isActivo() ? Boton.Estilo.PELIGRO_SUAVE : Boton.Estilo.SECUNDARIO);
        estado.setEnabled(u.getIdUsuario() != ctx.getUsuario().getIdUsuario());
    }

    private void guardar() {
        aviso.limpiar();
        nombre.setError(Validaciones.errorNombre("nombre", nombre.getText(), Validaciones.MAX_NOMBRE_USUARIO) != null);
        if (editando == null) {
            Usuario[] r = new Usuario[1];
            if (ejecutar(() -> r[0] = ctx.usuarios.registrarUsuario(nombre.getText(), acceso.getText(), rol.seleccionado(),
                    new String(clave.getPassword()), admin()))) {
                cargar();
                seleccionar(r[0]);
                exito("Usuario «" + r[0].getNombreAcceso() + "» registrado como " + r[0].getRol().getEtiqueta().toLowerCase() + ".");
            }
        } else {
            Usuario[] r = new Usuario[1];
            Usuario u = editando;
            if (ejecutar(() -> r[0] = ctx.usuarios.modificarUsuario(u, nombre.getText(), rol.seleccionado(), admin()))) {
                cargar();
                seleccionar(r[0]);
                exito("Cambios guardados.");
            }
        }
    }

    private void restablecer() {
        Usuario u = editando;
        if (u == null) {
            return;
        }
        String nueva = Dialogos.pedirTexto(this, "Nueva contraseña · " + u.getNombreAcceso(), "Contraseña (mínimo 8 caracteres)",
                "", "Guardar");
        if (nueva != null && ejecutar(() -> ctx.usuarios.restablecerClave(u, nueva, admin()))) {
            exito("Contraseña actualizada. Entrégasela a " + u.getNombre() + " en persona.");
        }
    }

    private void cambiarEstado() {
        Usuario u = editando;
        if (u == null) {
            return;
        }
        if (u.isActivo()) {
            if (Dialogos.confirmar(this, "¿Desactivar a " + u.getNombre() + "?", "No podrá iniciar sesión. Su historial de "
                    + "ventas y cierres se conserva.", "Desactivar", Boton.Estilo.PELIGRO)
                    && ejecutar(() -> ctx.usuarios.desactivarUsuario(u, admin()))) {
                cargar();
                seleccionar(u);
                exito("Usuario desactivado.");
            }
        } else if (ejecutar(() -> ctx.usuarios.activarUsuario(u, admin()))) {
            cargar();
            seleccionar(u);
            exito("Usuario activado.");
        }
    }

    private void seleccionar(Usuario u) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdUsuario() == u.getIdUsuario()) {
                tabla.setRowSelectionInterval(i, i);
                return;
            }
        }
    }
}
