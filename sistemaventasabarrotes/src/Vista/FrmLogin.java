package Vista;

import Controladores.ControladorSesion;
import Controladores.ControladorSistema;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import sistemaabarrotes.Usuario;

public class FrmLogin extends JFrame implements ActionListener {
    private final ControladorSistema sistema;
    private final JTextField txtUsuario = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JButton btnIngresar = UIUtils.botonColor("Ingresar", UIUtils.VERDE_OSCURO);
    private final JButton btnSalir = new JButton("Salir");

    public FrmLogin(ControladorSistema sistema) {
        this.sistema = sistema;
        setTitle("Abarrotes Los Andes - Iniciar sesión");
        setSize(450, 335);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(UIUtils.CONTENIDO_BG);
        componentes();
    }

    private void componentes() {
        JLabel titulo = new JLabel("Abarrotes Los Andes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 23));
        titulo.setForeground(UIUtils.VERDE_OSCURO);
        titulo.setBounds(65, 22, 330, 32);
        add(titulo);
        JLabel usuario = UIUtils.etiquetaCampo("Usuario");
        usuario.setBounds(45, 78, 110, 25);
        add(usuario);
        txtUsuario.setBounds(150, 78, 240, 30);
        UIUtils.estilizarCampo(txtUsuario);
        add(txtUsuario);
        JLabel clave = UIUtils.etiquetaCampo("Contraseña");
        clave.setBounds(45, 125, 110, 25);
        add(clave);
        txtPassword.setBounds(150, 125, 240, 30);
        UIUtils.estilizarCampo(txtPassword);
        add(txtPassword);
        btnIngresar.setBounds(150, 182, 115, 34);
        btnIngresar.addActionListener(this);
        add(btnIngresar);
        btnSalir.setBounds(275, 182, 115, 34);
        btnSalir.addActionListener(this);
        add(btnSalir);
        txtPassword.addActionListener(this);
        getRootPane().setDefaultButton(btnIngresar);
        JLabel ayuda = new JLabel("Acceso inicial: admin / admin123");
        ayuda.setForeground(UIUtils.GRIS_TEXTO);
        ayuda.setBounds(112, 244, 300, 22);
        add(ayuda);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        if (evento.getSource() == btnSalir) { dispose(); return; }
        try {
            String clave = new String(txtPassword.getPassword());
            Usuario usuario = sistema.getUsuarios().login(txtUsuario.getText(), clave);
            if (usuario == null) {
                UIUtils.mensajeError(this, "Usuario o contraseña incorrectos.");
                txtPassword.setText("");
                return;
            }
            ControladorSesion.iniciarSesion(usuario);
            new FrmDashboard(sistema).setVisible(true);
            dispose();
        } catch (IllegalArgumentException | IllegalStateException e) {
            UIUtils.mensajeError(this, e.getMessage());
        }
    }
}
