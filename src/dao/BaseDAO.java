package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<T> {

    protected Connection getConnection() throws SQLException {
        return controlador.Conexion.getInstance().getConnection();
    }

    protected abstract T mapear(ResultSet rs) throws SQLException;

    protected T buscarUno(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            asignarParametros(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al buscar: " + e.getMessage());
        }
        return null;
    }

    protected List<T> buscarLista(String sql, Object... params) {
        List<T> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            asignarParametros(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al buscar lista: " + e.getMessage());
        }
        return lista;
    }

    protected int contar(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            asignarParametros(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Error al contar: " + e.getMessage());
        }
        return 0;
    }

    protected int ejecutar(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(ps, params);
            int filas = ps.executeUpdate();
            return filas;
        } catch (SQLException e) {
            System.err.println("[DAO] Error al ejecutar: " + e.getMessage());
        }
        return 0;
    }

    private void asignarParametros(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                ps.setNull(i + 1, Types.NULL);
            } else if (params[i] instanceof String) {
                ps.setString(i + 1, (String) params[i]);
            } else if (params[i] instanceof Integer) {
                ps.setInt(i + 1, (Integer) params[i]);
            } else if (params[i] instanceof Long) {
                ps.setLong(i + 1, (Long) params[i]);
            } else if (params[i] instanceof Boolean) {
                ps.setBoolean(i + 1, (Boolean) params[i]);
            } else if (params[i] instanceof java.sql.Date) {
                ps.setDate(i + 1, (java.sql.Date) params[i]);
            } else if (params[i] instanceof java.sql.Timestamp) {
                ps.setTimestamp(i + 1, (java.sql.Timestamp) params[i]);
            } else if (params[i] instanceof java.math.BigDecimal) {
                ps.setBigDecimal(i + 1, (java.math.BigDecimal) params[i]);
            } else {
                ps.setObject(i + 1, params[i]);
            }
        }
    }
}