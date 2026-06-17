package modelo;

public class Aprendiz {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO aprendiz (id_usuario, id_empresa, ficha, estado_practica, horas_requeridas, horas_cumplidas) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE aprendiz SET id_empresa=?, ficha=?, estado_practica=?, horas_requeridas=?, horas_cumplidas=? WHERE id_aprendiz=?";
    public static final String DELETE = "DELETE FROM aprendiz WHERE id_aprendiz=?";
    public static final String SELECT_BY_ID = "SELECT * FROM aprendiz WHERE id_aprendiz=?";
    public static final String SELECT_BY_USUARIO = "SELECT * FROM aprendiz WHERE id_usuario=?";
    public static final String SELECT_ALL = "SELECT * FROM aprendiz ORDER BY ficha";
    public static final String SELECT_BY_FICHA = "SELECT * FROM aprendiz WHERE ficha=?";

    private int idAprendiz;
    private int idUsuario;
    private int idEmpresa;
    private String ficha;
    private String estadoPractica;
    private int horasRequeridas;
    private int horasCumplidas;

    public Aprendiz() {
    }

    public Aprendiz(int idAprendiz, int idUsuario, int idEmpresa, String ficha,
                    String estadoPractica, int horasRequeridas, int horasCumplidas) {
        this.idAprendiz = idAprendiz;
        this.idUsuario = idUsuario;
        this.idEmpresa = idEmpresa;
        this.ficha = ficha;
        this.estadoPractica = estadoPractica;
        this.horasRequeridas = horasRequeridas;
        this.horasCumplidas = horasCumplidas;
    }

    public int getIdAprendiz() {
        return idAprendiz;
    }

    public void setIdAprendiz(int idAprendiz) {
        this.idAprendiz = idAprendiz;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getFicha() {
        return ficha;
    }

    public void setFicha(String ficha) {
        this.ficha = ficha;
    }

    public String getEstadoPractica() {
        return estadoPractica;
    }

    public void setEstadoPractica(String estadoPractica) {
        this.estadoPractica = estadoPractica;
    }

    public int getHorasRequeridas() {
        return horasRequeridas;
    }

    public void setHorasRequeridas(int horasRequeridas) {
        this.horasRequeridas = horasRequeridas;
    }

    public int getHorasCumplidas() {
        return horasCumplidas;
    }

    public void setHorasCumplidas(int horasCumplidas) {
        this.horasCumplidas = horasCumplidas;
    }
}