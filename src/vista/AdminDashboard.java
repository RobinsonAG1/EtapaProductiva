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

    public AdminDashboard() {
        initComponents();
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

        JPanel brand = new JPanel();
        brand.setBackground(BG_SIDEBAR);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(24, 20, 20, 20));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandTitle = new JLabel("AdminConsole");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel brandSub = new JLabel("GLOBAL PRACTICE MGMT");
        brandSub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        brandSub.setForeground(TXT_DIM);
        brandSub.setHorizontalAlignment(SwingConstants.CENTER);

        brand.add(brandTitle);
        brand.add(Box.createVerticalStrut(4));
        brand.add(brandSub);

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
        contentStack.add(createPlaceholderView("Gesti\u00f3n de Usuarios"), "Usuarios");
        contentStack.add(createPlaceholderView("Administraci\u00f3n de Roles"), "Roles");
        contentStack.add(createPlaceholderView("Gesti\u00f3n de Instructores"), "Instructores");
        contentStack.add(createPlaceholderView("Gesti\u00f3n de Empresas"), "Empresas");
        contentStack.add(createPlaceholderView("Historial del Sistema"), "Historial");
        contentStack.add(createPlaceholderView("Respaldos y Seguridad"), "Backup");

        contentArea.add(contentStack, BorderLayout.CENTER);
        return contentArea;
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

        statsRow.add(new StatCard("TOTAL USUARIOS", "16", MenuIconType.PEOPLE, PURPLE, "Ver Usuarios"));
        statsRow.add(new StatCard("APRENDICES", "12", MenuIconType.BOOK, BLUE, "Ver Usuarios"));
        statsRow.add(new StatCard("INSTRUCTORES", "3", MenuIconType.PERSON, GREEN, "Ver Instructores"));
        statsRow.add(new StatCard("EMPRESAS", "5", MenuIconType.BUILDING, ORANGE, "Ver Empresas"));
        statsRow.add(new StatCard("FICHAS", "1", MenuIconType.GRID, GREEN, "Gestionar Fichas"));

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
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);

        tableModel.addRow(new Object[]{"2026-05-25 13:52:19", "Administrador Sistema SENA", "EMPRESAS", "MODIFICAR", "Empresa 3 editada"});
        tableModel.addRow(new Object[]{"2026-05-25 13:52:03", "Administrador Sistema SENA", "USUARIOS", "MODIFICAR", "Usuario instructor 1 editado"});
        tableModel.addRow(new Object[]{"2026-05-25 13:51:46", "Administrador Sistema SENA", "APRENDICES", "CREAR", "Aprendiz 9 creado"});
        tableModel.addRow(new Object[]{"2026-05-25 13:51:24", "Administrador Sistema SENA", "APRENDICES", "MODIFICAR", "Aprendiz 12 aprobado"});
        tableModel.addRow(new Object[]{"2026-05-25 13:51:07", "Administrador Sistema SENA", "USUARIOS", "ELIMINAR", "Usuario instructor 4 eliminado"});
        tableModel.addRow(new Object[]{"2026-05-25 13:50:47", "Administrador Sistema SENA", "EMPRESAS", "MODIFICAR", "Empresa 2 editada"});
        tableModel.addRow(new Object[]{"2026-05-25 13:50:32", "Administrador Sistema SENA", "INSTRUCTORES", "CREAR", "Instructor nuevo asignado"});
        tableModel.addRow(new Object[]{"2026-05-25 13:50:17", "Administrador Sistema SENA", "USUARIOS", "CREAR", "Usuario administrativo creado"});
        tableModel.addRow(new Object[]{"2026-05-25 13:49:56", "Administrador Sistema SENA", "EMPRESAS", "CREAR", "Nueva empresa registrada"});
        tableModel.addRow(new Object[]{"2026-05-25 13:49:37", "Administrador Sistema SENA", "FICHAS", "MODIFICAR", "Ficha 1 actualizada"});

        JTable table = new JTable(tableModel) {
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
        DefaultTableModel tm = new DefaultTableModel(columns, 0);
        tm.addRow(new Object[]{"FIC-001", "An\u00e1lisis y Desarrollo de Software", "Carlos M\u00e9ndez", "12", "Activo", "2026-02-10", "2027-08-15"});

        JTable t = new JTable(tm) {
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

        StatCard(String title, String value, MenuIconType iconType, Color accent, String linkText) {
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
