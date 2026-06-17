package modelo;

import java.sql.Timestamp;

public class Usuario {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO usuario (tipo_documento, numero_documento, nombres, apellidos, correo, telefono, password_hash, estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE usuario SET tipo_documento=?, numero_documento=?, nombres=?, apellidos=?, correo=?, telefono=?, estado=? WHERE id_usuario=?";
    public static final String UPDATE_PASSWORD = "UPDATE usuario SET password_hash=? WHERE id_usuario=?";
    public static final String DELETE = "DELETE FROM usuario WHERE id_usuario=?";
    public static final String SELECT_BY_ID = "SELECT * FROM usuario WHERE id_usuario=?";
    public static final String SELECT_BY_CORREO = "SELECT * FROM usuario WHERE correo=?";
    public static final String SELECT_ALL = "SELECT * FROM usuario ORDER BY nombres, apellidos";
    public static final String SELECT_ACTIVOS = "SELECT * FROM usuario WHERE estado=true ORDER BY nombres, apellidos";

    private int idUsuario;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String passwordHash;
    private Boolean estado;
    private Timestamp fechaCreacion;

    public Usuario() {
    }

    public Usuario(int idUsuario, String tipoDocumento, String numeroDocumento, String nombres,
                   String apellidos, String correo, String telefono, String passwordHash,
                   Boolean estado, Timestamp fechaCreacion) {
        this.idUsuario = idUsuario;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.passwordHash = passwordHash;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}