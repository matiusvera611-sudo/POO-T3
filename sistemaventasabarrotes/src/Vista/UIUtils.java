package Vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/** Configuración sencilla de componentes Swing reutilizados. */
public final class UIUtils {
    public static final Color VERDE = new Color(38, 130, 85);
    public static final Color VERDE_OSCURO = new Color(27, 91, 64);
    public static final Color AZUL = new Color(40, 98, 155);
    public static final Color ROJO = new Color(177, 55, 55);
    public static final Color MORADO = new Color(103, 77, 147);
    public static final Color AMBAR = new Color(180, 125, 25);
    public static final Color CONTENIDO_BG = new Color(242, 245, 243);
    public static final Color TEXTO_OSCURO = new Color(38, 50, 45);
    public static final Color GRIS_TEXTO = new Color(92, 100, 96);
    public static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FUENTE_NEGRITA = new Font("SansSerif", Font.BOLD, 12);

    private UIUtils() {
    }

    public static JPanel tarjetaBlanca() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(215, 223, 218)));
        return panel;
    }

    public static JButton botonColor(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_NEGRITA);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        return boton;
    }

    public static JLabel etiquetaCampo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE_NEGRITA);
        etiqueta.setForeground(TEXTO_OSCURO);
        return etiqueta;
    }

    public static void estilizarCampo(JComponent campo) {
        campo.setFont(FUENTE_NORMAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(195, 205, 198)),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)));
    }

    public static void estilizarTabla(JTable tabla, Color color) {
        tabla.setFont(FUENTE_NORMAL);
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(new Color(210, 231, 217));
        tabla.setSelectionForeground(TEXTO_OSCURO);
        tabla.getTableHeader().setFont(FUENTE_NEGRITA);
        tabla.getTableHeader().setBackground(color);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setFillsViewportHeight(true);
    }

    public static void seleccionarFila(JTable tabla, String codigo) {
        for (int fila = 0; fila < tabla.getRowCount(); fila++) {
            if (String.valueOf(tabla.getValueAt(fila, 0)).equals(codigo)) {
                tabla.setRowSelectionInterval(fila, fila);
                return;
            }
        }
    }

    public static void mensajeExito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Sistema de abarrotes", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mensajeError(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
