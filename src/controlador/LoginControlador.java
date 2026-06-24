package controlador;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import vista.Login;
import vista.MDI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        new Thread(() -> {
            String resultado = validarCredenciales(user, pass);
            javax.swing.SwingUtilities.invokeLater(() -> {
                if (resultado != null) {
                    JOptionPane.showMessageDialog(loginFrame,
                            "Error de autenticaci\u00f3n: " + resultado,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    password.setText("");
                    password.requestFocus();
                } else {
                    loginFrame.dispose();
                    java.awt.EventQueue.invokeLater(() -> {
                        MDI mdi = new MDI();
                        mdi.setVisible(true);
                        mdi.abrirDashboardSegunRol();
                    });
                }
            });
        }).start();
    }

    private static String validarCredenciales(String email, String password) {
        System.out.println("[LOGIN] Buscando usuario: " + email);
        String sql = "SELECT id_usuario, nombres, apellidos, password_hash, estado FROM usuario WHERE correo = ?";
        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("[LOGIN] Usuario no encontrado: " + email);
                    return "Credenciales incorrectas. Verifica tu correo y contrase\u00f1a.";
                }

                int id = rs.getInt("id_usuario");
                String nom = rs.getString("nombres");
                String ape = rs.getString("apellidos");
                boolean activo = rs.getBoolean("estado");
                String storedHash = rs.getString("password_hash");

                if (!activo) {
                    return "Tu cuenta est\u00e1 desactivada. Contacta al administrador.";
                }

                boolean verified = modelo.ContrasenaUtil.verificarUniversal(password, storedHash);
                if (!verified) {
                    return "Credenciales incorrectas. Verifica tu correo y contrase\u00f1a.";
                }

                // Cargar roles del usuario
                String sqlRoles = "SELECT r.nombre FROM usuario_rol ur JOIN rol r ON ur.id_rol = r.id_rol WHERE ur.id_usuario = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(sqlRoles)) {
                    ps2.setInt(1, id);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        java.util.List<String> roles = new java.util.ArrayList<>();
                        while (rs2.next()) roles.add(rs2.getString("nombre"));
                        Sesion.iniciar(id, nom, ape, email, roles);
                        System.out.println("[LOGIN] Roles: " + roles);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("[LOGIN] Error SQL: " + e.getMessage());
            return "No se pudo conectar a la base de datos. Verifica que el servidor est\u00e9 activo.";
        }

        return null;
    }
}