package com.mycompany.tienda3a.modelo;

/** Comprador identificado en boletas (DNI) y facturas (RUC). */
public class Cliente {

    private int id;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nombreORazonSocial;

    public Cliente(int id, TipoDocumento tipoDocumento, String numeroDocumento, String nombreORazonSocial) {
        if (tipoDocumento == TipoDocumento.DNI && (numeroDocumento == null || numeroDocumento.length() != 8)) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos.");
        }
        if (tipoDocumento == TipoDocumento.RUC && (numeroDocumento == null || numeroDocumento.length() != 11)) {
            throw new IllegalArgumentException("El RUC debe tener 11 dígitos.");
        }
        this.id = id;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombreORazonSocial = nombreORazonSocial;
    }

    public int getId() { return id; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getNombreORazonSocial() { return nombreORazonSocial; }
}
