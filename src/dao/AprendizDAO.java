package dao;

import modelo.Aprendiz;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AprendizDAO extends BaseDAO<Aprendiz> {

    @Override
    protected Aprendiz mapear(ResultSet rs) throws SQLException {
        Aprendiz a = new Aprendiz();
        a.setIdAprendiz(rs.getInt("id_aprendiz"));
        a.setIdUsuario(rs.getInt("id_usuario"));
        a.setIdEmpresa(rs.getInt("id_empresa"));
        if (rs.wasNull()) a.setIdEmpresa(0);
        a.setFicha(rs.getString("ficha"));
        a.setEstadoPractica(rs.getString("estado_practica"));
        a.setHorasRequeridas(rs.getInt("horas_requeridas"));
        a.setHorasCumplidas(rs.getInt("horas_cumplidas"));
        return a;
    }

    public Aprendiz buscarPorId(int id) {
        return buscarUno(Aprendiz.SELECT_BY_ID, id);
    }

    public Aprendiz buscarPorUsuario(int idUsuario) {
        return buscarUno(Aprendiz.SELECT_BY_USUARIO, idUsuario);
    }

    public List<Aprendiz> listarTodos() {
        return buscarLista(Aprendiz.SELECT_ALL);
    }

    public List<Aprendiz> buscarPorFicha(String ficha) {
        return buscarLista(Aprendiz.SELECT_BY_FICHA, ficha);
    }

    public int contarTotal() {
        return contar("SELECT COUNT(*) FROM aprendiz");
    }

    public int insertar(Aprendiz a) {
        return ejecutar(Aprendiz.INSERT, a.getIdUsuario(), a.getIdEmpresa(),
                a.getFicha(), a.getEstadoPractica(), a.getHorasRequeridas(), a.getHorasCumplidas());
    }

    public int actualizar(Aprendiz a) {
        return ejecutar(Aprendiz.UPDATE, a.getIdEmpresa(), a.getFicha(),
                a.getEstadoPractica(), a.getHorasRequeridas(), a.getHorasCumplidas(),
                a.getIdAprendiz());
    }

    public int eliminar(int id) {
        return ejecutar(Aprendiz.DELETE, id);
    }
}