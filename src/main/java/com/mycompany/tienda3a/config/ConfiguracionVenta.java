package com.mycompany.tienda3a.config;

/**
 * Constantes de negocio compartidas por toda la aplicación (sección 2.10 y
 * reglas de negocio del documento). No pertenecen a ningún objeto Venta en
 * particular, sino a la configuración de la tienda; por eso son static:
 * existen una sola vez y cualquier clase las consulta sin crear una
 * instancia (p. ej. ConfiguracionVenta.TASA_IGV, no un contador de objetos).
 *
 * SRP (principio de responsabilidad única): esta clase solo concentra
 * parámetros de configuración; no calcula totales ni conoce el flujo de
 * venta. Eso evita que esos valores queden "mágicos" y repetidos (18%, 150,
 * 700, 15) dispersos por el código.
 */
public final class ConfiguracionVenta {

    /** RN-04: los precios incluyen IGV. */
    public static final double TASA_IGV = 0.18;

    /** RN-10: fondo inicial de caja para el cierre diario. */
    public static final double FONDO_INICIAL_CAJA = 150.00;

    /** RN-05: límite de monto para emitir venta simple sin datos del cliente. */
    public static final double LIMITE_VENTA_SIMPLE = 700.00;

    /** RN-14: intentos fallidos consecutivos antes de bloquear al usuario. */
    public static final int INTENTOS_MAXIMOS_LOGIN = 5;

    /** RN-14 / RNF-16: minutos de bloqueo y de expiración de sesión por inactividad. */
    public static final int MINUTOS_BLOQUEO = 15;
    public static final int MINUTOS_EXPIRACION_SESION = 15;

    /** RN-15: reintentos del envío al simulador de SUNAT. */
    public static final int REINTENTOS_SUNAT = 3;

    /** S-01: cajas de la tienda (RNF-05). */
    public static final int MAXIMO_CAJAS = 3;

    private ConfiguracionVenta() {
        // Clase de solo constantes: no se instancia.
    }
}
