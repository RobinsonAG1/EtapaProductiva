package modelo;

public class Rol {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?)";
    public static final String UPDATE = "UPDATE rol SET nombre=?, descripcion=? WHERE id_rol=?";
    public static final String DELETE = "DELETE FROM rol WHERE id_rol=?";
    public static final String SELECT_BY_ID = "SELECT * FROM rol WHERE id_rol=?";
    public static final String SELECT_ALL = "SELECT * FROM rol ORDER BY nombre";

    private int idRol;
    private String nombre;
    private String descripcion;

    public Rol() {
    }

    public Rol(int idRol, String nombre, String descripcion) {
        this.idRol = idRol;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}