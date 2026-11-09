package Controladores;

import DatosSistema.AlmacenDatos;
import java.util.ArrayList;
import java.util.Iterator;
import sistemaabarrotes.Categoria;
import sistemaabarrotes.Producto;

public class ControladorCategoria {
    private final AlmacenDatos datos;
    private final ArrayList<Categoria> categorias;

    public ControladorCategoria(AlmacenDatos datos) {
        this.datos = datos;
        categorias = datos.getCategorias();
    }

    public Categoria buscarPorCodigo(String codigo) {
        for (Categoria categoria : categorias) {
            if (codigo != null && categoria.getIDcategoria().equalsIgnoreCase(codigo.trim())) { return categoria; }
        }
        return null;
    }

    public String registrar(String codigo, String nombre) {
        try {
            Categoria categoria = new Categoria(codigo, nombre);
            if (buscarPorCodigo(codigo) != null) { return "Ya existe una categoría con ese código."; }
            categorias.add(categoria);
            try { datos.guardar(); } catch (IllegalStateException e) { categorias.remove(categoria); throw e; }
            return "ok";
        } catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String actualizar(String codigo, String nombre) {
        Categoria categoria = buscarPorCodigo(codigo);
        if (categoria == null) { return "La categoría no existe."; }
        String anterior = categoria.getNombre();
        try {
            categoria.setNombre(nombre);
            try { datos.guardar(); } catch (IllegalStateException e) { categoria.setNombre(anterior); throw e; }
            return "ok";
        } catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String eliminar(String codigo) {
        Categoria categoria = buscarPorCodigo(codigo);
        if (categoria == null) { return "La categoría no existe."; }
        for (Producto producto : datos.getProductos()) {
            if (producto.getCategoria() == categoria) { return "La categoría tiene productos registrados."; }
        }
        // Iterator permite retirar un elemento sin modificar un for-each.
        int posicion = categorias.indexOf(categoria);
        Iterator<Categoria> recorrido = categorias.iterator();
        while (recorrido.hasNext()) {
            if (recorrido.next() == categoria) { recorrido.remove(); break; }
        }
        try { datos.guardar(); } catch (IllegalStateException e) { categorias.add(posicion, categoria); throw e; }
        return "ok";
    }

    public ArrayList<Categoria> listarTodos() { return new ArrayList<Categoria>(categorias); }
}
