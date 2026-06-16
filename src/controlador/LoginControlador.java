package controlador;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import vista.Login;
import vista.MDI;

public class LoginControlador {

    public static void login(JTextField correo, JPasswordField password, JFrame loginFrame) {
        String user = correo.getText().trim();
        String pass = new String(password.getPassword()).trim();

        if (user.isEmpty() || "usuario@soy.sena.edu.co".equals(user)) {
            JOptionPane.showMessageDialog(loginFrame,
                    "Ingresa tu correo electr\u00f3nico.",
                    "Campos vac\u00edos", JOptionPane.WARNING_MESSAGE);
            correo.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame,
                    "Ingresa tu contrase\u00f1a.",
                    "Campos vac\u00edos", JOptionPane.WARNING_MESSAGE);
            password.requestFocus();
            return;
        }

        if ("admin@soy.sena.edu.co".equals(user) && "12345".equals(pass)) {
            loginFrame.dispose();
            java.awt.EventQueue.invokeLater(() -> {
                MDI mdi = new MDI();
                mdi.setVisible(true);
                mdi.abrirAdminDashboard();
            });
        } else {
            JOptionPane.showMessageDialog(loginFrame,
                    "Credenciales incorrectas. Verifica tu correo y contrase\u00f1a.",
                    "Error de autenticaci\u00f3n", JOptionPane.ERROR_MESSAGE);
            password.setText("");
            password.requestFocus();
        }
    }
}
