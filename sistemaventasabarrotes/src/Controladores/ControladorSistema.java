package Controladores;

import DatosSistema.AlmacenDatos;
import sistemaabarrotes.Usuario;

/** Crea una sola colección de datos compartida por todas las ventanas. */
public class ControladorSistema {
    private final ControladorCategoria categorias;
    private final ControladorDistribuidor distribuidores;
    private final ControladorCliente clientes;
    private final ControladorProducto productos;
    private final ControladorUsuario usuarios;
    private final ControladorCompra compras;
    private final ControladorVenta ventas;

    public ControladorSistema() {
        this(new AlmacenDatos());
    }

    public ControladorSistema(AlmacenDatos datos) {
        if (datos.getUsuarios().isEmpty()) {
            datos.getUsuarios().add(new Usuario("admin", "Administrador", "admin123", "Titular Gerente"));
            datos.guardar();
        }
        categorias = new ControladorCategoria(datos);
        distribuidores = new ControladorDistribuidor(datos);
        clientes = new ControladorCliente(datos);
        productos = new ControladorProducto(datos, categorias, distribuidores);
        usuarios = new ControladorUsuario(datos);
        compras = new ControladorCompra(datos, productos, distribuidores);
        ventas = new ControladorVenta(datos, productos, clientes);
    }

    public ControladorCategoria getCategorias() { return categorias; }
    public ControladorDistribuidor getDistribuidores() { return distribuidores; }
    public ControladorCliente getClientes() { return clientes; }
    public ControladorProducto getProductos() { return productos; }
    public ControladorUsuario getUsuarios() { return usuarios; }
    public ControladorCompra getCompras() { return compras; }
    public ControladorVenta getVentas() { return ventas; }
}
