package Vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Controladores.ControladorUsuario;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import sistemaabarrotes.Usuario;

public class PanelUsuarios extends JPanel implements Formulario, ActionListener {

    private final ControladorUsuario controlador;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelUsuarios(ControladorUsuario controlador) {
        this.controlador = controlador;
        setLayout(null);
        setBackground(UIUtils.CONTENIDO_BG);
        construirEncabezado();
        construirFormulario();
        construirTabla();
        refrescar();
    }

    private void construirEncabezado() {
        JLabel lblTitulo = new JLabel("Usuarios del sistema");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(UIUtils.TEXTO_OSCURO);
        lblTitulo.setBounds(30, 25, 400, 30);
        add(lblTitulo);

        JLabel lblSub = new JLabel("Solo el Titular Gerente puede administrar usuarios.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UIUtils.GRIS_TEXTO);
        lblSub.setBounds(30, 55, 400, 18);
        add(lblSub);
    }

    private void construirFormulario() {
        JPanel tarjeta = UIUtils.tarjetaBlanca();
        tarjeta.setBounds(30, 85, 900, 130);
        add(tarjeta);

        JLabel lblCodigo = UIUtils.etiquetaCampo("C\u00f3digo");
        lblCodigo.setBounds(20, 15, 150, 18);
        tarjeta.add(lblCodigo);

        txtCodigo = new JTextField();
        UIUtils.estilizarCampo(txtCodigo);
        txtCodigo.setBounds(20, 36, 150, 34);
        tarjeta.add(txtCodigo);

        JLabel lblNombre = UIUtils.etiquetaCampo("Nombre completo");
        lblNombre.setBounds(185, 15, 200, 18);
        tarjeta.add(lblNombre);

        txtNombre = new JTextField();
        UIUtils.estilizarCampo(txtNombre);
        txtNombre.setBounds(185, 36, 260, 34);
        tarjeta.add(txtNombre);

        JLabel lblPassword = UIUtils.etiquetaCampo("Contrase\u00f1a");
        lblPassword.setBounds(460, 15, 150, 18);
        tarjeta.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setToolTipText("Al actualizar, déjela vacía para conservar la contraseña.");
        UIUtils.estilizarCampo(txtPassword);
        txtPassword.setBounds(460, 36, 160, 34);
        tarjeta.add(txtPassword);

        JLabel lblRol = UIUtils.etiquetaCampo("Rol");
        lblRol.setBounds(635, 15, 150, 18);
        tarjeta.add(lblRol);

        cmbRol = new JComboBox<>(new String[]{"Titular Gerente", "Director T\u00e9cnico", "Vendedor"});
        cmbRol.setBounds(635, 36, 200, 34);
        tarjeta.add(cmbRol);

        JButton btnRegistrar = UIUtils.botonColor("Registrar", UIUtils.VERDE_OSCURO);
        btnRegistrar.setBounds(20, 85, 120, 30);
        btnRegistrar.setActionCommand("registrar");
        btnRegistrar.addActionListener(this);
        tarjeta.add(btnRegistrar);

        JButton btnActualizar = UIUtils.botonColor("Actualizar", UIUtils.AZUL);
        btnActualizar.setBounds(150, 85, 120, 30);
        btnActualizar.setActionCommand("actualizar");
        btnActualizar.addActionListener(this);
        tarjeta.add(btnActualizar);

        JButton btnEliminar = UIUtils.botonColor("Eliminar", UIUtils.ROJO);
        btnEliminar.setBounds(280, 85, 120, 30);
        btnEliminar.setActionCommand("eliminar");
        btnEliminar.addActionListener(this);
        tarjeta.add(btnEliminar);

        JButton btnLimpiar = UIUtils.botonColor("Limpiar", new Color(150, 150, 150));
        btnLimpiar.setBounds(410, 85, 120, 30);
        btnLimpiar.setActionCommand("limpiar");
        btnLimpiar.addActionListener(this);
        tarjeta.add(btnLimpiar);
    }

    private void construirTabla() {
        modelo = new DefaultTableModel(new Object[]{"C\u00f3digo", "Nombre", "Rol"}, 0) {
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
        scroll.setBounds(30, 230, 900, 440);
        add(scroll);
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtCodigo.setText(String.valueOf(modelo.getValueAt(fila, 0)));
        txtCodigo.setEditable(false);
        txtNombre.setText(String.valueOf(modelo.getValueAt(fila, 1)));
        cmbRol.setSelectedItem(String.valueOf(modelo.getValueAt(fila, 2)));
        txtPassword.setText("");
    }

    private void registrar() {
        String resultado = controlador.registrar(
                txtCodigo.getText().trim(), txtNombre.getText().trim(),
                String.valueOf(txtPassword.getPassword()).trim(),
                String.valueOf(cmbRol.getSelectedItem()));
        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Usuario registrado correctamente.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void actualizar() {
        String resultado = controlador.actualizar(
                txtCodigo.getText().trim(), txtNombre.getText().trim(),
                String.valueOf(txtPassword.getPassword()).trim(),
                String.valueOf(cmbRol.getSelectedItem()));
        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Usuario actualizado.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void eliminar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            UIUtils.mensajeError(this, "Selecciona un usuario de la tabla.");
            return;
        }
        if (!UIUtils.confirmar(this, "\u00bfEliminar al usuario " + codigo + "?")) return;

        String resultado = controlador.eliminar(codigo);
        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Usuario eliminado.");
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
        txtPassword.setText("");
        cmbRol.setSelectedIndex(0);
        tabla.clearSelection();
    }

    public void refrescar() {
        modelo.setRowCount(0);
        ArrayList<Usuario> lista = controlador.listarTodos();
        for (Usuario u : lista) {
            modelo.addRow(new Object[]{u.getCodigo(), u.getNombre(), u.getRol()});
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
