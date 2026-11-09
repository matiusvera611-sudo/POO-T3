package Vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Controladores.ControladorCategoria;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import sistemaabarrotes.Categoria;

public class PanelCategorias extends JPanel implements Formulario, ActionListener {

    private final ControladorCategoria controlador;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelCategorias(ControladorCategoria controlador) {
        this.controlador = controlador;
        setLayout(null);
        setBackground(UIUtils.CONTENIDO_BG);
        construirEncabezado();
        construirFormulario();
        construirTabla();
        refrescar();
    }

    private void construirEncabezado() {
        JLabel lblTitulo = new JLabel("Categor\u00edas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(UIUtils.TEXTO_OSCURO);
        lblTitulo.setBounds(30, 25, 400, 30);
        add(lblTitulo);

        JLabel lblSub = new JLabel("Organiza tus productos por categor\u00eda.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UIUtils.GRIS_TEXTO);
        lblSub.setBounds(30, 55, 400, 18);
        add(lblSub);
    }

    private void construirFormulario() {
        JPanel tarjeta = UIUtils.tarjetaBlanca();
        tarjeta.setBounds(30, 85, 900, 110);
        add(tarjeta);

        JLabel lblId = UIUtils.etiquetaCampo("ID Categor\u00eda");
        lblId.setBounds(20, 15, 150, 18);
        tarjeta.add(lblId);

        txtId = new JTextField();
        UIUtils.estilizarCampo(txtId);
        txtId.setBounds(20, 36, 180, 34);
        tarjeta.add(txtId);

        JLabel lblNombre = UIUtils.etiquetaCampo("Nombre");
        lblNombre.setBounds(220, 15, 250, 18);
        tarjeta.add(lblNombre);

        txtNombre = new JTextField();
        UIUtils.estilizarCampo(txtNombre);
        txtNombre.setBounds(220, 36, 320, 34);
        tarjeta.add(txtNombre);

        JButton btnRegistrar = UIUtils.botonColor("Registrar", UIUtils.VERDE_OSCURO);
        btnRegistrar.setBounds(570, 36, 130, 34);
        btnRegistrar.setActionCommand("registrar");
        btnRegistrar.addActionListener(this);
        tarjeta.add(btnRegistrar);

        JButton btnEliminar = UIUtils.botonColor("Eliminar", UIUtils.ROJO);
        btnEliminar.setBounds(710, 36, 130, 34);
        btnEliminar.setActionCommand("eliminar");
        btnEliminar.addActionListener(this);
        tarjeta.add(btnEliminar);

        JButton btnLimpiar = UIUtils.botonColor("Limpiar", new Color(150, 150, 150));
        btnLimpiar.setBounds(160, 78, 130, 26);
        btnLimpiar.setActionCommand("limpiar");
        btnLimpiar.addActionListener(this);
        tarjeta.add(btnLimpiar);
        JButton btnActualizar = UIUtils.botonColor("Actualizar", UIUtils.AZUL);
        btnActualizar.setBounds(20, 78, 130, 26);
        btnActualizar.setActionCommand("actualizar");
        btnActualizar.addActionListener(this);
        tarjeta.add(btnActualizar);
    }

    private void construirTabla() {
        modelo = new DefaultTableModel(new Object[]{"ID", "Nombre"}, 0) {
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
        scroll.setBounds(30, 210, 900, 460);
        add(scroll);
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtId.setText(String.valueOf(modelo.getValueAt(fila, 0)));
        txtId.setEditable(false);
        txtNombre.setText(String.valueOf(modelo.getValueAt(fila, 1)));
    }

    private void registrar() {
        String resultado = controlador.registrar(txtId.getText().trim(), txtNombre.getText().trim());
        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Categor\u00eda registrada correctamente.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void actualizar() {
        String resultado = controlador.actualizar(txtId.getText(), txtNombre.getText());
        if ("ok".equals(resultado)) {
            UIUtils.mensajeExito(this, "Categoría actualizada.");
            limpiar();
            refrescar();
        } else { UIUtils.mensajeError(this, resultado); }
    }

    private void eliminar() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            UIUtils.mensajeError(this, "Selecciona o escribe el ID de la categor\u00eda a eliminar.");
            return;
        }
        if (!UIUtils.confirmar(this, "\u00bfEliminar la categor\u00eda " + id + "?")) return;

        String resultado = controlador.eliminar(id);
        if (resultado.equals("ok")) {
            UIUtils.mensajeExito(this, "Categor\u00eda eliminada.");
            limpiar();
            refrescar();
        } else {
            UIUtils.mensajeError(this, resultado);
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtId.setEditable(true);
        txtNombre.setText("");
        tabla.clearSelection();
    }

    public void refrescar() {
        modelo.setRowCount(0);
        ArrayList<Categoria> lista = controlador.listarTodos();
        for (Categoria c : lista) {
            modelo.addRow(new Object[]{c.getIDcategoria(), c.getNombre()});
        }
        UIUtils.seleccionarFila(tabla, txtId.getText().trim());
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
