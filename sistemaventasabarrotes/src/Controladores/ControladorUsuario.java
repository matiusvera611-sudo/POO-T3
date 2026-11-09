package Controladores;

import DatosSistema.AlmacenDatos;
import java.util.ArrayList;
import sistemaabarrotes.Usuario;
import sistemaabarrotes.Venta;

public class ControladorUsuario {
    private final AlmacenDatos datos;
    private final ArrayList<Usuario> usuarios;

    public ControladorUsuario(AlmacenDatos datos) {
        this.datos = datos;
        usuarios = datos.getUsuarios();
    }

    public Usuario login(String codigo, String password) {
        for (Usuario usuario : usuarios) {
            if (usuario.validarAcceso(codigo, password)) { return usuario; }
        }
        return null;
    }

    public Usuario buscarPorCodigo(String codigo) {
        for (Usuario usuario : usuarios) {
            if (codigo != null && usuario.getCodigo().equalsIgnoreCase(codigo.trim())) { return usuario; }
        }
        return null;
    }

    private int contarGerentes() {
        int cantidad = 0;
        for (Usuario usuario : usuarios) {
            if (usuario.isEstado() && "Titular Gerente".equals(usuario.getRol())) { cantidad++; }
        }
        return cantidad;
    }

    public String registrar(String codigo, String nombre, String password, String rol) {
        if (!ControladorSesion.esTitularGerente()) { return "Solo el gerente puede administrar usuarios."; }
        try {
            Usuario nuevo = new Usuario(codigo, nombre, password, rol);
            for (Usuario usuario : usuarios) {
                if (usuario.getCodigo().equalsIgnoreCase(nuevo.getCodigo())
                        || usuario.getNombreUsuario().equalsIgnoreCase(nuevo.getCodigo())) {
                    return "Ya existe un usuario con ese código.";
                }
            }
            usuarios.add(nuevo);
            try { datos.guardar(); } catch (IllegalStateException e) { usuarios.remove(nuevo); throw e; }
            return "ok";
        } catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String actualizar(String codigo, String nombre, String password, String rol) {
        if (!ControladorSesion.esTitularGerente()) { return "Solo el gerente puede administrar usuarios."; }
        Usuario actual = buscarPorCodigo(codigo);
        if (actual == null) { return "El usuario no existe."; }
        try {
            String clave = password == null || password.isEmpty() ? actual.getPassword() : password;
            Usuario nuevo = new Usuario(actual.getCodigo(), nombre, clave, rol);
            if ("Titular Gerente".equals(actual.getRol()) && actual.isEstado()
                    && !"Titular Gerente".equals(nuevo.getRol()) && contarGerentes() == 1) {
                return "Debe conservarse al menos un gerente activo.";
            }
            Usuario anterior = new Usuario(actual.getCodigo(), actual.getNombre(), actual.getPassword(), actual.getRol());
            actual.actualizarDatos(nuevo);
            try { datos.guardar(); } catch (IllegalStateException e) { actual.actualizarDatos(anterior); throw e; }
            return "ok";
        } catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String eliminar(String codigo) {
        if (!ControladorSesion.esTitularGerente()) { return "Solo el gerente puede administrar usuarios."; }
        Usuario usuario = buscarPorCodigo(codigo);
        if (usuario == null) { return "El usuario no existe."; }
        if (usuario == ControladorSesion.getUsuarioActivo()) { return "No puede eliminar al usuario de la sesión actual."; }
        if ("Titular Gerente".equals(usuario.getRol()) && usuario.isEstado() && contarGerentes() == 1) {
            return "Debe conservarse al menos un gerente activo.";
        }
        for (Venta venta : datos.getVentas()) {
            if (venta.getUsuario() == usuario) { return "El usuario tiene ventas registradas."; }
        }
        int posicion = usuarios.indexOf(usuario);
        usuarios.remove(usuario);
        try { datos.guardar(); } catch (IllegalStateException e) { usuarios.add(posicion, usuario); throw e; }
        return "ok";
    }

    public ArrayList<Usuario> listarTodos() { return new ArrayList<Usuario>(usuarios); }
}
