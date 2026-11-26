package com.mycompany.tienda3a.vista.componentes;

import com.mycompany.tienda3a.modelo.ValidacionesPago;
import java.util.regex.Pattern;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

public final class FiltrosNumericos {
    private static final Pattern MONTO_PARCIAL = Pattern.compile(
            "[0-9]{0," + ValidacionesPago.MAX_ENTEROS_MONTO + "}(\\.[0-9]{0," + ValidacionesPago.MAX_DECIMALES_MONTO + "})?");

    private FiltrosNumericos() {
    }

    public static void soloDigitos(JTextComponent campo, int maximo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroDigitos(maximo));
    }

    public static void soloMonto(JTextComponent campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroMonto());
    }

    private static final class FiltroDigitos extends DocumentFilter {
        private final int maximo;

        FiltroDigitos(int maximo) {
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
            if (texto == null || texto.isEmpty()) {
                super.replace(fb, offset, longitud, texto, atributos);
                return;
            }
            int libres = maximo - (fb.getDocument().getLength() - longitud);
            StringBuilder digitos = new StringBuilder();
            for (int i = 0; i < texto.length() && digitos.length() < libres; i++) {
                if (ValidacionesPago.esDigito(texto.charAt(i))) {
                    digitos.append(texto.charAt(i));
                }
            }
            if (digitos.length() > 0) {
                super.replace(fb, offset, longitud, digitos.toString(), atributos);
            }
        }
    }

    private static final class FiltroMonto extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String texto, AttributeSet atributos)
                throws BadLocationException {
            replace(fb, offset, 0, texto, atributos);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet atributos)
                throws BadLocationException {
            if (texto == null || texto.isEmpty()) {
                super.replace(fb, offset, longitud, texto, atributos);
                return;
            }
            Document doc = fb.getDocument();
            StringBuilder resultado = new StringBuilder(doc.getText(0, doc.getLength()));
            resultado.delete(offset, offset + longitud);
            int posicion = offset;
            StringBuilder aceptado = new StringBuilder();
            for (int i = 0; i < texto.length(); i++) {
                char c = texto.charAt(i) == ',' ? '.' : texto.charAt(i);
                if (!ValidacionesPago.esDigito(c) && c != '.') {
                    continue;
                }
                resultado.insert(posicion, c);
                if (!MONTO_PARCIAL.matcher(resultado).matches()) {
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