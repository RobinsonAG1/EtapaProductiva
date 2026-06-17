package modelo;

public class Instructor {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO instructor (id_usuario, area_formacion, activo) VALUES (?, ?, ?)";
    public static final String UPDATE = "UPDATE instructor SET area_formacion=?, activo=? WHERE id_instructor=?";
    public static final String DELETE = "DELETE FROM instructor WHERE id_instructor=?";
    public static final String SELECT_BY_ID = "SELECT * FROM instructor WHERE id_instructor=?";
    public static final String SELECT_BY_USUARIO = "SELECT * FROM instructor WHERE id_usuario=?";
    public static final String SELECT_ALL = "SELECT * FROM instructor ORDER BY area_formacion";
    public static final String SELECT_ACTIVOS = "SELECT * FROM instructor WHERE activo=true ORDER BY area_formacion";

    private int idInstructor;
    private int idUsuario;
    private String areaFormacion;
    private Boolean activo;

    public Instructor() {
    }

    public Instructor(int idInstructor, int idUsuario, String areaFormacion, Boolean activo) {
        this.idInstructor = idInstructor;
        this.idUsuario = idUsuario;
        this.areaFormacion = areaFormacion;
        this.activo = activo;
    }

    public int getIdInstructor() {
        return idInstructor;
    }

    public void setIdInstructor(int idInstructor) {
        this.idInstructor = idInstructor;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getAreaFormacion() {
        return areaFormacion;
    }

    public void setAreaFormacion(String areaFormacion) {
        this.areaFormacion = areaFormacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}