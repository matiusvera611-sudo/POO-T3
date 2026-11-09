package sistemaabarrotes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Métodos de clase compartidos por los modelos. */
public final class Validacion {
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter
            .ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Validacion() {
    }

    public static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        String resultado = valor.trim();
        if (resultado.contains("|") || resultado.contains("\n") || resultado.contains("\r")) {
            throw new IllegalArgumentException(campo + " no admite barras verticales ni saltos de línea.");
        }
        return resultado;
    }

    public static String nombres(String valor, String campo) {
        String resultado = texto(valor, campo);
        if (!resultado.matches("[\\p{L} .'-]+")) {
            throw new IllegalArgumentException(campo + " solo admite letras y espacios.");
        }
        return resultado;
    }

    public static double positivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser un número mayor a cero.");
        }
        return valor;
    }

    public static int noNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException(campo + " no puede ser negativo.");
        }
        return valor;
    }

    public static String fecha(String valor) {
        String resultado = texto(valor, "La fecha");
        try {
            LocalDate.parse(resultado, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ingrese una fecha real en formato dd/MM/yyyy.");
        }
        return resultado;
    }

    public static String opcion(String valor, String campo, String... opciones) {
        String resultado = texto(valor, campo);
        for (String opcion : opciones) {
            if (opcion.equalsIgnoreCase(resultado)) {
                return opcion;
            }
        }
        throw new IllegalArgumentException(campo + " no es válido.");
    }

    public static double moneda(double valor) {
        if (!Double.isFinite(valor) || valor < 0 || valor > Long.MAX_VALUE / 100.0) {
            throw new IllegalArgumentException("El importe no es válido.");
        }
        return Math.round(valor * 100.0) / 100.0;
    }
}
