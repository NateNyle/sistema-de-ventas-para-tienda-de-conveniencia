package com.mycompany.tienda3a.modelo;

import java.util.regex.Pattern;

public final class ValidacionesPago {
    public static final int MIN_DIGITOS_OPERACION = 4;
    public static final int MAX_DIGITOS_OPERACION = 20;
    public static final int MAX_ENTEROS_MONTO = 7;
    public static final int MAX_DECIMALES_MONTO = 2;

    private static final Pattern SOLO_DIGITOS = Pattern.compile("[0-9]*");
    private static final Pattern MONTO = Pattern.compile(
            "[0-9]{1," + MAX_ENTEROS_MONTO + "}(\\.[0-9]{0," + MAX_DECIMALES_MONTO + "})?");

    private ValidacionesPago() {
    }

    public static boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    public static String errorOperacion(String numero) {
        String n = numero == null ? "" : numero.trim();
        if (n.isEmpty()) {
            return "Escribe el número de operación del pago.";
        }
        if (!SOLO_DIGITOS.matcher(n).matches()) {
            return "El número de operación solo puede tener números (sin letras, espacios ni símbolos).";
        }
        if (n.length() < MIN_DIGITOS_OPERACION || n.length() > MAX_DIGITOS_OPERACION) {
            return "El número de operación debe tener de " + MIN_DIGITOS_OPERACION + " a "
                    + MAX_DIGITOS_OPERACION + " dígitos (tiene " + n.length() + ").";
        }
        return null;
    }

    public static boolean esOperacionValida(String numero) {
        return errorOperacion(numero) == null;
    }

    public static String errorMonto(String texto) {
        String t = texto == null ? "" : texto.trim().replace(',', '.');
        if (t.isEmpty()) {
            return "Escribe el monto que entregó el cliente (o pulsa «Exacto»).";
        }
        if (!MONTO.matcher(t).matches()) {
            return "El monto solo puede tener números y un punto decimal, con " + MAX_DECIMALES_MONTO
                    + " decimales como máximo (por ejemplo 50 o 20.50).";
        }
        return null;
    }
}