package com.mycompany.tienda3a.modelo;

/** Entidad del diagrama: USUARIO. */
public class Usuario {
    private int idUsuario;
    private int idTienda;
    private int idRol;
    private String usuario;
    private String nombre;
    private String clave;
    private boolean activo;

    public Usuario(int idUsuario, int idTienda, int idRol, String usuario, String nombre, String clave, boolean activo) {
        this.idUsuario = idUsuario;
        this.idTienda = idTienda;
        this.idRol = idRol;
        this.usuario = usuario;
        this.nombre = nombre;
        this.clave = clave;
        this.activo = activo;
    }

    public int getIdUsuario() { return idUsuario; }
    public int getIdTienda() { return idTienda; }
    public int getIdRol() { return idRol; }
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getClave() { return clave; }
    public boolean isActivo() { return activo; }

    public void desactivar() { this.activo = false; }

    @Override
    public String toString() {
        return "Usuario{" + "idUsuario=" + idUsuario + ", " + "idTienda=" + idTienda + ", " + "idRol=" + idRol + ", " + "usuario=" + usuario + ", " + "nombre=" + nombre + ", " + "activo=" + activo + "}";
    }
}
