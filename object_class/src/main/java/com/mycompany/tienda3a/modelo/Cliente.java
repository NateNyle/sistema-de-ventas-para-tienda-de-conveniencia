package com.mycompany.tienda3a.modelo;

/** Entidad del diagrama: CLIENTE. */
public class Cliente {
    private int idCliente;
    private String documento;
    private String nombre;
    private String tipoComprobante;

    public Cliente(int idCliente, String documento, String nombre, String tipoComprobante) {
        this.idCliente = idCliente;
        this.documento = documento;
        this.nombre = nombre;
        this.tipoComprobante = tipoComprobante;
    }

    public int getIdCliente() { return idCliente; }
    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public String getTipoComprobante() { return tipoComprobante; }

    @Override
    public String toString() {
        return "Cliente{" + "idCliente=" + idCliente + ", " + "documento=" + documento + ", " + "nombre=" + nombre + ", " + "tipoComprobante=" + tipoComprobante + "}";
    }
}
