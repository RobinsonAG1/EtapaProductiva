package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import static vista.Theme.*;

public class DashboardView extends JPanel {

    private final JInternalFrame parent;
    private DefaultTableModel tableModel;
    private JLabel[] statValues;

    public DashboardView(JInternalFrame parent) {
        this.parent = parent;
        setBackground(BG_DARK);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 24, 24, 24));
        buildUI();
        cargarDatos();
    }

    private void buildUI() {
        JPanel statsRow = new JPanel(new GridLayout(1, 5, 14, 0));
        statsRow.setBackground(BG_DARK);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));

        statValues = new JLabel[5];
        statsRow.add(new StatCard("TOTAL USUARIOS", "0", AdminDashboard.MenuIconType.PEOPLE, PURPLE, "Ver Usuarios", statValues, 0));
        statsRow.add(new StatCard("APRENDICES", "0", AdminDashboard.MenuIconType.BOOK, BLUE, "Ver Usuarios", statValues, 1));
        statsRow.add(new StatCard("INSTRUCTORES", "0", AdminDashboard.MenuIconType.PERSON, GREEN, "Ver Instructores", statValues, 2));
        statsRow.add(new StatCard("EMPRESAS", "0", AdminDashboard.MenuIconType.BUILDING, ORANGE, "Ver Empresas", statValues, 3));
        statsRow.add(new StatCard("FICHAS", "0", AdminDashboard.MenuIconType.GRID, GREEN, "Gestionar Fichas", statValues, 4));

        add(statsRow);
        add(Box.createRigidArea(new Dimension(0, 24)));

        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(BG_SIDEBAR);
        activityPanel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel activityHeader = new JPanel(new BorderLayout());
        activityHeader.setBackground(BG_SIDEBAR);
        activityHeader.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel activityTitle = new JLabel("\uD83D\uDD52  ACTIVIDAD RECIENTE DEL SISTEMA (AUDITOR\u00cdA)");
        activityTitle.setFont(FONT_BOLD_13);
        activityTitle.setForeground(Color.WHITE);

        JButton verHistorial = new JButton("Ver historial completo");
        verHistorial.setFont(FONT_BOLD_11);
        verHistorial.setForeground(TXT_SECONDARY);
        verHistorial.setBackground(BG_INNER_BTN);
        verHistorial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        verHistorial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        verHistorial.setFocusPainted(false);
        verHistorial.addActionListener(e -> verHistorialCompleto());

        activityHeader.add(activityTitle, BorderLayout.WEST);
        activityHeader.add(verHistorial, BorderLayout.EAST);
        activityPanel.add(activityHeader, BorderLayout.NORTH);

        String[] columns = {"FECHA", "USUARIO RESPONSABLE", "M\u00d3DULO", "ACCI\u00d3N", "DESCRIPCI\u00d3N"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        Theme.estilizarTabla(table);
        table.setDefaultRenderer(Object.class, new AuditoriaTableCellRenderer());

        JScrollPane scroll = Theme.styledScroll(table);
        scroll.setPreferredSize(new Dimension(800, 200));
        activityPanel.add(scroll, BorderLayout.CENTER);
        add(activityPanel);
    }

    private void verHistorialCompleto() {
        JOptionPane.showMessageDialog(parent, "Historial completo pr\u00f3ximamente.");
    }

    public void cargarDatos() {
        new Thread(() -> {
            try {
                dao.UsuarioDAO uDao = new dao.UsuarioDAO();
                dao.AprendizDAO aDao = new dao.AprendizDAO();
                dao.InstructorDAO iDao = new dao.InstructorDAO();
                dao.EmpresaDAO eDao = new dao.EmpresaDAO();
                dao.CursoDAO cDao = new dao.CursoDAO();
                int totalUsuarios = uDao.contarTotal();
                int totalAprendices = aDao.contarTotal();
                int totalInstructores = iDao.contarTotal();
                int totalEmpresas = eDao.contarTotal();
                int totalCursos = cDao.contarTotal();

                SwingUtilities.invokeLater(() -> {
                    if (statValues != null && statValues.length >= 5) {
                        statValues[0].setText(String.valueOf(totalUsuarios));
                        statValues[1].setText(String.valueOf(totalAprendices));
                        statValues[2].setText(String.valueOf(totalInstructores));
                        statValues[3].setText(String.valueOf(totalEmpresas));
                        statValues[4].setText(String.valueOf(totalCursos));
                    }
                });
                cargarActividadReciente();
            } catch (Exception ex) {
                System.err.println("[DASHBOARD] Error al cargar datos: " + ex.getMessage());
            }
        }).start();
    }

    private void cargarActividadReciente() {
        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sql = "SELECT h.fecha, u.nombres || ' ' || u.apellidos as usuario, "
                           + "h.modulo, h.accion, h.descripcion "
                           + "FROM historial_cambios h "
                           + "JOIN usuario u ON h.id_usuario = u.id_usuario "
                           + "ORDER BY h.fecha DESC LIMIT 10";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                     java.sql.ResultSet rs = ps.executeQuery()) {
                    java.util.List<Object[]> filas = new java.util.ArrayList<>();
                    while (rs.next()) {
                        filas.add(new Object[]{
                            rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toString().substring(0, 19) : "",
                            rs.getString("usuario"),
                            rs.getString("modulo"),
                            rs.getString("accion"),
                            rs.getString("descripcion")
                        });
                    }
                    SwingUtilities.invokeLater(() -> {
                        if (tableModel != null) {
                            tableModel.setRowCount(0);
                            for (Object[] row : filas) tableModel.addRow(row);
                        }
                    });
                }
                conn.close();
            } catch (Exception ex) {
                System.err.println("[DASHBOARD] Error al cargar actividad: " + ex.getMessage());
            }
        }).start();
    }

    class AuditoriaTableCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String text = value != null ? value.toString() : "";
            JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            cellPanel.setOpaque(true);
            cellPanel.setBackground(isSelected ? BG_CARD : BG_DARK);

            switch (column) {
                case 0: // FECHA
                    JPanel dateCapsule = new JPanel(new BorderLayout()) {
                        @Override protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(0x22, 0x22, 0x22));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                            g2.dispose();
                        }
                    };
                    dateCapsule.setOpaque(false);
                    dateCapsule.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                    JLabel dateLabel = new JLabel(text);
                    dateLabel.setFont(FONT_PLAIN_11);
                    dateLabel.setForeground(TXT_SECONDARY);
                    dateCapsule.add(dateLabel, BorderLayout.CENTER);
                    cellPanel.add(dateCapsule);
                    return cellPanel;
                case 1: // USUARIO
                    JLabel userLabel = new JLabel(text);
                    userLabel.setFont(FONT_BOLD_11);
                    userLabel.setForeground(GREEN);
                    cellPanel.add(userLabel);
                    return cellPanel;
                case 2: // MÓDULO
                    JPanel modCapsule = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)) {
                        @Override protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(0x2c, 0x2c, 0x2c));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                            g2.dispose();
                        }
                    };
                    modCapsule.setOpaque(false);
                    modCapsule.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                    JLabel modLabel = new JLabel(text.toUpperCase());
                    modLabel.setFont(FONT_BOLD_9);
                    modLabel.setForeground(Color.WHITE);
                    modCapsule.add(modLabel);
                    cellPanel.add(modCapsule);
                    return cellPanel;
                case 3: // ACCIÓN
                    JPanel actCapsule = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)) {
                        @Override protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(0x22, 0xc5, 0x5e, 15));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                            g2.setColor(GREEN);
                            g2.setStroke(new BasicStroke(1.0f));
                            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                            g2.dispose();
                        }
                    };
                    actCapsule.setOpaque(false);
                    actCapsule.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                    JLabel actLabel = new JLabel(text.toUpperCase());
                    actLabel.setFont(FONT_BOLD_9);
                    actLabel.setForeground(GREEN);
                    actCapsule.add(actLabel);
                    cellPanel.add(actCapsule);
                    return cellPanel;
                default: // DESCRIPCIÓN
                    JLabel descLabel = new JLabel(text);
                    descLabel.setFont(FONT_PLAIN_11);
                    descLabel.setForeground(TXT_SECONDARY);
                    cellPanel.add(descLabel);
                    return cellPanel;
            }
        }
    }
}
