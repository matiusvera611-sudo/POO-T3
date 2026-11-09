package Controladores;

import sistemaabarrotes.Usuario;

/** Ejemplo de atributo y métodos estáticos: una sesión para la aplicación. */
public final class ControladorSesion {
    private static Usuario usuarioActivo;

    private ControladorSesion() {
    }

    public static void iniciarSesion(Usuario usuario) {
        if (usuario == null || !usuario.isEstado()) {
            throw new IllegalArgumentException("No se puede iniciar sesión con este usuario.");
        }
        usuarioActivo = usuario;
    }

    public static void cerrarSesion() { usuarioActivo = null; }
    public static Usuario getUsuarioActivo() { return usuarioActivo; }
    public static boolean haySesionActiva() { return usuarioActivo != null; }
    public static String getRolActivo() { return usuarioActivo == null ? "" : usuarioActivo.getRol(); }
    public static boolean esTitularGerente() { return "Titular Gerente".equals(getRolActivo()); }
}
