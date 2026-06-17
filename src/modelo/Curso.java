package modelo;

import java.sql.Date;

public class Curso {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO curso (nombre, ficha, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE curso SET nombre=?, ficha=?, fecha_inicio=?, fecha_fin=? WHERE id_curso=?";
    public static final String DELETE = "DELETE FROM curso WHERE id_curso=?";
    public static final String SELECT_BY_ID = "SELECT * FROM curso WHERE id_curso=?";
    public static final String SELECT_ALL = "SELECT * FROM curso ORDER BY nombre";
    public static final String SELECT_BY_FICHA = "SELECT * FROM curso WHERE ficha=?";

    private int idCurso;
    private String nombre;
    private String ficha;
    private Date fechaInicio;
    private Date fechaFin;

    public Curso() {
    }

    public Curso(int idCurso, String nombre, String ficha, Date fechaInicio, Date fechaFin) {
        this.idCurso = idCurso;
        this.nombre = nombre;
        this.ficha = ficha;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFicha() {
        return ficha;
    }

    public void setFicha(String ficha) {
        this.ficha = ficha;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public String toString() {
        return ficha + " - " + nombre;
    }
}