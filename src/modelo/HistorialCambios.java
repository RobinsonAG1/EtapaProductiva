package modelo;

import java.sql.Timestamp;

public class HistorialCambios {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO historial_cambios (id_usuario, modulo, accion, descripcion, fecha) VALUES (?, ?, ?, ?, ?)";
    public static final String SELECT_BY_ID = "SELECT * FROM historial_cambios WHERE id_historial=?";
    public static final String SELECT_BY_USUARIO = "SELECT * FROM historial_cambios WHERE id_usuario=? ORDER BY fecha DESC";
    public static final String SELECT_BY_MODULO = "SELECT * FROM historial_cambios WHERE modulo=? ORDER BY fecha DESC";
    public static final String SELECT_ALL = "SELECT * FROM historial_cambios ORDER BY fecha DESC";

    private int idHistorial;
    private int idUsuario;
    private String modulo;
    private String accion;
    private String descripcion;
    private Timestamp fecha;

    public HistorialCambios() {
    }

    public HistorialCambios(int idHistorial, int idUsuario, String modulo, String accion,
                            String descripcion, Timestamp fecha) {
        this.idHistorial = idHistorial;
        this.idUsuario = idUsuario;
        this.modulo = modulo;
        this.accion = accion;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}