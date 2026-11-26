package com.mycompany.tienda3a.controlador;

import com.mycompany.tienda3a.excepcion.ExcepcionNegocio;
import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.repositorio.RepositorioUsuarios;
import com.mycompany.tienda3a.repositorio.RepositorioVentas;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ControladorUsuario {
    private final RepositorioUsuarios usuarios;
    private final RepositorioVentas ventas;
    private final Bitacora bitacora;

    public ControladorUsuario(RepositorioUsuarios usuarios, RepositorioVentas ventas, Bitacora bitacora) {
        this.usuarios = usuarios;
        this.ventas = ventas;
        this.bitacora = bitacora;
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = usuarios.listar();
        lista.sort(Comparator.comparing(Usuario::getNombre));
        return lista;
    }

    public Usuario registrarUsuario(String nombre, String nombreAcceso, Rol rol, String claveInicial,
                                    Administrador administrador) {
                String errorNombre = Validaciones.errorNombre("nombre", nombre, Validaciones.MAX_NOMBRE_USUARIO);
        if (errorNombre != null) {
            throw new ExcepcionNegocio(errorNombre);
        }
        nombre = Validaciones.normalizarNombre(nombre);
        if (rol == null) {
            throw new ExcepcionNegocio("Elige el rol del usuario.");
        }
        if (nombreAcceso != null && usuarios.buscarPorAcceso(nombreAcceso.trim()) != null) {
            throw new ExcepcionNegocio("El nombre de acceso «" + nombreAcceso.trim() + "» ya está en uso.");
        }
        Usuario u;
        try {
            u = rol == Rol.ADMINISTRADOR
                    ? new Administrador(0, nombre, nombreAcceso, null, true, LocalDateTime.now())
                    : new Cajero(0, nombre, nombreAcceso, null, true, LocalDateTime.now());
            u.cambiarClave(claveInicial);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
        usuarios.guardar(u);
        bitacora.registrar(administrador, "REGISTRAR_USUARIO", "usuario", u.getIdUsuario(), ResultadoOperacion.EXITO,
                u.getNombreAcceso() + " · " + rol.name());
        return u;
    }

        public Usuario modificarUsuario(Usuario usuario, String nombre, Rol rol, Administrador administrador) {
        String errorNombre = Validaciones.errorNombre("nombre", nombre, Validaciones.MAX_NOMBRE_USUARIO);
        if (errorNombre != null) {
            throw new ExcepcionNegocio(errorNombre);
        }
        nombre = Validaciones.normalizarNombre(nombre);
        try {
            usuario.setNombre(nombre);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
        Usuario resultado = usuario;
        if (rol != null && rol != usuario.getRol()) {
            if (usuario.getIdUsuario() == administrador.getIdUsuario()) {
                throw new ExcepcionNegocio("No puedes cambiar tu propio rol.");
            }
            if (tieneOperaciones(usuario)) {
                throw new ExcepcionNegocio("No se puede cambiar el rol de un cajero que ya registró ventas: su historial "
                        + "quedaría sin cajero. Registra un usuario nuevo con el otro rol.");
            }
            resultado = rol == Rol.ADMINISTRADOR
                    ? new Administrador(usuario.getIdUsuario(), usuario.getNombre(), usuario.getNombreAcceso(),
                            usuario.getClaveHash(), usuario.isActivo(), usuario.getCreadoEn())
                    : new Cajero(usuario.getIdUsuario(), usuario.getNombre(), usuario.getNombreAcceso(),
                            usuario.getClaveHash(), usuario.isActivo(), usuario.getCreadoEn());
        }
        usuarios.guardar(resultado);
        bitacora.registrar(administrador, "MODIFICAR_USUARIO", "usuario", resultado.getIdUsuario(),
                ResultadoOperacion.EXITO, resultado.getNombreAcceso() + " · " + resultado.getRol().name());
        return resultado;
    }

    public void desactivarUsuario(Usuario usuario, Administrador administrador) {
        if (!administrador.puedeDesactivar(usuario)) {
            throw new ExcepcionNegocio("No puedes desactivar tu propio usuario.");
        }
        usuario.desactivar();
        usuarios.guardar(usuario);
        bitacora.registrar(administrador, "DESACTIVAR_USUARIO", "usuario", usuario.getIdUsuario(),
                ResultadoOperacion.EXITO, usuario.getNombreAcceso());
    }

    public void activarUsuario(Usuario usuario, Administrador administrador) {
        usuario.setActivo(true);
        usuarios.guardar(usuario);
        bitacora.registrar(administrador, "ACTIVAR_USUARIO", "usuario", usuario.getIdUsuario(),
                ResultadoOperacion.EXITO, usuario.getNombreAcceso());
    }

    public void desbloquearUsuario(Usuario usuario, Administrador administrador) {
        usuario.desbloquear();
        usuarios.guardar(usuario);
        bitacora.registrar(administrador, "DESBLOQUEAR_USUARIO", "usuario", usuario.getIdUsuario(),
                ResultadoOperacion.EXITO, usuario.getNombreAcceso());
    }

    public void restablecerClave(Usuario usuario, String nuevaClave, Administrador administrador) {
        try {
            usuario.cambiarClave(nuevaClave);
        } catch (IllegalArgumentException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
        usuario.desbloquear();
        usuarios.guardar(usuario);
        bitacora.registrar(administrador, "RESTABLECER_CLAVE", "usuario", usuario.getIdUsuario(),
                ResultadoOperacion.EXITO, usuario.getNombreAcceso());
    }

    private boolean tieneOperaciones(Usuario usuario) {
        for (Venta v : ventas.listar()) {
            if (v.getCajero().getIdUsuario() == usuario.getIdUsuario()) {
                return true;
            }
        }
        return false;
    }
}
