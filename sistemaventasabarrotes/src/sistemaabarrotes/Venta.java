package sistemaabarrotes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public class Venta implements Comprobante {
    private final String IDventa;
    private final Usuario usuario;
    private final String fechaventa;
    private Cliente cliente;
    private String tipoComprobante = "Boleta";
    private String numComprobante;
    private String metodoPago = "Efectivo";
    private boolean confirmada;
    // Composición: los detalles nacen dentro de esta venta.
    private final ArrayList<DetalleVenta> detalles = new ArrayList<DetalleVenta>();

    public Venta(String IDventa, Usuario usuario) {
        this(IDventa, usuario, LocalDate.now().format(Validacion.FORMATO_FECHA));
    }

    public Venta(String IDventa, Usuario usuario, String fecha) {
        this.IDventa = Validacion.texto(IDventa, "El código de venta");
        if (usuario == null) { throw new IllegalArgumentException("La venta necesita un usuario."); }
        this.usuario = usuario;
        this.fechaventa = Validacion.fecha(fecha);
        this.numComprobante = IDventa;
    }

    public String getIDventa() { return IDventa; }
    public Usuario getUsuario() { return usuario; }
    public Cliente getCliente() { return cliente; }
    public String getCodigoUsuario() { return usuario.getCodigo(); }
    public String getCodigoCliente() { return cliente == null ? "" : cliente.getCodigo(); }
    public String getFechaventa() { return fechaventa; }
    public String getTipoComprobante() { return tipoComprobante; }
    public String getNumComprobante() { return numComprobante; }
    public String getMetodoPago() { return metodoPago; }
    public boolean isConfirmada() { return confirmada; }

    public ArrayList<DetalleVenta> getDetalles() {
        return new ArrayList<DetalleVenta>(detalles);
    }

    public void setCliente(Cliente cliente) { comprobarEdicion(); this.cliente = cliente; }

    public void configurarComprobante(String tipo, String numero, String metodo) {
        comprobarEdicion();
        String tipoValido = Validacion.opcion(tipo, "El comprobante", "Boleta", "Factura");
        String numeroValido = Validacion.texto(numero, "El número de comprobante");
        String metodoValido = Validacion.opcion(metodo, "El método de pago", "Efectivo", "Tarjeta", "Yape", "Plin", "Transferencia");
        tipoComprobante = tipoValido;
        numComprobante = numeroValido;
        metodoPago = metodoValido;
    }

    // Sobrecarga: en una venta nueva se toma el precio actual del producto.
    public void agregarProducto(Producto producto, int cantidad) {
        agregarProducto(producto, producto.getNombre(), cantidad, producto.getPrecio());
    }

    // Al leer el historial se recupera el nombre y precio que tuvo la venta.
    public void agregarProducto(Producto producto, String nombre, int cantidad, double precio) {
        comprobarEdicion();
        DetalleVenta detalle = new DetalleVenta(producto, nombre, cantidad, precio);
        Validacion.moneda(getTotal() + detalle.getSubtotal());
        detalles.add(detalle);
    }

    public void eliminarDetalle(int indice) {
        comprobarEdicion();
        if (indice < 0 || indice >= detalles.size()) {
            throw new IllegalArgumentException("Seleccione un detalle de venta válido.");
        }
        detalles.remove(indice);
    }

    public double getTotal() {
        double total = 0;
        for (DetalleVenta detalle : detalles) { total += detalle.getSubtotal(); }
        return Validacion.moneda(total);
    }

    public void confirmar() {
        comprobarEdicion();
        if (detalles.isEmpty()) { throw new IllegalArgumentException("La venta no tiene productos."); }
        confirmada = true;
    }

    private void comprobarEdicion() {
        if (confirmada) { throw new IllegalArgumentException("Una venta confirmada no se puede modificar."); }
    }

    @Override
    public String exportarTexto() {
        StringBuilder texto = new StringBuilder();
        texto.append("VENTA ").append(IDventa).append("\nFecha: ").append(fechaventa);
        texto.append("\nUsuario: ").append(usuario.obtenerDatos());
        texto.append("\nCliente: ").append(cliente == null ? "Público general" : cliente.obtenerDatos());
        texto.append("\n").append(tipoComprobante).append(" Nro. ").append(numComprobante);
        texto.append("\nPago: ").append(metodoPago).append("\n");
        for (DetalleVenta detalle : detalles) { texto.append(detalle).append("\n"); }
        texto.append("TOTAL: S/ ").append(String.format(Locale.ROOT, "%.2f", getTotal())).append("\n");
        return texto.toString();
    }

    @Override
    public String toString() { return IDventa + " - " + fechaventa; }
}
