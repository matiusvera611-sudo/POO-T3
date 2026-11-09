package sistemaventasabarrotes;

import Controladores.ControladorSistema;
import Vista.FrmLogin;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Sistemaventasabarrotes {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ControladorSistema sistema = new ControladorSistema();
                    new FrmLogin(sistema).setVisible(true);
                } catch (IllegalStateException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "No se pudo iniciar el sistema", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
