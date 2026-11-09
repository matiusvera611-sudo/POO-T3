package Vista;

import Controladores.ControladorVenta;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import sistemaabarrotes.Cliente;
import sistemaabarrotes.DetalleVenta;
import sistemaabarrotes.Producto;
import sistemaabarrotes.Venta;

public class PanelVentas extends JPanel implements Formulario, ActionListener {
    private final ControladorVenta controlador;
    private final JTextField txtCodigo = new JTextField();
    private final JTextField txtCantidad = new JTextField();
    private final JTextField txtNumero = new JTextField();
    private final JComboBox<Cliente> cboCliente = new JComboBox<Cliente>();
    private final JComboBox<String> cboTipo = new JComboBox<String>(new String[]{"Boleta", "Factura"});
    private final JComboBox<String> cboPago = new JComboBox<String>(new String[]{"Efectivo", "Tarjeta", "Yape", "Plin", "Transferencia"});
    private final JLabel lblEstado = new JLabel();
    private final JLabel lblProducto = new JLabel(" ");
    private final JLabel lblTotal = new JLabel();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Producto", "Cantidad", "Precio (S/)", "Subtotal (S/)"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private String ventaMostrada = "";

    public PanelVentas(ControladorVenta controlador) {
        this.controlador = controlador;
        setLayout(null);
        setBackground(UIUtils.CONTENIDO_BG);
        componentes();
        refrescar();
    }

    private void componentes() {
        JLabel titulo = new JLabel("Registrar venta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBounds(30, 20, 400, 30);
        add(titulo);
        lblEstado.setBounds(30, 53, 600, 20);
        add(lblEstado);
        boton("Nueva venta", "nueva", 750, 25, 180);

        JPanel producto = UIUtils.tarjetaBlanca();
        producto.setBounds(30, 85, 900, 115);
        add(producto);
        campo(producto, "Código del producto", txtCodigo, 20, 15, 230);
        campo(producto, "Cantidad", txtCantidad, 265, 15, 100);
        txtCodigo.setActionCommand("buscar");
        txtCodigo.addActionListener(this);
        JButton buscar = UIUtils.botonColor("Buscar", UIUtils.AZUL);
        buscar.setBounds(385, 36, 140, 32);
        buscar.setActionCommand("buscar");
        buscar.addActionListener(this);
        producto.add(buscar);
        JButton agregar = UIUtils.botonColor("Agregar producto", UIUtils.VERDE_OSCURO);
        agregar.setBounds(545, 36, 200, 32);
        agregar.setActionCommand("agregar");
        agregar.addActionListener(this);
        producto.add(agregar);
        lblProducto.setBounds(20, 80, 850, 24);
        producto.add(lblProducto);

        JPanel comprobante = UIUtils.tarjetaBlanca();
        comprobante.setBounds(30, 215, 900, 95);
        add(comprobante);
        etiqueta(comprobante, "Cliente (vacío: público general)", 20, 15, 260);
        cboCliente.setBounds(20, 38, 265, 32);
        comprobante.add(cboCliente);
        etiqueta(comprobante, "Comprobante", 300, 15, 140);
        cboTipo.setBounds(300, 38, 140, 32);
        comprobante.add(cboTipo);
        campo(comprobante, "Número", txtNumero, 455, 15, 165);
        etiqueta(comprobante, "Método de pago", 640, 15, 200);
        cboPago.setBounds(640, 38, 230, 32);
        comprobante.add(cboPago);

        UIUtils.estilizarTabla(tabla, UIUtils.VERDE_OSCURO);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setColumnHeaderView(tabla.getTableHeader());
        scroll.setBounds(30, 325, 900, 270);
        add(scroll);
        boton("Quitar detalle", "quitar", 30, 620, 150);
        boton("Cancelar venta", "cancelar", 195, 620, 160);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 23));
        lblTotal.setForeground(UIUtils.VERDE_OSCURO);
        lblTotal.setBounds(400, 620, 300, 36);
        add(lblTotal);
        boton("Confirmar venta", "confirmar", 750, 620, 180);
    }

    private void etiqueta(JPanel panel, String texto, int x, int y, int ancho) {
        JLabel etiqueta = UIUtils.etiquetaCampo(texto);
        etiqueta.setBounds(x, y, ancho, 20);
        panel.add(etiqueta);
    }

    private void campo(JPanel panel, String texto, JTextField campo, int x, int y, int ancho) {
        etiqueta(panel, texto, x, y, ancho);
        campo.setBounds(x, y + 23, ancho, 32);
        UIUtils.estilizarCampo(campo);
        panel.add(campo);
    }

    private void boton(String texto, String comando, int x, int y, int ancho) {
        JButton boton = UIUtils.botonColor(texto, UIUtils.VERDE_OSCURO);
        boton.setBounds(x, y, ancho, 36);
        boton.setActionCommand(comando);
        boton.addActionListener(this);
        add(boton);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        try {
            switch (evento.getActionCommand()) {
                case "nueva": nuevaVenta(); break;
                case "buscar": buscarProducto(); break;
                case "agregar":
                    String agregado = controlador.agregarDetalle(txtCodigo.getText(), txtCantidad.getText());
                    if (!"ok".equals(agregado)) { UIUtils.mensajeError(this, agregado); return; }
                    txtCodigo.setText("");
                    txtCantidad.setText("");
                    lblProducto.setText(" ");
                    refrescar();
                    break;
                case "quitar":
                    String eliminado = controlador.eliminarDetalle(tabla.getSelectedRow());
                    if (!"ok".equals(eliminado)) { UIUtils.mensajeError(this, eliminado); return; }
                    refrescar();
                    break;
                case "cancelar":
                    if (controlador.getVentaActual() != null && UIUtils.confirmar(this, "¿Cancelar la venta en curso?")) {
                        controlador.cancelarVenta(); refrescar();
                    }
                    break;
                case "confirmar": confirmarVenta(); break;
                default: break;
            }
        } catch (IllegalArgumentException | IllegalStateException e) { UIUtils.mensajeError(this, e.getMessage()); }
    }

    private void nuevaVenta() {
        Venta actual = controlador.getVentaActual();
        if (actual != null && !actual.getDetalles().isEmpty()) {
            if (!UIUtils.confirmar(this, "¿Cancelar la venta en curso e iniciar otra?")) { return; }
            controlador.cancelarVenta();
        }
        controlador.nuevaVenta();
        ventaMostrada = "";
        refrescar();
    }

    private void buscarProducto() {
        Producto producto = controlador.buscarProducto(txtCodigo.getText());
        lblProducto.setText(producto == null ? "Producto no encontrado."
                : producto.getNombre() + " | Precio: S/ " + String.format(Locale.ROOT, "%.2f", producto.getPrecio())
                  + " | Stock: " + producto.getStock() + (producto.estaVencido() ? " | VENCIDO" : ""));
    }

    private void confirmarVenta() {
        if (controlador.getVentaActual() == null) { UIUtils.mensajeError(this, "Primero inicie una nueva venta."); return; }
        Cliente cliente = (Cliente) cboCliente.getSelectedItem();
        String asociado = controlador.asociarCliente(cliente == null ? "" : cliente.getCodigo());
        if (!"ok".equals(asociado)) { UIUtils.mensajeError(this, asociado); return; }
        controlador.configurarComprobante(String.valueOf(cboTipo.getSelectedItem()),
                txtNumero.getText(), String.valueOf(cboPago.getSelectedItem()));
        String resultado = controlador.confirmarVenta();
        if (resultado.startsWith("ok:")) {
            UIUtils.mensajeExito(this, "Venta registrada correctamente. ID: " + resultado.substring(3));
            refrescar();
        } else { UIUtils.mensajeError(this, resultado); }
    }

    @Override
    public void refrescar() {
        Cliente anterior = (Cliente) cboCliente.getSelectedItem();
        cboCliente.removeAllItems();
        cboCliente.addItem(null);
        for (Cliente cliente : controlador.listarClientes()) {
            cboCliente.addItem(cliente);
            if (cliente == anterior) { cboCliente.setSelectedItem(cliente); }
        }
        Venta venta = controlador.getVentaActual();
        if (venta == null) {
            lblEstado.setText("No hay una venta en curso. Presione Nueva venta para comenzar.");
            ventaMostrada = "";
            txtNumero.setText("");
        } else {
            lblEstado.setText("Venta en curso: " + venta.getIDventa());
            if (!venta.getIDventa().equals(ventaMostrada)) {
                txtNumero.setText(venta.getNumComprobante());
                cboTipo.setSelectedItem(venta.getTipoComprobante());
                cboPago.setSelectedItem(venta.getMetodoPago());
                cboCliente.setSelectedItem(venta.getCliente());
                ventaMostrada = venta.getIDventa();
            }
        }
        modelo.setRowCount(0);
        if (venta != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                modelo.addRow(new Object[]{detalle.getCodigoProducto(), detalle.getNombreProducto(), detalle.getCantidad(),
                        String.format(Locale.ROOT, "%.2f", detalle.getPrecioUnitario()),
                        String.format(Locale.ROOT, "%.2f", detalle.getSubtotal())});
            }
        }
        lblTotal.setText("Total: S/ " + String.format(Locale.ROOT, "%.2f", controlador.getTotalActual()));
    }
}
