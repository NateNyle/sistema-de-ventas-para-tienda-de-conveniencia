package com.mycompany.tienda3a.modelo;

/**
 * Persona que usa el sistema (cajero o administrador). CU-01, RF-01, RF-02.
 * Restricciones del diccionario de datos: intentosFallidos >= 0.
 */
public class Usuario {

    private int id;
    private String nombre;
    private String nombreAcceso;
    private String hashContrasena;
    private Rol rol;
    private boolean activo;
    private int intentosFallidos;
    private java.time.LocalDateTime bloqueadoHasta;

    public Usuario(int id, String nombre, String nombreAcceso, String hashContrasena, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.nombreAcceso = nombreAcceso;
        this.hashContrasena = hashContrasena;
        this.rol = rol;
        this.activo = true;
        this.intentosFallidos = 0;
    }

    public boolean estaBloqueado() {
        return bloqueadoHasta != null && java.time.LocalDateTime.now().isBefore(bloqueadoHasta);
    }

    public void registrarIntentoFallido(int intentosMaximos, int minutosBloqueo) {
        this.intentosFallidos++;
        if (this.intentosFallidos >= intentosMaximos) {
            this.bloqueadoHasta = java.time.LocalDateTime.now().plusMinutes(minutosBloqueo);
        }
    }

    public void reiniciarIntentos() {
        this.intentosFallidos = 0;
        this.bloqueadoHasta = null;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getNombreAcceso() { return nombreAcceso; }
    public Rol getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public int getIntentosFallidos() { return intentosFallidos; }
}
