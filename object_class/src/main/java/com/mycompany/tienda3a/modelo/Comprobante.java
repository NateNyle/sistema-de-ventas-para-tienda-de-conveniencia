package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

/** Entidad del diagrama: COMPROBANTE. */
public class Comprobante {
    private int idComprobante;
    private int idVenta;
    private String tipo;
    private String serie;
    private String numero;
    private String estadoSunat;
    private LocalDateTime fechaEmision;

    public Comprobante(int idComprobante, int idVenta, String tipo, String serie, String numero, String estadoSunat, LocalDateTime fechaEmision) {
        this.idComprobante = idComprobante;
        this.idVenta = idVenta;
        this.tipo = tipo;
        this.serie = serie;
        this.numero = numero;
        this.estadoSunat = estadoSunat;
        this.fechaEmision = fechaEmision;
    }

    public int getIdComprobante() { return idComprobante; }
    public int getIdVenta() { return idVenta; }
    public String getTipo() { return tipo; }
    public String getSerie() { return serie; }
    public String getNumero() { return numero; }
    public String getEstadoSunat() { return estadoSunat; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }

    /** Metodo propio de esta clase. */
    public void imprimir() {
        System.out.println("COMPROBANTE " + tipo + " " + serie + "-" + numero
                + " | Venta: " + idVenta + " | Estado SUNAT: " + estadoSunat);
    }

    @Override
    public String toString() {
        return "Comprobante{" + "idComprobante=" + idComprobante + ", " + "idVenta=" + idVenta + ", " + "tipo=" + tipo + ", " + "serie=" + serie + ", " + "numero=" + numero + ", " + "estadoSunat=" + estadoSunat + ", " + "fechaEmision=" + fechaEmision + "}";
    }
}
