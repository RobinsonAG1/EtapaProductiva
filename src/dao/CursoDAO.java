package dao;

import modelo.Curso;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CursoDAO extends BaseDAO<Curso> {

    @Override
    protected Curso mapear(ResultSet rs) throws SQLException {
        Curso c = new Curso();
        c.setIdCurso(rs.getInt("id_curso"));
        c.setNombre(rs.getString("nombre"));
        c.setFicha(rs.getString("ficha"));
        java.sql.Date fi = rs.getDate("fecha_inicio");
        if (fi != null) c.setFechaInicio(fi);
        java.sql.Date ff = rs.getDate("fecha_fin");
        if (ff != null) c.setFechaFin(ff);
        return c;
    }

    public Curso buscarPorId(int id) {
        return buscarUno(Curso.SELECT_BY_ID, id);
    }

    public List<Curso> listarTodos() {
        return buscarLista(Curso.SELECT_ALL);
    }

    public Curso buscarPorFicha(String ficha) {
        return buscarUno(Curso.SELECT_BY_FICHA, ficha);
    }

    public int contarTotal() {
        return contar("SELECT COUNT(*) FROM curso");
    }

    public int insertar(Curso c) {
        return ejecutar(Curso.INSERT, c.getNombre(), c.getFicha(),
                c.getFechaInicio(), c.getFechaFin());
    }

    public int actualizar(Curso c) {
        return ejecutar(Curso.UPDATE, c.getNombre(), c.getFicha(),
                c.getFechaInicio(), c.getFechaFin(), c.getIdCurso());
    }

    public int eliminar(int id) {
        return ejecutar(Curso.DELETE, id);
    }
}