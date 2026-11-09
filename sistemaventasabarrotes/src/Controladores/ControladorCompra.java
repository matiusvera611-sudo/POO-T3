package Controladores;

import DatosSistema.AlmacenDatos;
import java.util.ArrayList;
import sistemaabarrotes.Compra;
import sistemaabarrotes.Distribuidor;
import sistemaabarrotes.Producto;
import sistemaabarrotes.Validacion;

public class ControladorCompra {
    private final AlmacenDatos datos;
    private final ArrayList<Compra> compras;
    private final ControladorProducto productos;
    private final ControladorDistribuidor distribuidores;

    public ControladorCompra(AlmacenDatos datos, ControladorProducto productos,
                             ControladorDistribuidor distribuidores) {
        this.datos = datos;
        compras = datos.getCompras();
        this.productos = productos;
        this.distribuidores = distribuidores;
    }

    public Compra buscarPorID(String id) {
        for (Compra compra : compras) {
            if (id != null && compra.getIDcompra().equalsIgnoreCase(id.trim())) { return compra; }
        }
        return null;
    }

    public String registrar(String id, String fecha, String estado, String cantidadTexto,
                            String idDistribuidor, String codigoProducto) {
        try {
            if (buscarPorID(id) != null) { return "Ya existe una compra con ese código."; }
            int cantidad = Integer.parseInt(cantidadTexto.trim());
            Producto producto = productos.buscarPorCodigo(codigoProducto);
            Distribuidor distribuidor = distribuidores.buscarPorCodigo(idDistribuidor);
            if (producto == null || distribuidor == null) { return "Seleccione un producto y un distribuidor existentes."; }
            String estadoValido = Validacion.opcion(estado, "El estado", "Pendiente", "Recibida");
            Compra compra = new Compra(id, fecha, producto.getPrecio() * cantidad, "Pendiente", cantidad, distribuidor, producto);
            int stockAnterior = producto.getStock();
            if ("Recibida".equals(estadoValido)) { compra.recibirMercaderia(); }
            compras.add(compra);
            try {
                datos.guardar();
            } catch (IllegalStateException e) {
                compras.remove(compra);
                producto.setStock(stockAnterior);
                throw e;
            }
            return "ok";
        } catch (NumberFormatException e) { return "La cantidad debe ser un número entero válido."; }
        catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String recibirMercaderia(String id) {
        Compra compra = buscarPorID(id);
        if (compra == null) { return "La compra no existe."; }
        int stockAnterior = compra.getProducto().getStock();
        String estadoAnterior = compra.getEstadoOrden();
        try {
            compra.recibirMercaderia();
            try {
                datos.guardar();
            } catch (IllegalStateException e) {
                compra.getProducto().setStock(stockAnterior);
                compra.setEstadoOrden(estadoAnterior);
                throw e;
            }
            return "ok";
        } catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public double calcularTotalAutomatico(String codigoProducto, String cantidadTexto) {
        Producto producto = productos.buscarPorCodigo(codigoProducto);
        if (producto == null || cantidadTexto == null) { return 0; }
        try {
            int cantidad = Integer.parseInt(cantidadTexto.trim());
            return cantidad <= 0 ? 0 : Validacion.moneda(producto.getPrecio() * cantidad);
        } catch (IllegalArgumentException e) { return 0; }
    }

    public String generarNuevoID() {
        int siguiente = compras.size() + 1;
        while (buscarPorID(String.format(java.util.Locale.ROOT, "C%04d", siguiente)) != null) { siguiente++; }
        return String.format(java.util.Locale.ROOT, "C%04d", siguiente);
    }

    public ArrayList<Compra> listarTodas() { return new ArrayList<Compra>(compras); }
}
