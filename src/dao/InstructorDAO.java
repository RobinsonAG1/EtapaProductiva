package dao;

import modelo.Instructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InstructorDAO extends BaseDAO<Instructor> {

    @Override
    protected Instructor mapear(ResultSet rs) throws SQLException {
        Instructor i = new Instructor();
        i.setIdInstructor(rs.getInt("id_instructor"));
        i.setIdUsuario(rs.getInt("id_usuario"));
        i.setAreaFormacion(rs.getString("area_formacion"));
        i.setActivo(rs.getBoolean("activo"));
        return i;
    }

    public Instructor buscarPorId(int id) {
        return buscarUno(Instructor.SELECT_BY_ID, id);
    }

    public Instructor buscarPorUsuario(int idUsuario) {
        return buscarUno(Instructor.SELECT_BY_USUARIO, idUsuario);
    }

    public List<Instructor> listarTodos() {
        return buscarLista(Instructor.SELECT_ALL);
    }

    public List<Instructor> listarActivos() {
        return buscarLista(Instructor.SELECT_ACTIVOS);
    }

    public int contarTotal() {
        return contar("SELECT COUNT(*) FROM instructor");
    }

    public int insertar(Instructor i) {
        return ejecutar(Instructor.INSERT, i.getIdUsuario(), i.getAreaFormacion(), i.getActivo());
    }

    public int actualizar(Instructor i) {
        return ejecutar(Instructor.UPDATE, i.getAreaFormacion(), i.getActivo(), i.getIdInstructor());
    }

    public int eliminar(int id) {
        return ejecutar(Instructor.DELETE, id);
    }
}