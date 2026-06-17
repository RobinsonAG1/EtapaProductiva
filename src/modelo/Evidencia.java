package modelo;

import java.sql.Timestamp;

public class Evidencia {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO evidencia (id_aprendiz, tipo, contenido, fecha_entrega, estado, observaciones, bitacora, actas) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE evidencia SET tipo=?, contenido=?, fecha_entrega=?, estado=?, observaciones=?, bitacora=?, actas=? WHERE id_evidencia=?";
    public static final String DELETE = "DELETE FROM evidencia WHERE id_evidencia=?";
    public static final String SELECT_BY_ID = "SELECT * FROM evidencia WHERE id_evidencia=?";
    public static final String SELECT_BY_APRENDIZ = "SELECT * FROM evidencia WHERE id_aprendiz=? ORDER BY fecha_entrega DESC";
    public static final String SELECT_ALL = "SELECT * FROM evidencia ORDER BY fecha_entrega DESC";

    private int idEvidencia;
    private int idAprendiz;
    private String tipo;
    private String contenido;
    private Timestamp fechaEntrega;
    private String estado;
    private String observaciones;
    private String bitacora;
    private String actas;

    public Evidencia() {
    }

    public Evidencia(int idEvidencia, int idAprendiz, String tipo, String contenido,
                     Timestamp fechaEntrega, String estado, String observaciones,
                     String bitacora, String actas) {
        this.idEvidencia = idEvidencia;
        this.idAprendiz = idAprendiz;
        this.tipo = tipo;
        this.contenido = contenido;
        this.fechaEntrega = fechaEntrega;
        this.estado = estado;
        this.observaciones = observaciones;
        this.bitacora = bitacora;
        this.actas = actas;
    }

    public int getIdEvidencia() {
        return idEvidencia;
    }

    public void setIdEvidencia(int idEvidencia) {
        this.idEvidencia = idEvidencia;
    }

    public int getIdAprendiz() {
        return idAprendiz;
    }

    public void setIdAprendiz(int idAprendiz) {
        this.idAprendiz = idAprendiz;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Timestamp getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Timestamp fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getBitacora() {
        return bitacora;
    }

    public void setBitacora(String bitacora) {
        this.bitacora = bitacora;
    }

    public String getActas() {
        return actas;
    }

    public void setActas(String actas) {
        this.actas = actas;
    }
}