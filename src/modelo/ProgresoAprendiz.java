package modelo;

import java.sql.Timestamp;
import java.math.BigDecimal;

public class ProgresoAprendiz {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO progreso_aprendiz (id_aprendiz, id_curso, porcentaje, ultima_actualizacion) VALUES (?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE progreso_aprendiz SET porcentaje=?, ultima_actualizacion=? WHERE id_progreso=?";
    public static final String DELETE = "DELETE FROM progreso_aprendiz WHERE id_progreso=?";
    public static final String SELECT_BY_ID = "SELECT * FROM progreso_aprendiz WHERE id_progreso=?";
    public static final String SELECT_BY_APRENDIZ = "SELECT * FROM progreso_aprendiz WHERE id_aprendiz=?";
    public static final String SELECT_BY_APRENDIZ_CURSO = "SELECT * FROM progreso_aprendiz WHERE id_aprendiz=? AND id_curso=?";
    public static final String SELECT_ALL = "SELECT * FROM progreso_aprendiz ORDER BY ultima_actualizacion DESC";

    private int idProgreso;
    private int idAprendiz;
    private int idCurso;
    private BigDecimal porcentaje;
    private Timestamp ultimaActualizacion;

    public ProgresoAprendiz() {
    }

    public ProgresoAprendiz(int idProgreso, int idAprendiz, int idCurso,
                            BigDecimal porcentaje, Timestamp ultimaActualizacion) {
        this.idProgreso = idProgreso;
        this.idAprendiz = idAprendiz;
        this.idCurso = idCurso;
        this.porcentaje = porcentaje;
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public int getIdProgreso() {
        return idProgreso;
    }

    public void setIdProgreso(int idProgreso) {
        this.idProgreso = idProgreso;
    }

    public int getIdAprendiz() {
        return idAprendiz;
    }

    public void setIdAprendiz(int idAprendiz) {
        this.idAprendiz = idAprendiz;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Timestamp getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(Timestamp ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
}