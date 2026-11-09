package sistemaabarrotes;

public final class Compra implements Comprobante {
    private final String IDcompra;
    private final String fechaCompra;
    private final double totalPagado;
    private String estadoOrden;
    private final int cantidadComprada;
    // Agregación: producto y distribuidor existen antes de registrar la compra.
    private final Distribuidor distribuidor;
    private final Producto producto;

    public Compra(String IDcompra, String fecha, double total, String estado,
                  int cantidad, Distribuidor distribuidor, Producto producto) {
        this.IDcompra = Validacion.texto(IDcompra, "El código de compra");
        this.fechaCompra = Validacion.fecha(fecha);
        if (cantidad <= 0 || distribuidor == null || producto == null) {
            throw new IllegalArgumentException("Seleccione producto, distribuidor y una cantidad mayor a cero.");
        }
        this.cantidadComprada = cantidad;
        this.distribuidor = distribuidor;
        this.producto = producto;
        this.totalPagado = Validacion.positivo(Validacion.moneda(total), "El total de compra");
        setEstadoOrden(estado);
    }

    public String getIDcompra() { return IDcompra; }
    public String getFechaCompra() { return fechaCompra; }
    public double getTotalPagado() { return totalPagado; }
    public String getEstadoOrden() { return estadoOrden; }
    public int getCantidadComprada() { return cantidadComprada; }
    public Distribuidor getDistribuidor() { return distribuidor; }
    public Producto getProducto() { return producto; }
    public String getIdDistribuidor() { return distribuidor.getCodigo(); }
    public String getCodigoProducto() { return producto.getCodigo(); }

    public void setEstadoOrden(String estado) {
        estadoOrden = Validacion.opcion(estado, "El estado de compra", "Pendiente", "Recibida");
    }

    public void recibirMercaderia() {
        if ("Recibida".equals(estadoOrden)) {
            throw new IllegalArgumentException("La compra ya fue recibida; el stock no se incrementará otra vez.");
        }
        producto.aumentarStock(cantidadComprada);
        estadoOrden = "Recibida";
    }

    @Override
    public String exportarTexto() {
        return "COMPRA " + IDcompra + "\nFecha: " + fechaCompra
                + "\nDistribuidor: " + distribuidor + "\nProducto: " + producto
                + "\nCantidad: " + cantidadComprada + "\nEstado: " + estadoOrden
                + "\nTOTAL: S/ " + String.format(java.util.Locale.ROOT, "%.2f", totalPagado) + "\n";
    }

    @Override
    public String toString() { return IDcompra + " - " + fechaCompra; }
}
