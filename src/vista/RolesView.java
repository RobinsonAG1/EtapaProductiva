package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import static vista.Theme.*;

public class RolesView extends JPanel {

    public RolesView() {
        setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Roles");
        title.setFont(FONT_BOLD_20);
        title.setForeground(TXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        DefaultTableModel tm = new DefaultTableModel(new String[]{"ROL", "USUARIOS"}, 0);
        JTable table = new JTable(tm) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        Theme.estilizarTabla(table);
        add(Theme.styledScroll(table), BorderLayout.CENTER);

        new Thread(() -> {
            try {
                var conn = controlador.Conexion.getInstance().getConnection();
                String sql = "SELECT r.nombre, COUNT(ur.id_usuario) as total FROM rol r LEFT JOIN usuario_rol ur ON r.id_rol = ur.id_rol GROUP BY r.id_rol, r.nombre";
                try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
                    SwingUtilities.invokeLater(() -> {
                        tm.setRowCount(0);
                        try { while (rs.next()) tm.addRow(new Object[]{rs.getString("nombre"), rs.getInt("total")}); } catch (Exception ignored) {}
                    });
                }
                conn.close();
            } catch (Exception e) {
                System.err.println("[ROLES] Error: " + e.getMessage());
            }
        }).start();
    }
}
