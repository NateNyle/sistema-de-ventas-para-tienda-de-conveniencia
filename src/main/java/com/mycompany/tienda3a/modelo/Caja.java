package com.mycompany.tienda3a.modelo;

/**
 * Punto de cobro de la tienda (hasta 3 cajas, S-01). Define las series de sus
 * comprobantes: serieBoleta (B00n, usada por venta simple y boleta) y
 * serieFactura (F00n).
 */
public class Caja {

    private int id;
    private String nombre;
    private String serieBoleta;
    private String serieFactura;
    private int correlativoBoleta;
    private int correlativoFactura;

    public Caja(int id, String nombre, String serieBoleta, String serieFactura) {
        this.id = id;
        this.nombre = nombre;
        this.serieBoleta = serieBoleta;
        this.serieFactura = serieFactura;
        this.correlativoBoleta = 0;
        this.correlativoFactura = 0;
    }

    /** RF-11: correlativo consecutivo e irrepetible por serie. */
    public int siguienteCorrelativoBoleta() {
        return ++correlativoBoleta;
    }

    public int siguienteCorrelativoFactura() {
        return ++correlativoFactura;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getSerieBoleta() { return serieBoleta; }
    public String getSerieFactura() { return serieFactura; }
}
