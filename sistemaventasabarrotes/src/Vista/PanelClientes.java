package Vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Controladores.ControladorCliente;
import java.awt.Color;
import java.awt.Font;
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
import javax.swing.table.DefaultTableModel;
import sistemaabarrotes.Cliente;

/**
 * Modulo de gestion de clientes: registro, actualizacion, eliminacion y
 * busqueda en vivo. Usa el modelo Cliente (Persona) ya existente con
 * validaciones de DNI/RUC/CE, correo y telefono.
 */
public class PanelClientes extends JPanel implements Formulario, ActionListener {

    private final ControladorCliente controlador;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JComboBox<String> cboTipoDoc;
    private JTextField txtNumDoc;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JComboBox<String> cboTipoCliente;
    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelClientes(ControladorCliente controlador) {
        this.controlador = controlador;
        setLayout(null);
        setBackground(UIUtils.CONTENIDO_BG);
        construirEncabezado();
        construirFormulario();
        construirTabla();
        refrescar();
    }

    private void construirEncabezado() {
        JLabel lblTitulo = new JLabel("Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(UIUtils.TEXTO_OSCURO);
        lblTitulo.setBounds(30, 25, 400, 30);
        add(lblTitulo);

        JLabel lblSub = new JLabel("Registra a tus clientes y acumula sus puntos de compra.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UIUtils.GRIS_TEXTO);
        lblSub.setBounds(30, 55, 500, 18);
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

        JLabel lblNombre = UIUtils.etiquetaCampo("Nombres");
        lblNombre.setBounds(145, 15, 160, 18);
        tarjeta.add(lblNombre);

        txtNombre = new JTextField();
        UIUtils.estilizarCampo(txtNombre);
        txtNombre.setBounds(145, 36, 190, 34);
        tarjeta.add(txtNombre);

        JLabel lblApellido = UIUtils.etiquetaCampo("Apellidos");
        lblApellido.setBounds(350, 15, 160, 18);
        tarjeta.add(lblApellido);

        txtApellido = new JTextField();
        UIUtils.estilizarCampo(txtApellido);
        txtApellido.setBounds(350, 36, 190, 34);
        tarjeta.add(txtApellido);

        JLabel lblTipoDoc = UIUtils.etiquetaCampo("Tipo doc.");
        lblTipoDoc.setBounds(555, 15, 100, 18);
        tarjeta.add(lblTipoDoc);

        cboTipoDoc = new JComboBox<>(new String[]{"DNI", "RUC", "CE"});
        UIUtils.estilizarCampo(cboTipoDoc);
        cboTipoDoc.setBackground(Color.WHITE);
        cboTipoDoc.setBounds(555, 36, 90, 34);
        tarjeta.add(cboTipoDoc);

        JLabel lblNumDoc = UIUtils.etiquetaCampo("N\u00b0 documento");
        lblNumDoc.setBounds(660, 15, 150, 18);
        tarjeta.add(lblNumDoc);

        txtNumDoc = new JTextField();
        UIUtils.estilizarCampo(txtNumDoc);
        txtNumDoc.setBounds(660, 36, 220, 34);
        tarjeta.add(txtNumDoc);

        // -------- fila 2 --------
        JLabel lblCorreo = UIUtils.etiquetaCampo("Correo");
        lblCorreo.setBounds(20, 78, 160, 18);
        tarjeta.add(lblCorreo);

        txtCorreo = new JTextField();
        UIUtils.estilizarCampo(txtCorreo);
        txtCorreo.setBounds(20, 99, 235, 34);
        tarjeta.add(txtCorreo);

        JLabel lblTelefono = UIUtils.etiquetaCampo("Tel\u00e9fono (9 d\u00edgitos)");
        lblTelefono.setBounds(270, 78, 180, 18);
        tarjeta.add(lblTelefono);

        txtTelefono = new JTextField();
        UIUtils.estilizarCampo(txtTelefono);
        txtTelefono.setBounds(270, 99, 150, 34);
        tarjeta.add(txtTelefono);

        JLabel lblDireccion = UIUtils.etiquetaCampo("Direcci\u00f3n");
        lblDireccion.setBounds(435, 78, 160, 18);
        tarjeta.add(lblDireccion);

        txtDireccion = new JTextField();
        UIUtils.estilizarCampo(txtDireccion);
        txtDireccion.setBounds(435, 99, 265, 34);
        tarjeta.add(txtDireccion);

        JLabel lblTipoCliente = UIUtils.etiquetaCampo("Tipo de cliente");
        lblTipoCliente.setBounds(715, 78, 160, 18);
        tarjeta.add(lblTipoCliente);

        cboTipoCliente = new JComboBox<>(new String[]{"NORMAL", "FRECUENTE", "MAYORISTA"});
        UIUtils.estilizarCampo(cboTipoCliente);
        cboTipoCliente.setBackground(Color.WHITE);
        cboTipoCliente.setBounds(715, 99, 165, 34);
        tarjeta.add(cboTipoCliente);

        // -------- fila 3: botones --------
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

        // -------- buscador --------
        JLabel lblBuscar = UIUtils.etiquetaCampo("\uD83D\uDD0D Buscar");
        lblBuscar.setBounds(590, 153, 90, 18);
        tarjeta.add(lblBuscar);

        txtBuscar = new JTextField();
        UIUtils.estilizarCampo(txtBuscar);
        txtBuscar.setBounds(670, 145, 210, 34);
        txtBuscar.setToolTipText("Filtra por c\u00f3digo, nombre, apellido o documento");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refrescar(); }
            @Override public void removeUpdate(DocumentEvent e) { refrescar(); }
            @Override public void changedUpdate(DocumentEvent e) { refrescar(); }
        });
        tarjeta.add(txtBuscar);
    }

    private void construirTabla() {
        modelo = new DefaultTableModel(new Object[]{
            "C\u00f3digo", "Nombres", "Apellidos", "Doc.", "N\u00b0 Doc.",
            "Correo", "Tel\u00e9fono", "Direcci\u00f3n", "Tipo", "Puntos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        UIUtils.estilizarTabla(tabla, UIUtils.MORADO);
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
        txtApellido.setText(String.valueOf(modelo.getValueAt(fila, 2)));
        cboTipoDoc.setSelectedItem(String.valueOf(modelo.getValueAt(fila, 3)));
        txtNumDoc.setText(String.valueOf(modelo.getValueAt(fila, 4)));
        txtCorreo.setText(String.valueOf(modelo.getValueAt(fila, 5)));
        txtTelefono.setText(String.valueOf(modelo.getValueAt(fila, 6)));
        txtDireccion.setText(String.valueOf(modelo.getValueAt(fila, 7)));
        cboTipoCliente.setSelectedItem(String.valueOf(modelo.getValueAt(fila, 8)));
    }

    private void registrar() {
        String resultado = controlador.registrar(
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                String.valueOf(cboTipoDoc.getSelectedItem()),
                txtNumDoc.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                txtDireccion.getText().trim(),
                String.valueOf(cboTipoCliente.getSelectedItem()),
                0);
        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Cliente registrado correctamente.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void actualizar() {
        String resultado = controlador.actualizar(
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                String.valueOf(cboTipoDoc.getSelectedItem()),
                txtNumDoc.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                txtDireccion.getText().trim(),
                String.valueOf(cboTipoCliente.getSelectedItem()),
                controlador.buscarPorCodigo(txtCodigo.getText().trim()) == null ? 0
                    : controlador.buscarPorCodigo(txtCodigo.getText().trim()).getPuntos());
        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Cliente actualizado.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void eliminar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            UIUtils.mensajeError(this, "Selecciona un cliente de la tabla.");
            return;
        }
        if (!UIUtils.confirmar(this, "\u00bfEliminar al cliente " + codigo + "?")) return;

        String resultado = controlador.eliminar(codigo);
        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Cliente eliminado.");
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
        txtApellido.setText("");
        cboTipoDoc.setSelectedIndex(0);
        txtNumDoc.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        cboTipoCliente.setSelectedIndex(0);
        tabla.clearSelection();
    }

    public void refrescar() {
        modelo.setRowCount(0);
        String filtro = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        ArrayList<Cliente> lista = controlador.listarTodos();
        for (Cliente c : lista) {
            if (!filtro.isEmpty()) {
                String texto = (c.getCodigo() + " " + c.getNombre() + " "
                        + c.getApellido() + " " + c.getNmrdoc()).toLowerCase();
                if (!texto.contains(filtro)) continue;
            }
            modelo.addRow(new Object[]{
                c.getCodigo(), c.getNombre(), c.getApellido(),
                c.getTipodoc(), c.getNmrdoc(), c.getCorreo(),
                c.getTelefono(), c.getDireccion(), c.getTipoCliente(), c.getPuntos()});
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
