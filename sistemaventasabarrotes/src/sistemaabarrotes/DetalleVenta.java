package sistemaabarrotes;

/** Parte de una venta. Solo Venta crea sus propios detalles. */
public final class DetalleVenta {
    private final Producto producto;
    private final String nombreProducto;
    private final int cantidad;
    private final double precioUnitario;

    DetalleVenta(Producto producto, String nombre, int cantidad, double precio) {
        if (producto == null || cantidad <= 0) {
            throw new IllegalArgumentException("Seleccione un producto y una cantidad mayor a cero.");
        }
        this.producto = producto;
        this.nombreProducto = Validacion.texto(nombre, "El nombre del producto");
        this.cantidad = cantidad;
        this.precioUnitario = Validacion.positivo(Validacion.moneda(precio), "El precio unitario");
        Validacion.moneda(cantidad * precioUnitario);
    }

    public Producto getProducto() { return producto; }
    public String getCodigoProducto() { return producto.getCodigo(); }
    public String getNombreProducto() { return nombreProducto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return Validacion.moneda(cantidad * precioUnitario); }

    @Override
    public String toString() {
        return nombreProducto + " x " + cantidad + " = S/ " + String.format(java.util.Locale.ROOT, "%.2f", getSubtotal());
    }
}
