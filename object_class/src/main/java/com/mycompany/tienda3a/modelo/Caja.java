package com.mycompany.tienda3a.modelo;

/** Entidad del diagrama: CAJA. */
public class Caja {
    private int idCaja;
    private int idTienda;
    private String codigo;
    private boolean activo;

    public Caja(int idCaja, int idTienda, String codigo, boolean activo) {
        this.idCaja = idCaja;
        this.idTienda = idTienda;
        this.codigo = codigo;
        this.activo = activo;
    }

    public int getIdCaja() { return idCaja; }
    public int getIdTienda() { return idTienda; }
    public String getCodigo() { return codigo; }
    public boolean isActivo() { return activo; }

    @Override
    public String toString() {
        return "Caja{" + "idCaja=" + idCaja + ", " + "idTienda=" + idTienda + ", " + "codigo=" + codigo + ", " + "activo=" + activo + "}";
    }
}
