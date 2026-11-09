package Controladores;

import DatosSistema.AlmacenDatos;
import java.util.ArrayList;
import sistemaabarrotes.Compra;
import sistemaabarrotes.DetalleVenta;
import sistemaabarrotes.Producto;
import sistemaabarrotes.Validacion;
import sistemaabarrotes.Venta;

public class ControladorProducto {
    private final AlmacenDatos datos;
    private final ArrayList<Producto> productos;
    private final ControladorCategoria categorias;
    private final ControladorDistribuidor distribuidores;

    public ControladorProducto(AlmacenDatos datos, ControladorCategoria categorias,
                               ControladorDistribuidor distribuidores) {
        this.datos = datos;
        this.productos = datos.getProductos();
        this.categorias = categorias;
        this.distribuidores = distribuidores;
    }

    public Producto buscarPorCodigo(String codigo) {
        for (Producto producto : productos) {
            if (codigo != null && producto.getCodigo().equalsIgnoreCase(codigo.trim())) { return producto; }
        }
        return null;
    }

    private Producto crear(String codigo, String nombre, String marca, String precio,
                           String stock, String minimo, String fecha, String idCategoria, String idDistribuidor) {
        return new Producto(codigo, nombre, marca, Double.parseDouble(Validacion.texto(precio, "El precio")),
                Integer.parseInt(Validacion.texto(stock, "El stock")), Integer.parseInt(Validacion.texto(minimo, "El stock mínimo")), fecha,
                categorias.buscarPorCodigo(idCategoria), distribuidores.buscarPorCodigo(idDistribuidor));
    }

    public String registrar(String codigo, String nombre, String marca, String precio,
                            String stock, String minimo, String fecha, String idCategoria, String idDistribuidor) {
        try {
            Producto nuevo = crear(codigo, nombre, marca, precio, stock, minimo, fecha, idCategoria, idDistribuidor);
            if (buscarPorCodigo(codigo) != null) { return "Ya existe un producto con ese código."; }
            if (nuevo.estaVencido()) { return "No se puede registrar un producto vencido."; }
            productos.add(nuevo);
            try { datos.guardar(); } catch (IllegalStateException e) { productos.remove(nuevo); throw e; }
            return "ok";
        } catch (NumberFormatException e) { return "Precio, stock y stock mínimo deben ser números válidos."; }
        catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String actualizar(String codigo, String nombre, String marca, String precio,
                             String stock, String minimo, String fecha, String idCategoria, String idDistribuidor) {
        Producto actual = buscarPorCodigo(codigo);
        if (actual == null) { return "El producto no existe."; }
        try {
            Producto nuevo = crear(codigo, nombre, marca, precio, stock, minimo, fecha, idCategoria, idDistribuidor);
            Producto anterior = new Producto(actual.getCodigo(), actual.getNombre(), actual.getMarca(), actual.getPrecio(),
                    actual.getStock(), actual.getStockMinimo(), actual.getFechaVencimiento(), actual.getCategoria(), actual.getDistribuidor());
            actual.actualizarDatos(nuevo);
            try { datos.guardar(); } catch (IllegalStateException e) { actual.actualizarDatos(anterior); throw e; }
            return "ok";
        } catch (NumberFormatException e) { return "Precio, stock y stock mínimo deben ser números válidos."; }
        catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String eliminar(String codigo) {
        Producto producto = buscarPorCodigo(codigo);
        if (producto == null) { return "El producto no existe."; }
        for (Compra compra : datos.getCompras()) {
            if (compra.getProducto() == producto) { return "El producto tiene compras registradas."; }
        }
        for (Venta venta : datos.getVentas()) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                if (detalle.getProducto() == producto) { return "El producto tiene ventas registradas."; }
            }
        }
        int posicion = productos.indexOf(producto);
        productos.remove(producto);
        try { datos.guardar(); } catch (IllegalStateException e) { productos.add(posicion, producto); throw e; }
        return "ok";
    }

    public ArrayList<Producto> listarTodos() { return new ArrayList<Producto>(productos); }

    public ArrayList<Producto> listarStockBajo() {
        ArrayList<Producto> resultado = new ArrayList<Producto>();
        for (Producto producto : productos) { if (producto.stockBajo()) { resultado.add(producto); } }
        return resultado;
    }

    public ArrayList<Producto> listarProximosVencer() {
        ArrayList<Producto> resultado = new ArrayList<Producto>();
        for (Producto producto : productos) { if (producto.proximoVencer()) { resultado.add(producto); } }
        return resultado;
    }
}
