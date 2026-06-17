package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class AdminDashboard extends JInternalFrame {

    private static final Color BG_DARK = new Color(0x0a, 0x0a, 0x0a);
    private static final Color BG_SIDEBAR = new Color(0x14, 0x14, 0x14);
    private static final Color BG_CARD = new Color(0x11, 0x11, 0x11);
    private static final Color BG_INNER_BTN = new Color(0x1a, 0x1a, 0x1a);
    private static final Color GREEN = new Color(0x22, 0xc5, 0x5e);
    private static final Color BLUE = new Color(0x3b, 0x82, 0xf6);
    private static final Color ORANGE = new Color(0xf5, 0x9e, 0x0b);
    private static final Color PURPLE = new Color(0xa8, 0x55, 0xf7);
    private static final Color TXT_SECONDARY = new Color(0x9c, 0xa3, 0xaf);
    private static final Color TXT_DIM = new Color(0x6b, 0x72, 0x80);
    private static final Color BORDER = new Color(0x22, 0x22, 0x22);
    private static final Color BANNER_BG = new Color(0x11, 0x16, 0x12);
    private static final Color BANNER_BORDER = new Color(0x16, 0x3a, 0x21);

    private SidebarButton activeMenu;
    private CardLayout contentLayout;
    private JPanel contentStack;
    private JLabel headerTitle;
    private JLabel[] statValues;
    private DefaultTableModel dashboardTableModel;
    private DefaultTableModel fichasTableModel;

    public AdminDashboard() {
        initComponents();
        javax.swing.SwingUtilities.invokeLater(this::cargarDatosDashboard);
    }

    private void cargarDatosDashboard() {
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

                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (statValues != null && statValues.length >= 5) {
                        statValues[0].setText(String.valueOf(totalUsuarios));
                        statValues[1].setText(String.valueOf(totalAprendices));
                        statValues[2].setText(String.valueOf(totalInstructores));
                        statValues[3].setText(String.valueOf(totalEmpresas));
                        statValues[4].setText(String.valueOf(totalCursos));
                    }
                });

                // Cargar actividad reciente
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
                        Object[] row = new Object[5];
                        row[0] = rs.getTimestamp("fecha") != null
                                ? rs.getTimestamp("fecha").toString().substring(0, 19)
                                : "";
                        row[1] = rs.getString("usuario");
                        row[2] = rs.getString("modulo");
                        row[3] = rs.getString("accion");
                        row[4] = rs.getString("descripcion");
                        filas.add(row);
                    }

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        if (dashboardTableModel != null) {
                            dashboardTableModel.setRowCount(0);
                            for (Object[] row : filas) {
                                dashboardTableModel.addRow(row);
                            }
                        }
                    });
                }
                conn.close();
            } catch (Exception ex) {
                System.err.println("[DASHBOARD] Error al cargar actividad: " + ex.getMessage());
            }
        }).start();
    }

    private void cargarFichas() {
        new Thread(() -> {
            try {
                String sql = "SELECT c.id_curso, c.ficha, c.nombre, c.fecha_inicio, c.fecha_fin, "
                           + "COALESCE(i_count.total, 0) as instructores, "
                           + "COALESCE(a_count.total, 0) as aprendices "
                           + "FROM curso c "
                           + "LEFT JOIN (SELECT id_curso, COUNT(*) as total FROM curso_instructor GROUP BY id_curso) i_count ON c.id_curso = i_count.id_curso "
                           + "LEFT JOIN (SELECT id_curso, COUNT(*) as total FROM curso_aprendiz GROUP BY id_curso) a_count ON c.id_curso = a_count.id_curso";
                try (java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                     java.sql.ResultSet rs = ps.executeQuery()) {

                    java.util.List<Object[]> filas = new java.util.ArrayList<>();
                    while (rs.next()) {
                        String ficha = rs.getString("ficha");
                        if (ficha == null || ficha.trim().isEmpty()) ficha = "FIC-" + rs.getInt("id_curso");
                        String nombre = rs.getString("nombre");
                        String instructores = String.valueOf(rs.getInt("instructores"));
                        String aprendices = String.valueOf(rs.getInt("aprendices"));
                        String inicio = rs.getDate("fecha_inicio") != null ? rs.getDate("fecha_inicio").toString() : "";
                        String fin = rs.getDate("fecha_fin") != null ? rs.getDate("fecha_fin").toString() : "";
                        String estado = "Activo";
                        filas.add(new Object[]{ficha, nombre, instructores, aprendices, estado, inicio, fin});
                    }

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        if (fichasTableModel != null) {
                            fichasTableModel.setRowCount(0);
                            for (Object[] row : filas) {
                                fichasTableModel.addRow(row);
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                System.err.println("[DASHBOARD] Error al cargar fichas: " + ex.getMessage());
            }
        }).start();
    }

    private void initComponents() {
        setTitle("Panel de Administraci\u00f3n");
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setBorder(BorderFactory.createLineBorder(BORDER));
        setUI(new ModernInternalFrameUI(this));
        setSize(1200, 750);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createContent(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private void switchContent(String view, String header) {
        contentLayout.show(contentStack, view);
        headerTitle.setText("\uD83D\uDEE1\uFE0F  " + header);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new BorderLayout());
        brand.setBackground(BG_SIDEBAR);
        brand.setBorder(new EmptyBorder(24, 0, 20, 0));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel brandCenter = new JPanel();
        brandCenter.setBackground(BG_SIDEBAR);
        brandCenter.setLayout(new BoxLayout(brandCenter, BoxLayout.Y_AXIS));

        JTextField brandTitle = new JTextField("AdminConsole");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setBackground(BG_SIDEBAR);
        brandTitle.setBorder(null);
        brandTitle.setHorizontalAlignment(SwingConstants.CENTER);
        brandTitle.setEditable(true);
        brandTitle.setCaretColor(GREEN);
        brandTitle.setSelectedTextColor(Color.BLACK);
        brandTitle.setSelectionColor(GREEN);

        JTextField brandSub = new JTextField("GLOBAL PRACTICE MGMT");
        brandSub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        brandSub.setForeground(TXT_DIM);
        brandSub.setBackground(BG_SIDEBAR);
        brandSub.setBorder(null);
        brandSub.setHorizontalAlignment(SwingConstants.CENTER);
        brandSub.setEditable(false);

        brandCenter.add(brandTitle);
        brandCenter.add(Box.createVerticalStrut(4));
        brandCenter.add(brandSub);

        brand.add(brandCenter, BorderLayout.CENTER);

        sidebar.add(brand);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] labels = {"Panel", "Fichas/Cursos", "Usuarios", "Roles", "Instructores", "Empresas", "Historial", "Backup"};
        String[] headers = {"Panel de Administraci\u00f3n Global", "Gesti\u00f3n de Fichas y Cursos",
                "Gesti\u00f3n de Usuarios", "Administraci\u00f3n de Roles",
                "Gesti\u00f3n de Instructores", "Gesti\u00f3n de Empresas",
                "Historial del Sistema", "Respaldos y Seguridad"};
        MenuIconType[] types = {MenuIconType.GRID, MenuIconType.BOOK, MenuIconType.PEOPLE, MenuIconType.SHIELD,
                MenuIconType.PERSON, MenuIconType.BUILDING, MenuIconType.CLOCK, MenuIconType.CLOUD};

        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            final String lbl = labels[idx];
            final String hdr = headers[idx];
            SidebarButton btn = new SidebarButton(lbl, types[idx], () -> switchContent(lbl, hdr));
            if (i == 0) {
                btn.setActive(true);
                activeMenu = btn;
            }
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(220, 1));
        sep.setForeground(BORDER);
        sidebar.add(sep);

        JPanel userPanelWrapper = new JPanel(new BorderLayout());
        userPanelWrapper.setBackground(BG_SIDEBAR);
        userPanelWrapper.setBorder(new EmptyBorder(12, 12, 16, 12));
        userPanelWrapper.setMaximumSize(new Dimension(220, 80));
        userPanelWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel userPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x22, 0x22, 0x22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(0x33, 0x33, 0x33));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        userPanel.setOpaque(false);
        userPanel.setLayout(new BorderLayout(10, 0));
        userPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        userPanel.setPreferredSize(new Dimension(196, 54));
        userPanel.setMaximumSize(new Dimension(196, 54));
        userPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN);
                g2.fillOval(0, 0, 34, 34);
                g2.setColor(new Color(0x0a, 0x0a, 0x0a));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(11, 4, 12, 12);
                g2.drawLine(17, 17, 17, 26);
                g2.drawLine(10, 20, 17, 17);
                g2.drawLine(24, 20, 17, 17);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setOpaque(false);

        JPanel userInfo = new JPanel();
        userInfo.setOpaque(false);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));

        JLabel userName = new JLabel("Administrador");
        userName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userName.setForeground(Color.WHITE);

        JLabel userRole = new JLabel("System Practices");
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userRole.setForeground(TXT_SECONDARY);

        userInfo.add(userName);
        userInfo.add(Box.createVerticalStrut(2));
        userInfo.add(userRole);

        JLabel arrow = new JLabel("\u25BC");
        arrow.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        arrow.setForeground(TXT_DIM);
        arrow.setHorizontalAlignment(SwingConstants.CENTER);

        userPanel.add(avatar, BorderLayout.WEST);
        userPanel.add(userInfo, BorderLayout.CENTER);
        userPanel.add(arrow, BorderLayout.EAST);

        userPanelWrapper.add(userPanel, BorderLayout.CENTER);
        sidebar.add(userPanelWrapper);

        return sidebar;
    }

    private JPanel createContent() {
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_DARK);

        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setOpaque(false);
        headerWrapper.setBorder(new EmptyBorder(24, 24, 16, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BANNER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BANNER_BORDER, 1),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        headerTitle = new JLabel("\uD83D\uDEE1\uFE0F  Panel de Administraci\u00f3n Global");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerTitle.setForeground(Color.WHITE);

        header.add(headerTitle, BorderLayout.WEST);
        headerWrapper.add(header, BorderLayout.CENTER);
        contentArea.add(headerWrapper, BorderLayout.NORTH);

        contentLayout = new CardLayout();
        contentStack = new JPanel(contentLayout);
        contentStack.setBackground(BG_DARK);

        contentStack.add(createDashboardView(), "Panel");
        contentStack.add(createFichasView(), "Fichas/Cursos");
        contentStack.add(createUsuariosView(), "Usuarios");
        contentStack.add(createRolesView(), "Roles");
        contentStack.add(createInstructoresView(), "Instructores");
        contentStack.add(createEmpresasView(), "Empresas");
        contentStack.add(createHistorialView(), "Historial");
        contentStack.add(createBackupView(), "Backup");

        contentArea.add(contentStack, BorderLayout.CENTER);
        return contentArea;
    }

    private void estilizarTabla(JTable table) {
        table.setBackground(BG_DARK);
        table.setForeground(TXT_SECONDARY);
        table.setGridColor(BORDER);
        table.setSelectionBackground(BG_CARD);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setBackground(BG_SIDEBAR);
                lbl.setForeground(TXT_SECONDARY);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        BorderFactory.createEmptyBorder(10, 8, 10, 8)));
                lbl.setHorizontalAlignment(LEFT);
                return lbl;
            }
        });
        table.getTableHeader().setBackground(BG_SIDEBAR);
        table.getTableHeader().setForeground(TXT_SECONDARY);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
    }

    // ── View panels ──────────────────────────────────────────

    private JPanel createDashboardView() {
        JPanel body = new JPanel();
        body.setBackground(BG_DARK);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JPanel statsRow = new JPanel(new GridLayout(1, 5, 14, 0));
        statsRow.setBackground(BG_DARK);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));

        statValues = new JLabel[5];

        statsRow.add(new StatCard("TOTAL USUARIOS", "16", MenuIconType.PEOPLE, PURPLE, "Ver Usuarios", statValues, 0));
        statsRow.add(new StatCard("APRENDICES", "12", MenuIconType.BOOK, BLUE, "Ver Usuarios", statValues, 1));
        statsRow.add(new StatCard("INSTRUCTORES", "3", MenuIconType.PERSON, GREEN, "Ver Instructores", statValues, 2));
        statsRow.add(new StatCard("EMPRESAS", "5", MenuIconType.BUILDING, ORANGE, "Ver Empresas", statValues, 3));
        statsRow.add(new StatCard("FICHAS", "1", MenuIconType.GRID, GREEN, "Gestionar Fichas", statValues, 4));

        body.add(statsRow);
        body.add(Box.createRigidArea(new Dimension(0, 24)));

        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(BG_SIDEBAR);
        activityPanel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel activityHeader = new JPanel(new BorderLayout());
        activityHeader.setBackground(BG_SIDEBAR);
        activityHeader.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel activityTitle = new JLabel("\uD83D\uDD52  ACTIVIDAD RECIENTE DEL SISTEMA (AUDITOR\u00cdA)");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        activityTitle.setForeground(Color.WHITE);

        JButton verHistorial = new JButton("Ver historial completo");
        verHistorial.setFont(new Font("Segoe UI", Font.BOLD, 11));
        verHistorial.setForeground(TXT_SECONDARY);
        verHistorial.setBackground(BG_INNER_BTN);
        verHistorial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        verHistorial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        verHistorial.setFocusPainted(false);
        verHistorial.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Historial completo pr\u00f3ximamente.");
        });

        activityHeader.add(activityTitle, BorderLayout.WEST);
        activityHeader.add(verHistorial, BorderLayout.EAST);
        activityPanel.add(activityHeader, BorderLayout.NORTH);

        String[] columns = {"FECHA", "USUARIO RESPONSABLE", "M\u00d3DULO", "ACCI\u00d3N", "DESCRIPCI\u00d3N"};
        dashboardTableModel = new DefaultTableModel(columns, 0);

        JTable table = new JTable(dashboardTableModel) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table.setBackground(BG_DARK);
        table.setForeground(TXT_SECONDARY);
        table.setGridColor(BORDER);
        table.setSelectionBackground(BG_CARD);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setBackground(BG_SIDEBAR);
                lbl.setForeground(TXT_SECONDARY);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        BorderFactory.createEmptyBorder(10, 8, 10, 8)));
                lbl.setHorizontalAlignment(LEFT);
                return lbl;
            }
        });
        table.getTableHeader().setBackground(BG_SIDEBAR);
        table.getTableHeader().setForeground(TXT_SECONDARY);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setPreferredSize(new Dimension(800, 200));

        activityPanel.add(scroll, BorderLayout.CENTER);
        body.add(activityPanel);

        return body;
    }

    private JPanel createFichasView() {
        JPanel body = new JPanel();
        body.setBackground(BG_DARK);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Summary cards for fichas
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setBackground(BG_DARK);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));

        statsRow.add(createSummaryCard("TOTAL FICHAS", "1", GREEN));
        statsRow.add(createSummaryCard("FICHAS ACTIVAS", "1", BLUE));
        statsRow.add(createSummaryCard("APRENDICES", "12", PURPLE));
        statsRow.add(createSummaryCard("CURSOS", "3", ORANGE));

        body.add(statsRow);
        body.add(Box.createRigidArea(new Dimension(0, 20)));

        // Fichas table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BG_SIDEBAR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tableHeaderP = new JPanel(new BorderLayout());
        tableHeaderP.setBackground(BG_SIDEBAR);
        tableHeaderP.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel tableTitle = new JLabel("\uD83D\uDCDA  LISTADO DE FICHAS Y CURSOS");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableTitle.setForeground(Color.WHITE);

        JButton btnNuevo = new JButton("+ Nueva Ficha");
        btnNuevo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnNuevo.setForeground(Color.BLACK);
        btnNuevo.setBackground(GREEN);
        btnNuevo.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.setFocusPainted(false);
        btnNuevo.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Crear nueva ficha - pr\u00f3ximamente.");
        });

        tableHeaderP.add(tableTitle, BorderLayout.WEST);
        tableHeaderP.add(btnNuevo, BorderLayout.EAST);
        tablePanel.add(tableHeaderP, BorderLayout.NORTH);

        String[] columns = {"C\u00d3DIGO", "NOMBRE DEL CURSO", "INSTRUCTOR", "APRENDICES", "ESTADO", "INICIO", "FIN"};
        fichasTableModel = new DefaultTableModel(columns, 0);

        JTable t = new JTable(fichasTableModel) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        t.setBackground(BG_DARK);
        t.setForeground(TXT_SECONDARY);
        t.setGridColor(BORDER);
        t.setSelectionBackground(BG_CARD);
        t.setSelectionForeground(Color.WHITE);
        t.setRowHeight(36);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);

        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tab, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tab, v, s, f, r, c);
                lbl.setBackground(BG_SIDEBAR);
                lbl.setForeground(TXT_SECONDARY);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        BorderFactory.createEmptyBorder(10, 8, 10, 8)));
                lbl.setHorizontalAlignment(LEFT);
                return lbl;
            }
        });
        t.getTableHeader().setBackground(BG_SIDEBAR);
        t.getTableHeader().setForeground(TXT_SECONDARY);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setResizingAllowed(false);

        JScrollPane sp = new JScrollPane(t);
        sp.setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_DARK);
        sp.setPreferredSize(new Dimension(800, 200));

        tablePanel.add(sp, BorderLayout.CENTER);
        body.add(tablePanel);

        cargarFichas();

        return body;
    }

    private JPanel createUsuariosView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(16, 0, 16, 0));

        JLabel countLabel = new JLabel("Cargando...");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TXT_SECONDARY);

        topBar.add(countLabel, BorderLayout.WEST);
        body.add(topBar, BorderLayout.NORTH);

        String[] columns = {"NOMBRES", "APELLIDOS", "CORREO", "TEL\u00c9FONO", "DOCUMENTO", "ESTADO"};
        DefaultTableModel tm = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tm) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        estilizarTabla(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_DARK);

        body.add(sp, BorderLayout.CENTER);

        // Cargar datos
        new Thread(() -> {
            try {
                java.util.List<modelo.Usuario> lista = new dao.UsuarioDAO().listarTodos();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    tm.setRowCount(0);
                    for (modelo.Usuario u : lista) {
                        tm.addRow(new Object[]{
                            u.getNombres(), u.getApellidos(), u.getCorreo(),
                            u.getTelefono() != null ? u.getTelefono() : "",
                            u.getNumeroDocumento() != null ? u.getNumeroDocumento() : "",
                            u.getEstado() ? "Activo" : "Inactivo"
                        });
                    }
                    countLabel.setText("Total: " + lista.size() + " usuarios");
                });
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> countLabel.setText("Error al cargar usuarios"));
            }
        }).start();

        return body;
    }

    private JPanel createInstructoresView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(16, 0, 16, 0));

        JButton btnNuevo = crearBotonAccion("+ Nuevo Instructor", GREEN);
        JButton btnEditar = crearBotonAccion("\u270E  Editar", BLUE);
        JButton btnEliminar = crearBotonAccion("\u2716  Desactivar", ORANGE);

        JLabel countLabel = new JLabel("Cargando...");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TXT_SECONDARY);

        toolbar.add(btnNuevo);
        toolbar.add(Box.createRigidArea(new Dimension(8, 0)));
        toolbar.add(btnEditar);
        toolbar.add(Box.createRigidArea(new Dimension(8, 0)));
        toolbar.add(btnEliminar);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(countLabel);

        body.add(toolbar, BorderLayout.NORTH);

        // Tabla
        String[] columns = {"ID", "NOMBRES", "APELLIDOS", "CORREO", "TEL\u00c9FONO", "\u00c1REA", "CURSOS", "ESTADO"};
        DefaultTableModel tm = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tm);
        estilizarTabla(table);
        table.removeColumn(table.getColumnModel().getColumn(0));

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_DARK);

        body.add(sp, BorderLayout.CENTER);

        // Acciones
        btnNuevo.addActionListener(e -> mostrarDialogoInstructor(null, tm, countLabel));
        btnEditar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un instructor de la tabla.", "Editar", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) tm.getValueAt(row, 0);
            mostrarDialogoInstructor(id, tm, countLabel);
        });
        btnEliminar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un instructor de la tabla.", "Desactivar", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) tm.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "\u00bfDesactivar instructor " + tm.getValueAt(row, 1) + " " + tm.getValueAt(row, 2) + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.InstructorDAO iDao = new dao.InstructorDAO();
                modelo.Instructor inst = iDao.buscarPorId(id);
                if (inst != null) {
                    inst.setActivo(false);
                    iDao.actualizar(inst);
                    cargarInstructoresTabla(tm, countLabel);
                }
            }
        });

        cargarInstructoresTabla(tm, countLabel);

        return body;
    }

    private void cargarInstructoresTabla(DefaultTableModel tm, JLabel countLabel) {
        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sql = "SELECT i.id_instructor, u.nombres, u.apellidos, u.correo, u.telefono, "
                           + "i.area_formacion, i.activo, "
                           + "COALESCE(c_count.total, 0) as cursos "
                           + "FROM instructor i "
                           + "JOIN usuario u ON i.id_usuario = u.id_usuario "
                           + "LEFT JOIN (SELECT id_instructor, COUNT(*) as total FROM curso_instructor GROUP BY id_instructor) c_count ON i.id_instructor = c_count.id_instructor";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                     java.sql.ResultSet rs = ps.executeQuery()) {

                    java.util.List<Object[]> filas = new java.util.ArrayList<>();
                    while (rs.next()) {
                        filas.add(new Object[]{
                            rs.getInt("id_instructor"),
                            rs.getString("nombres"),
                            rs.getString("apellidos"),
                            rs.getString("correo"),
                            rs.getString("telefono") != null ? rs.getString("telefono") : "",
                            rs.getString("area_formacion") != null ? rs.getString("area_formacion") : "",
                            rs.getInt("cursos"),
                            rs.getBoolean("activo") ? "Activo" : "Inactivo"
                        });
                    }

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        tm.setRowCount(0);
                        for (Object[] row : filas) tm.addRow(row);
                        countLabel.setText("Total: " + filas.size() + " instructores");
                    });
                }
                conn.close();
            } catch (Exception ex) {
                System.err.println("[INSTRUCTORES] Error: " + ex.getMessage());
            }
        }).start();
    }

    private void mostrarDialogoInstructor(Integer idInstructor, DefaultTableModel tm, JLabel countLabel) {
        boolean esNuevo = (idInstructor == null);
        modelo.Instructor inst = null;
        modelo.Usuario usr = null;

        if (!esNuevo) {
            dao.InstructorDAO iDao = new dao.InstructorDAO();
            inst = iDao.buscarPorId(idInstructor);
            if (inst != null) usr = new dao.UsuarioDAO().buscarPorId(inst.getIdUsuario());
        }

        final modelo.Instructor instF = inst;
        final modelo.Usuario usrF = usr;

        JTextField txtNombres = new JTextField(usrF != null ? usrF.getNombres() : "");
        JTextField txtApellidos = new JTextField(usrF != null ? usrF.getApellidos() : "");
        JTextField txtCorreo = new JTextField(usrF != null ? usrF.getCorreo() : "");
        JTextField txtTelefono = new JTextField(usrF != null ? usrF.getTelefono() : "");
        JTextField txtArea = new JTextField(instF != null ? instF.getAreaFormacion() : "");
        JTextField txtPassword = new JTextField(esNuevo ? "" : "********");

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 8, 4, 8);

        String[] labels = {"Nombres*:", "Apellidos*:", "Correo*:", "Tel\u00e9fono:", "\u00c1rea Formaci\u00f3n:", "Contrase\u00f1a*:"};
        JTextField txtPassField = txtPassword;
        if (!esNuevo) { txtPassField = new JTextField("********"); txtPassField.setEditable(false); }
        JTextField[] campos = {txtNombres, txtApellidos, txtCorreo, txtTelefono, txtArea, txtPassField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.weightx = 0;
            gbc.insets = new Insets(4, 8, 4, 4);
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            gbc.insets = new Insets(4, 4, 4, 8);
            form.add(campos[i], gbc);
        }

        int result = JOptionPane.showConfirmDialog(this, form,
                esNuevo ? "Nuevo Instructor" : "Editar Instructor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String area = txtArea.getText().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombres, apellidos y correo son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                if (esNuevo) {
                    String pass = txtPassword.getText().trim();
                    if (pass.isEmpty()) {
                        javax.swing.SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "La contrase\u00f1a es obligatoria.", "Error", JOptionPane.ERROR_MESSAGE));
                        return;
                    }
                    modelo.Usuario nuevoUsr = new modelo.Usuario();
                    nuevoUsr.setNombres(nombres);
                    nuevoUsr.setApellidos(apellidos);
                    nuevoUsr.setCorreo(correo);
                    nuevoUsr.setTelefono(telefono);
                    nuevoUsr.setPasswordHash(controlador.PasswordUtils.hash(pass));
                    nuevoUsr.setEstado(true);
                    nuevoUsr.setFechaCreacion(new java.sql.Timestamp(System.currentTimeMillis()));

                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    String sql = "INSERT INTO usuario (nombres, apellidos, correo, telefono, password_hash, estado, fecha_creacion) "
                               + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_usuario";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, nuevoUsr.getNombres());
                        ps.setString(2, nuevoUsr.getApellidos());
                        ps.setString(3, nuevoUsr.getCorreo());
                        ps.setString(4, nuevoUsr.getTelefono());
                        ps.setString(5, nuevoUsr.getPasswordHash());
                        ps.setBoolean(6, nuevoUsr.getEstado());
                        ps.setTimestamp(7, nuevoUsr.getFechaCreacion());
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            rs.next();
                            int idUsuario = rs.getInt(1);
                            String sqlRol = "INSERT INTO usuario_rol (id_usuario, id_rol) "
                                          + "SELECT ?, id_rol FROM rol WHERE nombre = 'instructor'";
                            try (java.sql.PreparedStatement ps2 = conn.prepareStatement(sqlRol)) {
                                ps2.setInt(1, idUsuario);
                                ps2.executeUpdate();
                            }
                            String sqlInst = "INSERT INTO instructor (id_usuario, area_formacion, activo) VALUES (?, ?, true)";
                            try (java.sql.PreparedStatement ps3 = conn.prepareStatement(sqlInst)) {
                                ps3.setInt(1, idUsuario);
                                ps3.setString(2, area.isEmpty() ? null : area);
                                ps3.executeUpdate();
                            }
                        }
                    }
                    conn.close();
                } else {
                    usrF.setNombres(nombres);
                    usrF.setApellidos(apellidos);
                    usrF.setCorreo(correo);
                    usrF.setTelefono(telefono);
                    new dao.UsuarioDAO().actualizar(usrF);
                    if (instF != null) {
                        instF.setAreaFormacion(area.isEmpty() ? null : area);
                        new dao.InstructorDAO().actualizar(instF);
                    }
                }
                javax.swing.SwingUtilities.invokeLater(() -> cargarInstructoresTabla(tm, countLabel));
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private JButton crearBotonAccion(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(bg == GREEN || bg == BLUE || bg == ORANGE ? Color.BLACK : Color.WHITE);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel createEmpresasView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(16, 0, 16, 0));

        JLabel countLabel = new JLabel("Cargando...");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TXT_SECONDARY);

        topBar.add(countLabel, BorderLayout.WEST);
        body.add(topBar, BorderLayout.NORTH);

        String[] columns = {"NIT", "NOMBRE", "DIRECCI\u00d3N", "TEL\u00c9FONO", "CONTACTO", "ESTADO"};
        DefaultTableModel tm = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tm) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        estilizarTabla(table);

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_DARK);

        body.add(sp, BorderLayout.CENTER);

        new Thread(() -> {
            try {
                java.util.List<modelo.Empresa> lista = new dao.EmpresaDAO().listarTodas();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    tm.setRowCount(0);
                    for (modelo.Empresa e : lista) {
                        tm.addRow(new Object[]{
                            e.getNit() != null ? e.getNit() : "",
                            e.getNombre(),
                            e.getDireccion() != null ? e.getDireccion() : "",
                            e.getTelefono() != null ? e.getTelefono() : "",
                            e.getContacto() != null ? e.getContacto() : "",
                            e.getActiva() ? "Activa" : "Inactiva"
                        });
                    }
                    countLabel.setText("Total: " + lista.size() + " empresas");
                });
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> countLabel.setText("Error al cargar empresas"));
            }
        }).start();

        return body;
    }

    private JPanel createPlaceholderView(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);

        JLabel lbl = new JLabel("\uD83D\uDD17  " + title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(TXT_DIM);

        JLabel sub = new JLabel("Esta secci\u00f3n estar\u00e1 disponible pr\u00f3ximamente.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TXT_DIM);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(lbl);
        stack.add(Box.createRigidArea(new Dimension(0, 12)));
        stack.add(sub);

        panel.add(stack);
        return panel;
    }

    private JPanel createSummaryCard(String title, String value, Color accent) {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 14, 18));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        titleLbl.setForeground(TXT_SECONDARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLbl.setForeground(accent);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(valueLbl);

        return card;
    }

    // ── Roles ─────────────────────────────────────────────────
    private JPanel createRolesView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(16, 0, 16, 0));

        JButton btnNuevo = crearBotonAccion("+ Nuevo Rol", GREEN);
        JButton btnEditar = crearBotonAccion("\u270E  Editar", BLUE);
        JButton btnEliminar = crearBotonAccion("\u2716  Eliminar", ORANGE);

        JLabel countLabel = new JLabel("Cargando...");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TXT_SECONDARY);

        toolbar.add(btnNuevo);
        toolbar.add(Box.createRigidArea(new Dimension(8, 0)));
        toolbar.add(btnEditar);
        toolbar.add(Box.createRigidArea(new Dimension(8, 0)));
        toolbar.add(btnEliminar);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(countLabel);

        body.add(toolbar, BorderLayout.NORTH);

        String[] columns = {"ID", "NOMBRE", "DESCRIPCI\u00d3N", "USUARIOS"};
        DefaultTableModel tm = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tm);
        estilizarTabla(table);
        table.removeColumn(table.getColumnModel().getColumn(0));

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_DARK);
        body.add(sp, BorderLayout.CENTER);

        Runnable cargar = () -> {
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    String sql = "SELECT r.id_rol, r.nombre, r.descripcion, COUNT(ur.id_usuario) as usuarios "
                               + "FROM rol r LEFT JOIN usuario_rol ur ON r.id_rol = ur.id_rol "
                               + "GROUP BY r.id_rol, r.nombre, r.descripcion ORDER BY r.id_rol";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                         java.sql.ResultSet rs = ps.executeQuery()) {
                        java.util.List<Object[]> filas = new java.util.ArrayList<>();
                        while (rs.next()) filas.add(new Object[]{
                            rs.getInt("id_rol"), rs.getString("nombre"),
                            rs.getString("descripcion") != null ? rs.getString("descripcion") : "",
                            rs.getInt("usuarios")
                        });
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            tm.setRowCount(0);
                            for (Object[] r : filas) tm.addRow(r);
                            countLabel.setText("Total: " + filas.size() + " roles");
                        });
                    }
                    conn.close();
                } catch (Exception ex) {
                    System.err.println("[ROLES] Error: " + ex.getMessage());
                }
            }).start();
        };
        cargar.run();

        btnNuevo.addActionListener(e -> {
            JTextField txtNombre = new JTextField();
            JTextField txtDesc = new JTextField();
            JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
            form.add(new JLabel("Nombre*:"));
            form.add(txtNombre);
            form.add(new JLabel("Descripci\u00f3n:"));
            form.add(txtDesc);
            int r = JOptionPane.showConfirmDialog(this, form, "Nuevo Rol", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return;
            String nom = txtNombre.getText().trim();
            if (nom.isEmpty()) { JOptionPane.showMessageDialog(this, "El nombre es obligatorio."); return; }
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    String sql = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?)";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, nom);
                        ps.setString(2, txtDesc.getText().trim().isEmpty() ? null : txtDesc.getText().trim());
                        ps.executeUpdate();
                    }
                    conn.close();
                    javax.swing.SwingUtilities.invokeLater(cargar);
                } catch (Exception ex) {
                    javax.swing.SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()));
                }
            }).start();
        });

        btnEditar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un rol."); return; }
            int id = (int) tm.getValueAt(row, 0);
            String nomActual = (String) tm.getValueAt(row, 1);
            String descActual = (String) tm.getValueAt(row, 2);
            JTextField txtNombre = new JTextField(nomActual);
            JTextField txtDesc = new JTextField(descActual);
            JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
            form.add(new JLabel("Nombre:"));
            form.add(txtNombre);
            form.add(new JLabel("Descripci\u00f3n:"));
            form.add(txtDesc);
            int r = JOptionPane.showConfirmDialog(this, form, "Editar Rol", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return;
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    String sql = "UPDATE rol SET nombre = ?, descripcion = ? WHERE id_rol = ?";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, txtNombre.getText().trim());
                        ps.setString(2, txtDesc.getText().trim().isEmpty() ? null : txtDesc.getText().trim());
                        ps.setInt(3, id);
                        ps.executeUpdate();
                    }
                    conn.close();
                    javax.swing.SwingUtilities.invokeLater(cargar);
                } catch (Exception ex) {
                    javax.swing.SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()));
                }
            }).start();
        });

        btnEliminar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un rol."); return; }
            int id = (int) tm.getValueAt(row, 0);
            String nom = (String) tm.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "\u00bfEliminar rol \"" + nom + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    conn.setAutoCommit(false);
                    try {
                        try (java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM usuario_rol WHERE id_rol = ?")) {
                            ps.setInt(1, id); ps.executeUpdate();
                        }
                        try (java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM rol WHERE id_rol = ?")) {
                            ps.setInt(1, id); ps.executeUpdate();
                        }
                        conn.commit();
                    } catch (Exception ex) {
                        conn.rollback(); throw ex;
                    }
                    conn.close();
                    javax.swing.SwingUtilities.invokeLater(cargar);
                } catch (Exception ex) {
                    javax.swing.SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()));
                }
            }).start();
        });

        return body;
    }

    // ── Historial ──────────────────────────────────────────────
    private JPanel createHistorialView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JLabel countLabel = new JLabel("Cargando...");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TXT_SECONDARY);
        countLabel.setBorder(new EmptyBorder(16, 0, 16, 0));
        body.add(countLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "TABLA", "ID REGISTRO", "OPERACI\u00d3N", "VALOR ANTERIOR", "VALOR NUEVO", "USUARIO", "FECHA"};
        DefaultTableModel tm = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tm);
        estilizarTabla(table);
        table.removeColumn(table.getColumnModel().getColumn(0));
        table.setRowHeight(28);

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_DARK);
        body.add(sp, BorderLayout.CENTER);

        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sql = "SELECT h.id_cambio, h.tabla_afectada, h.id_registro, h.tipo_operacion, "
                           + "h.valor_anterior, h.valor_nuevo, "
                           + "COALESCE(u.nombres || ' ' || u.apellidos, 'Sistema') as usuario, "
                           + "h.fecha_cambio "
                           + "FROM historial_cambios h "
                           + "LEFT JOIN usuario u ON h.id_usuario = u.id_usuario "
                           + "ORDER BY h.fecha_cambio DESC LIMIT 500";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                     java.sql.ResultSet rs = ps.executeQuery()) {
                    java.util.List<Object[]> filas = new java.util.ArrayList<>();
                    while (rs.next()) {
                        String vA = rs.getString("valor_anterior");
                        String vN = rs.getString("valor_nuevo");
                        filas.add(new Object[]{
                            rs.getInt("id_cambio"),
                            rs.getString("tabla_afectada"),
                            rs.getInt("id_registro"),
                            rs.getString("tipo_operacion"),
                            vA != null && vA.length() > 80 ? vA.substring(0, 80) + "..." : (vA != null ? vA : ""),
                            vN != null && vN.length() > 80 ? vN.substring(0, 80) + "..." : (vN != null ? vN : ""),
                            rs.getString("usuario"),
                            rs.getTimestamp("fecha_cambio") != null ? rs.getTimestamp("fecha_cambio").toString() : ""
                        });
                    }
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        tm.setRowCount(0);
                        for (Object[] r : filas) tm.addRow(r);
                        countLabel.setText("\u00daltimos " + filas.size() + " cambios registrados");
                    });
                }
                conn.close();
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> countLabel.setText("Error al cargar historial"));
            }
        }).start();

        return body;
    }

    // ── Backup ─────────────────────────────────────────────────
    private JPanel createBackupView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_DARK);
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JLabel title = new JLabel("Respaldos y Seguridad");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(16, 0, 16, 0));

        JPanel center = new JPanel();
        center.setBackground(BG_DARK);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel sub = new JLabel("Estado actual de las tablas en la base de datos:");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(sub);
        center.add(Box.createRigidArea(new Dimension(0, 16)));

        String[] cols = {"TABLA", "REGISTROS"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tm);
        estilizarTabla(table);
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(0, 0, 0, 0)));
        sp.getViewport().setBackground(BG_DARK);
        center.add(sp);
        center.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton btnExportar = crearBotonAccion("EXPORTAR TODO A CSV", BLUE);
        btnExportar.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(btnExportar);

        body.add(title, BorderLayout.NORTH);
        body.add(center, BorderLayout.CENTER);

        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String[] tablas = {"usuario", "aprendiz", "instructor", "empresa", "curso",
                                   "rol", "usuario_rol", "curso_instructor", "curso_aprendiz",
                                   "notificacion", "historial_cambios", "progreso_aprendiz"};
                java.util.List<Object[]> filas = new java.util.ArrayList<>();
                for (String t : tablas) {
                    try (java.sql.Statement st = conn.createStatement();
                         java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + t)) {
                        rs.next();
                        filas.add(new Object[]{t, rs.getInt(1)});
                    } catch (Exception ignored) {
                        filas.add(new Object[]{t, "Error"});
                    }
                }
                javax.swing.SwingUtilities.invokeLater(() -> {
                    for (Object[] f : filas) tm.addRow(f);
                });
                conn.close();
            } catch (Exception ex) {
                System.err.println("[BACKUP] Error: " + ex.getMessage());
            }
        }).start();

        btnExportar.addActionListener(e -> {
            String dir = "backup_csv_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            new java.io.File(dir).mkdirs();
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    String[] tablas = {"usuario", "aprendiz", "instructor", "empresa", "curso",
                                       "rol", "usuario_rol", "curso_instructor", "curso_aprendiz",
                                       "notificacion", "historial_cambios", "progreso_aprendiz"};
                    for (String t : tablas) {
                        try (java.sql.Statement st = conn.createStatement();
                             java.sql.ResultSet rs = st.executeQuery("SELECT * FROM " + t);
                             java.io.FileWriter fw = new java.io.FileWriter(dir + "/" + t + ".csv")) {
                            int colsCount = rs.getMetaData().getColumnCount();
                            for (int i = 1; i <= colsCount; i++) {
                                if (i > 1) fw.write(",");
                                fw.write(rs.getMetaData().getColumnName(i));
                            }
                            fw.write("\n");
                            while (rs.next()) {
                                for (int i = 1; i <= colsCount; i++) {
                                    if (i > 1) fw.write(",");
                                    String val = rs.getString(i);
                                    if (val != null) {
                                        val = val.replace("\"", "\"\"");
                                        fw.write("\"" + val + "\"");
                                    }
                                }
                                fw.write("\n");
                            }
                        } catch (Exception ignored) {}
                    }
                    conn.close();
                    javax.swing.SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Exportaci\u00f3n completada en: " + dir));
                } catch (Exception ex) {
                    javax.swing.SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()));
                }
            }).start();
        });

        return body;
    }

    // ── Inner classes ──────────────────────────────────────────

    enum MenuIconType { GRID, BOOK, PEOPLE, SHIELD, PERSON, BUILDING, CLOCK, CLOUD }

    class SidebarButton extends JPanel {

        private final String menuText;
        private final MenuIconType iconType;
        private final Runnable onClick;
        private boolean active;
        private boolean hover;

        SidebarButton(String text, MenuIconType iconType, Runnable onClick) {
            this.menuText = text;
            this.iconType = iconType;
            this.onClick = onClick;
            setBackground(BG_SIDEBAR);
            setLayout(null);
            setPreferredSize(new Dimension(220, 38));
            setMaximumSize(new Dimension(220, 38));
            setMinimumSize(new Dimension(220, 38));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!active) {
                        if (activeMenu != null) activeMenu.setActive(false);
                        setActive(true);
                        activeMenu = SidebarButton.this;
                        onClick.run();
                    }
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    setHover(true);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    setHover(false);
                }
            });
        }

        @Override
        public void addNotify() {
            super.addNotify();
            setOpaque(false);
        }

        void setActive(boolean a) {
            this.active = a;
            repaint();
        }

        void setHover(boolean h) {
            if (this.hover != h) {
                this.hover = h;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(BG_SIDEBAR);
            g2.fillRect(0, 0, w, h);

            if (active || hover) {
                if (active) {
                    g2.setColor(new Color(0x22, 0xc5, 0x5e, 40));
                } else {
                    g2.setColor(new Color(0xff, 0xff, 0xff, 12));
                }
                g2.fillRoundRect(4, 2, w - 8, h - 4, 6, 6);
            }

            if (active) {
                g2.setColor(GREEN);
                g2.fillRoundRect(0, 6, 3, h - 12, 2, 2);
            }

            Color iconColor = active ? GREEN : (hover ? Color.WHITE : TXT_SECONDARY);
            int iconX = 18;
            int iconY = (h - 16) / 2;
            paintMenuIcon(g2, iconType, iconX, iconY, 16, iconColor);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(active ? GREEN : (hover ? Color.WHITE : TXT_SECONDARY));
            FontMetrics fm = g2.getFontMetrics();
            int textX = iconX + 16 + 10;
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(menuText, textX, textY);

            g2.dispose();
        }

        private void paintMenuIcon(Graphics2D g2, MenuIconType type, int x, int y, int s, Color c) {
            g2.setColor(c);
            g2.setStroke(new BasicStroke(1.8f));
            int m = 1;
            switch (type) {
                case GRID:
                    int half = s / 2;
                    g2.drawRect(x + m, y + m, half - 2, half - 2);
                    g2.drawRect(x + half + 1, y + m, half - 2, half - 2);
                    g2.drawRect(x + m, y + half + 1, half - 2, half - 2);
                    g2.drawRect(x + half + 1, y + half + 1, half - 2, half - 2);
                    break;
                case BOOK:
                    g2.drawRect(x + m, y + m, s - 4, s - 2);
                    g2.drawLine(x + s - 4, y + m, x + s - 4, y + s - 2);
                    break;
                case PEOPLE:
                    g2.drawOval(x + m + 2, y + m, 4, 4);
                    g2.drawOval(x + s - 6, y + m, 4, 4);
                    g2.drawLine(x + m + 4, y + m + 6, x + m + 4, y + s - 2);
                    g2.drawLine(x + s - 4, y + m + 6, x + s - 4, y + s - 2);
                    g2.drawArc(x + m - 2, y + s - 8, 12, 8, 0, -180);
                    g2.drawArc(x + s - 10, y + s - 8, 12, 8, 0, 180);
                    break;
                case SHIELD:
                    int[] xs = {x + s / 2, x + m + 1, x + m + 1, x + s / 2, x + s - 2, x + s - 2};
                    int[] ys = {y + m + 1, y + s / 3, y + s - 4, y + s - 2, y + s - 4, y + s / 3};
                    g2.drawPolygon(xs, ys, 6);
                    break;
                case PERSON:
                    g2.drawOval(x + s / 2 - 3, y + m, 6, 6);
                    g2.drawLine(x + s / 2, y + m + 7, x + s / 2, y + s - 3);
                    g2.drawLine(x + m, y + s / 2 + 2, x + s - 1, y + s / 2 + 2);
                    break;
                case BUILDING:
                    g2.drawRect(x + m + 2, y + s / 2, s - 6, s / 2 - 2);
                    g2.drawLine(x + s / 2, y + m, x + s / 2, y + s / 2);
                    g2.drawRect(x + m + 2, y + m, s - 6, s / 2 - 2);
                    break;
                case CLOCK:
                    g2.drawOval(x + m, y + m, s - 2, s - 2);
                    g2.drawLine(x + s / 2, y + s / 2, x + s / 2, y + m + 3);
                    g2.drawLine(x + s / 2, y + s / 2, x + s - 4, y + s / 2);
                    break;
                case CLOUD:
                    g2.drawArc(x + m + 2, y + s / 3, s - 6, s / 2, 0, 180);
                    g2.drawArc(x + m, y + s / 4, s / 2, s / 2, 0, 180);
                    g2.drawArc(x + s / 2, y + s / 4, s / 2, s / 2, 0, 180);
                    break;
            }
        }
    }

    class StatCard extends JPanel {

        StatCard(String title, String value, MenuIconType iconType, Color accent, String linkText, JLabel[] store, int index) {
            setBackground(BG_CARD);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(16, 18, 14, 18));

            JPanel iconCircle = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25));
                    g2.fillOval(0, 0, 36, 36);
                    g2.setColor(accent);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(0, 0, 36, 36);
                    g2.translate(10, 10);
                    paintMiniIcon(g2, iconType, 16, accent);
                    g2.dispose();
                }
            };
            iconCircle.setPreferredSize(new Dimension(36, 36));
            iconCircle.setMaximumSize(new Dimension(36, 36));
            iconCircle.setBackground(BG_CARD);
            iconCircle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
            titleLabel.setForeground(TXT_SECONDARY);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            valueLabel.setForeground(Color.WHITE);
            valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (store != null && index >= 0 && index < store.length) {
                store[index] = valueLabel;
            }

            JLabel linkLabel = new JLabel(linkText);
            linkLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            linkLabel.setForeground(GREEN);
            linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            linkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            linkLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                            "Funci\u00f3n pr\u00f3ximamente: " + linkText);
                }
            });

            add(iconCircle);
            add(Box.createRigidArea(new Dimension(0, 10)));
            add(titleLabel);
            add(Box.createRigidArea(new Dimension(0, 2)));
            add(valueLabel);
            add(Box.createVerticalGlue());
            add(linkLabel);
        }

        private void paintMiniIcon(Graphics2D g2, MenuIconType type, int s, Color c) {
            g2.setColor(c);
            g2.setStroke(new BasicStroke(1.6f));
            int m = 1;
            switch (type) {
                case GRID:
                    int half = s / 2;
                    g2.drawRect(m, m, half - 2, half - 2);
                    g2.drawRect(half + 1, m, half - 2, half - 2);
                    g2.drawRect(m, half + 1, half - 2, half - 2);
                    g2.drawRect(half + 1, half + 1, half - 2, half - 2);
                    break;
                case BOOK:
                    g2.drawRect(m, m, s - 4, s - 2);
                    g2.drawLine(s - 4, m, s - 4, s - 2);
                    break;
                case PEOPLE:
                    g2.drawOval(m + 2, m, 4, 4);
                    g2.drawOval(s - 6, m, 4, 4);
                    g2.drawLine(m + 4, m + 6, m + 4, s - 2);
                    g2.drawLine(s - 4, m + 6, s - 4, s - 2);
                    g2.drawArc(m - 2, s - 8, 12, 8, 0, -180);
                    g2.drawArc(s - 10, s - 8, 12, 8, 0, 180);
                    break;
                case SHIELD:
                    int[] xs = {s / 2, m + 1, m + 1, s / 2, s - 2, s - 2};
                    int[] ys = {m + 1, s / 3, s - 4, s - 2, s - 4, s / 3};
                    g2.drawPolygon(xs, ys, 6);
                    break;
                case PERSON:
                    g2.drawOval(s / 2 - 3, m, 6, 6);
                    g2.drawLine(s / 2, m + 7, s / 2, s - 3);
                    g2.drawLine(m, s / 2 + 2, s - 1, s / 2 + 2);
                    break;
                case BUILDING:
                    g2.drawRect(m + 2, s / 2, s - 6, s / 2 - 2);
                    g2.drawLine(s / 2, m, s / 2, s / 2);
                    g2.drawRect(m + 2, m, s - 6, s / 2 - 2);
                    break;
                case CLOCK:
                    g2.drawOval(m, m, s - 2, s - 2);
                    g2.drawLine(s / 2, s / 2, s / 2, m + 3);
                    g2.drawLine(s / 2, s / 2, s - 4, s / 2);
                    break;
                case CLOUD:
                    g2.drawArc(m + 2, s / 3, s - 6, s / 2, 0, 180);
                    g2.drawArc(m, s / 4, s / 2, s / 2, 0, 180);
                    g2.drawArc(s / 2, s / 4, s / 2, s / 2, 0, 180);
                    break;
            }
        }
    }
}
