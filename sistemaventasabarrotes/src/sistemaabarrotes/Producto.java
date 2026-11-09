package sistemaabarrotes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class Producto {
    private final String codigo;
    private String nombre;
    private String marca;
    private double precio;
    private int stock;
    private int stockMinimo;
    private String fechaVencimiento;
    // Asociación: estas entidades se crean y administran independientemente.
    private Categoria categoria;
    private Distribuidor distribuidor;

    public Producto(String codigo, String nombre, String marca, double precio,
                    int stock, int stockMinimo, String fechaVencimiento,
                    Categoria categoria, Distribuidor distribuidor) {
        this.codigo = Validacion.texto(codigo, "El código del producto");
        setNombre(nombre);
        setMarca(marca);
        setPrecio(precio);
        setStock(stock);
        setStockMinimo(stockMinimo);
        setFechaVencimiento(fechaVencimiento);
        setCategoria(categoria);
        setDistribuidor(distribuidor);
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getMarca() { return marca; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public int getStockMinimo() { return stockMinimo; }
    public String getFechaVencimiento() { return fechaVencimiento; }
    public Categoria getCategoria() { return categoria; }
    public Distribuidor getDistribuidor() { return distribuidor; }
    public String getIdcategoria() { return categoria.getIDcategoria(); }
    public String getIddistribuidor() { return distribuidor.getCodigo(); }

    public void setNombre(String valor) { nombre = Validacion.texto(valor, "El nombre del producto"); }
    public void setMarca(String valor) { marca = Validacion.texto(valor, "La marca"); }
    public void setStock(int valor) { stock = Validacion.noNegativo(valor, "El stock"); }
    public void setStockMinimo(int valor) { stockMinimo = Validacion.noNegativo(valor, "El stock mínimo"); }
    public void setFechaVencimiento(String valor) { fechaVencimiento = Validacion.fecha(valor); }

    public void setPrecio(double valor) {
        precio = Validacion.positivo(Validacion.moneda(valor), "El precio");
    }

    public void setCategoria(Categoria valor) {
        if (valor == null) { throw new IllegalArgumentException("Seleccione una categoría existente."); }
        categoria = valor;
    }

    public void setDistribuidor(Distribuidor valor) {
        if (valor == null) { throw new IllegalArgumentException("Seleccione un distribuidor existente."); }
        distribuidor = valor;
    }

    public boolean estaVencido() {
        return LocalDate.now().isAfter(LocalDate.parse(fechaVencimiento, Validacion.FORMATO_FECHA));
    }

    public boolean proximoVencer() {
        long dias = ChronoUnit.DAYS.between(LocalDate.now(),
                LocalDate.parse(fechaVencimiento, Validacion.FORMATO_FECHA));
        return dias >= 0 && dias <= 30;
    }

    public boolean stockBajo() { return stock <= stockMinimo; }

    public void reducirStock(int cantidad) {
        if (cantidad <= 0 || cantidad > stock) {
            throw new IllegalArgumentException("Cantidad inválida o stock insuficiente para " + nombre + ".");
        }
        stock -= cantidad;
    }

    public void aumentarStock(int cantidad) {
        if (cantidad <= 0 || cantidad > Integer.MAX_VALUE - stock) {
            throw new IllegalArgumentException("La cantidad de mercadería no es válida.");
        }
        stock += cantidad;
    }

    public void actualizarDatos(Producto otro) {
        nombre = otro.nombre;
        marca = otro.marca;
        precio = otro.precio;
        stock = otro.stock;
        stockMinimo = otro.stockMinimo;
        fechaVencimiento = otro.fechaVencimiento;
        categoria = otro.categoria;
        distribuidor = otro.distribuidor;
    }

    @Override
    public String toString() { return codigo + " - " + nombre; }
}
