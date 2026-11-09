package DatosSistema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import sistemaabarrotes.Categoria;
import sistemaabarrotes.Cliente;
import sistemaabarrotes.Compra;
import sistemaabarrotes.DetalleVenta;
import sistemaabarrotes.Distribuidor;
import sistemaabarrotes.Producto;
import sistemaabarrotes.Usuario;
import sistemaabarrotes.Venta;

/** Lee y guarda datos. No contiene ventanas ni reglas de venta. */
public class AlmacenDatos {
    private final ArrayList<Categoria> categorias = new ArrayList<Categoria>();
    private final ArrayList<Distribuidor> distribuidores = new ArrayList<Distribuidor>();
    private final ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
    private final ArrayList<Cliente> clientes = new ArrayList<Cliente>();
    private final ArrayList<Producto> productos = new ArrayList<Producto>();
    private final ArrayList<Compra> compras = new ArrayList<Compra>();
    private final ArrayList<Venta> ventas = new ArrayList<Venta>();
    private final Path archivo;

    public AlmacenDatos() {
        this(Paths.get("."));
    }

    public AlmacenDatos(Path carpeta) {
        archivo = carpeta.resolve("datos_abarrotes.txt");
        cargar(carpeta);
    }

    public ArrayList<Categoria> getCategorias() { return categorias; }
    public ArrayList<Distribuidor> getDistribuidores() { return distribuidores; }
    public ArrayList<Usuario> getUsuarios() { return usuarios; }
    public ArrayList<Cliente> getClientes() { return clientes; }
    public ArrayList<Producto> getProductos() { return productos; }
    public ArrayList<Compra> getCompras() { return compras; }
    public ArrayList<Venta> getVentas() { return ventas; }

    private void cargar(Path carpeta) {
        try {
            ArrayList<String> lineas = new ArrayList<String>();
            if (Files.exists(archivo)) {
                lineas.addAll(Files.readAllLines(archivo, StandardCharsets.UTF_8));
                if (lineas.isEmpty() || !"SISTEMA|1".equals(lineas.get(0))) {
                    throw new IllegalArgumentException("El archivo de datos no tiene un formato válido.");
                }
            } else {
                // Se pueden recuperar los TXT de la versión anterior si existen.
                importar(carpeta, lineas, "categorias.txt", "CATEGORIA");
                importar(carpeta, lineas, "proveedores.txt", "DISTRIBUIDOR");
                importar(carpeta, lineas, "usuarios.txt", "USUARIO");
                importar(carpeta, lineas, "clientes.txt", "CLIENTE");
                importar(carpeta, lineas, "productos.txt", "PRODUCTO");
                importar(carpeta, lineas, "compras.txt", "COMPRA");
                importar(carpeta, lineas, "ventas.txt", "VENTA");
                importar(carpeta, lineas, "detalles.txt", "DETALLE");
            }
            String[] tipos = {"CATEGORIA", "DISTRIBUIDOR", "USUARIO", "CLIENTE",
                    "PRODUCTO", "COMPRA", "VENTA", "DETALLE"};
            for (String linea : lineas) {
                if (linea.trim().isEmpty() || "SISTEMA|1".equals(linea)) { continue; }
                boolean reconocido = false;
                for (String tipo : tipos) {
                    if (linea.startsWith(tipo + "|")) { reconocido = true; break; }
                }
                if (!reconocido) { throw new IllegalArgumentException("Hay un registro de datos desconocido."); }
            }
            // Primero se crean los objetos independientes; después se enlazan.
            for (String tipo : tipos) {
                for (String linea : lineas) {
                    if (linea.startsWith(tipo + "|")) {
                        cargarRegistro(linea.split("\\|", -1));
                    }
                }
            }
            for (String linea : lineas) {
                if (linea.startsWith("VENTA|")) {
                    String[] registro = linea.split("\\|", -1);
                    double totalGuardado = Double.parseDouble(registro[4]);
                    if (!Double.isFinite(totalGuardado)
                            || Math.abs(venta(registro[1]).getTotal() - totalGuardado) > 0.005) {
                        throw new IllegalArgumentException("El total de la venta " + registro[1] + " no coincide con sus detalles.");
                    }
                }
            }
            for (Venta venta : ventas) { venta.confirmar(); }
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("No se pudieron cargar los datos: " + e.getMessage(), e);
        }
    }

    private void importar(Path carpeta, ArrayList<String> lineas, String nombre, String tipo) throws IOException {
        Path origen = carpeta.resolve(nombre);
        if (Files.exists(origen)) {
            for (String linea : Files.readAllLines(origen, StandardCharsets.UTF_8)) {
                if (!linea.trim().isEmpty()) { lineas.add(tipo + "|" + linea); }
            }
        }
    }

    private void campos(String[] datos, int cantidad) {
        if (datos.length != cantidad) {
            throw new IllegalArgumentException("Registro incompleto de " + datos[0] + ".");
        }
    }

    private void cargarRegistro(String[] d) {
        switch (d[0]) {
            case "CATEGORIA":
                campos(d, 3);
                for (Categoria c : categorias) {
                    if (c.getIDcategoria().equalsIgnoreCase(d[1])) { throw repetido(d[1]); }
                }
                categorias.add(new Categoria(d[1], d[2]));
                break;
            case "DISTRIBUIDOR":
                campos(d, 7);
                for (Distribuidor c : distribuidores) {
                    if (c.getCodigo().equalsIgnoreCase(d[1])) { throw repetido(d[1]); }
                }
                distribuidores.add(new Distribuidor(d[1], d[2], d[3], d[4], d[5], d[6]));
                break;
            case "USUARIO":
                for (Usuario u : usuarios) {
                    if (u.getCodigo().equalsIgnoreCase(d[1])) { throw repetido(d[1]); }
                }
                if (d.length == 5) {
                    usuarios.add(new Usuario(d[1], d[2], d[3], d[4]));
                    break;
                }
                campos(d, 11);
                if (!"true".equalsIgnoreCase(d[10]) && !"false".equalsIgnoreCase(d[10])) {
                    throw new IllegalArgumentException("El estado del usuario no es válido.");
                }
                usuarios.add(new Usuario(d[1], d[2], d[3], d[4], d[5], d[6],
                        d[7], d[8], d[9], Boolean.parseBoolean(d[10])));
                break;
            case "CLIENTE":
                campos(d, 11);
                for (Cliente c : clientes) {
                    if (c.getCodigo().equalsIgnoreCase(d[1])) { throw repetido(d[1]); }
                }
                clientes.add(new Cliente(d[1], d[2], d[3], d[4], d[5], d[6],
                        d[7], d[8], d[9], Integer.parseInt(d[10])));
                break;
            case "PRODUCTO":
                campos(d, 10);
                for (Producto p : productos) {
                    if (p.getCodigo().equalsIgnoreCase(d[1])) { throw repetido(d[1]); }
                }
                productos.add(new Producto(d[1], d[2], d[3], Double.parseDouble(d[4]),
                        Integer.parseInt(d[5]), Integer.parseInt(d[6]), d[7], categoria(d[8]), distribuidor(d[9])));
                break;
            case "COMPRA":
                campos(d, 8);
                for (Compra c : compras) {
                    if (c.getIDcompra().equalsIgnoreCase(d[1])) { throw repetido(d[1]); }
                }
                compras.add(new Compra(d[1], d[2], Double.parseDouble(d[3]), d[4],
                        Integer.parseInt(d[5]), distribuidor(d[6]), producto(d[7])));
                break;
            case "VENTA":
                campos(d, 9);
                for (Venta v : ventas) {
                    if (v.getIDventa().equalsIgnoreCase(d[1])) { throw repetido(d[1]); }
                }
                Venta venta = new Venta(d[1], usuario(d[2]), d[3]);
                venta.configurarComprobante(d[5], d[6], d[7]);
                venta.setCliente(cliente(d[8]));
                ventas.add(venta);
                break;
            case "DETALLE":
                campos(d, 7);
                double subtotal = Double.parseDouble(d[6]);
                if (!Double.isFinite(subtotal)
                        || Math.abs(subtotal - Integer.parseInt(d[4]) * Double.parseDouble(d[5])) > 0.005) {
                    throw new IllegalArgumentException("Un detalle de la venta " + d[1] + " tiene un subtotal inválido.");
                }
                venta(d[1]).agregarProducto(producto(d[2]), d[3], Integer.parseInt(d[4]), Double.parseDouble(d[5]));
                break;
            default:
                throw new IllegalArgumentException("Tipo de registro desconocido.");
        }
    }

    private IllegalArgumentException repetido(String codigo) {
        return new IllegalArgumentException("Hay un código duplicado: " + codigo);
    }

    private Categoria categoria(String codigo) {
        for (Categoria c : categorias) { if (c.getIDcategoria().equalsIgnoreCase(codigo)) { return c; } }
        throw new IllegalArgumentException("No existe la categoría " + codigo);
    }

    private Distribuidor distribuidor(String codigo) {
        for (Distribuidor d : distribuidores) { if (d.getCodigo().equalsIgnoreCase(codigo)) { return d; } }
        throw new IllegalArgumentException("No existe el distribuidor " + codigo);
    }

    private Usuario usuario(String codigo) {
        for (Usuario u : usuarios) { if (u.getCodigo().equalsIgnoreCase(codigo)) { return u; } }
        throw new IllegalArgumentException("No existe el usuario " + codigo);
    }

    private Cliente cliente(String codigo) {
        if (codigo.isEmpty()) { return null; }
        for (Cliente c : clientes) { if (c.getCodigo().equalsIgnoreCase(codigo)) { return c; } }
        throw new IllegalArgumentException("No existe el cliente " + codigo);
    }

    private Producto producto(String codigo) {
        for (Producto p : productos) { if (p.getCodigo().equalsIgnoreCase(codigo)) { return p; } }
        throw new IllegalArgumentException("No existe el producto " + codigo);
    }

    private Venta venta(String codigo) {
        for (Venta v : ventas) { if (v.getIDventa().equalsIgnoreCase(codigo)) { return v; } }
        throw new IllegalArgumentException("No existe la venta " + codigo);
    }

    public void guardar() {
        ArrayList<String> lineas = new ArrayList<String>();
        lineas.add("SISTEMA|1");
        for (Categoria c : categorias) {
            lineas.add("CATEGORIA|" + c.getIDcategoria() + "|" + c.getNombre());
        }
        for (Distribuidor d : distribuidores) {
            lineas.add("DISTRIBUIDOR|" + d.getCodigo() + "|" + d.getRazonSocial() + "|" + d.getTelefono()
                    + "|" + d.getEmpresa() + "|" + d.getDireccionEmpresa() + "|" + d.getRuc());
        }
        for (Usuario u : usuarios) {
            lineas.add("USUARIO|" + u.getCodigo() + "|" + u.getNombre() + "|" + u.getApellido() + "|"
                    + u.getTipodoc() + "|" + u.getNmrdoc() + "|" + u.getCorreo() + "|" + u.getRol()
                    + "|" + u.getPassword() + "|" + u.getNombreUsuario() + "|" + u.isEstado());
        }
        for (Cliente c : clientes) {
            lineas.add("CLIENTE|" + c.getCodigo() + "|" + c.getNombre() + "|" + c.getApellido() + "|"
                    + c.getTipodoc() + "|" + c.getNmrdoc() + "|" + c.getCorreo() + "|" + c.getTelefono()
                    + "|" + c.getDireccion() + "|" + c.getTipoCliente() + "|" + c.getPuntos());
        }
        for (Producto p : productos) {
            lineas.add("PRODUCTO|" + p.getCodigo() + "|" + p.getNombre() + "|" + p.getMarca() + "|"
                    + p.getPrecio() + "|" + p.getStock() + "|" + p.getStockMinimo() + "|"
                    + p.getFechaVencimiento() + "|" + p.getIdcategoria() + "|" + p.getIddistribuidor());
        }
        for (Compra c : compras) {
            lineas.add("COMPRA|" + c.getIDcompra() + "|" + c.getFechaCompra() + "|" + c.getTotalPagado()
                    + "|" + c.getEstadoOrden() + "|" + c.getCantidadComprada() + "|"
                    + c.getIdDistribuidor() + "|" + c.getCodigoProducto());
        }
        for (Venta v : ventas) {
            lineas.add("VENTA|" + v.getIDventa() + "|" + v.getCodigoUsuario() + "|" + v.getFechaventa()
                    + "|" + v.getTotal() + "|" + v.getTipoComprobante() + "|" + v.getNumComprobante()
                    + "|" + v.getMetodoPago() + "|" + v.getCodigoCliente());
            for (DetalleVenta d : v.getDetalles()) {
                lineas.add("DETALLE|" + v.getIDventa() + "|" + d.getCodigoProducto() + "|" + d.getNombreProducto()
                        + "|" + d.getCantidad() + "|" + d.getPrecioUnitario() + "|" + d.getSubtotal());
            }
        }
        // Una compra o venta y su stock se guardan juntos en un solo archivo.
        Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        try {
            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            try {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudieron guardar los datos: " + e.getMessage(), e);
        }
    }

    public static void guardarReporte(String texto, String ruta) {
        try {
            Files.write(Paths.get(ruta), texto.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo exportar el reporte: " + e.getMessage(), e);
        }
    }
}
