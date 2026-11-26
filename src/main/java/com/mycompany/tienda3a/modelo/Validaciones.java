package com.mycompany.tienda3a.modelo;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Validaciones {
    public static final int LONGITUD_DNI = 8;
    public static final int MAX_NOMBRE_USUARIO = 120;
    public static final int MAX_NOMBRE_CLIENTE = 150;

    private static final String LETRAS = "A-Za-zÁÉÍÓÚÜÑáéíóúüñ";
    private static final Pattern DNI = Pattern.compile("[0-9]{8}");
    private static final Pattern SOLO_DIGITOS = Pattern.compile("[0-9]*");
    private static final Pattern NOMBRE = Pattern.compile("[" + LETRAS + "]+(?:[ '\\-][" + LETRAS + "]+)*");
    private static final Pattern VOCAL_REPETIDA = Pattern.compile("([aeiou])\\1{2,}");

    private Validaciones() {
    }

    public static boolean esDigitoDni(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean esLetraNombre(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || "ÁÉÍÓÚÜÑáéíóúüñ".indexOf(c) >= 0;
    }

    public static boolean esCaracterNombre(char c) {
        return esLetraNombre(c) || c == ' ' || c == '-' || c == '\'';
    }

    public static boolean tieneVocalRepetida(CharSequence texto) {
        String plano = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
        return VOCAL_REPETIDA.matcher(plano).find();
    }

    public static String normalizarNombre(String nombre) {
        if (nombre == null) {
            return "";
        }
        return Normalizer.normalize(nombre, Normalizer.Form.NFC).trim().replaceAll("\\s+", " ");
    }

    public static String errorDni(String dni) {
        if (dni == null || dni.isEmpty()) {
            return "El DNI es obligatorio.";
        }
        if (!SOLO_DIGITOS.matcher(dni).matches()) {
            return "El DNI solo puede tener números (sin letras, espacios ni símbolos).";
        }
        if (!DNI.matcher(dni).matches()) {
            return "El DNI debe tener exactamente 8 dígitos (tiene " + dni.length() + ").";
        }
        return null;
    }

    public static String errorNombre(String etiqueta, String nombre, int maximo) {
        String n = normalizarNombre(nombre);
        if (n.isEmpty()) {
            return "El " + etiqueta + " es obligatorio.";
        }
        if (n.length() > maximo) {
            return "El " + etiqueta + " no puede pasar de " + maximo + " caracteres.";
        }
        if (!NOMBRE.matcher(n).matches()) {
            return "El " + etiqueta + " solo puede tener letras, tildes, ñ y espacios (también guion y apóstrofe entre letras). "
                    + "No se permiten números ni símbolos.";
        }
        if (tieneVocalRepetida(n)) {
            return "El " + etiqueta + " no puede repetir la misma vocal más de dos veces seguidas (por ejemplo «Maaaría»).";
        }
        return null;
    }
}