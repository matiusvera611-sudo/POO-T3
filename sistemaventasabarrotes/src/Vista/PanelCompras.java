package Vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Controladores.ControladorCompra;
import Controladores.ControladorDistribuidor;
import Controladores.ControladorProducto;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sistemaabarrotes.Compra;
import sistemaabarrotes.Distribuidor;
import sistemaabarrotes.Producto;

/**
 * Modulo de compras a distribuidores. Permite registrar ordenes de compra
 * (con el ID generado automaticamente) y marcar la mercaderia como recibida,
 * lo que incrementa el stock del producto de forma automatica.
 */
public class PanelCompras extends JPanel implements Formulario, ActionListener {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ControladorCompra controlador;
    private final ControladorDistribuidor ctrlDistribuidor;
    private final ControladorProducto ctrlProducto;

    private JTextField txtId;
    private JTextField txtFecha;
    private JComboBox<Producto> cboProducto;
    private JComboBox<Distribuidor> cboDistribuidor;
    private JTextField txtCantidad;
    private JTextField txtTotal;
    private JComboBox<String> cboEstado;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel lblPendientes;

    public PanelCompras(ControladorCompra controlador, ControladorProducto ctrlProducto, ControladorDistribuidor ctrlDistribuidor) {
        this.controlador = controlador;
        this.ctrlProducto = ctrlProducto;
        this.ctrlDistribuidor = ctrlDistribuidor;
        setLayout(null);
        setBackground(UIUtils.CONTENIDO_BG);
        construirEncabezado();
        construirFormulario();
        construirTabla();
        refrescar();
    }

    private void construirEncabezado() {
        JLabel lblTitulo = new JLabel("Compras a distribuidores");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(UIUtils.TEXTO_OSCURO);
        lblTitulo.setBounds(30, 25, 450, 30);
        add(lblTitulo);

        JLabel lblSub = new JLabel("Registra \u00f3rdenes de compra y recibe la mercader\u00eda para actualizar el stock.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UIUtils.GRIS_TEXTO);
        lblSub.setBounds(30, 55, 550, 18);
        add(lblSub);

        lblPendientes = new JLabel("", javax.swing.SwingConstants.RIGHT);
        lblPendientes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPendientes.setForeground(UIUtils.AMBAR.darker());
        lblPendientes.setBounds(600, 55, 330, 18);
        add(lblPendientes);
    }

    private void construirFormulario() {
        JPanel tarjeta = UIUtils.tarjetaBlanca();
        tarjeta.setBounds(30, 85, 900, 150);
        add(tarjeta);

        // -------- fila 1 --------
        JLabel lblId = UIUtils.etiquetaCampo("ID compra");
        lblId.setBounds(20, 15, 100, 18);
        tarjeta.add(lblId);

        txtId = new JTextField();
        UIUtils.estilizarCampo(txtId);
        txtId.setEditable(false);
        txtId.setBackground(new Color(245, 247, 250));
        txtId.setBounds(20, 36, 90, 34);
        tarjeta.add(txtId);

        JLabel lblFecha = UIUtils.etiquetaCampo("Fecha (dd/mm/aaaa)");
        lblFecha.setBounds(125, 15, 170, 18);
        tarjeta.add(lblFecha);

        txtFecha = new JTextField();
        UIUtils.estilizarCampo(txtFecha);
        txtFecha.setBounds(125, 36, 130, 34);
        tarjeta.add(txtFecha);

        JLabel lblProducto = UIUtils.etiquetaCampo("Producto");
        lblProducto.setBounds(270, 15, 150, 18);
        tarjeta.add(lblProducto);

        cboProducto = new JComboBox<>();
        UIUtils.estilizarCampo(cboProducto);
        cboProducto.setBackground(Color.WHITE);
        cboProducto.setBounds(270, 36, 300, 34);
        tarjeta.add(cboProducto);

        JLabel lblDistribuidor = UIUtils.etiquetaCampo("Distribuidor");
        lblDistribuidor.setBounds(585, 15, 150, 18);
        tarjeta.add(lblDistribuidor);

        cboDistribuidor = new JComboBox<>();
        UIUtils.estilizarCampo(cboDistribuidor);
        cboDistribuidor.setBackground(Color.WHITE);
        cboDistribuidor.setBounds(585, 36, 295, 34);
        tarjeta.add(cboDistribuidor);

        // -------- fila 2 --------
        JLabel lblCantidad = UIUtils.etiquetaCampo("Cantidad");
        lblCantidad.setBounds(20, 78, 100, 18);
        tarjeta.add(lblCantidad);

        txtCantidad = new JTextField();
        UIUtils.estilizarCampo(txtCantidad);
        txtCantidad.setBounds(20, 99, 90, 34);
        tarjeta.add(txtCantidad);

        JLabel lblTotal = UIUtils.etiquetaCampo("Total pagado (S/)");
        lblTotal.setBounds(125, 78, 150, 18);
        tarjeta.add(lblTotal);

        txtTotal = new JTextField();
        UIUtils.estilizarCampo(txtTotal);
        txtTotal.setEditable(false);
        txtTotal.setBackground(new Color(245, 247, 250));
        txtTotal.setBounds(125, 99, 130, 34);
        tarjeta.add(txtTotal);

        txtCantidad.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarTotalAutomatico();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarTotalAutomatico();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarTotalAutomatico();
            }
        });
        cboProducto.setActionCommand("actualizarTotalAutomatico");
        cboProducto.addActionListener(this);

        JLabel lblEstado = UIUtils.etiquetaCampo("Estado");
        lblEstado.setBounds(270, 78, 100, 18);
        tarjeta.add(lblEstado);

        cboEstado = new JComboBox<>(new String[]{"Pendiente", "Recibida"});
        UIUtils.estilizarCampo(cboEstado);
        cboEstado.setBackground(Color.WHITE);
        cboEstado.setBounds(270, 99, 140, 34);
        tarjeta.add(cboEstado);

        JButton btnRegistrar = UIUtils.botonColor("Registrar compra", UIUtils.VERDE_OSCURO);
        btnRegistrar.setBounds(430, 102, 150, 30);
        btnRegistrar.setActionCommand("registrar");
        btnRegistrar.addActionListener(this);
        tarjeta.add(btnRegistrar);

        JButton btnRecibir = UIUtils.botonColor("\u2714 Recibir mercader\u00eda", UIUtils.VERDE);
        btnRecibir.setBounds(590, 102, 170, 30);
        btnRecibir.setActionCommand("recibir");
        btnRecibir.addActionListener(this);
        tarjeta.add(btnRecibir);

        JButton btnLimpiar = UIUtils.botonColor("Limpiar", new Color(150, 150, 150));
        btnLimpiar.setBounds(770, 102, 110, 30);
        btnLimpiar.setActionCommand("limpiar");
        btnLimpiar.addActionListener(this);
        tarjeta.add(btnLimpiar);
    }

    private void construirTabla() {
        modelo = new DefaultTableModel(new Object[]{
            "ID", "Fecha", "Producto", "Distribuidor", "Cantidad", "Total (S/)", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        UIUtils.estilizarTabla(tabla, UIUtils.VERDE_OSCURO);

        // Resalta en ambar las compras pendientes de recibir
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean sel, boolean foco, int fila, int col) {
                Component c = super.getTableCellRendererComponent(t, valor, sel, foco, fila, col);
                if (!sel) {
                    String estado = String.valueOf(t.getValueAt(fila, 6));
                    if ("Pendiente".equalsIgnoreCase(estado)) {
                        c.setBackground(new Color(255, 248, 225));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                    c.setForeground(UIUtils.TEXTO_OSCURO);
                }
                return c;
            }
        });
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evento) {
                if (!evento.getValueIsAdjusting()) { cargarSeleccion(); }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setColumnHeaderView(tabla.getTableHeader());
        scroll.setBounds(30, 250, 900, 420);
        add(scroll);
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtId.setText(String.valueOf(modelo.getValueAt(fila, 0)));
    }

    private void registrar() {
        String producto = codigoProducto();
        String distribuidor = codigoDistribuidor();

        if (producto.isEmpty() || distribuidor.isEmpty()) {
            UIUtils.mensajeError(this, "Registra primero al menos un producto y un distribuidor.");
            return;
        }

        actualizarTotalAutomatico();

        String resultado = controlador.registrar(
                txtId.getText().trim(),
                txtFecha.getText().trim(),
                String.valueOf(cboEstado.getSelectedItem()),
                txtCantidad.getText().trim(),
                distribuidor,
                producto);

        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Compra registrada correctamente.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }



    private void actualizarTotalAutomatico() {
        String producto = codigoProducto();
        String cantidad = txtCantidad.getText().trim();
        double total = controlador.calcularTotalAutomatico(producto, cantidad);
        txtTotal.setText(total > 0 ? String.format(java.util.Locale.ROOT, "%.2f", total) : "");
    }

    private void recibir() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            UIUtils.mensajeError(this, "Selecciona una compra de la tabla.");
            return;
        }
        String id = String.valueOf(modelo.getValueAt(fila, 0));
        if (!UIUtils.confirmar(this, "\u00bfMarcar la compra " + id
                + " como recibida?\nEl stock del producto se incrementar\u00e1 autom\u00e1ticamente.")) {
            return;
        }

        String resultado = controlador.recibirMercaderia(id);
        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Mercader\u00eda recibida. Stock actualizado.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void limpiar() {
        txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        txtCantidad.setText("");
        txtTotal.setText("");
        cboEstado.setSelectedIndex(0);
        tabla.clearSelection();
        txtId.setText(controlador.generarNuevoID());
    }

    private String codigoProducto() {
        Producto producto = (Producto) cboProducto.getSelectedItem();
        return producto == null ? "" : producto.getCodigo();
    }

    private String codigoDistribuidor() {
        Distribuidor distribuidor = (Distribuidor) cboDistribuidor.getSelectedItem();
        return distribuidor == null ? "" : distribuidor.getCodigo();
    }

    private void cargarCombos() {
        Producto productoAnterior = (Producto) cboProducto.getSelectedItem();
        Distribuidor distribuidorAnterior = (Distribuidor) cboDistribuidor.getSelectedItem();
        cboProducto.removeAllItems();
        for (Producto producto : ctrlProducto.listarTodos()) { cboProducto.addItem(producto); }
        cboDistribuidor.removeAllItems();
        for (Distribuidor distribuidor : ctrlDistribuidor.listarTodos()) { cboDistribuidor.addItem(distribuidor); }
        if (productoAnterior != null && ctrlProducto.buscarPorCodigo(productoAnterior.getCodigo()) != null) {
            cboProducto.setSelectedItem(productoAnterior);
        }
        if (distribuidorAnterior != null && ctrlDistribuidor.buscarPorCodigo(distribuidorAnterior.getCodigo()) != null) {
            cboDistribuidor.setSelectedItem(distribuidorAnterior);
        }
        actualizarTotalAutomatico();
    }

    public void refrescar() {
        cargarCombos();

        if (txtFecha.getText().trim().isEmpty()) {
            txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        }
        txtId.setText(controlador.generarNuevoID());

        modelo.setRowCount(0);
        int pendientes = 0;
        ArrayList<Compra> lista = controlador.listarTodas();
        for (Compra c : lista) {
            if ("Pendiente".equalsIgnoreCase(c.getEstadoOrden())) pendientes++;
            modelo.addRow(new Object[]{
                c.getIDcompra(), c.getFechaCompra(),
                nombreProducto(c.getCodigoProducto()),
                nombreDistribuidor(c.getIdDistribuidor()),
                c.getCantidadComprada(),
                String.format(java.util.Locale.ROOT, "%.2f", c.getTotalPagado()),
                c.getEstadoOrden()});
        }
        lblPendientes.setText(pendientes == 0
                ? "\u2714 No hay compras pendientes"
                : "\u23F3 " + pendientes + " compra(s) pendiente(s) de recibir");
        lblPendientes.setForeground(pendientes == 0 ? UIUtils.VERDE_OSCURO : UIUtils.AMBAR.darker());
    }

    private String nombreProducto(String codigo) {
        Producto p = ctrlProducto.buscarPorCodigo(codigo);
        return p != null ? codigo + " - " + p.getNombre() : codigo;
    }

    private String nombreDistribuidor(String codigo) {
        Distribuidor d = ctrlDistribuidor.buscarPorCodigo(codigo);
        return d != null ? codigo + " - " + d.getNombre() : codigo;
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        try {
            switch (evento.getActionCommand()) {
                case "actualizarTotalAutomatico": actualizarTotalAutomatico(); break;
                case "registrar": registrar(); break;
                case "recibir": recibir(); break;
                case "limpiar": limpiar(); break;
                default: break;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            UIUtils.mensajeError(this, e.getMessage());
        }
    }
}
