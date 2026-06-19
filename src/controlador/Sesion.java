package controlador;

import java.util.List;

public class Sesion {
    private static int idUsuario;
    private static String nombres;
    private static String apellidos;
    private static String correo;
    private static List<String> roles;

    public static void iniciar(int id, String nom, String ape, String mail, List<String> r) {
        idUsuario = id;
        nombres = nom;
        apellidos = ape;
        correo = mail;
        roles = r;
    }

    public static void cerrar() {
        idUsuario = 0;
        nombres = null;
        apellidos = null;
        correo = null;
        roles = null;
    }

    public static int getIdUsuario() { return idUsuario; }
    public static String getNombres() { return nombres; }
    public static String getApellidos() { return apellidos; }
    public static String getNombreCompleto() { return nombres + " " + apellidos; }
    public static String getCorreo() { return correo; }
    public static List<String> getRoles() { return roles; }

    public static boolean tieneRol(String rol) {
        return roles != null && roles.contains(rol);
    }

    public static boolean esAdmin() { return tieneRol("superusuario"); }
    public static boolean esInstructor() { return tieneRol("instructor"); }
    public static boolean esAprendiz() { return tieneRol("aprendiz"); }
}
