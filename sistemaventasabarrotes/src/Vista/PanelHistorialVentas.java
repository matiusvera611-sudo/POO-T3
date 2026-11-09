package Vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Controladores.ControladorVenta;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import sistemaabarrotes.Venta;

public class PanelHistorialVentas extends JPanel implements Formulario, ActionListener {

    private final ControladorVenta controlador;

    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel lblTotalGeneral;

    public PanelHistorialVentas(ControladorVenta controlador) {
        this.controlador = controlador;
        setLayout(null);
        setBackground(UIUtils.CONTENIDO_BG);
        construirEncabezado();
        construirTabla();
        refrescar();
    }

    private void construirEncabezado() {
        JLabel lblTitulo = new JLabel("Historial de Ventas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(UIUtils.TEXTO_OSCURO);
        lblTitulo.setBounds(30, 25, 400, 30);
        add(lblTitulo);

        JLabel lblSub = new JLabel("Consulta todas las ventas registradas y exporta un reporte.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(UIUtils.GRIS_TEXTO);
        lblSub.setBounds(30, 55, 500, 18);
        add(lblSub);

        JButton btnExportar = UIUtils.botonColor("\u2B07 Exportar reporte (.txt)", UIUtils.VERDE_OSCURO);
        btnExportar.setBounds(700, 30, 220, 36);
        btnExportar.setActionCommand("exportarReporte");
        btnExportar.addActionListener(this);
        add(btnExportar);

        JButton btnRefrescar = UIUtils.botonColor("\u21BB Actualizar", UIUtils.AZUL);
        btnRefrescar.setBounds(560, 30, 130, 36);
        btnRefrescar.setActionCommand("refrescar");
        btnRefrescar.addActionListener(this);
        add(btnRefrescar);
    }

    private void construirTabla() {
        modelo = new DefaultTableModel(new Object[]{"ID Venta", "Fecha", "Usuario", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        UIUtils.estilizarTabla(tabla, UIUtils.VERDE_OSCURO);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setColumnHeaderView(tabla.getTableHeader());
        scroll.setBounds(30, 90, 900, 550);
        add(scroll);

        lblTotalGeneral = new JLabel("Total general: S/ 0.00");
        lblTotalGeneral.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalGeneral.setForeground(UIUtils.VERDE_OSCURO);
        lblTotalGeneral.setBounds(30, 650, 400, 24);
        add(lblTotalGeneral);
    }

    private void exportarReporte() {
        ArrayList<Venta> ventas = controlador.listarTodas();
        if (ventas.isEmpty()) {
            UIUtils.mensajeError(this, "A\u00fan no hay ventas registradas para exportar.");
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File("reporte_ventas.txt"));
        int opcion = selector.showSaveDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            String ruta = selector.getSelectedFile().getAbsolutePath();
            controlador.exportarReporte(ventas, ruta);
            UIUtils.mensajeExito(this, "Reporte exportado en:\n" + ruta);
        }
    }

    public void refrescar() {
        modelo.setRowCount(0);
        ArrayList<Venta> lista = controlador.listarTodas();
        double totalGeneral = 0;
        for (Venta v : lista) {
            modelo.addRow(new Object[]{
                v.getIDventa(), v.getFechaventa(), v.getCodigoUsuario(),
                "S/ " + String.format(java.util.Locale.ROOT, "%.2f", v.getTotal())
            });
            totalGeneral += v.getTotal();
        }
        lblTotalGeneral.setText("Total general: S/ " + String.format(java.util.Locale.ROOT, "%.2f", totalGeneral));
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        try {
            switch (evento.getActionCommand()) {
                case "exportarReporte": exportarReporte(); break;
                case "refrescar": refrescar(); break;
                default: break;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            UIUtils.mensajeError(this, e.getMessage());
        }
    }
}
