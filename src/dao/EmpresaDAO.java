package dao;

import modelo.Empresa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmpresaDAO extends BaseDAO<Empresa> {

    @Override
    protected Empresa mapear(ResultSet rs) throws SQLException {
        Empresa e = new Empresa();
        e.setIdEmpresa(rs.getInt("id_empresa"));
        e.setNombre(rs.getString("nombre"));
        e.setNit(rs.getString("nit"));
        e.setDireccion(rs.getString("direccion"));
        e.setTelefono(rs.getString("telefono"));
        e.setContacto(rs.getString("contacto"));
        e.setPersonaContacto(rs.getString("persona_contacto"));
        e.setActiva(rs.getBoolean("activa"));
        return e;
    }

    public Empresa buscarPorId(int id) {
        return buscarUno(Empresa.SELECT_BY_ID, id);
    }

    public List<Empresa> listarTodas() {
        return buscarLista(Empresa.SELECT_ALL);
    }

    public List<Empresa> listarActivas() {
        return buscarLista(Empresa.SELECT_ACTIVAS);
    }

    public int contarTotal() {
        return contar("SELECT COUNT(*) FROM empresa");
    }

    public int contarActivas() {
        return contar("SELECT COUNT(*) FROM empresa WHERE activa = true");
    }

    public int insertar(Empresa e) {
        return ejecutar(Empresa.INSERT, e.getNombre(), e.getNit(), e.getDireccion(),
                e.getTelefono(), e.getContacto(), e.getPersonaContacto(), e.getActiva());
    }

    public int actualizar(Empresa e) {
        return ejecutar(Empresa.UPDATE, e.getNombre(), e.getNit(), e.getDireccion(),
                e.getTelefono(), e.getContacto(), e.getPersonaContacto(),
                e.getActiva(), e.getIdEmpresa());
    }

    public int eliminar(int id) {
        return ejecutar(Empresa.DELETE, id);
    }
}