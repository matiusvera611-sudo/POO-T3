package Vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Controladores.ControladorDistribuidor;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import sistemaabarrotes.Distribuidor;

/**
 * Modulo de distribuidores (proveedores) con todos los campos del modelo:
 * razon social, telefono, empresa, direccion y RUC, mas busqueda en vivo.
 */
public class PanelDistribuidores extends JPanel implements Formulario, ActionListener {

    private final ControladorDistribuidor controlador;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtEmpresa;
    private JTextField txtDireccion;
    private JTextField txtRuc;
    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelDistribuidores(ControladorDistribuidor controlador) {
        this.controlador = controlador;
        setLayout(null);
        setBackground(UIUtils.CONTENIDO_BG);
        construirEncabezado();
        construirFormulario();
        construirTabla();
        refrescar();
    }

    private void construirEncabezado() {
        JLabel lblTitulo = new JLabel("Distribuidores");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(UIUtils.TEXTO_OSCURO);
        lblTitulo.setBounds(30, 25, 400, 30);
        add(lblTitulo);

        JLabel lblSub = new JLabel("Administra a tus proveedores de abarrotes.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UIUtils.GRIS_TEXTO);
        lblSub.setBounds(30, 55, 400, 18);
        add(lblSub);
    }

    private void construirFormulario() {
        JPanel tarjeta = UIUtils.tarjetaBlanca();
        tarjeta.setBounds(30, 85, 900, 190);
        add(tarjeta);

        // -------- fila 1 --------
        JLabel lblCodigo = UIUtils.etiquetaCampo("C\u00f3digo");
        lblCodigo.setBounds(20, 15, 120, 18);
        tarjeta.add(lblCodigo);

        txtCodigo = new JTextField();
        UIUtils.estilizarCampo(txtCodigo);
        txtCodigo.setBounds(20, 36, 110, 34);
        tarjeta.add(txtCodigo);

        JLabel lblNombre = UIUtils.etiquetaCampo("Nombre / Raz\u00f3n social");
        lblNombre.setBounds(145, 15, 250, 18);
        tarjeta.add(lblNombre);

        txtNombre = new JTextField();
        UIUtils.estilizarCampo(txtNombre);
        txtNombre.setBounds(145, 36, 280, 34);
        tarjeta.add(txtNombre);

        JLabel lblTelefono = UIUtils.etiquetaCampo("Tel\u00e9fono");
        lblTelefono.setBounds(440, 15, 120, 18);
        tarjeta.add(lblTelefono);

        txtTelefono = new JTextField();
        UIUtils.estilizarCampo(txtTelefono);
        txtTelefono.setBounds(440, 36, 140, 34);
        tarjeta.add(txtTelefono);

        JLabel lblRuc = UIUtils.etiquetaCampo("RUC (11 d\u00edgitos)");
        lblRuc.setBounds(595, 15, 150, 18);
        tarjeta.add(lblRuc);

        txtRuc = new JTextField();
        UIUtils.estilizarCampo(txtRuc);
        txtRuc.setBounds(595, 36, 285, 34);
        tarjeta.add(txtRuc);

        // -------- fila 2 --------
        JLabel lblEmpresa = UIUtils.etiquetaCampo("Empresa");
        lblEmpresa.setBounds(20, 78, 150, 18);
        tarjeta.add(lblEmpresa);

        txtEmpresa = new JTextField();
        UIUtils.estilizarCampo(txtEmpresa);
        txtEmpresa.setBounds(20, 99, 280, 34);
        tarjeta.add(txtEmpresa);

        JLabel lblDireccion = UIUtils.etiquetaCampo("Direcci\u00f3n");
        lblDireccion.setBounds(315, 78, 150, 18);
        tarjeta.add(lblDireccion);

        txtDireccion = new JTextField();
        UIUtils.estilizarCampo(txtDireccion);
        txtDireccion.setBounds(315, 99, 565, 34);
        tarjeta.add(txtDireccion);

        // -------- fila 3: botones + buscador --------
        JButton btnRegistrar = UIUtils.botonColor("Registrar", UIUtils.VERDE_OSCURO);
        btnRegistrar.setBounds(20, 148, 120, 28);
        btnRegistrar.setActionCommand("registrar");
        btnRegistrar.addActionListener(this);
        tarjeta.add(btnRegistrar);

        JButton btnActualizar = UIUtils.botonColor("Actualizar", UIUtils.AZUL);
        btnActualizar.setBounds(150, 148, 120, 28);
        btnActualizar.setActionCommand("actualizar");
        btnActualizar.addActionListener(this);
        tarjeta.add(btnActualizar);

        JButton btnEliminar = UIUtils.botonColor("Eliminar", UIUtils.ROJO);
        btnEliminar.setBounds(280, 148, 120, 28);
        btnEliminar.setActionCommand("eliminar");
        btnEliminar.addActionListener(this);
        tarjeta.add(btnEliminar);

        JButton btnLimpiar = UIUtils.botonColor("Limpiar", new Color(150, 150, 150));
        btnLimpiar.setBounds(410, 148, 120, 28);
        btnLimpiar.setActionCommand("limpiar");
        btnLimpiar.addActionListener(this);
        tarjeta.add(btnLimpiar);

        JLabel lblBuscar = UIUtils.etiquetaCampo("\uD83D\uDD0D Buscar");
        lblBuscar.setBounds(590, 153, 90, 18);
        tarjeta.add(lblBuscar);

        txtBuscar = new JTextField();
        UIUtils.estilizarCampo(txtBuscar);
        txtBuscar.setBounds(670, 145, 210, 34);
        txtBuscar.setToolTipText("Filtra por c\u00f3digo, nombre, empresa o RUC");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refrescar(); }
            @Override public void removeUpdate(DocumentEvent e) { refrescar(); }
            @Override public void changedUpdate(DocumentEvent e) { refrescar(); }
        });
        tarjeta.add(txtBuscar);
    }

    private void construirTabla() {
        modelo = new DefaultTableModel(new Object[]{
            "C\u00f3digo", "Nombre / Raz\u00f3n social", "Tel\u00e9fono",
            "Empresa", "Direcci\u00f3n", "RUC"}, 0) {
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

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setColumnHeaderView(tabla.getTableHeader());
        scroll.setBounds(30, 290, 900, 380);
        add(scroll);
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtCodigo.setText(String.valueOf(modelo.getValueAt(fila, 0)));
        txtCodigo.setEditable(false);
        txtNombre.setText(String.valueOf(modelo.getValueAt(fila, 1)));
        txtTelefono.setText(String.valueOf(modelo.getValueAt(fila, 2)));
        txtEmpresa.setText(String.valueOf(modelo.getValueAt(fila, 3)));
        txtDireccion.setText(String.valueOf(modelo.getValueAt(fila, 4)));
        txtRuc.setText(String.valueOf(modelo.getValueAt(fila, 5)));
    }

    private void registrar() {
        String resultado = controlador.registrar(
                txtCodigo.getText().trim(), txtNombre.getText().trim(),
                txtTelefono.getText().trim(), txtEmpresa.getText().trim(),
                txtDireccion.getText().trim(), txtRuc.getText().trim());
        if (resultado.equalsIgnoreCase("ok")) {
            UIUtils.mensajeExito(this, "Distribuidor registrado correctamente.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void actualizar() {
        String resultado = controlador.actualizar(
                txtCodigo.getText().trim(), txtNombre.getText().trim(),
                txtTelefono.getText().trim(), txtEmpresa.getText().trim(),
                txtDireccion.getText().trim(), txtRuc.getText().trim());
        if (resultado.equalsIgnoreCase("ok")) {
            UIUtils.mensajeExito(this, "Distribuidor actualizado.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void eliminar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            UIUtils.mensajeError(this, "Selecciona un distribuidor de la tabla.");
            return;
        }
        if (!UIUtils.confirmar(this, "\u00bfEliminar al distribuidor " + codigo + "?")) return;

        String resultado = controlador.eliminar(codigo);
        if (resultado.equalsIgnoreCase("ok")) {
            UIUtils.mensajeExito(this, "Distribuidor eliminado.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtCodigo.setEditable(true);
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEmpresa.setText("");
        txtDireccion.setText("");
        txtRuc.setText("");
        tabla.clearSelection();
    }

    public void refrescar() {
        modelo.setRowCount(0);
        String filtro = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        ArrayList<Distribuidor> lista = controlador.listarTodos();
        for (Distribuidor d : lista) {
            if (!filtro.isEmpty()) {
                String texto = (d.getCodigo() + " " + d.getNombre() + " "
                        + d.getEmpresa() + " " + d.getRuc()).toLowerCase();
                if (!texto.contains(filtro)) continue;
            }
            modelo.addRow(new Object[]{
                d.getCodigo(), d.getNombre(), d.getTelefono(),
                d.getEmpresa(), d.getDireccionEmpresa(), d.getRuc()});
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
                default: break;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            UIUtils.mensajeError(this, e.getMessage());
        }
    }
}
