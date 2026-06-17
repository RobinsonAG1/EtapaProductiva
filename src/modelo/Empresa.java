package modelo;

public class Empresa {

    // Sentencias SQL
    public static final String INSERT = "INSERT INTO empresa (nombre, nit, direccion, telefono, contacto, persona_contacto, activa) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE empresa SET nombre=?, nit=?, direccion=?, telefono=?, contacto=?, persona_contacto=?, activa=? WHERE id_empresa=?";
    public static final String DELETE = "DELETE FROM empresa WHERE id_empresa=?";
    public static final String SELECT_BY_ID = "SELECT * FROM empresa WHERE id_empresa=?";
    public static final String SELECT_ALL = "SELECT * FROM empresa ORDER BY nombre";
    public static final String SELECT_ACTIVAS = "SELECT * FROM empresa WHERE activa=true ORDER BY nombre";

    private int idEmpresa;
    private String nombre;
    private String nit;
    private String direccion;
    private String telefono;
    private String contacto;
    private String personaContacto;
    private Boolean activa;

    public Empresa() {
    }

    public Empresa(int idEmpresa, String nombre, String nit, String direccion, String telefono,
                   String contacto, String personaContacto, Boolean activa) {
        this.idEmpresa = idEmpresa;
        this.nombre = nombre;
        this.nit = nit;
        this.direccion = direccion;
        this.telefono = telefono;
        this.contacto = contacto;
        this.personaContacto = personaContacto;
        this.activa = activa;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getPersonaContacto() {
        return personaContacto;
    }

    public void setPersonaContacto(String personaContacto) {
        this.personaContacto = personaContacto;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return nombre;
    }
}