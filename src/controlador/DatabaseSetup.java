package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DatabaseSetup {

    private static final String ADMIN_EMAIL = "admin@soy.sena.edu.co";
    private static final String ADMIN_PASSWORD = "12345"; // Se hashea con PasswordUtils.hash() que usa scrypt

    public static void inicializar() {
        try (Connection conn = Conexion.getInstance().getConnection()) {
            System.out.println("[DB] Conexi\u00f3n exitosa a PostgreSQL - master_db");
            if (!adminExiste(conn)) {
                crearAdmin(conn);
            } else {
                System.out.println("[DB] Usuario admin ya existe en la base de datos");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error al conectar: " + e.getMessage());
        }
    }

    private static boolean adminExiste(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ADMIN_EMAIL);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private static void crearAdmin(Connection conn) throws SQLException {
        String hash = PasswordUtils.hash(ADMIN_PASSWORD);
        int idUsuario;

        // Insertar usuario
        String insertUsuario = "INSERT INTO usuario (nombres, apellidos, correo, password_hash, estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?) RETURNING id_usuario";
        try (PreparedStatement ps = conn.prepareStatement(insertUsuario)) {
            ps.setString(1, "Administrador");
            ps.setString(2, "Sistema");
            ps.setString(3, ADMIN_EMAIL);
            ps.setString(4, hash);
            ps.setBoolean(5, true);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                idUsuario = rs.getInt(1);
            }
        }

        // Asegurar que existe el rol superusuario
        int idRol = asegurarRol(conn, "superusuario", "Administrador general del sistema");

        // Asignar rol
        String insertUsuarioRol = "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertUsuarioRol)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idRol);
            ps.executeUpdate();
        }

        System.out.println("Usuario admin creado exitosamente.");
    }

    private static int asegurarRol(Connection conn, String nombre, String descripcion) throws SQLException {
        String select = "SELECT id_rol FROM rol WHERE nombre = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        // Crear rol si no existe
        String insert = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?) RETURNING id_rol";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}