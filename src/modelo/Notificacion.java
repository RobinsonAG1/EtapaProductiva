package modelo;

import java.sql.Timestamp;

public class Notificacion {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO notificacion (id_usuario, mensaje, leida, fecha) VALUES (?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE notificacion SET leida=? WHERE id_notificacion=?";
    public static final String DELETE = "DELETE FROM notificacion WHERE id_notificacion=?";
    public static final String SELECT_BY_ID = "SELECT * FROM notificacion WHERE id_notificacion=?";
    public static final String SELECT_BY_USUARIO = "SELECT * FROM notificacion WHERE id_usuario=? ORDER BY fecha DESC";
    public static final String SELECT_NO_LEIDAS = "SELECT * FROM notificacion WHERE id_usuario=? AND leida=false ORDER BY fecha DESC";
    public static final String SELECT_ALL = "SELECT * FROM notificacion ORDER BY fecha DESC";

    private int idNotificacion;
    private int idUsuario;
    private String mensaje;
    private Boolean leida;
    private Timestamp fecha;

    public Notificacion() {
    }

    public Notificacion(int idNotificacion, int idUsuario, String mensaje, Boolean leida, Timestamp fecha) {
        this.idNotificacion = idNotificacion;
        this.idUsuario = idUsuario;
        this.mensaje = mensaje;
        this.leida = leida;
        this.fecha = fecha;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}