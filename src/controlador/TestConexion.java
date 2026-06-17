package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestConexion {
    public static void main(String[] args) {
        try {
            Connection conn = Conexion.getInstance().getConnection();
            
            // Verificar que funciona
            String sql = "SELECT password_hash FROM usuario WHERE correo = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "admin@sena.edu.co");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String hash = rs.getString("password_hash");
                        boolean ok = PasswordUtils.verify("Admin123", hash);
                        System.out.println("[TEST] Verify 'Admin123': " + (ok ? "EXITOSO ✅" : "FALLÓ ❌"));
                    }
                }
            }
            
            conn.close();
        } catch (Exception e) {
            System.err.println("[TEST] ERROR: " + e.getMessage());
        }
    }
}