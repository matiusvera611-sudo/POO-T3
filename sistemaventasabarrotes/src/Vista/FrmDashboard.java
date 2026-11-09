package Vista;

import Controladores.ControladorSesion;
import Controladores.ControladorSistema;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import sistemaabarrotes.Compra;

/** Ventana principal con menús y formularios internos, como en la clase Swing. */
public class FrmDashboard extends JFrame implements ActionListener {
    private final ControladorSistema sistema;
    private final JDesktopPane escritorio = new JDesktopPane();
    private final JLabel lblSesion = new JLabel();

    public FrmDashboard(ControladorSistema sistema) {
        if (!ControladorSesion.haySesionActiva()) {
            throw new IllegalArgumentException("Primero inicie sesión.");
        }
        this.sistema = sistema;
        setTitle("Abarrotes Los Andes - Sistema de ventas");
        setSize(1060, 820);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        escritorio.setBackground(UIUtils.CONTENIDO_BG);
        add(escritorio, BorderLayout.CENTER);
        lblSesion.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 12, 7, 12));
        add(lblSesion, BorderLayout.SOUTH);
        construirMenu();
        actualizarSesion();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void construirMenu() {
        JMenuBar barra = new JMenuBar();
        JMenu datos = new JMenu("Datos básicos");
        agregarOpcion(datos, "Categorías");
        agregarOpcion(datos, "Distribuidores");
        agregarOpcion(datos, "Productos");
        agregarOpcion(datos, "Clientes");
        JMenuItem usuarios = agregarOpcion(datos, "Usuarios");
        usuarios.setEnabled(ControladorSesion.esTitularGerente());
        barra.add(datos);
        JMenu procesos = new JMenu("Procesos");
        agregarOpcion(procesos, "Compras");
        agregarOpcion(procesos, "Ventas");
        barra.add(procesos);
        JMenu reportes = new JMenu("Reportes");
        agregarOpcion(reportes, "Historial de ventas");
        agregarOpcion(reportes, "Resumen");
        barra.add(reportes);
        JMenu sesion = new JMenu("Sesión");
        agregarOpcion(sesion, "Cerrar sesión");
        agregarOpcion(sesion, "Salir");
        barra.add(sesion);
        setJMenuBar(barra);
    }

    private JMenuItem agregarOpcion(JMenu menu, String texto) {
        JMenuItem opcion = new JMenuItem(texto);
        opcion.addActionListener(this);
        menu.add(opcion);
        return opcion;
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        try {
            switch (evento.getActionCommand()) {
                case "Categorías": abrir("Categorías", new PanelCategorias(sistema.getCategorias())); break;
                case "Distribuidores": abrir("Distribuidores", new PanelDistribuidores(sistema.getDistribuidores())); break;
                case "Productos": abrir("Productos", new PanelProductos(sistema.getProductos(), sistema.getCategorias(), sistema.getDistribuidores())); break;
                case "Clientes": abrir("Clientes", new PanelClientes(sistema.getClientes())); break;
                case "Usuarios":
                    if (!ControladorSesion.esTitularGerente()) { throw new IllegalArgumentException("Solo el gerente administra usuarios."); }
                    abrir("Usuarios", new PanelUsuarios(sistema.getUsuarios()));
                    break;
                case "Compras": abrir("Compras", new PanelCompras(sistema.getCompras(), sistema.getProductos(), sistema.getDistribuidores())); break;
                case "Ventas": abrir("Ventas", new PanelVentas(sistema.getVentas())); break;
                case "Historial de ventas": abrir("Historial de ventas", new PanelHistorialVentas(sistema.getVentas())); break;
                case "Resumen": mostrarResumen(); break;
                case "Cerrar sesión": cerrarSesion(); break;
                case "Salir":
                    if (UIUtils.confirmar(this, "¿Cerrar el sistema?")) { dispose(); }
                    break;
                default: break;
            }
            actualizarSesion();
        } catch (IllegalArgumentException | IllegalStateException e) { UIUtils.mensajeError(this, e.getMessage()); }
    }

    private void abrir(String titulo, JPanel panel) {
        for (JInternalFrame abierta : escritorio.getAllFrames()) {
            if (titulo.equals(abierta.getTitle())) {
                activar(abierta);
                return;
            }
        }
        panel.setPreferredSize(new Dimension(960, 715));
        final JInternalFrame ventana = new JInternalFrame(titulo, true, true, true, true);
        ventana.setContentPane(new JScrollPane(panel));
        ventana.putClientProperty("formulario", panel);
        ventana.setSize(1000, 770);
        ventana.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent evento) {
                try { actualizarFormulario(ventana); actualizarSesion(); }
                catch (IllegalArgumentException | IllegalStateException e) { UIUtils.mensajeError(ventana, e.getMessage()); }
            }
        });
        escritorio.add(ventana);
        ventana.setVisible(true);
        activar(ventana);
    }

    private void activar(JInternalFrame ventana) {
        try {
            ventana.setIcon(false);
            ventana.setMaximum(true);
            ventana.setSelected(true);
            ventana.toFront();
            actualizarFormulario(ventana);
        } catch (PropertyVetoException e) { UIUtils.mensajeError(this, "No se pudo activar la ventana."); }
    }

    private void actualizarFormulario(JInternalFrame ventana) {
        Object vista = ventana.getClientProperty("formulario");
        // Casting comprobado y polimorfismo mediante la interfaz Formulario.
        if (vista instanceof Formulario) { ((Formulario) vista).refrescar(); }
    }

    private void actualizarSesion() {
        lblSesion.setText(ControladorSesion.getUsuarioActivo().obtenerDatos() + " | " + ControladorSesion.getRolActivo()
                + " | Productos: " + sistema.getProductos().listarTodos().size()
                + " | Ventas: " + sistema.getVentas().listarTodas().size());
    }

    private void mostrarResumen() {
        int pendientes = 0;
        for (Compra compra : sistema.getCompras().listarTodas()) {
            if ("Pendiente".equals(compra.getEstadoOrden())) { pendientes++; }
        }
        UIUtils.mensajeExito(this, "Productos: " + sistema.getProductos().listarTodos().size()
                + "\nProductos con stock bajo: " + sistema.getProductos().listarStockBajo().size()
                + "\nPróximos a vencer: " + sistema.getProductos().listarProximosVencer().size()
                + "\nClientes: " + sistema.getClientes().listarTodos().size()
                + "\nCompras pendientes: " + pendientes
                + "\nVentas: " + sistema.getVentas().listarTodas().size());
    }

    private void cerrarSesion() {
        if (!UIUtils.confirmar(this, "¿Cerrar sesión? Se cancelará la venta que esté en curso.")) { return; }
        sistema.getVentas().cancelarVenta();
        ControladorSesion.cerrarSesion();
        new FrmLogin(sistema).setVisible(true);
        dispose();
    }
}
