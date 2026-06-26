package controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:postgresql://144.91.74.225:5433/master_db";
    private static final String USER = "admin";
    private static final String PASSWORD = "1052020405Nr.";
    private static Conexion instance;

    private Conexion() {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("\u2705 Driver PostgreSQL cargado exitosamente");
        } catch (ClassNotFoundException e) {
            System.err.println("\u274c Error: Driver PostgreSQL no encontrado");
            throw new RuntimeException("Driver PostgreSQL no encontrado", e);
        }
    }

    public static synchronized Conexion getInstance() {
        if (instance == null) {
            instance = new Conexion();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("\u2705 Conexi\u00f3n a BD establecida correctamente");
        System.out.println("   \ud83d\udccd Host: 144.91.74.225:5433");
        System.out.println("   \ud83d\uddc4\ufe0f  Base de datos: master_db");
        System.out.println("   \ud83d\udc64 Usuario: " + USER);
        return conn;
    }

    public void close() {
        // No-op: connections are now managed per-caller
    }

    // Método para probar la conexión
    public static void main(String[] args) {
        System.out.println("=== TEST DE CONEXIÓN A BASE DE DATOS ===\n");
        try {
            Conexion conexion = Conexion.getInstance();
            Connection conn = conexion.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("\n✅ ¡CONEXIÓN EXITOSA!");
                System.out.println("La aplicación está conectada a la base de datos correctamente.");
                conexion.close();
            } else {
                System.out.println("\n❌ La conexión es nula o está cerrada");
            }
        } catch (SQLException e) {
            System.out.println("\n❌ ¡CONEXIÓN FALLIDA!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}