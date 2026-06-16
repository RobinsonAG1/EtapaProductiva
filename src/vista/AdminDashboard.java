package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class AdminDashboard extends JInternalFrame {

    private static final Color BG_DARK = new Color(0x0a, 0x0a, 0x0a);
    private static final Color BG_SIDEBAR = new Color(0x14, 0x14, 0x14);
    private static final Color BG_CARD = new Color(0x1a, 0x1a, 0x1a);
    private static final Color GREEN = new Color(0x22, 0xc5, 0x5e);
    private static final Color BLUE = new Color(0x3b, 0x82, 0xf6);
    private static final Color ORANGE = new Color(0xf5, 0x9e, 0x0b);
    private static final Color TXT_SECONDARY = new Color(0x9c, 0xa3, 0xaf);
    private static final Color TXT_DIM = new Color(0x6b, 0x72, 0x80);
    private static final Color BORDER = new Color(0x2a, 0x2a, 0x2a);

    private SidebarButton activeMenu;

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
        setBorder(BorderFactory.createLineBorder(new Color(0x2a, 0x2a, 0x2a)));

        // Apply modern UI for title bar
        setUI(new ModernInternalFrameUI(this));

        setSize(1100, 700);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createContent(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Brand
        JPanel brand = new JPanel();
        brand.setBackground(BG_SIDEBAR);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(20, 16, 20, 16));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setMaximumSize(new Dimension(200, 80));

        JLabel brandTitle = new JLabel("AdminConsole");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandSub = new JLabel("GLOBAL PRACTICE MGMT");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        brandSub.setForeground(TXT_DIM);
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(brandTitle);
        brand.add(Box.createVerticalStrut(2));
        brand.add(brandSub);

        sidebar.add(brand);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));

        // Separator line
        JSeparator sep1 = new JSeparator();
        sep1.setMaximumSize(new Dimension(200, 1));
        sep1.setForeground(BORDER);
        sidebar.add(sep1);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));

        // Menu
        String[] labels = {"Panel", "Fichas/Cursos", "Usuarios", "Roles", "Instructores", "Empresas", "Historial", "Backup"};
        MenuIconType[] types = {MenuIconType.GRID, MenuIconType.BOOK, MenuIconType.PEOPLE, MenuIconType.SHIELD,
                MenuIconType.PERSON, MenuIconType.BUILDING, MenuIconType.CLOCK, MenuIconType.CLOUD};

        for (int i = 0; i < labels.length; i++) {
            SidebarButton btn = new SidebarButton(labels[i], types[i]);
            if (i == 0) {
                btn.setActive(true);
                activeMenu = btn;
            }
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());

        // Separator before user
        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(200, 1));
        sep2.setForeground(BORDER);
        sidebar.add(sep2);

        // User panel
        JPanel userPanel = new JPanel();
        userPanel.setBackground(BG_SIDEBAR);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.X_AXIS));
        userPanel.setBorder(new EmptyBorder(14, 16, 14, 16));
        userPanel.setMaximumSize(new Dimension(200, 60));
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.setOpaque(true);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN);
                g2.fillOval(0, 0, 32, 32);
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("A", (32 - fm.stringWidth("A")) / 2, (32 + fm.getAscent() / 2) - 2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setMaximumSize(new Dimension(32, 32));
        avatar.setMinimumSize(new Dimension(32, 32));
        avatar.setBackground(BG_SIDEBAR);

        JPanel userInfo = new JPanel();
        userInfo.setBackground(BG_SIDEBAR);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));

        JLabel userName = new JLabel("Administrador");
        userName.setFont(new Font("Segoe UI", Font.BOLD, 11));
        userName.setForeground(Color.WHITE);

        JLabel userRole = new JLabel("System Practices");
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        userRole.setForeground(TXT_DIM);

        userInfo.add(userName);
        userInfo.add(Box.createVerticalStrut(1));
        userInfo.add(userRole);

        userPanel.add(avatar);
        userPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        userPanel.add(userInfo);

        sidebar.add(userPanel);

        return sidebar;
    }

    private JPanel createContent() {
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_DARK);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setBackground(BG_DARK);
        header.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel headerTitle = new JLabel("\uD83D\uDEE1\uFE0F Panel de Administraci\u00f3n Global");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTitle.setForeground(Color.WHITE);

        header.add(headerTitle);
        contentArea.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel();
        body.setBackground(BG_DARK);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Stats row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        statsRow.setBackground(BG_DARK);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsRow.add(new StatCard("TOTAL USUARIOS", BLUE, "Ver Usuarios"));
        statsRow.add(new StatCard("APRENDICES", BLUE, "Ver Usuarios"));
        statsRow.add(new StatCard("INSTRUCTORES", GREEN, "Ver Instructores"));
        statsRow.add(new StatCard("EMPRESAS", ORANGE, "Ver Empresas"));
        statsRow.add(new StatCard("FICHAS", GREEN, "Gestionar Fichas"));

        body.add(statsRow);
        body.add(Box.createRigidArea(new Dimension(0, 24)));

        // Activity section
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(BG_CARD);
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Activity header
        JPanel activityHeader = new JPanel(new BorderLayout());
        activityHeader.setBackground(BG_CARD);
        activityHeader.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel activityTitle = new JLabel("ACTIVIDAD RECIENTE DEL SISTEMA (AUDITOR\u00cdA)");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        activityTitle.setForeground(Color.WHITE);

        JButton verHistorial = new JButton("Ver historial completo");
        verHistorial.setFont(new Font("Segoe UI", Font.BOLD, 10));
        verHistorial.setForeground(GREEN);
        verHistorial.setBackground(BG_CARD);
        verHistorial.setBorder(BorderFactory.createLineBorder(GREEN, 1));
        verHistorial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        verHistorial.setFocusPainted(false);
        verHistorial.setPreferredSize(new Dimension(160, 28));
        verHistorial.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Historial completo pr\u00f3ximamente.");
        });

        activityHeader.add(activityTitle, BorderLayout.WEST);
        activityHeader.add(verHistorial, BorderLayout.EAST);

        activityPanel.add(activityHeader, BorderLayout.NORTH);

        // Table
        String[] columns = {"FECHA", "USUARIO RESPONSABLE", "M\u00d3DULO", "ACCI\u00d3N", "DESCRIPCI\u00d3N"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
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
                lbl.setBackground(BG_CARD);
                lbl.setForeground(TXT_SECONDARY);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        BorderFactory.createEmptyBorder(10, 8, 10, 8)));
                lbl.setHorizontalAlignment(LEFT);
                return lbl;
            }
        });
        table.getTableHeader().setBackground(BG_CARD);
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
        contentArea.add(body, BorderLayout.CENTER);

        return contentArea;
    }

    // ── Inner classes ──────────────────────────────────────────

    enum MenuIconType { GRID, BOOK, PEOPLE, SHIELD, PERSON, BUILDING, CLOCK, CLOUD }

    class SidebarButton extends JPanel {

        private final String menuText;
        private final MenuIconType iconType;
        private boolean active;
        private boolean hover;

        SidebarButton(String text, MenuIconType iconType) {
            this.menuText = text;
            this.iconType = iconType;
            setBackground(BG_SIDEBAR);
            setLayout(null);
            setPreferredSize(new Dimension(200, 36));
            setMaximumSize(new Dimension(200, 36));
            setMinimumSize(new Dimension(200, 36));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!active) {
                        if (activeMenu != null) activeMenu.setActive(false);
                        setActive(true);
                        activeMenu = SidebarButton.this;
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

            // Full clear background
            g2.setColor(BG_SIDEBAR);
            g2.fillRect(0, 0, w, h);

            // Background highlight
            if (active || hover) {
                if (active) {
                    g2.setColor(new Color(0x22, 0xc5, 0x5e, 40));
                } else {
                    g2.setColor(new Color(0xff, 0xff, 0xff, 12));
                }
                g2.fillRoundRect(4, 2, w - 8, h - 4, 6, 6);
            }

            // Active left bar
            if (active) {
                g2.setColor(GREEN);
                g2.fillRoundRect(0, 6, 3, h - 12, 2, 2);
            }

            // Icon
            Color iconColor = active ? GREEN : (hover ? Color.WHITE : TXT_SECONDARY);
            int iconX = 16;
            int iconY = (h - 16) / 2;
            paintMenuIcon(g2, iconType, iconX, iconY, 16, iconColor);

            // Text
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

        StatCard(String title, Color accent, String linkText) {
            setBackground(BG_CARD);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(16, 16, 14, 16));
            setPreferredSize(new Dimension(175, 130));

            JPanel iconCircle = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25));
                    g2.fillOval(0, 0, 32, 32);
                    g2.setColor(accent);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(0, 0, 32, 32);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    String ini = title.substring(0, 1);
                    g2.drawString(ini, (32 - fm.stringWidth(ini)) / 2, (32 + fm.getAscent() / 2) - 2);
                    g2.dispose();
                }
            };
            iconCircle.setPreferredSize(new Dimension(32, 32));
            iconCircle.setMaximumSize(new Dimension(32, 32));
            iconCircle.setBackground(BG_CARD);
            iconCircle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
            titleLabel.setForeground(TXT_SECONDARY);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel valueLabel = new JLabel("0");
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
            add(Box.createRigidArea(new Dimension(0, 8)));
            add(titleLabel);
            add(Box.createRigidArea(new Dimension(0, 2)));
            add(valueLabel);
            add(Box.createVerticalGlue());
            add(linkLabel);
        }
    }
}
