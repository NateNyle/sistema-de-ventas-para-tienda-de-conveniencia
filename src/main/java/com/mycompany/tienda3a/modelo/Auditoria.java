package com.mycompany.tienda3a.modelo;

import java.time.LocalDateTime;

/** Bitácora de operaciones críticas (RF-20). */
public class Auditoria {

    private int id;
    private Usuario usuario;
    private String accion;
    private String resultado;
    private LocalDateTime fecha;

    public Auditoria(int id, Usuario usuario, String accion, String resultado) {
        this.id = id;
        this.usuario = usuario;
        this.accion = accion;
        this.resultado = resultado;
        this.fecha = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return fecha + " | " + usuario.getNombreAcceso() + " | " + accion + " -> " + resultado;
    }
}
