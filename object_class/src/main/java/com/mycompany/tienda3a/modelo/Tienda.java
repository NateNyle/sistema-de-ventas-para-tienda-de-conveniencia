package com.mycompany.tienda3a.modelo;

/** Entidad del diagrama: TIENDA. */
public class Tienda {
    private int idTienda;
    private String codigo;
    private String nombre;
    private String distrito;
    private String direccion;
    private boolean activo;

    public Tienda(int idTienda, String codigo, String nombre, String distrito, String direccion, boolean activo) {
        this.idTienda = idTienda;
        this.codigo = codigo;
        this.nombre = nombre;
        this.distrito = distrito;
        this.direccion = direccion;
        this.activo = activo;
    }

    public int getIdTienda() { return idTienda; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDistrito() { return distrito; }
    public String getDireccion() { return direccion; }
    public boolean isActivo() { return activo; }

    @Override
    public String toString() {
        return "Tienda{" + "idTienda=" + idTienda + ", " + "codigo=" + codigo + ", " + "nombre=" + nombre + ", " + "distrito=" + distrito + ", " + "direccion=" + direccion + ", " + "activo=" + activo + "}";
    }
}
