package modelo;

public class CursoInstructor {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO curso_instructor (id_curso, id_instructor) VALUES (?, ?)";
    public static final String DELETE = "DELETE FROM curso_instructor WHERE id_curso=? AND id_instructor=?";
    public static final String SELECT_BY_CURSO = "SELECT * FROM curso_instructor WHERE id_curso=?";
    public static final String SELECT_BY_INSTRUCTOR = "SELECT * FROM curso_instructor WHERE id_instructor=?";
    public static final String SELECT_ALL = "SELECT * FROM curso_instructor ORDER BY id_curso, id_instructor";

    private int idCurso;
    private int idInstructor;

    public CursoInstructor() {
    }

    public CursoInstructor(int idCurso, int idInstructor) {
        this.idCurso = idCurso;
        this.idInstructor = idInstructor;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public int getIdInstructor() {
        return idInstructor;
    }

    public void setIdInstructor(int idInstructor) {
        this.idInstructor = idInstructor;
    }
}