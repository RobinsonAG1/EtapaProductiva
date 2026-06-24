package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class HashAdmin {
    public static void main(String[] args) throws Exception {
        String correo = "admin@soy.sena.edu.co";
        String password = "12345";  // contraseña que quieres usar
        String hash = PasswordUtils.hash(password);

        Connection conn = Conexion.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE usuario SET password_hash = ? WHERE correo = ?");
        ps.setString(1, hash);
        ps.setString(2, correo);
        int rows = ps.executeUpdate();
        ps.close();
        conn.close();

        if (rows > 0) {
            System.out.println("✅ Hash scrypt actualizado para " + correo);
            System.out.println("   Contraseña: " + password);
            System.out.println("   Hash: " + hash);
        } else {
            System.out.println("❌ Usuario no encontrado: " + correo);
        }
    }
}
