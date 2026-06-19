package controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:postgresql://144.91.74.225:5433/master_db";
    private static final String USER = "admin";
    private static final String PASSWORD = "1052020405Nr.";
    private static Conexion instance;
    private Connection connection;

    private Conexion() {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ Driver PostgreSQL cargado exitosamente");
           
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: Driver PostgreSQL no encontrado");
            throw new RuntimeException("Driver PostgreSQL no encontrado", e);
        }
    }

    public static Conexion getInstance() {
        if (instance == null) {
            instance = new Conexion();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Conexión a BD establecida correctamente");
                System.out.println("   📍 Host: 144.91.74.225:5433");
                System.out.println("   🗄️  Base de datos: master_db");
                System.out.println("   👤 Usuario: " + USER);
            } catch (SQLException e) {
                System.err.println("❌ Error al conectar a la base de datos:");
                System.err.println("   Host: " + URL);
                System.err.println("   Usuario: " + USER);
                System.err.println("   Detalle: " + e.getMessage());
                throw e;
            }
        } else {
            System.out.println("ℹ️  Usando conexión existente");
        }
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Conexión cerrada correctamente");
            } catch (SQLException e) {
                System.err.println("❌ Error al cerrar la conexión: " + e.getMessage());
            }
        }
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