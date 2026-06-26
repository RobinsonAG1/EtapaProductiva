package dao;

import modelo.Evidencia;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvidenciaDAO extends BaseDAO<Evidencia> {

    @Override
    protected Evidencia mapear(ResultSet rs) throws SQLException {
        Evidencia e = new Evidencia();
        e.setIdEvidencia(rs.getInt("id_evidencia"));
        e.setIdAprendiz(rs.getInt("id_aprendiz"));
        e.setTipo(rs.getString("tipo"));
        e.setContenido(rs.getString("contenido"));
        e.setFechaEntrega(rs.getTimestamp("fecha_entrega"));
        e.setEstado(rs.getString("estado"));
        e.setObservaciones(rs.getString("observaciones"));
        e.setBitacora(rs.getString("bitacora"));
        e.setActas(rs.getString("actas"));
        return e;
    }

    public Evidencia buscarPorId(int id) {
        return buscarUno(Evidencia.SELECT_BY_ID, id);
    }

    public List<Evidencia> listarPorAprendiz(int idAprendiz) {
        return buscarLista(Evidencia.SELECT_BY_APRENDIZ, idAprendiz);
    }

    public List<Evidencia> listarTodas() {
        return buscarLista(Evidencia.SELECT_ALL);
    }

    public int insertar(Evidencia e) {
        return ejecutar(Evidencia.INSERT, e.getIdAprendiz(), e.getTipo(),
                e.getContenido(), e.getFechaEntrega(), e.getEstado(),
                e.getObservaciones(), e.getBitacora(), e.getActas());
    }

    public int actualizarEstado(int idEvidencia, String estado, String observaciones) {
        return ejecutar(
                "UPDATE evidencia SET estado=?, observaciones=? WHERE id_evidencia=?",
                estado, observaciones, idEvidencia);
    }

    public int aprobar(int idEvidencia, String observaciones) {
        return actualizarEstado(idEvidencia, "Aprobada", observaciones);
    }

    public int rechazar(int idEvidencia, String observaciones) {
        return actualizarEstado(idEvidencia, "No Aprobada", observaciones);
    }

    public int contarPendientesPorInstructor(int idInstructor) {
        String sql = "SELECT COUNT(*) FROM evidencia ev "
                + "JOIN aprendiz a ON ev.id_aprendiz = a.id_aprendiz "
                + "JOIN curso_aprendiz ca ON a.id_aprendiz = ca.id_aprendiz "
                + "JOIN curso_instructor ci ON ca.id_curso = ci.id_curso "
                + "WHERE ci.id_instructor = ? AND ev.estado = 'Entregada'";
        return contar(sql, idInstructor);
    }

    public List<Map<String, Object>> listarEvidenciasPorInstructor(int idInstructor) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT ev.*, a.ficha, "
                + "u.nombres || ' ' || COALESCE(u.apellidos, '') as nombre_aprendiz "
                + "FROM evidencia ev "
                + "JOIN aprendiz a ON ev.id_aprendiz = a.id_aprendiz "
                + "JOIN usuario u ON a.id_usuario = u.id_usuario "
                + "JOIN curso_aprendiz ca ON a.id_aprendiz = ca.id_aprendiz "
                + "JOIN curso_instructor ci ON ca.id_curso = ci.id_curso "
                + "WHERE ci.id_instructor = ? "
                + "ORDER BY ev.fecha_entrega DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInstructor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id_evidencia", rs.getInt("id_evidencia"));
                    row.put("id_aprendiz", rs.getInt("id_aprendiz"));
                    row.put("tipo", rs.getString("tipo"));
                    row.put("contenido", rs.getString("contenido"));
                    row.put("fecha_entrega", rs.getTimestamp("fecha_entrega"));
                    row.put("estado", rs.getString("estado"));
                    row.put("observaciones", rs.getString("observaciones"));
                    row.put("nombre_aprendiz", rs.getString("nombre_aprendiz"));
                    row.put("ficha", rs.getString("ficha"));
                    lista.add(row);
                }
            }
        } catch (SQLException ex) {
            System.err.println("[EVIDENCIA_DAO] " + ex.getMessage());
        }
        return lista;
    }

    public List<Map<String, Object>> listarEvidenciasPendientesPorInstructor(int idInstructor) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT ev.*, a.ficha, "
                + "u.nombres || ' ' || COALESCE(u.apellidos, '') as nombre_aprendiz "
                + "FROM evidencia ev "
                + "JOIN aprendiz a ON ev.id_aprendiz = a.id_aprendiz "
                + "JOIN usuario u ON a.id_usuario = u.id_usuario "
                + "JOIN curso_aprendiz ca ON a.id_aprendiz = ca.id_aprendiz "
                + "JOIN curso_instructor ci ON ca.id_curso = ci.id_curso "
                + "WHERE ci.id_instructor = ? AND ev.estado = 'Entregada' "
                + "ORDER BY ev.fecha_entrega DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInstructor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id_evidencia", rs.getInt("id_evidencia"));
                    row.put("id_aprendiz", rs.getInt("id_aprendiz"));
                    row.put("tipo", rs.getString("tipo"));
                    row.put("contenido", rs.getString("contenido"));
                    row.put("fecha_entrega", rs.getTimestamp("fecha_entrega"));
                    row.put("estado", rs.getString("estado"));
                    row.put("observaciones", rs.getString("observaciones"));
                    row.put("nombre_aprendiz", rs.getString("nombre_aprendiz"));
                    row.put("ficha", rs.getString("ficha"));
                    lista.add(row);
                }
            }
        } catch (SQLException ex) {
            System.err.println("[EVIDENCIA_DAO] " + ex.getMessage());
        }
        return lista;
    }
}
