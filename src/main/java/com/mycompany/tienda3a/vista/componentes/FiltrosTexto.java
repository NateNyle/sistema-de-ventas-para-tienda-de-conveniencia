package com.mycompany.tienda3a.vista.componentes;

import com.mycompany.tienda3a.modelo.Validaciones;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

public final class FiltrosTexto {
    private static final String SIN_FILTRO = "tienda3a.sinFiltro";

    private FiltrosTexto() {
    }

        public static void soloDni(JTextComponent campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroDni(Validaciones.LONGITUD_DNI));
    }
    public static void soloCantidad(JTextComponent campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroCantidad());
    }
        
    public static void soloRuc(JTextComponent campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroDni(Validaciones.LONGITUD_RUC));
    }
    public static void soloNombre(JTextComponent campo, int maximo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroNombre(maximo));
    }

    public static void asignar(JTextComponent campo, String texto) {
        Document doc = campo.getDocument();
        doc.putProperty(SIN_FILTRO, Boolean.TRUE);
        try {
            campo.setText(texto);
        } finally {
            doc.putProperty(SIN_FILTRO, null);
        }
    }

    public static void quitarErrorAlEscribir(Campo campo) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { campo.setError(false); }

            @Override
            public void removeUpdate(DocumentEvent e) { campo.setError(false); }

            @Override
            public void changedUpdate(DocumentEvent e) { campo.setError(false); }
        });
    }

    private static boolean sinFiltro(DocumentFilter.FilterBypass fb) {
        return Boolean.TRUE.equals(fb.getDocument().getProperty(SIN_FILTRO));
    }

    private static final class FiltroDni extends DocumentFilter {
                private final int maximo;

        FiltroDni(int maximo) {
            this.maximo = maximo;
        }
        @Override
        public void insertString(FilterBypass fb, int offset, String texto, AttributeSet atributos)
                throws BadLocationException {
            replace(fb, offset, 0, texto, atributos);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet atributos)
                throws BadLocationException {
            if (texto == null || texto.isEmpty() || sinFiltro(fb)) {
                super.replace(fb, offset, longitud, texto, atributos);
                return;
            }
            int libres = maximo - (fb.getDocument().getLength() - longitud);
            StringBuilder digitos = new StringBuilder();
            for (int i = 0; i < texto.length() && digitos.length() < libres; i++) {
                if (Validaciones.esDigitoDni(texto.charAt(i))) {
                    digitos.append(texto.charAt(i));
                }
            }
            if (digitos.length() > 0) {
                super.replace(fb, offset, longitud, digitos.toString(), atributos);
            }
        }
    }
    private static final class FiltroCantidad extends DocumentFilter {
        private static final java.util.regex.Pattern FORMATO = java.util.regex.Pattern.compile("[0-9]{0,5}([.,][0-9]{0,3})?");

        @Override
        public void insertString(FilterBypass fb, int offset, String texto, AttributeSet atributos)
                throws BadLocationException {
            replace(fb, offset, 0, texto, atributos);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet atributos)
                throws BadLocationException {
            if (texto == null || texto.isEmpty() || sinFiltro(fb)) {
                super.replace(fb, offset, longitud, texto, atributos);
                return;
            }
            Document doc = fb.getDocument();
            StringBuilder resultado = new StringBuilder(doc.getText(0, doc.getLength()));
            resultado.delete(offset, offset + longitud);
            int posicion = offset;
            StringBuilder aceptado = new StringBuilder();
            for (int i = 0; i < texto.length(); i++) {
                char c = texto.charAt(i);
                resultado.insert(posicion, c);
                if (FORMATO.matcher(resultado).matches()) {
                    aceptado.append(c);
                    posicion++;
                } else {
                    resultado.deleteCharAt(posicion);
                }
            }
            if (aceptado.length() > 0) {
                super.replace(fb, offset, longitud, aceptado.toString(), atributos);
            }
        }
    }
    private static final class FiltroNombre extends DocumentFilter {
        private final int maximo;

        FiltroNombre(int maximo) {
            this.maximo = maximo;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String texto, AttributeSet atributos)
                throws BadLocationException {
            replace(fb, offset, 0, texto, atributos);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet atributos)
                throws BadLocationException {
            if (texto == null || texto.isEmpty() || sinFiltro(fb)) {
                super.replace(fb, offset, longitud, texto, atributos);
                return;
            }
            Document doc = fb.getDocument();
            StringBuilder resultado = new StringBuilder(doc.getText(0, doc.getLength()));
            resultado.delete(offset, offset + longitud);
            int posicion = offset;
            StringBuilder aceptado = new StringBuilder();
            for (int i = 0; i < texto.length(); i++) {
                char c = texto.charAt(i);
                if (c == '\t' || c == '\n' || c == '\r') {
                    c = ' ';
                }
                if (!Validaciones.esCaracterNombre(c) || resultado.length() >= maximo) {
                    continue;
                }
                if (!Validaciones.esLetraNombre(c)) {
                    boolean despuesDeLetra = posicion > 0 && Validaciones.esLetraNombre(resultado.charAt(posicion - 1));
                    boolean antesDeLetra = posicion >= resultado.length() || Validaciones.esLetraNombre(resultado.charAt(posicion));
                    if (!despuesDeLetra || !antesDeLetra) {
                        continue;
                    }
                }
                resultado.insert(posicion, c);
                if (Validaciones.tieneVocalRepetida(resultado)) {
                    resultado.deleteCharAt(posicion);
                    continue;
                }
                aceptado.append(c);
                posicion++;
            }
            if (aceptado.length() > 0) {
                super.replace(fb, offset, longitud, aceptado.toString(), atributos);
            }
        }
    }
}