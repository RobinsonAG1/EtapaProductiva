package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class InstructorDashboard extends JInternalFrame {

    private static final Color BG = Color.BLACK;
    private static final Color CARD = new Color(0x0A, 0x0A, 0x0A);
    private static final Color GREEN = new Color(0x22, 0xC5, 0x5E);
    private static final Color BLUE = new Color(0x3B, 0x82, 0xF6);
    private static final Color ORANGE = new Color(0xFF, 0x99, 0x00);
    private static final Color TXT = new Color(0xAA, 0xAA, 0xAA);
    private static final Color TXT_BRIGHT = new Color(0xEE, 0xEE, 0xEE);
    private static final Color BORDER = new Color(0x1A, 0x1A, 0x1A);

    private CardLayout contentLayout;
    private JPanel contentStack;

    public InstructorDashboard() {
        setTitle("Panel del Instructor - " + controlador.Sesion.getNombreCompleto());
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setBorder(BorderFactory.createEmptyBorder());
        setFrameIcon(null);

        ((javax.swing.plaf.basic.BasicInternalFrameUI) getUI()).setNorthPane(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        setContentPane(main);

        main.add(crearSidebar(), BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentStack = new JPanel(contentLayout);
        contentStack.setBackground(BG);

        contentStack.add(crearDashboardView(), "Dashboard");
        contentStack.add(crearFichasView(), "Gesti\u00f3n de Fichas");

        main.add(contentStack, BorderLayout.CENTER);

        setFrameIcon(null);
        setBackground(BG);
    }

    // ── Sidebar ────────────────────────────────────────────────
    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(CARD);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(CARD);
        header.setBorder(new EmptyBorder(20, 16, 20, 16));
        header.setAlignmentX(LEFT_ALIGNMENT);

        JLabel iconLbl = new JLabel("SENA");
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconLbl.setForeground(GREEN);
        iconLbl.setAlignmentX(LEFT_ALIGNMENT);
        header.add(iconLbl);
        header.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel nameLbl = new JLabel(controlador.Sesion.getNombreCompleto());
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        nameLbl.setForeground(TXT_BRIGHT);
        nameLbl.setAlignmentX(LEFT_ALIGNMENT);
        header.add(nameLbl);

        JLabel roleLbl = new JLabel("Instructor");
        roleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleLbl.setForeground(TXT);
        roleLbl.setAlignmentX(LEFT_ALIGNMENT);
        header.add(roleLbl);

        sidebar.add(header);
        sidebar.add(crearNavBtn("Dashboard", "📊", e -> contentLayout.show(contentStack, "Dashboard")));
        sidebar.add(crearNavBtn("Gesti\u00f3n de Fichas", "📋", e -> contentLayout.show(contentStack, "Gesti\u00f3n de Fichas")));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("CERRAR SESI\u00d3N");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(0x33, 0x33, 0x33));
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x44, 0x44, 0x44), 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setMaximumSize(new Dimension(220, 36));
        logoutBtn.setAlignmentX(LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            JFrame top = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (top instanceof MDI) ((MDI) top).cerrarSesion();
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        return sidebar;
    }

    private JButton crearNavBtn(String text, String icon, ActionListener action) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(TXT_BRIGHT);
        btn.setBackground(CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(action);
        return btn;
    }

    // ── Dashboard ──────────────────────────────────────────────
    private JPanel crearDashboardView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JLabel title = new JLabel("Panel de Control");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TXT_BRIGHT);
        title.setBorder(new EmptyBorder(16, 0, 16, 0));
        body.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        // Progreso General card
        JPanel progPanel = new JPanel();
        progPanel.setLayout(new BoxLayout(progPanel, BoxLayout.Y_AXIS));
        progPanel.setBackground(CARD);
        progPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)));
        progPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel progTitle = new JLabel("Progreso General de Aprendices");
        progTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        progTitle.setForeground(TXT_BRIGHT);
        progTitle.setAlignmentX(LEFT_ALIGNMENT);
        progPanel.add(progTitle);
        progPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JProgressBar progBar = new JProgressBar(0, 100);
        progBar.setStringPainted(true);
        progBar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        progBar.setForeground(GREEN);
        progBar.setBackground(new Color(0x15, 0x15, 0x15));
        progBar.setBorder(BorderFactory.createEmptyBorder());
        progBar.setPreferredSize(new Dimension(500, 18));
        progBar.setAlignmentX(LEFT_ALIGNMENT);
        progPanel.add(progBar);

        JLabel progLabel = new JLabel("Cargando...");
        progLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        progLabel.setForeground(TXT);
        progLabel.setAlignmentX(LEFT_ALIGNMENT);
        progLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        progPanel.add(progLabel);

        gbc.gridy = 0;
        gbc.weightx = 0;
        grid.add(progPanel, gbc);

        // Mis Fichas Asignadas
        JLabel fichasTitle = new JLabel("Mis Fichas Asignadas");
        fichasTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fichasTitle.setForeground(TXT_BRIGHT);
        fichasTitle.setBorder(new EmptyBorder(24, 0, 8, 0));
        fichasTitle.setAlignmentX(LEFT_ALIGNMENT);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        grid.add(fichasTitle, gbc);

        String[] cols = {"C\u00d3DIGO FICHA", "NOMBRE", "APRENDICES", "EVIDENCIAS PENDIENTES"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tm);
        estilizarTabla(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG);
        sp.setPreferredSize(new Dimension(600, 180));

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        grid.add(sp, gbc);

        // Acciones Rápidas
        JLabel accTitle = new JLabel("Acciones R\u00e1pidas");
        accTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        accTitle.setForeground(TXT_BRIGHT);
        accTitle.setBorder(new EmptyBorder(16, 0, 8, 0));
        accTitle.setAlignmentX(LEFT_ALIGNMENT);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        grid.add(accTitle, gbc);

        JPanel accPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        accPanel.setBackground(BG);
        accPanel.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnVerFichas = new JButton("Ver Detalle de Fichas");
        estilizarBotonAccion(btnVerFichas, BLUE);
        btnVerFichas.addActionListener(e -> contentLayout.show(contentStack, "Gesti\u00f3n de Fichas"));
        accPanel.add(btnVerFichas);

        gbc.gridy = 4;
        gbc.gridx = 0;
        grid.add(accPanel, gbc);

        // Cargar datos
        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                int idUsr = controlador.Sesion.getIdUsuario();

                // Buscar id_instructor
                String sqlInst = "SELECT id_instructor FROM instructor WHERE id_usuario = ?";
                int idInstructor = -1;
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlInst)) {
                    ps.setInt(1, idUsr);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idInstructor = rs.getInt("id_instructor");
                    }
                }

                if (idInstructor > 0) {
                    // Progreso general: promedio de horas validadas
                    String sqlPG = "SELECT COALESCE(AVG(p.porcentaje), 0) as promedio "
                                 + "FROM progreso_aprendiz p "
                                 + "JOIN curso_aprendiz ca ON p.id_curso_aprendiz = ca.id_curso_aprendiz "
                                 + "JOIN curso_instructor ci ON ca.id_curso = ci.id_curso "
                                 + "WHERE ci.id_instructor = ?";
                    double promedio = 0;
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlPG)) {
                        ps.setInt(1, idInstructor);
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) promedio = rs.getDouble("promedio");
                        }
                    }

                    // Fichas asignadas
                    String sqlF = "SELECT c.id_curso, c.ficha, c.nombre, "
                                + "COUNT(DISTINCT ca.id_aprendiz) as aprendices, "
                                + "COALESCE(SUM(CASE WHEN e.estado = 'Entregada' THEN 1 ELSE 0 END), 0) as pendientes "
                                + "FROM curso c "
                                + "JOIN curso_instructor ci ON c.id_curso = ci.id_curso "
                                + "LEFT JOIN curso_aprendiz ca ON c.id_curso = ca.id_curso "
                                + "LEFT JOIN evidencia e ON ca.id_aprendiz = e.id_aprendiz "
                                + "WHERE ci.id_instructor = ? "
                                + "GROUP BY c.id_curso, c.ficha, c.nombre";
                    java.util.List<Object[]> filas = new java.util.ArrayList<>();
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlF)) {
                        ps.setInt(1, idInstructor);
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                filas.add(new Object[]{
                                    rs.getString("ficha"),
                                    rs.getString("nombre"),
                                    rs.getInt("aprendices"),
                                    rs.getInt("pendientes")
                                });
                            }
                        }
                    }

                    final double prom = promedio;
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        int pct = (int) Math.round(prom);
                        progBar.setValue(pct);
                        progBar.setString(pct + "%");
                        progLabel.setText("Promedio general de avance: " + String.format("%.1f", prom) + "%");
                        for (Object[] f : filas) tm.addRow(f);
                    });
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        progBar.setValue(0);
                        progLabel.setText("No tienes perfil de instructor registrado.");
                    });
                }
                conn.close();
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> progLabel.setText("Error al cargar datos"));
            }
        }).start();

        body.add(grid, BorderLayout.CENTER);
        return body;
    }

    // ── Gestión de Fichas ──────────────────────────────────────
    private JPanel crearFichasView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JLabel title = new JLabel("Gesti\u00f3n de Fichas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TXT_BRIGHT);
        title.setBorder(new EmptyBorder(16, 0, 16, 0));
        body.add(title, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        JTextField txtBuscar = new JTextField(20);
        txtBuscar.setBackground(CARD);
        txtBuscar.setForeground(TXT_BRIGHT);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(6, 8, 6, 8)));
        txtBuscar.setCaretColor(TXT_BRIGHT);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(CARD);
        btnBuscar.setForeground(TXT_BRIGHT);
        btnBuscar.setBorder(BorderFactory.createLineBorder(BORDER));
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        toolbar.add(new JLabel("Buscar ficha:"));
        toolbar.add(txtBuscar);
        toolbar.add(btnBuscar);
        body.add(toolbar, BorderLayout.NORTH);

        String[] cols = {"ID", "C\u00d3DIGO", "NOMBRE", "APRENDICES", "PENDIENTES", "ACCI\u00d3N"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tm);
        estilizarTabla(table);
        table.removeColumn(table.getColumnModel().getColumn(0));

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG);
        body.add(sp, BorderLayout.CENTER);

        Runnable cargarFichas = () -> {
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    String sqlInst = "SELECT id_instructor FROM instructor WHERE id_usuario = ?";
                    int idInst = -1;
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlInst)) {
                        ps.setInt(1, controlador.Sesion.getIdUsuario());
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) idInst = rs.getInt("id_instructor");
                        }
                    }
                    if (idInst < 0) { conn.close(); return; }

                    String filtro = txtBuscar.getText().trim();
                    String sql = "SELECT c.id_curso, c.ficha, c.nombre, "
                               + "COUNT(DISTINCT ca.id_aprendiz) as aprendices, "
                               + "COALESCE(SUM(CASE WHEN e.estado = 'Entregada' THEN 1 ELSE 0 END), 0) as pendientes "
                               + "FROM curso c "
                               + "JOIN curso_instructor ci ON c.id_curso = ci.id_curso "
                               + "LEFT JOIN curso_aprendiz ca ON c.id_curso = ca.id_curso "
                               + "LEFT JOIN evidencia e ON ca.id_aprendiz = e.id_aprendiz "
                               + "WHERE ci.id_instructor = ? "
                               + (filtro.isEmpty() ? "" : "AND (c.ficha ILIKE ? OR c.nombre ILIKE ?) ")
                               + "GROUP BY c.id_curso, c.ficha, c.nombre";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, idInst);
                        if (!filtro.isEmpty()) {
                            String p = "%" + filtro + "%";
                            ps.setString(2, p);
                            ps.setString(3, p);
                        }
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            java.util.List<Object[]> filas = new java.util.ArrayList<>();
                            while (rs.next()) {
                                filas.add(new Object[]{
                                    rs.getInt("id_curso"),
                                    rs.getString("ficha"),
                                    rs.getString("nombre"),
                                    rs.getInt("aprendices"),
                                    rs.getInt("pendientes"),
                                    "Ver Detalle"
                                });
                            }
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                tm.setRowCount(0);
                                for (Object[] f : filas) tm.addRow(f);
                            });
                        }
                    }
                    conn.close();
                } catch (Exception ex) {
                    System.err.println("[FICHAS] Error: " + ex.getMessage());
                }
            }).start();
        };
        cargarFichas.run();
        btnBuscar.addActionListener(e -> cargarFichas.run());
        txtBuscar.addActionListener(e -> cargarFichas.run());

        // Detalle de ficha al hacer clic
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row < 0) return;
                    int idCurso = (int) tm.getValueAt(row, 0);
                    String codigo = (String) tm.getValueAt(row, 1);
                    mostrarDetalleFicha(idCurso, codigo);
                }
            }
        });

        return body;
    }

    private void mostrarDetalleFicha(int idCurso, String codigo) {
        JDialog detalle = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalle de Ficha " + codigo, java.awt.Dialog.ModalityType.MODELESS);
        detalle.setBackground(BG);
        detalle.getContentPane().setBackground(BG);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Info general
        JLabel info = new JLabel("Cargando informaci\u00f3n de la ficha...");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        info.setForeground(TXT);
        info.setBorder(new EmptyBorder(0, 0, 16, 0));

        String[] cols = {"APRENDIZ", "DOCUMENTO", "EMPRESA", "HORAS", "PROGRESO", "ESTADO"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tm);
        estilizarTabla(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG);

        panel.add(info, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        detalle.getContentPane().add(panel);

        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sqlF = "SELECT nombre, ficha, fecha_inicio, fecha_fin FROM curso WHERE id_curso = ?";
                String nomCurso = "", fInicio = "", fFin = "";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlF)) {
                    ps.setInt(1, idCurso);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            nomCurso = rs.getString("nombre");
                            fInicio = rs.getDate("fecha_inicio") != null ? rs.getDate("fecha_inicio").toString() : "N/A";
                            fFin = rs.getDate("fecha_fin") != null ? rs.getDate("fecha_fin").toString() : "N/A";
                        }
                    }
                }

                String sqlAp = "SELECT u.nombres || ' ' || u.apellidos as nombre, u.numero_documento, "
                             + "COALESCE(e.nombre, 'Sin asignar') as empresa, "
                             + "COALESCE(SUM(ev.horas_validadas), 0) as horas, "
                             + "c.horas_requeridas, "
                             + "a.id_aprendiz "
                             + "FROM curso_aprendiz ca "
                             + "JOIN aprendiz a ON ca.id_aprendiz = a.id_aprendiz "
                             + "JOIN usuario u ON a.id_usuario = u.id_usuario "
                             + "LEFT JOIN empresa e ON a.id_empresa = e.id_empresa "
                             + "LEFT JOIN evidencia ev ON a.id_aprendiz = ev.id_aprendiz AND ev.estado = 'Aprobada' "
                             + "JOIN curso c ON ca.id_curso = c.id_curso "
                             + "WHERE ca.id_curso = ? "
                             + "GROUP BY u.nombres, u.apellidos, u.numero_documento, e.nombre, c.horas_requeridas, a.id_aprendiz";
                java.util.List<Object[]> filas = new java.util.ArrayList<>();
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlAp)) {
                    ps.setInt(1, idCurso);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int horas = rs.getInt("horas");
                            int req = rs.getInt("horas_requeridas");
                            int pct = req > 0 ? Math.min(100, horas * 100 / req) : 0;
                            filas.add(new Object[]{
                                rs.getString("nombre"),
                                rs.getString("numero_documento") != null ? rs.getString("numero_documento") : "",
                                rs.getString("empresa"),
                                horas + " / " + req,
                                pct + "%",
                                pct >= 100 ? "Completado" : "En proceso"
                            });
                        }
                    }
                }
                conn.close();

                final String infoText = String.format("Ficha: %s | %s | Inicio: %s | Fin: %s | Aprendices: %d",
                        codigo, nomCurso, fInicio, fFin, filas.size());
                javax.swing.SwingUtilities.invokeLater(() -> {
                    info.setText(infoText);
                    for (Object[] f : filas) tm.addRow(f);
                });
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> info.setText("Error al cargar detalle"));
            }
        }).start();

        detalle.setSize(750, 500);
        detalle.setLocationRelativeTo(this);
        detalle.setVisible(true);
    }

    private void estilizarBotonAccion(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(Color.BLACK);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
    }

    private void estilizarTabla(JTable table) {
        table.setBackground(BG);
        table.setForeground(TXT);
        table.setGridColor(BORDER);
        table.setSelectionBackground(CARD);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setBackground(CARD);
                lbl.setForeground(TXT);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                lbl.setHorizontalAlignment(LEFT);
                return lbl;
            }
        });
        table.getTableHeader().setBackground(CARD);
        table.getTableHeader().setForeground(TXT);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
    }
}
