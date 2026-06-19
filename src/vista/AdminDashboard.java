package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class AdminDashboard extends JInternalFrame {

    private static final Color BG_DARK = Color.BLACK;
    private static final Color BG_SIDEBAR = Theme.BG_SIDEBAR;
    private static final Color BG_CARD = Theme.BG_CARD;
    private static final Color BG_INNER_BTN = Theme.BG_INNER_BTN;
    private static final Color GREEN = Theme.GREEN;
    private static final Color BLUE = Theme.BLUE;
    private static final Color ORANGE = Theme.ORANGE;
    private static final Color PURPLE = Theme.PURPLE;
    private static final Color TXT_SECONDARY = Theme.TXT_SECONDARY;
    private static final Color TXT_DIM = Theme.TXT_DIM;
    private static final Color BORDER = Theme.BORDER;
    private static final Color BANNER_BG = Theme.BANNER_BG;
    private static final Color BANNER_BORDER = Theme.BANNER_BORDER;

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

    private void cargarFichas(String filtroNombre) {
        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sql = "SELECT c.id_curso, c.ficha, c.nombre, "
                           + "COALESCE(i_count.total, 0) as instructores, "
                           + "COALESCE(a_count.total, 0) as aprendices "
                           + "FROM curso c "
                           + "LEFT JOIN (SELECT id_curso, COUNT(*) as total FROM curso_instructor GROUP BY id_curso) i_count ON c.id_curso = i_count.id_curso "
                           + "LEFT JOIN (SELECT id_curso, COUNT(*) as total FROM curso_aprendiz GROUP BY id_curso) a_count ON c.id_curso = a_count.id_curso ";
                if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                    sql += "WHERE LOWER(c.nombre) LIKE ? ";
                }
                sql += "ORDER BY c.nombre";

                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                        ps.setString(1, "%" + filtroNombre.trim().toLowerCase() + "%");
                    }
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        java.util.List<Object[]> filas = new java.util.ArrayList<>();
                        while (rs.next()) {
                            int idCurso = rs.getInt("id_curso");
                            String ficha = rs.getString("ficha");
                            if (ficha == null || ficha.trim().isEmpty()) ficha = "FIC-" + idCurso;
                            String nombre = rs.getString("nombre");
                            String instructores = String.valueOf(rs.getInt("instructores"));
                            String aprendices = String.valueOf(rs.getInt("aprendices"));
                            filas.add(new Object[]{nombre, instructores, aprendices, ficha, "", idCurso});
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
                }
                conn.close();
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
        setBorder(BorderFactory.createLineBorder(Theme.BORDER_GLASS));
        setUI(new ModernInternalFrameUI(this));
        setSize(1200, 750);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Glass window backdrop
                Theme.paintGlassEffect(g, this, 0, Theme.BG_GLASS, Theme.BORDER_GLASS);
            }
        };
        root.setOpaque(false);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createContent(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private void switchContent(String view, String header) {
        contentLayout.show(contentStack, view);
        headerTitle.setText(header);
        if (view.equals("Panel")) {
            headerTitle.setIcon(new ShieldIcon());
        } else if (view.equals("Fichas/Cursos")) {
            headerTitle.setIcon(new BookIcon());
        } else {
            headerTitle.setIcon(new ShieldIcon());
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Glass sidebar paint
                Theme.paintGlassEffect(g, this, 0, Theme.BG_SIDEBAR_GLASS, Theme.BORDER_GLASS);
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new BorderLayout());
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(24, 0, 20, 0));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel brandCenter = new JPanel();
        brandCenter.setOpaque(false);
        brandCenter.setLayout(new BoxLayout(brandCenter, BoxLayout.Y_AXIS));

        JTextField brandTitle = new JTextField("AdminConsole");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setBackground(new Color(0, 0, 0, 0));
        brandTitle.setOpaque(false);
        brandTitle.setBorder(null);
        brandTitle.setHorizontalAlignment(SwingConstants.CENTER);
        brandTitle.setEditable(false);
        brandTitle.setFocusable(false);

        JTextField brandSub = new JTextField("GLOBAL PRACTICE MGMT");
        brandSub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        brandSub.setForeground(TXT_DIM);
        brandSub.setBackground(new Color(0, 0, 0, 0));
        brandSub.setOpaque(false);
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
        userPanelWrapper.setOpaque(false);
        userPanelWrapper.setBorder(new EmptyBorder(12, 12, 16, 12));
        userPanelWrapper.setMaximumSize(new Dimension(220, 80));
        userPanelWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel userPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Glass user profile
                Theme.paintGlassEffect(g, this, 20, new Color(255, 255, 255, 12), Theme.BORDER_GLASS);
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

        JLabel userName = new JLabel(controlador.Sesion.getNombreCompleto());
        userName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userName.setForeground(Color.WHITE);

        JLabel userRole = new JLabel(controlador.Sesion.esAdmin() ? "Superusuario" : "Administrador");
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

        // Logout button
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
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            JFrame top = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (top instanceof MDI) ((MDI) top).cerrarSesion();
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        return sidebar;
    }

    private JPanel createContent() {
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);

        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setOpaque(false);
        headerWrapper.setBorder(new EmptyBorder(24, 24, 16, 24));

        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Beautiful glass header instead of murky green
                Theme.paintGlassEffect(g, this, 14, new Color(255, 255, 255, 12), Theme.BORDER_GLASS);
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        headerTitle = new JLabel("Panel de Administración Global");
        headerTitle.setIcon(new ShieldIcon());
        headerTitle.setIconTextGap(10);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerTitle.setForeground(Color.WHITE);

        header.add(headerTitle, BorderLayout.WEST);
        headerWrapper.add(header, BorderLayout.CENTER);
        contentArea.add(headerWrapper, BorderLayout.NORTH);

        contentLayout = new CardLayout();
        contentStack = new JPanel(contentLayout);
        contentStack.setOpaque(false);

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
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JPanel statsRow = new JPanel(new GridLayout(1, 5, 14, 0));
        statsRow.setOpaque(false);
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

        JPanel activityPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Theme.paintGlassEffect(g, this, 16, Theme.BG_GLASS, Theme.BORDER_GLASS);
            }
        };
        activityPanel.setOpaque(false);
        activityPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel activityHeader = new JPanel(new BorderLayout());
        activityHeader.setOpaque(false);
        activityHeader.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel activityTitle = new JLabel("\uD83D\uDD52  ACTIVIDAD RECIENTE DEL SISTEMA (AUDITOR\u00cdA)");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        activityTitle.setForeground(Color.WHITE);

        JButton verHistorial = Theme.crearBoton("Ver historial completo", BG_INNER_BTN);
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
        Theme.estilizarTabla(table);
        table.setDefaultRenderer(Object.class, new AuditoriaTableCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(800, 200));
        Theme.estilizarScrollbar(scroll);

        activityPanel.add(scroll, BorderLayout.CENTER);
        body.add(activityPanel);

        return body;
    }

    private JPanel createFichasView() {
        JPanel body = new JPanel();
        body.setBackground(BG_DARK);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        // 1. Cabecera (Título y Botón "+ Nueva Ficha")
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 14, 0));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Gestión de Fichas / Cursos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        GradientButton btnNuevo = new GradientButton("+ Nueva Ficha");
        btnNuevo.setPreferredSize(new Dimension(130, 32));
        btnNuevo.addActionListener(e -> mostrarDialogoNuevaFicha());

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnNuevo, BorderLayout.EAST);
        body.add(headerPanel);

        // 2. Barra de Búsqueda
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(new EmptyBorder(0, 0, 16, 0));
        searchBar.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        searchBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtBuscar = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x11, 0x11, 0x11));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtBuscar.setOpaque(false);
        txtBuscar.setCaretColor(Color.WHITE);
        txtBuscar.setForeground(Color.WHITE);
        txtBuscar.setBackground(new Color(0x11, 0x11, 0x11));
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setText("Buscar ficha por nombre...");
        txtBuscar.setForeground(TXT_DIM);
        txtBuscar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtBuscar.getText().equals("Buscar ficha por nombre...")) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(Color.WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    txtBuscar.setText("Buscar ficha por nombre...");
                    txtBuscar.setForeground(TXT_DIM);
                }
            }
        });

        GradientButton btnBuscar = new GradientButton("Buscar", new SearchIcon());
        btnBuscar.setPreferredSize(new Dimension(100, 36));
        btnBuscar.addActionListener(e -> {
            String query = txtBuscar.getText().trim();
            if (query.equals("Buscar ficha por nombre...")) query = "";
            cargarFichas(query);
        });

        searchBar.add(txtBuscar, BorderLayout.CENTER);
        searchBar.add(btnBuscar, BorderLayout.EAST);
        body.add(searchBar);

        // 3. Fichas disponibles panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BG_SIDEBAR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tableHeaderP = new JPanel(new BorderLayout());
        tableHeaderP.setBackground(BG_SIDEBAR);
        tableHeaderP.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel tableTitle = new JLabel("FICHAS DISPONIBLES");
        tableTitle.setIcon(new BookIcon());
        tableTitle.setIconTextGap(8);
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableTitle.setForeground(Color.WHITE);
        tableHeaderP.add(tableTitle, BorderLayout.WEST);
        tablePanel.add(tableHeaderP, BorderLayout.NORTH);

        String[] columns = {"NOMBRE", "INSTRUCTORES", "APRENDICES", "CÓDIGO", "ACCIONES", "ID_CURSO"};
        fichasTableModel = new DefaultTableModel(columns, 0);

        JTable t = new JTable(fichasTableModel) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 4;
            }
        };
        t.setBackground(BG_DARK);
        t.setForeground(TXT_SECONDARY);
        t.setGridColor(BORDER);
        t.setSelectionBackground(BG_CARD);
        t.setSelectionForeground(Color.WHITE);
        t.setRowHeight(38);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);

        t.removeColumn(t.getColumnModel().getColumn(5));

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

        FichasTableCellRenderer renderer = new FichasTableCellRenderer();
        t.getColumnModel().getColumn(0).setCellRenderer(renderer);
        t.getColumnModel().getColumn(1).setCellRenderer(renderer);
        t.getColumnModel().getColumn(2).setCellRenderer(renderer);
        t.getColumnModel().getColumn(3).setCellRenderer(renderer);
        t.getColumnModel().getColumn(4).setCellRenderer(renderer);
        t.getColumnModel().getColumn(4).setCellEditor(new FichasTableCellEditor());

        JScrollPane sp = new JScrollPane(t);
        sp.setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_DARK);
        sp.setPreferredSize(new Dimension(800, 300));

        tablePanel.add(sp, BorderLayout.CENTER);
        body.add(tablePanel);
        body.add(Box.createRigidArea(new Dimension(0, 20)));

        // 4. Pie de Página (Footer)
        JPanel footerWrapper = new JPanel();
        footerWrapper.setLayout(new BoxLayout(footerWrapper, BoxLayout.Y_AXIS));
        footerWrapper.setOpaque(false);
        footerWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel greenLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(GREEN);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        greenLine.setPreferredSize(new Dimension(0, 2));
        greenLine.setMaximumSize(new Dimension(Short.MAX_VALUE, 2));

        JPanel footerTextPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        footerTextPanel.setOpaque(false);
        JLabel capLabel = new JLabel(new MortarboardMiniIcon());
        JLabel senaCopy = new JLabel("SENA Prácticas © 2026");
        senaCopy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        senaCopy.setForeground(TXT_DIM);
        footerTextPanel.add(capLabel);
        footerTextPanel.add(senaCopy);

        footerWrapper.add(greenLine);
        footerWrapper.add(footerTextPanel);
        body.add(footerWrapper);

        cargarFichas(null);

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

    private void mostrarDialogoNuevaFicha() {
            JTextField txtNombre = new JTextField();
            JTextField txtFicha = new JTextField();
            JTextField txtInicio = new JTextField();
            JTextField txtFin = new JTextField();
            
            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 8, 4, 8);
            
            String[] labels = {"Nombre Curso*:", "Ficha / Código*:", "Fecha Inicio:", "Fecha Fin:"};
            JTextField[] campos = {txtNombre, txtFicha, txtInicio, txtFin};
            for (int i = 0; i < labels.length; i++) {
                gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
                form.add(new JLabel(labels[i]), gbc);
                gbc.gridx = 1; gbc.weightx = 1;
                form.add(campos[i], gbc);
            }
            
            int result = JOptionPane.showConfirmDialog(this, form, "Nueva Ficha / Curso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String nombre = txtNombre.getText().trim();
                String ficha = txtFicha.getText().trim();
                String inicio = txtInicio.getText().trim();
                String fin = txtFin.getText().trim();
                
                if (nombre.isEmpty() || ficha.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nombre y Ficha son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                new Thread(() -> {
                    try {
                        dao.CursoDAO cDao = new dao.CursoDAO();
                        modelo.Curso c = new modelo.Curso();
                        c.setNombre(nombre);
                        c.setFicha(ficha);
                        if (!inicio.isEmpty()) c.setFechaInicio(java.sql.Date.valueOf(inicio));
                        if (!fin.isEmpty()) c.setFechaFin(java.sql.Date.valueOf(fin));
                        
                        cDao.insertar(c);
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            cargarFichas(null);
                            cargarDatosDashboard();
                        });
                    } catch (Exception ex) {
                        javax.swing.SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                    }
                }).start();
            }
        }

        private void editarFichaAccion(int idCurso) {
            new Thread(() -> {
                try {
                    dao.CursoDAO cDao = new dao.CursoDAO();
                    modelo.Curso c = cDao.buscarPorId(idCurso);
                    if (c == null) return;
                    
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        JTextField txtNombre = new JTextField(c.getNombre());
                        JTextField txtFicha = new JTextField(c.getFicha());
                        JTextField txtInicio = new JTextField(c.getFechaInicio() != null ? c.getFechaInicio().toString() : "");
                        JTextField txtFin = new JTextField(c.getFechaFin() != null ? c.getFechaFin().toString() : "");
                        
                        JPanel form = new JPanel(new GridBagLayout());
                        GridBagConstraints gbc = new GridBagConstraints();
                        gbc.fill = GridBagConstraints.HORIZONTAL;
                        gbc.insets = new Insets(4, 8, 4, 8);
                        
                        String[] labels = {"Nombre Curso*:", "Ficha / Código*:", "Fecha Inicio:", "Fecha Fin:"};
                        JTextField[] campos = {txtNombre, txtFicha, txtInicio, txtFin};
                        for (int i = 0; i < labels.length; i++) {
                            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
                            form.add(new JLabel(labels[i]), gbc);
                            gbc.gridx = 1; gbc.weightx = 1;
                            form.add(campos[i], gbc);
                        }
                        
                        int result = JOptionPane.showConfirmDialog(this, form, "Editar Ficha / Curso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                        if (result == JOptionPane.OK_OPTION) {
                            String nombre = txtNombre.getText().trim();
                            String ficha = txtFicha.getText().trim();
                            String inicio = txtInicio.getText().trim();
                            String fin = txtFin.getText().trim();
                            
                            if (nombre.isEmpty() || ficha.isEmpty()) {
                                JOptionPane.showMessageDialog(this, "Nombre y Ficha son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            
                            new Thread(() -> {
                                try {
                                    c.setNombre(nombre);
                                    c.setFicha(ficha);
                                    if (!inicio.isEmpty()) c.setFechaInicio(java.sql.Date.valueOf(inicio));
                                    else c.setFechaInicio(null);
                                    if (!fin.isEmpty()) c.setFechaFin(java.sql.Date.valueOf(fin));
                                    else c.setFechaFin(null);
                                    
                                    cDao.actualizar(c);
                                    javax.swing.SwingUtilities.invokeLater(() -> {
                                        cargarFichas(null);
                                        cargarDatosDashboard();
                                    });
                                } catch (Exception ex) {
                                    javax.swing.SwingUtilities.invokeLater(() -> 
                                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                                }
                            }).start();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        }

        private void eliminarFichaAccion(int idCurso, String name) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Estás seguro de que deseas eliminar la ficha \"" + name + "\"?\nEsta acción borrará todas las asociaciones.", 
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                new Thread(() -> {
                    try {
                        java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                        try (java.sql.PreparedStatement ps1 = conn.prepareStatement("DELETE FROM curso_instructor WHERE id_curso = ?");
                             java.sql.PreparedStatement ps2 = conn.prepareStatement("DELETE FROM curso_aprendiz WHERE id_curso = ?")) {
                            ps1.setInt(1, idCurso);
                            ps1.executeUpdate();
                            ps2.setInt(1, idCurso);
                            ps2.executeUpdate();
                        }
                        dao.CursoDAO cDao = new dao.CursoDAO();
                        cDao.eliminar(idCurso);
                        conn.close();
                        
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            cargarFichas(null);
                            cargarDatosDashboard();
                            JOptionPane.showMessageDialog(this, "Ficha eliminada correctamente.");
                        });
                    } catch (Exception ex) {
                        javax.swing.SwingUtilities.invokeLater(() -> 
                            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                    }
                }).start();
            }
        }

        private void asignarFichaAccion(int idCurso, String name) {
            JDialog dialog = new JDialog(javax.swing.SwingUtilities.getWindowAncestor(this), "Asignaciones - " + name, Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setSize(550, 450);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(BG_DARK);
            dialog.setLayout(new BorderLayout());
            
            JTabbedPane tabs = new JTabbedPane();
            tabs.setBackground(BG_SIDEBAR);
            tabs.setForeground(Color.WHITE);
            tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            // PANEL INSTRUCTORES
            JPanel panelInst = new JPanel(new BorderLayout(8, 8));
            panelInst.setBackground(BG_DARK);
            panelInst.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            
            JPanel topInst = new JPanel(new BorderLayout(8, 8));
            topInst.setOpaque(false);
            JComboBox<ComboItem> comboInst = new JComboBox<>();
            comboInst.setBackground(BG_SIDEBAR);
            comboInst.setForeground(Color.WHITE);
            JButton btnAddInst = crearBotonAccion("+ Asignar", GREEN);
            topInst.add(new JLabel("Instructor disponible:"), BorderLayout.WEST);
            topInst.add(comboInst, BorderLayout.CENTER);
            topInst.add(btnAddInst, BorderLayout.EAST);
            
            DefaultListModel<ComboItem> listModelInst = new DefaultListModel<>();
            JList<ComboItem> listInst = new JList<>(listModelInst);
            listInst.setBackground(BG_SIDEBAR);
            listInst.setForeground(Color.WHITE);
            listInst.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            JScrollPane scrollInst = new JScrollPane(listInst);
            scrollInst.setBorder(BorderFactory.createLineBorder(BORDER));
            
            JButton btnDelInst = crearBotonAccion("Remover Seleccionado", new Color(0xef, 0x44, 0x44));
            
            panelInst.add(topInst, BorderLayout.NORTH);
            panelInst.add(scrollInst, BorderLayout.CENTER);
            panelInst.add(btnDelInst, BorderLayout.SOUTH);
            
            // PANEL APRENDICES
            JPanel panelApr = new JPanel(new BorderLayout(8, 8));
            panelApr.setBackground(BG_DARK);
            panelApr.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            
            JPanel topApr = new JPanel(new BorderLayout(8, 8));
            topApr.setOpaque(false);
            JComboBox<ComboItem> comboApr = new JComboBox<>();
            comboApr.setBackground(BG_SIDEBAR);
            comboApr.setForeground(Color.WHITE);
            JButton btnAddApr = crearBotonAccion("+ Asignar", GREEN);
            topApr.add(new JLabel("Aprendiz disponible:"), BorderLayout.WEST);
            topApr.add(comboApr, BorderLayout.CENTER);
            topApr.add(btnAddApr, BorderLayout.EAST);
            
            DefaultListModel<ComboItem> listModelApr = new DefaultListModel<>();
            JList<ComboItem> listApr = new JList<>(listModelApr);
            listApr.setBackground(BG_SIDEBAR);
            listApr.setForeground(Color.WHITE);
            listApr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            JScrollPane scrollApr = new JScrollPane(listApr);
            scrollApr.setBorder(BorderFactory.createLineBorder(BORDER));
            
            JButton btnDelApr = crearBotonAccion("Remover Seleccionado", new Color(0xef, 0x44, 0x44));
            
            panelApr.add(topApr, BorderLayout.NORTH);
            panelApr.add(scrollApr, BorderLayout.CENTER);
            panelApr.add(btnDelApr, BorderLayout.SOUTH);
            
            tabs.addTab("Instructores", panelInst);
            tabs.addTab("Aprendices", panelApr);
            dialog.add(tabs, BorderLayout.CENTER);
            
            Runnable cargarInstructores = () -> {
                new Thread(() -> {
                    try {
                        java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                        String sqlAssigned = "SELECT i.id_instructor, u.nombres || ' ' || u.apellidos as nombre FROM instructor i JOIN usuario u ON i.id_usuario = u.id_usuario JOIN curso_instructor ci ON i.id_instructor = ci.id_instructor WHERE ci.id_curso = ?";
                        java.util.List<ComboItem> assignedList = new java.util.ArrayList<>();
                        try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlAssigned)) {
                            ps.setInt(1, idCurso);
                            try (java.sql.ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    assignedList.add(new ComboItem(rs.getInt("id_instructor"), rs.getString("nombre")));
                                }
                            }
                        }
                        String sqlUnassigned = "SELECT i.id_instructor, u.nombres || ' ' || u.apellidos as nombre FROM instructor i JOIN usuario u ON i.id_usuario = u.id_usuario WHERE i.activo = true AND i.id_instructor NOT IN (SELECT id_instructor FROM curso_instructor WHERE id_curso = ?)";
                        java.util.List<ComboItem> unassignedList = new java.util.ArrayList<>();
                        try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlUnassigned)) {
                            ps.setInt(1, idCurso);
                            try (java.sql.ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    unassignedList.add(new ComboItem(rs.getInt("id_instructor"), rs.getString("nombre")));
                                }
                            }
                        }
                        conn.close();
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            listModelInst.clear();
                            for (ComboItem item : assignedList) listModelInst.addElement(item);
                            comboInst.removeAllItems();
                            for (ComboItem item : unassignedList) comboInst.addItem(item);
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            };
            
            Runnable cargarAprendices = () -> {
                new Thread(() -> {
                    try {
                        java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                        String sqlAssigned = "SELECT a.id_aprendiz, u.nombres || ' ' || u.apellidos as nombre FROM aprendiz a JOIN usuario u ON a.id_usuario = u.id_usuario JOIN curso_aprendiz ca ON a.id_aprendiz = ca.id_aprendiz WHERE ca.id_curso = ?";
                        java.util.List<ComboItem> assignedList = new java.util.ArrayList<>();
                        try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlAssigned)) {
                            ps.setInt(1, idCurso);
                            try (java.sql.ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    assignedList.add(new ComboItem(rs.getInt("id_aprendiz"), rs.getString("nombre")));
                                }
                            }
                        }
                        String sqlUnassigned = "SELECT a.id_aprendiz, u.nombres || ' ' || u.apellidos as nombre FROM aprendiz a JOIN usuario u ON a.id_usuario = u.id_usuario WHERE a.id_aprendiz NOT IN (SELECT id_aprendiz FROM curso_aprendiz WHERE id_curso = ?)";
                        java.util.List<ComboItem> unassignedList = new java.util.ArrayList<>();
                        try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlUnassigned)) {
                            ps.setInt(1, idCurso);
                            try (java.sql.ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    unassignedList.add(new ComboItem(rs.getInt("id_aprendiz"), rs.getString("nombre")));
                                }
                            }
                        }
                        conn.close();
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            listModelApr.clear();
                            for (ComboItem item : assignedList) listModelApr.addElement(item);
                            comboApr.removeAllItems();
                            for (ComboItem item : unassignedList) comboApr.addItem(item);
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            };
            
            btnAddInst.addActionListener(ev -> {
                ComboItem selected = (ComboItem) comboInst.getSelectedItem();
                if (selected == null) return;
                new Thread(() -> {
                    try {
                        java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                        String sql = "INSERT INTO curso_instructor (id_curso, id_instructor) VALUES (?, ?)";
                        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, idCurso);
                            ps.setInt(2, selected.id);
                            ps.executeUpdate();
                        }
                        conn.close();
                        cargarInstructores.run();
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            cargarFichas(null);
                            cargarDatosDashboard();
                        });
                    } catch (Exception ex) {
                        javax.swing.SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage()));
                    }
                }).start();
            });
            
            btnDelInst.addActionListener(ev -> {
                ComboItem selected = listInst.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(dialog, "Seleccione un instructor de la lista.");
                    return;
                }
                new Thread(() -> {
                    try {
                        java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                        String sql = "DELETE FROM curso_instructor WHERE id_curso = ? AND id_instructor = ?";
                        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, idCurso);
                            ps.setInt(2, selected.id);
                            ps.executeUpdate();
                        }
                        conn.close();
                        cargarInstructores.run();
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            cargarFichas(null);
                            cargarDatosDashboard();
                        });
                    } catch (Exception ex) {
                        javax.swing.SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(dialog, "Error: " + ev.toString()));
                    }
                }).start();
            });
            
            btnAddApr.addActionListener(ev -> {
                ComboItem selected = (ComboItem) comboApr.getSelectedItem();
                if (selected == null) return;
                new Thread(() -> {
                    try {
                        java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                        String sql = "INSERT INTO curso_aprendiz (id_curso, id_aprendiz, estado) VALUES (?, ?, ?)";
                        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, idCurso);
                            ps.setInt(2, selected.id);
                            ps.setString(3, "Activo");
                            ps.executeUpdate();
                        }
                        conn.close();
                        cargarAprendices.run();
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            cargarFichas(null);
                            cargarDatosDashboard();
                        });
                    } catch (Exception ex) {
                        javax.swing.SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage()));
                    }
                }).start();
            });
            
            btnDelApr.addActionListener(ev -> {
                ComboItem selected = listApr.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(dialog, "Seleccione un aprendiz de la lista.");
                    return;
                }
                new Thread(() -> {
                    try {
                        java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                        String sql = "DELETE FROM curso_aprendiz WHERE id_curso = ? AND id_aprendiz = ?";
                        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, idCurso);
                            ps.setInt(2, selected.id);
                            ps.executeUpdate();
                        }
                        conn.close();
                        cargarAprendices.run();
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            cargarFichas(null);
                            cargarDatosDashboard();
                        });
                    } catch (Exception ex) {
                        javax.swing.SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage()));
                    }
                }).start();
            });
            
            cargarInstructores.run();
            cargarAprendices.run();
            
            dialog.setVisible(true);
        }
        
        static class ComboItem {
            int id;
            String name;
            ComboItem(int id, String name) {
                this.id = id;
                this.name = name;
            }
            @Override
            public String toString() {
                return name;
            }
        }

    // Custom Java 2D and Renderer Classes
    static class ShieldIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x22, 0xc5, 0x5e));
            g2.setStroke(new BasicStroke(2.0f));
            int size = 16;
            int px = x + 1;
            int py = y + 1;
            int[] xs = {px + size / 2, px + 2, px + 2, px + size / 2, px + size - 2, px + size - 2};
            int[] ys = {py + 2, py + 5, py + size - 5, py + size - 1, py + size - 5, py + 5};
            g2.drawPolygon(xs, ys, 6);
            g2.fillOval(px + size / 2 - 2, py + size / 2 - 2, 4, 4);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }
    }

    static class BookIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x22, 0xc5, 0x5e));
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawRect(x + 2, y + 2, 12, 14);
            g2.drawLine(x + 11, y + 2, x + 11, y + 16);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 18; }
    }

    static class SearchIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawOval(x + 2, y + 2, 7, 7);
            g2.drawLine(x + 8, y + 8, x + 13, y + 13);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 16; }
    }

    static class MortarboardMiniIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x6b, 0x72, 0x80));
            g2.setStroke(new BasicStroke(1.5f));
            Polygon p = new Polygon();
            p.addPoint(x + 8, y + 2);
            p.addPoint(x + 15, y + 6);
            p.addPoint(x + 8, y + 10);
            p.addPoint(x + 1, y + 6);
            g2.drawPolygon(p);
            g2.drawArc(x + 4, y + 8, 8, 4, 0, -180);
            g2.drawLine(x + 8, y + 6, x + 13, y + 10);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 16; }
    }

    class GradientButton extends JButton {
        private Color startColor = new Color(0x10, 0xb9, 0x81);
        private Color endColor = new Color(0x05, 0x96, 0x69);
        private int radius = 16;
        
        public GradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public GradientButton(String text, Icon icon) {
            super(text, icon);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            
            Color cStart = startColor;
            Color cEnd = endColor;
            
            if (getModel().isPressed()) {
                cStart = startColor.darker();
                cEnd = endColor.darker();
            } else if (getModel().isRollover()) {
                cStart = startColor.brighter();
                cEnd = endColor.brighter();
            }
            
            LinearGradientPaint lgp = new LinearGradientPaint(
                0, 0, w, 0,
                new float[]{0f, 1f},
                new Color[]{cStart, cEnd}
            );
            g2.setPaint(lgp);
            g2.fillRoundRect(0, 0, w, h, radius, radius);
            
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class ActionIconButton extends JButton {
        private Color color;
        private int iconType; // 0=view, 1=edit, 2=assign, 3=delete
        
        public ActionIconButton(int iconType, Color color) {
            this.iconType = iconType;
            this.color = color;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setPreferredSize(new Dimension(28, 28));
            setMinimumSize(new Dimension(28, 28));
            setMaximumSize(new Dimension(28, 28));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            
            Color c = color;
            if (getModel().isPressed()) {
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 35));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
                g2.setColor(c.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 20));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
                g2.setColor(c.brighter());
            } else {
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 10));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
                g2.setColor(c);
            }
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
            
            g2.setColor(c);
            g2.setStroke(new BasicStroke(1.5f));
            int size = 12;
            int cx = (w - size) / 2;
            int cy = (h - size) / 2;
            
            switch (iconType) {
                case 0: // View (Eye)
                    g2.drawArc(cx, cy + 2, size, size - 4, 0, 180);
                    g2.drawArc(cx, cy + 2, size, size - 4, 0, -180);
                    g2.fillOval(cx + size/2 - 2, cy + size/2 - 2, 4, 4);
                    break;
                case 1: // Edit (Pencil)
                    g2.drawRect(cx + 2, cy + 2, 4, size - 4);
                    g2.drawLine(cx + 2, cy + 2, cx + 5, cy + 5);
                    g2.drawLine(cx, cy + size, cx + 2, cy + size - 2);
                    g2.drawLine(cx, cy + size, cx + 2, cy + size);
                    break;
                case 2: // Assign (Person + Plus)
                    g2.drawOval(cx + 1, cy, 6, 6);
                    g2.drawArc(cx - 3, cy + 7, 14, 8, 0, 180);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawLine(cx + 9, cy + 4, cx + 13, cy + 4);
                    g2.drawLine(cx + 11, cy + 2, cx + 11, cy + 6);
                    break;
                case 3: // Delete (Trash)
                    g2.drawRect(cx + 2, cy + 3, size - 4, size - 3);
                    g2.drawLine(cx, cy + 3, cx + size, cy + 3);
                    g2.drawLine(cx + 3, cy, cx + size - 3, cy);
                    g2.drawLine(cx + 4, cy + 5, cx + 4, cy + size - 2);
                    g2.drawLine(cx + size - 4, cy + 5, cx + size - 4, cy + size - 2);
                    break;
            }
            
            g2.dispose();
        }
    }

    class AuditoriaTableCellRenderer implements TableCellRenderer {
        private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String text = value != null ? value.toString() : "";
            
            JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            cellPanel.setOpaque(false);
            
            if (isSelected) {
                cellPanel.setOpaque(true);
                cellPanel.setBackground(table.getSelectionBackground());
            }
            
            switch (column) {
                case 0: // FECHA
                    JPanel dateCapsule = new JPanel(new BorderLayout()) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(0x22, 0x22, 0x22));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                            g2.dispose();
                        }
                    };
                    dateCapsule.setOpaque(false);
                    dateCapsule.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                    JLabel dateLabel = new JLabel(dateCapsule.getWidth() > 0 ? text : text); // just label
                    dateLabel.setText(text);
                    dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    dateLabel.setForeground(TXT_SECONDARY);
                    dateCapsule.add(dateLabel, BorderLayout.CENTER);
                    cellPanel.add(dateCapsule);
                    return cellPanel;
                    
                case 1: // USUARIO RESPONSABLE
                    JLabel userLabel = new JLabel(text);
                    userLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    userLabel.setForeground(GREEN);
                    cellPanel.add(userLabel);
                    return cellPanel;
                    
                case 2: // MÓDULO
                    JPanel modCapsule = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(0x2c, 0x2c, 0x2c));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                            g2.dispose();
                        }
                    };
                    modCapsule.setOpaque(false);
                    modCapsule.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                    
                    JLabel chainIcon = new JLabel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(TXT_SECONDARY);
                            g2.setStroke(new BasicStroke(1.2f));
                            g2.drawOval(1, 4, 4, 4);
                            g2.drawOval(4, 4, 4, 4);
                            g2.dispose();
                        }
                        @Override
                        public Dimension getPreferredSize() {
                            return new Dimension(10, 12);
                        }
                    };
                    
                    JLabel modLabel = new JLabel(text.toUpperCase());
                    modLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    modLabel.setForeground(Color.WHITE);
                    
                    modCapsule.add(chainIcon);
                    modCapsule.add(modLabel);
                    cellPanel.add(modCapsule);
                    return cellPanel;
                    
                case 3: // ACCIÓN
                    JPanel actCapsule = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)) {
                        @Override
                        protected void paintComponent(Graphics g) {
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
                    
                    JLabel pencilIcon = new JLabel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(GREEN);
                            g2.setStroke(new BasicStroke(1.2f));
                            g2.drawRect(2, 2, 2, 8);
                            g2.drawLine(2, 2, 4, 4);
                            g2.dispose();
                        }
                        @Override
                        public Dimension getPreferredSize() {
                            return new Dimension(8, 12);
                        }
                    };
                    
                    JLabel actLabel = new JLabel(text.toUpperCase());
                    actLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    actLabel.setForeground(GREEN);
                    
                    actCapsule.add(pencilIcon);
                    actCapsule.add(actLabel);
                    cellPanel.add(actCapsule);
                    return cellPanel;
                    
                case 4: // DESCRIPCIÓN
                default:
                    JLabel descLabel = new JLabel(text);
                    descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    descLabel.setForeground(TXT_SECONDARY);
                    cellPanel.add(descLabel);
                    return cellPanel;
            }
        }
    }

    class FichasTableCellRenderer implements TableCellRenderer {
        private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String text = value != null ? value.toString() : "";
            
            JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            cellPanel.setOpaque(false);
            if (isSelected) {
                cellPanel.setOpaque(true);
                cellPanel.setBackground(table.getSelectionBackground());
            }
            
            switch (column) {
                case 0: // NOMBRE
                    JLabel nameLabel = new JLabel(text);
                    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    nameLabel.setForeground(BLUE);
                    nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    cellPanel.add(nameLabel);
                    return cellPanel;
                    
                case 1: // INSTRUCTORES
                case 2: // APRENDICES
                    JPanel countCapsule = new JPanel(new BorderLayout()) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(0x22, 0xc5, 0x5e, 10));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                            g2.setColor(GREEN);
                            g2.setStroke(new BasicStroke(1.0f));
                            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                            g2.dispose();
                        }
                    };
                    countCapsule.setOpaque(false);
                    countCapsule.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
                    JLabel countLabel = new JLabel(text);
                    countLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    countLabel.setForeground(GREEN);
                    countCapsule.add(countLabel, BorderLayout.CENTER);
                    cellPanel.add(countCapsule);
                    return cellPanel;
                    
                case 3: // CÓDIGO
                    JLabel codeLabel = new JLabel(text);
                    codeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    codeLabel.setForeground(TXT_SECONDARY);
                    cellPanel.add(codeLabel);
                    return cellPanel;
                    
                case 4: // ACCIONES
                    JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
                    actionsPanel.setOpaque(false);
                    
                    ActionIconButton btnVer = new ActionIconButton(0, BLUE);
                    ActionIconButton btnEditar = new ActionIconButton(1, TXT_SECONDARY);
                    ActionIconButton btnAsignar = new ActionIconButton(2, GREEN);
                    ActionIconButton btnEliminar = new ActionIconButton(3, new Color(0xef, 0x44, 0x44));
                    
                    actionsPanel.add(btnVer);
                    actionsPanel.add(btnEditar);
                    actionsPanel.add(btnAsignar);
                    actionsPanel.add(btnEliminar);
                    
                    return actionsPanel;
                    
                default:
                    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
    }

    class FichasTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private final JPanel panel;
        private final ActionIconButton btnVer;
        private final ActionIconButton btnEditar;
        private final ActionIconButton btnAsignar;
        private final ActionIconButton btnEliminar;
        private JTable table;
        private int currentRow;
        
        public FichasTableCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            panel.setOpaque(true);
            panel.setBackground(BG_CARD);
            
            btnVer = new ActionIconButton(0, BLUE);
            btnEditar = new ActionIconButton(1, TXT_SECONDARY);
            btnAsignar = new ActionIconButton(2, GREEN);
            btnEliminar = new ActionIconButton(3, new Color(0xef, 0x44, 0x44));
            
            btnVer.addActionListener(this);
            btnEditar.addActionListener(this);
            btnAsignar.addActionListener(this);
            btnEliminar.addActionListener(this);
            
            panel.add(btnVer);
            panel.add(btnEditar);
            panel.add(btnAsignar);
            panel.add(btnEliminar);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            this.currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            fireEditingStopped();
            
            int idCurso = (Integer) table.getModel().getValueAt(currentRow, 5);
            String name = table.getModel().getValueAt(currentRow, 0).toString();
            
            if (src == btnVer) {
                System.out.println("[ACCIONES] Ver Ficha ID: " + idCurso + " - " + name);
            } else if (src == btnEditar) {
                editarFichaAccion(idCurso);
            } else if (src == btnAsignar) {
                asignarFichaAccion(idCurso, name);
            } else if (src == btnEliminar) {
                eliminarFichaAccion(idCurso, name);
            }
        }
    }
}
