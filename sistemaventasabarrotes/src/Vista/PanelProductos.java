package Vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Controladores.ControladorCategoria;
import Controladores.ControladorDistribuidor;
import Controladores.ControladorProducto;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sistemaabarrotes.Categoria;
import sistemaabarrotes.Distribuidor;
import sistemaabarrotes.Producto;

public class PanelProductos extends JPanel implements Formulario, ActionListener {

    private final ControladorProducto controladorProducto;
    private final ControladorCategoria controladorCategoria;
    private final ControladorDistribuidor controladorDistribuidor;

    private JTextField txtCodigo, txtNombre, txtMarca, txtPrecio, txtStock, txtStockMin, txtFechaVenc;
    private JComboBox<Categoria> cmbCategoria;
    private JComboBox<Distribuidor> cmbDistribuidor;
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelProductos(ControladorProducto controladorProducto, ControladorCategoria controladorCategoria, ControladorDistribuidor controladorDistribuidor) {
        this.controladorProducto = controladorProducto;
        this.controladorCategoria = controladorCategoria;
        this.controladorDistribuidor = controladorDistribuidor;
        setLayout(null);
        setBackground(UIUtils.CONTENIDO_BG);
        construirEncabezado();
        construirFormulario();
        construirTabla();
        refrescar();
    }

    private void construirEncabezado() {
        JLabel lblTitulo = new JLabel("Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(UIUtils.TEXTO_OSCURO);
        lblTitulo.setBounds(30, 20, 400, 30);
        add(lblTitulo);

        JLabel lblSub = new JLabel("Registra y controla el inventario de abarrotes.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UIUtils.GRIS_TEXTO);
        lblSub.setBounds(30, 50, 400, 18);
        add(lblSub);
    }

    private void construirFormulario() {
        JPanel tarjeta = UIUtils.tarjetaBlanca();
        tarjeta.setBounds(30, 78, 900, 195);
        add(tarjeta);

        // fila 1
        txtCodigo = campoConEtiqueta(tarjeta, "C\u00f3digo", 20, 15, 120);
        txtNombre = campoConEtiqueta(tarjeta, "Nombre", 155, 15, 220);
        txtMarca  = campoConEtiqueta(tarjeta, "Marca", 390, 15, 160);
        txtPrecio = campoConEtiqueta(tarjeta, "Precio (S/)", 565, 15, 130);
        txtStock  = campoConEtiqueta(tarjeta, "Stock", 710, 15, 80);
        txtStockMin = campoConEtiqueta(tarjeta, "Stock M\u00edn.", 805, 15, 80);

        // fila 2
        txtFechaVenc = campoConEtiqueta(tarjeta, "Fecha Vencimiento (dd/MM/yyyy)", 20, 82, 230);

        JLabel lblCategoria = UIUtils.etiquetaCampo("Categor\u00eda");
        lblCategoria.setBounds(265, 72, 220, 18);
        tarjeta.add(lblCategoria);
        cmbCategoria = new JComboBox<>();
        cmbCategoria.setBounds(265, 92, 220, 32);
        tarjeta.add(cmbCategoria);

        JLabel lblDistribuidor = UIUtils.etiquetaCampo("Distribuidor");
        lblDistribuidor.setBounds(500, 72, 220, 18);
        tarjeta.add(lblDistribuidor);
        cmbDistribuidor = new JComboBox<>();
        cmbDistribuidor.setBounds(500, 92, 220, 32);
        tarjeta.add(cmbDistribuidor);

        // botones
        JButton btnRegistrar = UIUtils.botonColor("Registrar", UIUtils.VERDE_OSCURO);
        btnRegistrar.setBounds(20, 145, 120, 32);
        btnRegistrar.setActionCommand("registrar");
        btnRegistrar.addActionListener(this);
        tarjeta.add(btnRegistrar);

        JButton btnActualizar = UIUtils.botonColor("Actualizar", UIUtils.AZUL);
        btnActualizar.setBounds(150, 145, 120, 32);
        btnActualizar.setActionCommand("actualizar");
        btnActualizar.addActionListener(this);
        tarjeta.add(btnActualizar);

        JButton btnEliminar = UIUtils.botonColor("Eliminar", UIUtils.ROJO);
        btnEliminar.setBounds(280, 145, 120, 32);
        btnEliminar.setActionCommand("eliminar");
        btnEliminar.addActionListener(this);
        tarjeta.add(btnEliminar);

        JButton btnLimpiar = UIUtils.botonColor("Limpiar", new Color(150, 150, 150));
        btnLimpiar.setBounds(410, 145, 120, 32);
        btnLimpiar.setActionCommand("limpiar");
        btnLimpiar.addActionListener(this);
        tarjeta.add(btnLimpiar);

        JButton btnActualizarListas = UIUtils.botonColor("\u21BB Recargar categor\u00edas/distribuidores", UIUtils.MORADO);
        btnActualizarListas.setBounds(545, 145, 335, 32);
        btnActualizarListas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnActualizarListas.setActionCommand("cargarCombos");
        btnActualizarListas.addActionListener(this);
        tarjeta.add(btnActualizarListas);
    }

    private JTextField campoConEtiqueta(JPanel contenedor, String etiqueta, int x, int y, int ancho) {
        JLabel lbl = UIUtils.etiquetaCampo(etiqueta);
        lbl.setBounds(x, y, ancho, 18);
        contenedor.add(lbl);

        JTextField campo = new JTextField();
        UIUtils.estilizarCampo(campo);
        campo.setBounds(x, y + 21, ancho, 34);
        contenedor.add(campo);
        return campo;
    }

    private void construirTabla() {
        modelo = new DefaultTableModel(new Object[]{
            "C\u00f3digo", "Nombre", "Marca", "Precio", "Stock", "Stock M\u00edn.",
            "Vencimiento", "Categor\u00eda", "Distribuidor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        UIUtils.estilizarTabla(tabla, UIUtils.VERDE_OSCURO);
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evento) {
                if (!evento.getValueIsAdjusting()) { cargarSeleccion(); }
            }
        });

        // resalta filas con stock bajo o proximas a vencer
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    Producto p = obtenerProductoDeFila(row);
                    if (p != null && p.estaVencido()) {
                        c.setBackground(new Color(250, 210, 210));
                    } else if (p != null && p.stockBajo()) {
                        c.setBackground(new Color(255, 235, 205));
                    } else if (p != null && p.proximoVencer()) {
                        c.setBackground(new Color(255, 245, 200));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        };
        tabla.setDefaultRenderer(Object.class, renderer);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setColumnHeaderView(tabla.getTableHeader());
        scroll.setBounds(30, 285, 900, 385);
        add(scroll);

        JLabel lblLeyenda = new JLabel(
                "\u25A0 Rojo: vencido    \u25A0 Naranja: stock bajo    \u25A0 Amarillo: pr\u00f3ximo a vencer");
        lblLeyenda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLeyenda.setForeground(UIUtils.GRIS_TEXTO);
        lblLeyenda.setBounds(30, 675, 700, 18);
        add(lblLeyenda);
    }

    private Producto obtenerProductoDeFila(int fila) {
        if (fila < 0 || fila >= modelo.getRowCount()) return null;
        String codigo = String.valueOf(modelo.getValueAt(fila, 0));
        return controladorProducto.buscarPorCodigo(codigo);
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtCodigo.setText(String.valueOf(modelo.getValueAt(fila, 0)));
        txtCodigo.setEditable(false);
        txtNombre.setText(String.valueOf(modelo.getValueAt(fila, 1)));
        txtMarca.setText(String.valueOf(modelo.getValueAt(fila, 2)));
        txtPrecio.setText(String.valueOf(modelo.getValueAt(fila, 3)));
        txtStock.setText(String.valueOf(modelo.getValueAt(fila, 4)));
        txtStockMin.setText(String.valueOf(modelo.getValueAt(fila, 5)));
        txtFechaVenc.setText(String.valueOf(modelo.getValueAt(fila, 6)));
        String categoria = String.valueOf(modelo.getValueAt(fila, 7));
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getIDcategoria().equals(categoria)) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
        String distribuidor = String.valueOf(modelo.getValueAt(fila, 8));
        for (int i = 0; i < cmbDistribuidor.getItemCount(); i++) {
            if (cmbDistribuidor.getItemAt(i).getCodigo().equals(distribuidor)) {
                cmbDistribuidor.setSelectedIndex(i);
                break;
            }
        }
    }

    private void cargarCombos() {
        cmbCategoria.removeAllItems();
        for (Categoria c : controladorCategoria.listarTodos()) {
            cmbCategoria.addItem(c);
        }
        cmbDistribuidor.removeAllItems();
        for (Distribuidor d : controladorDistribuidor.listarTodos()) {
            cmbDistribuidor.addItem(d);
        }
    }

    private void registrar() {
        Categoria cat = (Categoria) cmbCategoria.getSelectedItem();
        Distribuidor dis = (Distribuidor) cmbDistribuidor.getSelectedItem();
        if (cat == null || dis == null) {
            UIUtils.mensajeError(this, "Registra primero al menos una categor\u00eda y un distribuidor.");
            return;
        }
        String resultado = controladorProducto.registrar(
                txtCodigo.getText().trim(), txtNombre.getText().trim(), txtMarca.getText().trim(),
                txtPrecio.getText().trim(), txtStock.getText().trim(), txtStockMin.getText().trim(),
                txtFechaVenc.getText().trim(), cat.getIDcategoria(), dis.getCodigo());

        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Producto registrado correctamente.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void actualizar() {
        Categoria cat = (Categoria) cmbCategoria.getSelectedItem();
        Distribuidor dis = (Distribuidor) cmbDistribuidor.getSelectedItem();
        if (cat == null || dis == null) {
            UIUtils.mensajeError(this, "Selecciona una categor\u00eda y un distribuidor.");
            return;
        }
        String resultado = controladorProducto.actualizar(
                txtCodigo.getText().trim(), txtNombre.getText().trim(), txtMarca.getText().trim(),
                txtPrecio.getText().trim(), txtStock.getText().trim(), txtStockMin.getText().trim(),
                txtFechaVenc.getText().trim(), cat.getIDcategoria(), dis.getCodigo());

        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Producto actualizado.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void eliminar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            UIUtils.mensajeError(this, "Selecciona un producto de la tabla.");
            return;
        }
        if (!UIUtils.confirmar(this, "\u00bfEliminar el producto " + codigo + "?")) return;

        String resultado = controladorProducto.eliminar(codigo);
        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, resultado);
        } else {
            UIUtils.mensajeError(this, resultado);
        }
        limpiar();
        refrescar();
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtCodigo.setEditable(true);
        txtNombre.setText("");
        txtMarca.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtStockMin.setText("");
        txtFechaVenc.setText("");
        if (cmbCategoria.getItemCount() > 0) cmbCategoria.setSelectedIndex(0);
        if (cmbDistribuidor.getItemCount() > 0) cmbDistribuidor.setSelectedIndex(0);
        tabla.clearSelection();
    }

    public void refrescar() {
        cargarCombos();
        modelo.setRowCount(0);
        ArrayList<Producto> lista = controladorProducto.listarTodos();
        for (Producto p : lista) {
            modelo.addRow(new Object[]{
                p.getCodigo(), p.getNombre(), p.getMarca(),
                String.format(java.util.Locale.ROOT, "%.2f", p.getPrecio()), p.getStock(), p.getStockMinimo(),
                p.getFechaVencimiento(), p.getIdcategoria(), p.getIddistribuidor()
            });
        }
        UIUtils.seleccionarFila(tabla, txtCodigo.getText().trim());
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        try {
            switch (evento.getActionCommand()) {
                case "registrar": registrar(); break;
                case "actualizar": actualizar(); break;
                case "eliminar": eliminar(); break;
                case "limpiar": limpiar(); break;
                case "cargarCombos": cargarCombos(); break;
                default: break;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            UIUtils.mensajeError(this, e.getMessage());
        }
    }
}
