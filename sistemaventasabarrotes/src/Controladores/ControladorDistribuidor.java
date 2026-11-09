package Controladores;

import DatosSistema.AlmacenDatos;
import java.util.ArrayList;
import sistemaabarrotes.Compra;
import sistemaabarrotes.Distribuidor;
import sistemaabarrotes.Producto;

public class ControladorDistribuidor {
    private final AlmacenDatos datos;
    private final ArrayList<Distribuidor> distribuidores;

    public ControladorDistribuidor(AlmacenDatos datos) {
        this.datos = datos;
        distribuidores = datos.getDistribuidores();
    }

    public Distribuidor buscarPorCodigo(String codigo) {
        for (Distribuidor distribuidor : distribuidores) {
            if (codigo != null && distribuidor.getCodigo().equalsIgnoreCase(codigo.trim())) { return distribuidor; }
        }
        return null;
    }

    public String registrar(String codigo, String razon, String telefono, String empresa, String direccion, String ruc) {
        try {
            Distribuidor nuevo = new Distribuidor(codigo, razon, telefono, empresa, direccion, ruc);
            if (buscarPorCodigo(codigo) != null) { return "Ya existe un distribuidor con ese código."; }
            distribuidores.add(nuevo);
            try { datos.guardar(); } catch (IllegalStateException e) { distribuidores.remove(nuevo); throw e; }
            return "ok";
        } catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String actualizar(String codigo, String razon, String telefono, String empresa, String direccion, String ruc) {
        Distribuidor actual = buscarPorCodigo(codigo);
        if (actual == null) { return "El distribuidor no existe."; }
        try {
            Distribuidor nuevo = new Distribuidor(codigo, razon, telefono, empresa, direccion, ruc);
            Distribuidor anterior = new Distribuidor(actual.getCodigo(), actual.getRazonSocial(), actual.getTelefono(),
                    actual.getEmpresa(), actual.getDireccionEmpresa(), actual.getRuc());
            actual.actualizarDatos(nuevo);
            try { datos.guardar(); } catch (IllegalStateException e) { actual.actualizarDatos(anterior); throw e; }
            return "ok";
        } catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String eliminar(String codigo) {
        Distribuidor distribuidor = buscarPorCodigo(codigo);
        if (distribuidor == null) { return "El distribuidor no existe."; }
        for (Producto producto : datos.getProductos()) {
            if (producto.getDistribuidor() == distribuidor) { return "El distribuidor está asociado a productos."; }
        }
        for (Compra compra : datos.getCompras()) {
            if (compra.getDistribuidor() == distribuidor) { return "El distribuidor tiene compras registradas."; }
        }
        int posicion = distribuidores.indexOf(distribuidor);
        distribuidores.remove(distribuidor);
        try { datos.guardar(); } catch (IllegalStateException e) { distribuidores.add(posicion, distribuidor); throw e; }
        return "ok";
    }

    public ArrayList<Distribuidor> listarTodos() { return new ArrayList<Distribuidor>(distribuidores); }
}
