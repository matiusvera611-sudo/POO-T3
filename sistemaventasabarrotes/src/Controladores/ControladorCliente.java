package Controladores;

import DatosSistema.AlmacenDatos;
import java.util.ArrayList;
import sistemaabarrotes.Cliente;
import sistemaabarrotes.Venta;

public class ControladorCliente {
    private final AlmacenDatos datos;
    private final ArrayList<Cliente> clientes;

    public ControladorCliente(AlmacenDatos datos) {
        this.datos = datos;
        clientes = datos.getClientes();
    }

    public Cliente buscarPorCodigo(String codigo) {
        for (Cliente cliente : clientes) {
            if (codigo != null && cliente.getCodigo().equalsIgnoreCase(codigo.trim())) { return cliente; }
        }
        return null;
    }

    private boolean documentoRepetido(Cliente candidato) {
        for (Cliente cliente : clientes) {
            if (!cliente.getCodigo().equalsIgnoreCase(candidato.getCodigo())
                    && cliente.validarIdentificacion(candidato.getTipodoc(), candidato.getNmrdoc())) { return true; }
        }
        return false;
    }

    public String registrar(String codigo, String nombre, String apellido, String tipoDoc,
                            String numeroDoc, String correo, String telefono, String direccion,
                            String tipoCliente, int puntos) {
        try {
            Cliente nuevo = new Cliente(codigo, nombre, apellido, tipoDoc, numeroDoc, correo,
                    telefono, direccion, tipoCliente, puntos);
            if (buscarPorCodigo(codigo) != null) { return "Ya existe un cliente con ese código."; }
            if (documentoRepetido(nuevo)) { return "El documento ya pertenece a otro cliente."; }
            clientes.add(nuevo);
            try { datos.guardar(); } catch (IllegalStateException e) { clientes.remove(nuevo); throw e; }
            return "ok";
        } catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String actualizar(String codigo, String nombre, String apellido, String tipoDoc,
                             String numeroDoc, String correo, String telefono, String direccion,
                             String tipoCliente, int puntos) {
        Cliente actual = buscarPorCodigo(codigo);
        if (actual == null) { return "El cliente no existe."; }
        try {
            Cliente nuevo = new Cliente(codigo, nombre, apellido, tipoDoc, numeroDoc, correo,
                    telefono, direccion, tipoCliente, puntos);
            if (documentoRepetido(nuevo)) { return "El documento ya pertenece a otro cliente."; }
            Cliente anterior = new Cliente(actual.getCodigo(), actual.getNombre(), actual.getApellido(), actual.getTipodoc(),
                    actual.getNmrdoc(), actual.getCorreo(), actual.getTelefono(), actual.getDireccion(), actual.getTipoCliente(), actual.getPuntos());
            actual.actualizarDatos(nuevo);
            try { datos.guardar(); } catch (IllegalStateException e) { actual.actualizarDatos(anterior); throw e; }
            return "ok";
        } catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String eliminar(String codigo) {
        Cliente cliente = buscarPorCodigo(codigo);
        if (cliente == null) { return "El cliente no existe."; }
        for (Venta venta : datos.getVentas()) {
            if (venta.getCliente() == cliente) { return "El cliente tiene ventas registradas."; }
        }
        int posicion = clientes.indexOf(cliente);
        clientes.remove(cliente);
        try { datos.guardar(); } catch (IllegalStateException e) { clientes.add(posicion, cliente); throw e; }
        return "ok";
    }

    public ArrayList<Cliente> listarTodos() { return new ArrayList<Cliente>(clientes); }
}
