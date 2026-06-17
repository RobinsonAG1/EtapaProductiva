package modelo;

public class UsuarioRol {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";
    public static final String DELETE = "DELETE FROM usuario_rol WHERE id_usuario=? AND id_rol=?";
    public static final String SELECT_BY_USUARIO = "SELECT * FROM usuario_rol WHERE id_usuario=?";
    public static final String SELECT_BY_ROL = "SELECT * FROM usuario_rol WHERE id_rol=?";
    public static final String SELECT_ALL = "SELECT * FROM usuario_rol ORDER BY id_usuario, id_rol";

    private int idUsuario;
    private int idRol;

    public UsuarioRol() {
    }

    public UsuarioRol(int idUsuario, int idRol) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
}