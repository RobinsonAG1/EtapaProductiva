package dao;

import modelo.Usuario;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class UsuarioDAO extends BaseDAO<Usuario> {

    @Override
    protected Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setTipoDocumento(rs.getString("tipo_documento"));
        u.setNumeroDocumento(rs.getString("numero_documento"));
        u.setNombres(rs.getString("nombres"));
        u.setApellidos(rs.getString("apellidos"));
        u.setCorreo(rs.getString("correo"));
        u.setTelefono(rs.getString("telefono"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setEstado(rs.getBoolean("estado"));
        Timestamp fc = rs.getTimestamp("fecha_creacion");
        if (fc != null) u.setFechaCreacion(fc);
        return u;
    }

    public Usuario buscarPorId(int id) {
        return buscarUno(Usuario.SELECT_BY_ID, id);
    }

    public Usuario buscarPorCorreo(String correo) {
        return buscarUno(Usuario.SELECT_BY_CORREO, correo);
    }

    public List<Usuario> listarTodos() {
        return buscarLista(Usuario.SELECT_ALL);
    }

    public List<Usuario> listarActivos() {
        return buscarLista(Usuario.SELECT_ACTIVOS);
    }

    public int contarTotal() {
        return contar("SELECT COUNT(*) FROM usuario");
    }

    public int insertar(Usuario u) {
        return ejecutar(Usuario.INSERT, u.getTipoDocumento(), u.getNumeroDocumento(),
                u.getNombres(), u.getApellidos(), u.getCorreo(), u.getTelefono(),
                u.getPasswordHash(), u.getEstado(), u.getFechaCreacion());
    }

    public int actualizar(Usuario u) {
        return ejecutar(Usuario.UPDATE, u.getTipoDocumento(), u.getNumeroDocumento(),
                u.getNombres(), u.getApellidos(), u.getCorreo(), u.getTelefono(),
                u.getEstado(), u.getIdUsuario());
    }

    public int eliminar(int id) {
        return ejecutar(Usuario.DELETE, id);
    }
}