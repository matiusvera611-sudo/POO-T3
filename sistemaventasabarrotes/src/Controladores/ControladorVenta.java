package Controladores;

import DatosSistema.AlmacenDatos;
import java.util.ArrayList;
import java.util.Locale;
import sistemaabarrotes.Cliente;
import sistemaabarrotes.Comprobante;
import sistemaabarrotes.DetalleVenta;
import sistemaabarrotes.Producto;
import sistemaabarrotes.Venta;

public class ControladorVenta {
    private final AlmacenDatos datos;
    private final ArrayList<Venta> ventas;
    private final ControladorProducto productos;
    private final ControladorCliente clientes;
    private Venta ventaActual;

    public ControladorVenta(AlmacenDatos datos, ControladorProducto productos, ControladorCliente clientes) {
        this.datos = datos;
        ventas = datos.getVentas();
        this.productos = productos;
        this.clientes = clientes;
    }

    public void nuevaVenta() {
        if (!ControladorSesion.haySesionActiva()) { throw new IllegalArgumentException("Primero inicie sesión."); }
        if (ventaActual != null && !ventaActual.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Confirme o cancele la venta en curso.");
        }
        int siguiente = ventas.size() + 1;
        String id = String.format(Locale.ROOT, "V%04d", siguiente);
        while (existeID(id)) { id = String.format(Locale.ROOT, "V%04d", ++siguiente); }
        ventaActual = new Venta(id, ControladorSesion.getUsuarioActivo());
        ventaActual.configurarComprobante("Boleta", String.format(Locale.ROOT, "%06d", siguiente), "Efectivo");
    }

    private boolean existeID(String id) {
        for (Venta venta : ventas) { if (venta.getIDventa().equalsIgnoreCase(id)) { return true; } }
        return false;
    }

    public String asociarCliente(String codigo) {
        if (ventaActual == null) { return "Primero inicie una nueva venta."; }
        if (codigo == null || codigo.trim().isEmpty()) { ventaActual.setCliente(null); return "ok"; }
        Cliente cliente = clientes.buscarPorCodigo(codigo);
        if (cliente == null) { return "El cliente seleccionado no existe."; }
        ventaActual.setCliente(cliente);
        return "ok";
    }

    public void configurarComprobante(String tipo, String numero, String metodo) {
        if (ventaActual == null) { throw new IllegalArgumentException("Primero inicie una nueva venta."); }
        ventaActual.configurarComprobante(tipo, numero, metodo);
    }

    public String agregarDetalle(String codigo, String cantidadTexto) {
        if (ventaActual == null) { return "Primero inicie una nueva venta."; }
        Producto producto = productos.buscarPorCodigo(codigo);
        if (producto == null) { return "Producto no encontrado."; }
        if (producto.estaVencido()) { return "No se puede vender un producto vencido."; }
        try {
            int cantidad = Integer.parseInt(cantidadTexto.trim());
            if (cantidad <= 0) { return "La cantidad debe ser mayor a cero."; }
            long acumulado = cantidad;
            for (DetalleVenta detalle : ventaActual.getDetalles()) {
                if (detalle.getProducto() == producto) { acumulado += detalle.getCantidad(); }
            }
            if (acumulado > producto.getStock()) { return "Stock insuficiente. Disponible: " + producto.getStock(); }
            ventaActual.agregarProducto(producto, cantidad);
            return "ok";
        } catch (NumberFormatException e) { return "La cantidad debe ser un número entero válido."; }
        catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String eliminarDetalle(int indice) {
        if (ventaActual == null) { return "No hay una venta en curso."; }
        try { ventaActual.eliminarDetalle(indice); return "ok"; }
        catch (IllegalArgumentException e) { return e.getMessage(); }
    }

    public String confirmarVenta() {
        if (ventaActual == null || ventaActual.getDetalles().isEmpty()) { return "No hay productos en la venta."; }
        if (ventaActual.getUsuario() != ControladorSesion.getUsuarioActivo()) { return "La sesión de la venta no coincide."; }
        Cliente cliente = ventaActual.getCliente();
        if (cliente != null && clientes.buscarPorCodigo(cliente.getCodigo()) != cliente) { return "El cliente fue eliminado."; }
        for (Venta venta : ventas) {
            if (venta.getTipoComprobante().equals(ventaActual.getTipoComprobante())
                    && venta.getNumComprobante().equalsIgnoreCase(ventaActual.getNumComprobante())) {
                return "Ya existe una venta con ese número de comprobante.";
            }
        }
        // Se verifica todo antes de modificar stock, incluso productos repetidos.
        ArrayList<Producto> afectados = new ArrayList<Producto>();
        ArrayList<Integer> stockAnterior = new ArrayList<Integer>();
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            Producto producto = productos.buscarPorCodigo(detalle.getCodigoProducto());
            if (producto != detalle.getProducto()) { return "Un producto fue eliminado. Revise la venta."; }
            if (producto.estaVencido()) { return "El producto " + producto.getNombre() + " está vencido."; }
            if (!afectados.contains(producto)) {
                long cantidad = 0;
                for (DetalleVenta otro : ventaActual.getDetalles()) {
                    if (otro.getProducto() == producto) { cantidad += otro.getCantidad(); }
                }
                if (cantidad > producto.getStock()) { return "Stock insuficiente para " + producto.getNombre() + "."; }
                afectados.add(producto);
                stockAnterior.add(producto.getStock());
            }
        }
        int puntosAnteriores = cliente == null ? 0 : cliente.getPuntos();
        try {
            if (cliente != null) { cliente.acumularPuntos(ventaActual.getTotal()); }
            for (DetalleVenta detalle : ventaActual.getDetalles()) {
                detalle.getProducto().reducirStock(detalle.getCantidad());
            }
            ventas.add(ventaActual);
            datos.guardar();
        } catch (IllegalArgumentException | IllegalStateException e) {
            for (int i = 0; i < afectados.size(); i++) { afectados.get(i).setStock(stockAnterior.get(i)); }
            if (cliente != null) { cliente.setPuntos(puntosAnteriores); }
            ventas.remove(ventaActual);
            if (e instanceof IllegalStateException) { throw e; }
            return e.getMessage();
        }
        ventaActual.confirmar();
        String id = ventaActual.getIDventa();
        ventaActual = null;
        return "ok:" + id;
    }

    public void cancelarVenta() { ventaActual = null; }
    public Venta getVentaActual() { return ventaActual; }
    public double getTotalActual() { return ventaActual == null ? 0 : ventaActual.getTotal(); }
    public Producto buscarProducto(String codigo) { return productos.buscarPorCodigo(codigo); }
    public ArrayList<Cliente> listarClientes() { return clientes.listarTodos(); }
    public ArrayList<Venta> listarTodas() { return new ArrayList<Venta>(ventas); }

    public void exportarReporte(ArrayList<Venta> lista, String ruta) {
        StringBuilder texto = new StringBuilder();
        // Polimorfismo: se usa el contrato, no la implementación concreta.
        for (Comprobante comprobante : lista) { texto.append(comprobante.exportarTexto()).append("\n"); }
        AlmacenDatos.guardarReporte(texto.toString(), ruta);
    }
}
