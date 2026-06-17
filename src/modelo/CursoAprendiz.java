package modelo;

public class CursoAprendiz {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO curso_aprendiz (id_curso, id_aprendiz, estado) VALUES (?, ?, ?)";
    public static final String UPDATE = "UPDATE curso_aprendiz SET estado=? WHERE id_curso=? AND id_aprendiz=?";
    public static final String DELETE = "DELETE FROM curso_aprendiz WHERE id_curso=? AND id_aprendiz=?";
    public static final String SELECT_BY_CURSO = "SELECT * FROM curso_aprendiz WHERE id_curso=?";
    public static final String SELECT_BY_APRENDIZ = "SELECT * FROM curso_aprendiz WHERE id_aprendiz=?";
    public static final String SELECT_ALL = "SELECT * FROM curso_aprendiz ORDER BY id_curso, id_aprendiz";

    private int idCurso;
    private int idAprendiz;
    private String estado;

    public CursoAprendiz() {
    }

    public CursoAprendiz(int idCurso, int idAprendiz, String estado) {
        this.idCurso = idCurso;
        this.idAprendiz = idAprendiz;
        this.estado = estado;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public int getIdAprendiz() {
        return idAprendiz;
    }

    public void setIdAprendiz(int idAprendiz) {
        this.idAprendiz = idAprendiz;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}