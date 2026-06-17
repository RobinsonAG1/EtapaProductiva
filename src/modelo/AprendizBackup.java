package modelo;

import java.sql.Timestamp;

public class AprendizBackup {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO aprendiz_backup (id_aprendiz, datos, fecha_respaldo) VALUES (?, ?, ?)";
    public static final String UPDATE = "UPDATE aprendiz_backup SET datos=?, fecha_respaldo=? WHERE id_backup=?";
    public static final String DELETE = "DELETE FROM aprendiz_backup WHERE id_backup=?";
    public static final String SELECT_BY_ID = "SELECT * FROM aprendiz_backup WHERE id_backup=?";
    public static final String SELECT_BY_APRENDIZ = "SELECT * FROM aprendiz_backup WHERE id_aprendiz=?";
    public static final String SELECT_ALL = "SELECT * FROM aprendiz_backup ORDER BY fecha_respaldo DESC";

    private int idBackup;
    private int idAprendiz;
    private String datos;
    private Timestamp fechaRespaldo;

    public AprendizBackup() {
    }

    public AprendizBackup(int idBackup, int idAprendiz, String datos, Timestamp fechaRespaldo) {
        this.idBackup = idBackup;
        this.idAprendiz = idAprendiz;
        this.datos = datos;
        this.fechaRespaldo = fechaRespaldo;
    }

    public int getIdBackup() {
        return idBackup;
    }

    public void setIdBackup(int idBackup) {
        this.idBackup = idBackup;
    }

    public int getIdAprendiz() {
        return idAprendiz;
    }

    public void setIdAprendiz(int idAprendiz) {
        this.idAprendiz = idAprendiz;
    }

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public Timestamp getFechaRespaldo() {
        return fechaRespaldo;
    }

    public void setFechaRespaldo(Timestamp fechaRespaldo) {
        this.fechaRespaldo = fechaRespaldo;
    }
}