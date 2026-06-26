package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.Notificacion;

public class AprendizDashboard extends JInternalFrame {

    private static final Color BG = new Color(0x0a, 0x0a, 0x0a);
    private static final Color SIDEBAR_BG = new Color(0x14, 0x14, 0x14);
    private static final Color CARD_BG = new Color(0x11, 0x11, 0x11);
    private static final Color BORDER = new Color(0x22, 0x22, 0x22);
    private static final Color GREEN = new Color(0x22, 0xc5, 0x5e);
    private static final Color BLUE = new Color(0x3b, 0x82, 0xf6);
    private static final Color PURPLE = new Color(0xa8, 0x55, 0xf7);
    private static final Color ORANGE = new Color(0xf5, 0x9e, 0x0b);
    private static final Color TXT = new Color(0x9c, 0xa3, 0xaf);
    private static final Color TXT_DIM = new Color(0x6b, 0x72, 0x80);
    private static final Color TXT_BRIGHT = new Color(0xf0, 0xf0, 0xf0);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BOLD_11 = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FONT_BOLD_9 = new Font("Segoe UI", Font.BOLD, 9);
    private static final Font FONT_PLAIN_12 = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_PLAIN_11 = new Font("Segoe UI", Font.PLAIN, 11);

    private CardLayout contentLayout;
    private JPanel contentStack;

    public AprendizDashboard() {
        setClosable(false);
        setMaximizable(false);
        setIconifiable(false);
        setBorder(BorderFactory.createEmptyBorder());
        setFrameIcon(null);
        setTitle("Panel del Aprendiz");
        try { setUI(new ModernInternalFrameUI(this)); } catch (Exception ignored) {}

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        setContentPane(main);

        main.add(createSidebar(), BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentStack = new JPanel(contentLayout);
        contentStack.setBackground(BG);
        contentStack.add(createDashboardPanel(), "Panel");
        contentStack.add(createSubirEvidenciasView(), "Subir evidencias");
        contentStack.add(createMiProgresoView(), "Mi progreso");
        contentStack.add(createMiInfoView(), "Mi informaci\u00f3n");
        contentStack.add(createNotificacionesView(), "Notificaciones");
        contentStack.add(createMisEvidenciasView(), "Mis evidencias");

        main.add(contentStack, BorderLayout.CENTER);
        setBackground(BG);
    }

    // ═══════════════════════ SIDEBAR ═══════════════════════
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER);
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // ── Brand ──
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(20, 16, 16, 16));
        brand.setMaximumSize(new Dimension(220, 70));

        JLabel brandTitle = new JLabel("AdminConsole");
        brandTitle.setFont(FONT_BOLD_14);
        brandTitle.setForeground(TXT_BRIGHT);
        brandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandSub = new JLabel("GLOBAL PRACTICE MGMT");
        brandSub.setFont(FONT_BOLD_9);
        brandSub.setForeground(TXT_DIM);
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(brandTitle);
        brand.add(Box.createVerticalStrut(2));
        brand.add(brandSub);
        sidebar.add(brand);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));

        // ── Menu items ──
        String[] labels = {"Panel", "Subir evidencias", "Mi progreso", "Mi informaci\u00f3n", "Notificaciones", "Mis evidencias"};
        MenuIconType[] icons = {MenuIconType.GRID, MenuIconType.UPLOAD, MenuIconType.CHART, MenuIconType.PERSON, MenuIconType.BELL, MenuIconType.FOLDER};
        SidebarButton[] allBtns = new SidebarButton[labels.length];

        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            allBtns[idx] = new SidebarButton(labels[i], icons[i], () -> {
                contentLayout.show(contentStack, labels[idx]);
                for (SidebarButton b : allBtns) {
                    if (b != null) b.setActive(b == allBtns[idx]);
                }
            });
            if (i == 0) allBtns[0].setActive(true);
            sidebar.add(allBtns[i]);
        }

        sidebar.add(Box.createVerticalGlue());

        // ── User pill ──
        JPanel userPill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1a, 0x1a, 0x1a));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(0x2a, 0x2a, 0x2a));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        userPill.setOpaque(false);
        userPill.setLayout(new BorderLayout(8, 0));
        userPill.setBorder(new EmptyBorder(8, 10, 8, 10));
        userPill.setMaximumSize(new Dimension(200, 48));
        userPill.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPill.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN);
                g2.fillOval(0, 0, 30, 30);
                g2.setColor(BG);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(10, 4, 10, 10);
                g2.drawLine(15, 14, 15, 22);
                g2.drawLine(9, 17, 15, 14);
                g2.drawLine(21, 17, 15, 14);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(30, 30));
        avatar.setOpaque(false);

        JPanel userInfo = new JPanel();
        userInfo.setOpaque(false);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));

        JLabel userName = new JLabel(controlador.Sesion.getNombreCompleto());
        userName.setFont(FONT_BOLD_11);
        userName.setForeground(TXT_BRIGHT);

        JLabel userRole = new JLabel("System Practices");
        userRole.setFont(FONT_PLAIN_11);
        userRole.setForeground(TXT_DIM);

        userInfo.add(userName);
        userInfo.add(Box.createVerticalStrut(1));
        userInfo.add(userRole);

        JLabel arrow = new JLabel("\u25BC");
        arrow.setFont(new Font("Segoe UI", Font.PLAIN, 8));
        arrow.setForeground(TXT_DIM);
        arrow.setHorizontalAlignment(SwingConstants.CENTER);

        userPill.add(avatar, BorderLayout.WEST);
        userPill.add(userInfo, BorderLayout.CENTER);
        userPill.add(arrow, BorderLayout.EAST);

        // ── Logout panel (hidden, expands on pill click) ──
        JPanel logoutPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1a, 0x1a, 0x1a));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0x2a, 0x2a, 0x2a));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        logoutPanel.setOpaque(false);
        logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.Y_AXIS));
        logoutPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
        logoutPanel.setMaximumSize(new Dimension(200, 0));
        logoutPanel.setMinimumSize(new Dimension(200, 0));
        logoutPanel.setPreferredSize(new Dimension(200, 0));
        logoutPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton logoutBtn = new JButton("Cerrar sesi\u00f3n") {
            private final Color normalFg = new Color(0xef, 0x44, 0x44);
            private final Color hoverBg = new Color(0x2a, 0x1a, 0x1a);
            private final Color hoverFg = new Color(0xf8, 0x71, 0x71);
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2.setColor(getModel().isPressed() ? hoverBg.darker() : hoverBg);
                    g2.fillRoundRect(0, 0, w, h, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setOpaque(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        logoutBtn.setForeground(new Color(0xef, 0x44, 0x44));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setMaximumSize(new Dimension(200, 36));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setBorder(new EmptyBorder(0, 8, 0, 0));
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { logoutBtn.repaint(); }
            @Override public void mouseExited(MouseEvent e) { logoutBtn.repaint(); }
        });
        logoutBtn.addActionListener(e -> {
            java.awt.Window w = SwingUtilities.getWindowAncestor(logoutBtn);
            if (w instanceof MDI) ((MDI) w).cerrarSesion();
        });
        logoutPanel.add(logoutBtn);

        // ── Expand/collapse animation ──
        final boolean[] menuOpen = {false};
        final int TARGET_H = 44;
        final int ANIM_MS = 200;

        userPill.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                menuOpen[0] = !menuOpen[0];
                int startH = logoutPanel.getHeight();
                int endH = menuOpen[0] ? TARGET_H : 0;
                arrow.setText(menuOpen[0] ? "\u25B2" : "\u25BC");

                javax.swing.Timer anim = new javax.swing.Timer(16, null);
                final long t0 = System.currentTimeMillis();
                anim.addActionListener(ev -> {
                    long elapsed = System.currentTimeMillis() - t0;
                    float t = Math.min(1f, elapsed / (float) ANIM_MS);
                    t = t < 0.5f ? 2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
                    int curH = startH + (int) ((endH - startH) * t);
                    logoutPanel.setMaximumSize(new Dimension(200, curH));
                    logoutPanel.setMinimumSize(new Dimension(200, curH));
                    logoutPanel.setPreferredSize(new Dimension(200, curH));
                    logoutPanel.revalidate();
                    logoutPanel.repaint();
                    if (t >= 1f) anim.stop();
                });
                anim.setInitialDelay(0);
                anim.start();
            }
        });

        JPanel pillWrapper = new JPanel();
        pillWrapper.setLayout(new BoxLayout(pillWrapper, BoxLayout.Y_AXIS));
        pillWrapper.setOpaque(false);
        pillWrapper.setBorder(new EmptyBorder(8, 10, 16, 10));
        pillWrapper.setMaximumSize(new Dimension(220, 110));
        pillWrapper.add(userPill);
        pillWrapper.add(Box.createRigidArea(new Dimension(0, 4)));
        pillWrapper.add(logoutPanel);
        sidebar.add(pillWrapper);

        return sidebar;
    }

    private void setActiveButton(SidebarButton active) {
        for (Component c : active.getParent().getComponents()) {
            if (c instanceof SidebarButton) ((SidebarButton) c).setActive(c == active);
        }
    }

    // ═══════════════════════ DASHBOARD PANEL ═══════════════════════
    private JPanel createDashboardPanel() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 24, 24, 24));

        // ── Header bar ──
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setLayout(new BorderLayout(16, 0));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel headerTitle = new JLabel("Panel Central \u2014 Aprendiz");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerTitle.setForeground(TXT_BRIGHT);
        header.add(headerTitle, BorderLayout.WEST);

        JButton btnNuevaEvidencia = createGreenButton("\u2601  Nueva Evidencia");
        btnNuevaEvidencia.addActionListener(e -> contentLayout.show(contentStack, "Subir evidencias"));
        header.add(btnNuevaEvidencia, BorderLayout.EAST);

        body.add(header, BorderLayout.NORTH);

        // ── Center: cards + evidencias ──
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        // ── Stats row ──
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        statsRow.setPreferredSize(new Dimension(0, 180));

        statsRow.add(createStatCard("PROGRESO\nTIEMPO", "0%", "De 6 meses proyectados", BLUE, 0));
        statsRow.add(createStatCard("EVIDENCIAS", "0 / 12", "Avance: 0.0%", PURPLE, 0));
        statsRow.add(createAlertCard());
        statsRow.add(createEstadoCard());

        center.add(statsRow);
        center.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Evidencias recientes ──
        center.add(createEvidenciasRecientes());

        body.add(center, BorderLayout.CENTER);
        return body;
    }

    // ═══════════════════════ STAT CARDS ═══════════════════════
    private JPanel createStatCard(String title, String value, String subtitle, Color accent, int progressPct) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Top row: title + icon
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel titleLbl = new JLabel("<html>" + title.replace("\n", "<br>") + "</html>");
        titleLbl.setFont(FONT_BOLD_9);
        titleLbl.setForeground(TXT_DIM);
        topRow.add(titleLbl, BorderLayout.WEST);

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
                g2.translate(8, 8);
                drawMiniIcon(g2, accent, 16);
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(32, 32));
        topRow.add(iconCircle, BorderLayout.EAST);

        card.add(topRow);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // Value
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLbl.setForeground(TXT_BRIGHT);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(valueLbl);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        // Subtitle
        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(FONT_PLAIN_11);
        subLbl.setForeground(TXT_DIM);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subLbl);

        // Progress bar
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel barBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x22, 0x22, 0x22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                if (progressPct > 0) {
                    g2.setColor(accent);
                    g2.fillRoundRect(0, 0, (int)(getWidth() * progressPct / 100.0), getHeight(), 4, 4);
                }
                g2.dispose();
            }
        };
        barBg.setOpaque(false);
        barBg.setPreferredSize(new Dimension(0, 4));
        barBg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        barBg.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(barBg);

        return card;
    }

    private JPanel createAlertCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel titleLbl = new JLabel("ALERTAS");
        titleLbl.setFont(FONT_BOLD_9);
        titleLbl.setForeground(TXT_DIM);
        topRow.add(titleLbl, BorderLayout.WEST);

        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(ORANGE.getRed(), ORANGE.getGreen(), ORANGE.getBlue(), 25));
                g2.fillOval(0, 0, 32, 32);
                g2.setColor(ORANGE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(0, 0, 32, 32);
                // Bell icon
                g2.translate(8, 8);
                g2.drawArc(2, 2, 12, 12, 200, 140);
                g2.drawLine(2, 10, 14, 10);
                g2.drawArc(5, 12, 6, 4, 0, 180);
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(32, 32));
        topRow.add(iconCircle, BorderLayout.EAST);

        card.add(topRow);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel valueLbl = new JLabel("0");
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLbl.setForeground(TXT_BRIGHT);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(valueLbl);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel subLbl = new JLabel("Mensajes sin leer");
        subLbl.setFont(FONT_PLAIN_11);
        subLbl.setForeground(TXT_DIM);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subLbl);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // "Ver alertas" link button
        JButton verAlertas = new JButton("Ver alertas");
        verAlertas.setFont(FONT_BOLD_11);
        verAlertas.setForeground(GREEN);
        verAlertas.setBackground(new Color(0x1a, 0x1a, 0x1a));
        verAlertas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        verAlertas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        verAlertas.setFocusPainted(false);
        verAlertas.setAlignmentX(Component.LEFT_ALIGNMENT);
        verAlertas.setMaximumSize(new Dimension(140, 34));
        verAlertas.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "No tienes alertas nuevas."));
        card.add(verAlertas);

        return card;
    }

    private JPanel createEstadoCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel titleLbl = new JLabel("ESTADO");
        titleLbl.setFont(FONT_BOLD_9);
        titleLbl.setForeground(TXT_DIM);
        topRow.add(titleLbl, BorderLayout.WEST);

        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(GREEN.getRed(), GREEN.getGreen(), GREEN.getBlue(), 25));
                g2.fillOval(0, 0, 32, 32);
                g2.setColor(GREEN);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(0, 0, 32, 32);
                // Check icon
                g2.translate(8, 8);
                g2.drawArc(2, 2, 12, 12, 0, 360);
                g2.drawLine(6, 10, 10, 13);
                g2.drawLine(10, 13, 15, 5);
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(32, 32));
        topRow.add(iconCircle, BorderLayout.EAST);

        card.add(topRow);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel valueLbl = new JLabel("En proceso");
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLbl.setForeground(GREEN);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(valueLbl);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel fichaLbl = new JLabel("Ficha: N/A");
        fichaLbl.setFont(FONT_PLAIN_11);
        fichaLbl.setForeground(TXT_DIM);
        fichaLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(fichaLbl);

        return card;
    }

    // ═══════════════════════ EVIDENCIAS RECIENTES ═══════════════════════
    private JPanel createEvidenciasRecientes() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 0));
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Title row
        JPanel titleRow = new JPanel(new BorderLayout(12, 0));
        titleRow.setOpaque(false);

        JPanel docIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1a, 0x1a, 0x1a));
                g2.fillRoundRect(0, 0, 36, 36, 8, 8);
                g2.setColor(TXT_DIM);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(4, 2, 28, 32, 4, 4);
                g2.drawLine(8, 12, 28, 12);
                g2.drawLine(8, 18, 28, 18);
                g2.drawLine(8, 24, 22, 24);
                g2.dispose();
            }
        };
        docIcon.setOpaque(false);
        docIcon.setPreferredSize(new Dimension(36, 36));
        titleRow.add(docIcon, BorderLayout.WEST);

        JLabel sectionTitle = new JLabel("EVIDENCIAS SUBIDAS RECIENTEMENTE");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sectionTitle.setForeground(TXT_BRIGHT);
        titleRow.add(sectionTitle, BorderLayout.CENTER);

        panel.add(titleRow, BorderLayout.NORTH);

        // Empty state
        JPanel emptyState = new JPanel(new GridBagLayout());
        emptyState.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel folderIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TXT_DIM);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(10, 8, 40, 6, 4, 4);
                g2.drawRoundRect(4, 14, 52, 38, 6, 6);
                g2.dispose();
            }
        };
        folderIcon.setOpaque(false);
        folderIcon.setPreferredSize(new Dimension(60, 56));
        gbc.gridx = 0; gbc.gridy = 0;
        emptyState.add(folderIcon, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(12, 0, 0, 0);
        JLabel emptyText = new JLabel("No tienes evidencias recientes.");
        emptyText.setFont(FONT_PLAIN_12);
        emptyText.setForeground(TXT_DIM);
        emptyState.add(emptyText, gbc);

        panel.add(emptyState, BorderLayout.CENTER);
        return panel;
    }

    // ═══════════════════════ HELPERS ═══════════════════════
    private JButton createGreenButton(String text) {
        int btnR = 12;
        JButton btn = new JButton(text) {
            private final Color startColor = GREEN;
            private final Color endColor = GREEN.darker();
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(); int h = getHeight();
                Color cS = getModel().isPressed() ? startColor.darker() : getModel().isRollover() ? startColor.brighter() : startColor;
                Color cE = getModel().isPressed() ? endColor.darker() : getModel().isRollover() ? startColor : endColor;
                g2.setPaint(new LinearGradientPaint(0, 0, w, 0, new float[]{0f, 1f}, new Color[]{cS, cE}));
                g2.fillRoundRect(0, 0, w, h, btnR, btnR);
                g2.setColor(new Color(255, 255, 255, 45));
                g2.drawRoundRect(0, 0, w - 1, h - 1, btnR, btnR);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(FONT_BOLD_11);
        btn.setForeground(Color.BLACK);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 38));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e) { btn.repaint(); }
        });
        return btn;
    }

    // ═══════════════════════ SUBIR EVIDENCIAS ═══════════════════════
    private JPanel createSubirEvidenciasView() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 24, 24, 24));

        // ── Header bar ──
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setLayout(new BorderLayout(16, 0));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerLeft.setOpaque(false);
        JLabel cloudIcon = new JLabel("\u2601");
        cloudIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cloudIcon.setForeground(GREEN);
        JLabel headerTitle = new JLabel("Subir Evidencia");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerTitle.setForeground(TXT_BRIGHT);
        headerLeft.add(cloudIcon);
        headerLeft.add(headerTitle);
        header.add(headerLeft, BorderLayout.WEST);

        int btnR2 = 12;
        JButton btnVolver = new JButton("\u2190  Volver a mis evidencias") {
            private final Color startColor = GREEN;
            private final Color endColor = GREEN.darker();
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                Color cS = getModel().isPressed() ? startColor.darker() : getModel().isRollover() ? startColor.brighter() : startColor;
                Color cE = getModel().isPressed() ? endColor.darker() : getModel().isRollover() ? startColor : endColor;
                g2.setPaint(new LinearGradientPaint(0, 0, w, 0, new float[]{0f, 1f}, new Color[]{cS, cE}));
                g2.fillRoundRect(0, 0, w, h, btnR2, btnR2);
                g2.setColor(new Color(255, 255, 255, 45));
                g2.drawRoundRect(0, 0, w - 1, h - 1, btnR2, btnR2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnVolver.setOpaque(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setFont(FONT_BOLD_11);
        btnVolver.setForeground(Color.BLACK);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnVolver.repaint(); }
            @Override public void mouseExited(MouseEvent e) { btnVolver.repaint(); }
        });
        btnVolver.addActionListener(e -> contentLayout.show(contentStack, "Mis evidencias"));
        header.add(btnVolver, BorderLayout.EAST);

        body.add(header, BorderLayout.NORTH);

        // ── Center scrollable form ──
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        // ── Progress card ──
        center.add(createProgressCard());
        center.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Details form card ──
        center.add(createDetailsFormCard());
        center.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Footer ──
        JLabel footer = new JLabel("\uD83C\uDF93  SENA Pr\u00e1cticas \u00a9 2026");
        footer.setFont(FONT_PLAIN_11);
        footer.setForeground(TXT_DIM);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(footer);

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        body.add(scroll, BorderLayout.CENTER);
        return body;
    }

    private JPanel createProgressCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 24, 20, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Top row: title + percentage badge
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Progreso de Evidencias");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TXT_BRIGHT);
        topRow.add(title, BorderLayout.WEST);

        JLabel badge = new JLabel(" 0.0% ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setPreferredSize(new Dimension(70, 28));
        badge.setBorder(new EmptyBorder(0, 0, 0, 0));
        topRow.add(badge, BorderLayout.EAST);

        card.add(topRow);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        // Progress bar
        JPanel barBg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x22, 0x22, 0x22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
            }
        };
        barBg.setOpaque(false);
        barBg.setPreferredSize(new Dimension(0, 10));
        barBg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        barBg.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(barBg);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // Info text
        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        infoRow.setOpaque(false);
        infoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel infoIcon = new JLabel("\u24D8");
        infoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoIcon.setForeground(TXT_DIM);
        JLabel infoText = new JLabel("Sube un total de 12 evidencias de calidad");
        infoText.setFont(FONT_PLAIN_12);
        infoText.setForeground(TXT_DIM);
        infoRow.add(infoIcon);
        infoRow.add(infoText);
        card.add(infoRow);

        return card;
    }

    private JPanel createDetailsFormCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 24, 20, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 480));

        // Title row with icon
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel uploadIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1a, 0x1a, 0x1a));
                g2.fillRoundRect(0, 0, 36, 36, 8, 8);
                g2.setColor(GREEN);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(18, 8, 18, 26);
                g2.drawLine(18, 8, 12, 14);
                g2.drawLine(18, 8, 24, 14);
                g2.drawLine(8, 28, 28, 28);
                g2.dispose();
            }
        };
        uploadIcon.setOpaque(false);
        uploadIcon.setPreferredSize(new Dimension(36, 36));

        JLabel sectionTitle = new JLabel("DETALLES DE LA EVIDENCIA");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionTitle.setForeground(TXT_BRIGHT);

        titleRow.add(uploadIcon);
        titleRow.add(sectionTitle);
        card.add(titleRow);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(BORDER);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(sep);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Tipo de evidencia field
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        labelRow.setOpaque(false);
        JLabel tipoLabel = new JLabel("Tipo de evidencia");
        tipoLabel.setFont(FONT_BOLD_11);
        tipoLabel.setForeground(TXT_BRIGHT);
        JLabel required = new JLabel(" *");
        required.setFont(FONT_BOLD_11);
        required.setForeground(new Color(0xef, 0x44, 0x44));
        labelRow.add(tipoLabel);
        labelRow.add(required);
        fieldPanel.add(labelRow);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        // Combo box styled
        String[] tipos = {"Selecciona una opci\u00f3n...", "Informe de avance", "Captura de pantalla", "Foto del proceso", "Documento PDF", "Video de evidencia"};
        JComboBox<String> comboTipos = new JComboBox<>(tipos);
        comboTipos.setFont(FONT_PLAIN_12);
        comboTipos.setForeground(TXT);
        comboTipos.setBackground(new Color(0x1a, 0x1a, 0x1a));
        comboTipos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        comboTipos.setPreferredSize(new Dimension(0, 38));
        comboTipos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        comboTipos.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboTipos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fieldPanel.add(comboTipos);

        card.add(fieldPanel);

        // ── Archivo upload field ──
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
        filePanel.setOpaque(false);
        filePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fileLabelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        fileLabelRow.setOpaque(false);
        JLabel fileLabel = new JLabel("Archivo de evidencia");
        fileLabel.setFont(FONT_BOLD_11);
        fileLabel.setForeground(TXT_BRIGHT);
        JLabel fileRequired = new JLabel(" *");
        fileRequired.setFont(FONT_BOLD_11);
        fileRequired.setForeground(new Color(0xef, 0x44, 0x44));
        fileLabelRow.add(fileLabel);
        fileLabelRow.add(fileRequired);
        filePanel.add(fileLabelRow);
        filePanel.add(Box.createRigidArea(new Dimension(0, 6)));

        final JLabel[] fileNameHolder = {new JLabel("Ning\u00fan archivo seleccionado")};
        fileNameHolder[0].setFont(FONT_PLAIN_12);
        fileNameHolder[0].setForeground(TXT_DIM);

        final JFileChooser[] fileChooserHolder = {new JFileChooser()};

        JPanel uploadArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x14, 0x14, 0x14));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0x33, 0x33, 0x33));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{6, 4}, 0));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
                g2.dispose();
            }
        };
        uploadArea.setOpaque(false);
        uploadArea.setLayout(new BorderLayout(12, 0));
        uploadArea.setBorder(new EmptyBorder(14, 16, 14, 16));
        uploadArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        uploadArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        uploadArea.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel upIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawLine(10, 4, 10, 18);
                g2.drawLine(10, 4, 5, 9);
                g2.drawLine(10, 4, 15, 9);
                g2.drawLine(2, 20, 18, 20);
                g2.dispose();
            }
        };
        upIcon.setOpaque(false);
        upIcon.setPreferredSize(new Dimension(20, 22));

        JPanel fileInfo = new JPanel();
        fileInfo.setOpaque(false);
        fileInfo.setLayout(new BoxLayout(fileInfo, BoxLayout.Y_AXIS));
        JLabel dragLabel = new JLabel("Haz clic para seleccionar un archivo");
        dragLabel.setFont(FONT_PLAIN_12);
        dragLabel.setForeground(TXT);
        fileInfo.add(dragLabel);
        fileInfo.add(fileNameHolder[0]);
        uploadArea.add(upIcon, BorderLayout.WEST);
        uploadArea.add(fileInfo, BorderLayout.CENTER);

        uploadArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int result = fileChooserHolder[0].showOpenDialog(AprendizDashboard.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    java.io.File f = fileChooserHolder[0].getSelectedFile();
                    fileNameHolder[0].setText(f.getName());
                    fileNameHolder[0].setForeground(TXT_BRIGHT);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { uploadArea.repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { uploadArea.repaint(); }
        });

        filePanel.add(uploadArea);
        card.add(filePanel);

        // Separator before button
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setForeground(BORDER);
        sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep2);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        // Enviar button
        int btnR = 12;
        JButton btnEnviar = new JButton("\u2708  Enviar Evidencia") {
            private final Color startColor = GREEN;
            private final Color endColor = GREEN.darker();
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                Color cS = getModel().isPressed() ? startColor.darker() : getModel().isRollover() ? startColor.brighter() : startColor;
                Color cE = getModel().isPressed() ? endColor.darker() : getModel().isRollover() ? startColor : endColor;
                g2.setPaint(new LinearGradientPaint(0, 0, w, 0, new float[]{0f, 1f}, new Color[]{cS, cE}));
                g2.fillRoundRect(0, 0, w, h, btnR, btnR);
                g2.setColor(new Color(255, 255, 255, 45));
                g2.drawRoundRect(0, 0, w - 1, h - 1, btnR, btnR);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnEnviar.setOpaque(false);
        btnEnviar.setContentAreaFilled(false);
        btnEnviar.setBorderPainted(false);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEnviar.setForeground(Color.BLACK);
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnviar.setPreferredSize(new Dimension(200, 44));
        btnEnviar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnEnviar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnEnviar.repaint(); }
            @Override public void mouseExited(MouseEvent e) { btnEnviar.repaint(); }
        });
        btnEnviar.addActionListener(e -> {
            int selectedIdx = comboTipos.getSelectedIndex();
            if (selectedIdx == 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un tipo de evidencia.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (fileNameHolder[0].getText().equals("Ning\u00fan archivo seleccionado")) {
                JOptionPane.showMessageDialog(this, "Selecciona un archivo de evidencia.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Evidencia enviada correctamente.\nTipo: " + comboTipos.getSelectedItem()
                    + "\nArchivo: " + fileNameHolder[0].getText(),
                    "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
            comboTipos.setSelectedIndex(0);
            fileNameHolder[0].setText("Ning\u00fan archivo seleccionado");
            fileNameHolder[0].setForeground(TXT_DIM);
        });

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrapper.setOpaque(false);
        btnWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnWrapper.add(btnEnviar);
        card.add(btnWrapper);

        return card;
    }

    // ═══════════════════════ MI PROGRESO ═══════════════════════
    private JPanel createMiProgresoView() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header
        JPanel header = createSectionHeader("\uD83D\uDCC8", "Mi Progreso");
        body.add(header, BorderLayout.NORTH);

        // Two cards side by side
        JPanel cardsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        cardsRow.setOpaque(false);

        // ── Left card: PROGRESO DEL SEMESTRE ──
        JPanel leftCard = createGlassCard();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));
        leftCard.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Title row with icon
        JPanel leftTitleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftTitleRow.setOpaque(false);
        leftTitleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel clockIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x0d, 0x94, 0x88, 30));
                g2.fillRoundRect(0, 0, 36, 36, 8, 8);
                g2.setColor(new Color(0x0d, 0x94, 0x88));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(6, 4, 24, 24);
                g2.drawLine(18, 10, 18, 18);
                g2.drawLine(18, 18, 24, 22);
                g2.dispose();
            }
        };
        clockIcon.setOpaque(false);
        clockIcon.setPreferredSize(new Dimension(36, 36));

        JLabel leftTitle = new JLabel("PROGRESO DEL SEMESTRE");
        leftTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        leftTitle.setForeground(TXT_BRIGHT);

        leftTitleRow.add(clockIcon);
        leftTitleRow.add(leftTitle);
        leftCard.add(leftTitleRow);

        // Separator
        JSeparator sepL = new JSeparator();
        sepL.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sepL.setForeground(BORDER);
        sepL.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCard.add(Box.createRigidArea(new Dimension(0, 12)));
        leftCard.add(sepL);
        leftCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Big percentage
        final JLabel leftPct = new JLabel("0%");
        leftPct.setFont(new Font("Segoe UI", Font.BOLD, 48));
        leftPct.setForeground(TXT_BRIGHT);
        leftPct.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftCard.add(leftPct);

        JLabel leftCompletado = new JLabel("Completado");
        leftCompletado.setFont(FONT_PLAIN_12);
        leftCompletado.setForeground(TXT_DIM);
        leftCompletado.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftCard.add(leftCompletado);
        leftCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Progress bar
        final GlassProgressBar leftBar = new GlassProgressBar();
        leftBar.setAccentColor(BLUE);
        leftBar.setPreferredSize(new Dimension(0, 8));
        leftBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        leftBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCard.add(leftBar);
        leftCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Stats row
        JPanel leftStats = new JPanel(new GridLayout(1, 2, 0, 0));
        leftStats.setOpaque(false);
        leftStats.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel statDias = new JPanel();
        statDias.setOpaque(false);
        statDias.setLayout(new BoxLayout(statDias, BoxLayout.Y_AXIS));
        JLabel diasLabel = new JLabel("D\u00cdAS TRANSCURRIDOS");
        diasLabel.setFont(FONT_BOLD_9);
        diasLabel.setForeground(TXT_DIM);
        diasLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statDias.add(diasLabel);
        statDias.add(Box.createRigidArea(new Dimension(0, 4)));
        final JLabel diasValue = new JLabel("0");
        diasValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        diasValue.setForeground(new Color(0x06, 0xb6, 0xd4));
        diasValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        statDias.add(diasValue);

        JPanel statProy = new JPanel();
        statProy.setOpaque(false);
        statProy.setLayout(new BoxLayout(statProy, BoxLayout.Y_AXIS));
        JLabel proyLabel = new JLabel("PROYECTADO (6 MESES)");
        proyLabel.setFont(FONT_BOLD_9);
        proyLabel.setForeground(TXT_DIM);
        proyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statProy.add(proyLabel);
        statProy.add(Box.createRigidArea(new Dimension(0, 4)));
        final JLabel proyValue = new JLabel("180");
        proyValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        proyValue.setForeground(GREEN);
        proyValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        statProy.add(proyValue);

        leftStats.add(statDias);
        leftStats.add(statProy);
        leftCard.add(leftStats);

        // ── Right card: ENTREGAS DE EVIDENCIAS ──
        JPanel rightCard = createGlassCard();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));
        rightCard.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel rightTitleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rightTitleRow.setOpaque(false);
        rightTitleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel docIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(PURPLE.getRed(), PURPLE.getGreen(), PURPLE.getBlue(), 30));
                g2.fillRoundRect(0, 0, 36, 36, 8, 8);
                g2.setColor(PURPLE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(8, 4, 20, 28, 4, 4);
                g2.drawLine(12, 12, 24, 12);
                g2.drawLine(12, 18, 24, 18);
                g2.drawLine(12, 24, 20, 24);
                g2.dispose();
            }
        };
        docIcon.setOpaque(false);
        docIcon.setPreferredSize(new Dimension(36, 36));

        JLabel rightTitle = new JLabel("ENTREGAS DE EVIDENCIAS");
        rightTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightTitle.setForeground(TXT_BRIGHT);

        rightTitleRow.add(docIcon);
        rightTitleRow.add(rightTitle);
        rightCard.add(rightTitleRow);

        JSeparator sepR = new JSeparator();
        sepR.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sepR.setForeground(BORDER);
        sepR.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCard.add(Box.createRigidArea(new Dimension(0, 12)));
        rightCard.add(sepR);
        rightCard.add(Box.createRigidArea(new Dimension(0, 20)));

        final JLabel rightPct = new JLabel("0.0%");
        rightPct.setFont(new Font("Segoe UI", Font.BOLD, 48));
        rightPct.setForeground(TXT_BRIGHT);
        rightPct.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightCard.add(rightPct);

        JLabel rightCompletado = new JLabel("Completado");
        rightCompletado.setFont(FONT_PLAIN_12);
        rightCompletado.setForeground(TXT_DIM);
        rightCompletado.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightCard.add(rightCompletado);
        rightCard.add(Box.createRigidArea(new Dimension(0, 20)));

        final GlassProgressBar rightBar = new GlassProgressBar();
        rightBar.setAccentColor(PURPLE);
        rightBar.setPreferredSize(new Dimension(0, 8));
        rightBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        rightBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCard.add(rightBar);
        rightCard.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel rightStats = new JPanel(new GridLayout(1, 2, 0, 0));
        rightStats.setOpaque(false);
        rightStats.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel statEnv = new JPanel();
        statEnv.setOpaque(false);
        statEnv.setLayout(new BoxLayout(statEnv, BoxLayout.Y_AXIS));
        JLabel envLabel = new JLabel("EVIDENCIAS ENVIADAS");
        envLabel.setFont(FONT_BOLD_9);
        envLabel.setForeground(TXT_DIM);
        envLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statEnv.add(envLabel);
        statEnv.add(Box.createRigidArea(new Dimension(0, 4)));
        final JLabel envValue = new JLabel("0");
        envValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        envValue.setForeground(PURPLE);
        envValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        statEnv.add(envValue);

        JPanel statReq = new JPanel();
        statReq.setOpaque(false);
        statReq.setLayout(new BoxLayout(statReq, BoxLayout.Y_AXIS));
        JLabel reqLabel = new JLabel("TOTAL REQUERIDAS");
        reqLabel.setFont(FONT_BOLD_9);
        reqLabel.setForeground(TXT_DIM);
        reqLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statReq.add(reqLabel);
        statReq.add(Box.createRigidArea(new Dimension(0, 4)));
        final JLabel reqValue = new JLabel("12");
        reqValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        reqValue.setForeground(GREEN);
        reqValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        statReq.add(reqValue);

        rightStats.add(statEnv);
        rightStats.add(statReq);
        rightCard.add(rightStats);

        cardsRow.add(leftCard);
        cardsRow.add(rightCard);
        body.add(cardsRow, BorderLayout.CENTER);

        // Load data
        new Thread(() -> {
            try {
                int idUsr = controlador.Sesion.getIdUsuario();
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();

                // Days & hours
                String sqlA = "SELECT a.horas_requeridas, a.horas_cumplidas, c.fecha_inicio "
                            + "FROM aprendiz a "
                            + "JOIN curso_aprendiz ca ON a.id_aprendiz = ca.id_aprendiz "
                            + "JOIN curso c ON ca.id_curso = c.id_curso WHERE a.id_usuario = ?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlA)) {
                    ps.setInt(1, idUsr);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int req = rs.getInt("horas_requeridas");
                            int cum = rs.getInt("horas_cumplidas");
                            java.sql.Date fechaInicio = rs.getDate("fecha_inicio");
                            final int diasTranscurridos;
                            if (fechaInicio != null) {
                                long diff = System.currentTimeMillis() - fechaInicio.getTime();
                                diasTranscurridos = (int)(diff / (1000 * 60 * 60 * 24));
                            } else {
                                diasTranscurridos = 0;
                            }
                            final int proyectado = 180;
                            final int pctHoras = req > 0 ? Math.min(100, cum * 100 / req) : 0;
                            SwingUtilities.invokeLater(() -> {
                                leftPct.setText(pctHoras + "%");
                                leftBar.setPct(pctHoras);
                                diasValue.setText(String.valueOf(diasTranscurridos));
                                proyValue.setText(String.valueOf(proyectado));
                            });
                        }
                    }
                }

                // Evidences count
                String sqlAp = "SELECT id_aprendiz FROM aprendiz WHERE id_usuario = ?";
                int idAprendiz = -1;
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlAp)) {
                    ps.setInt(1, idUsr);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idAprendiz = rs.getInt("id_aprendiz");
                    }
                }
                if (idAprendiz >= 0) {
                    String sqlE = "SELECT COUNT(*) as total FROM evidencia WHERE id_aprendiz = ?";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlE)) {
                        ps.setInt(1, idAprendiz);
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                int enviadas = rs.getInt("total");
                                int totalReq = 12;
                                double pctEvid = totalReq > 0 ? Math.min(100, enviadas * 100.0 / totalReq) : 0;
                                final int env = enviadas;
                                final int req2 = totalReq;
                                SwingUtilities.invokeLater(() -> {
                                    rightPct.setText(String.format("%.1f%%", pctEvid));
                                    rightBar.setPct((int) pctEvid);
                                    envValue.setText(String.valueOf(env));
                                    reqValue.setText(String.valueOf(req2));
                                });
                            }
                        }
                    }
                }

                conn.close();
            } catch (Exception ex) {
                System.err.println("[PROGRESO] Error: " + ex.getMessage());
            }
        }).start();

        return body;
    }

    // ═══════════════════════ MI INFORMACIÓN ═══════════════════════
    private JPanel createMiInfoView() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Header
        JPanel header = createSectionHeader("\uD83D\uDC65", "Mi Informaci\u00f3n Personal");
        body.add(header, BorderLayout.NORTH);

        // Main card
        JPanel card = createGlassCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));

        // ── Title row ──
        JPanel titleRow = new JPanel();
        titleRow.setLayout(new BoxLayout(titleRow, BoxLayout.X_AXIS));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel personIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x1a, 0x1a, 0x1a));
                g2.fillRoundRect(0, 0, 36, 36, 8, 8);
                g2.setColor(GREEN);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(10, 3, 16, 14);
                g2.drawLine(18, 17, 18, 30);
                g2.drawLine(10, 23, 18, 18);
                g2.drawLine(26, 23, 18, 18);
                g2.dispose();
            }
        };
        personIcon.setOpaque(false);
        personIcon.setPreferredSize(new Dimension(36, 36));
        personIcon.setMinimumSize(new Dimension(36, 36));
        personIcon.setMaximumSize(new Dimension(36, 36));

        JLabel sectionTitle = new JLabel("ACTUALIZAR PERFIL");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionTitle.setForeground(TXT_BRIGHT);
        sectionTitle.setAlignmentY(Component.CENTER_ALIGNMENT);

        titleRow.add(personIcon);
        titleRow.add(Box.createRigidArea(new Dimension(10, 0)));
        titleRow.add(sectionTitle);
        titleRow.add(Box.createHorizontalGlue());
        card.add(titleRow);

        // Separator
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        JSeparator sep1 = createSep();
        card.add(sep1);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Read-only fields ──
        JPanel readOnlyPanel = new JPanel(new GridLayout(0, 2, 20, 16));
        readOnlyPanel.setOpaque(false);
        readOnlyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JTextField[] roFields = new JTextField[3];
        String[] roLabels = {"Nombre Completo", "Documento", "Correo Electr\u00f3nico (SENA)"};
        for (int i = 0; i < roLabels.length; i++) {
            JPanel fieldGroup = createFieldGroup(roLabels[i], true);
            roFields[i] = (JTextField) fieldGroup.getComponent(2);
            readOnlyPanel.add(fieldGroup);
        }
        card.add(readOnlyPanel);

        // Separator
        card.add(Box.createRigidArea(new Dimension(0, 24)));
        card.add(createSep());
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Datos Actualizables ──
        JLabel updTitle = new JLabel("Datos Actualizables");
        updTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updTitle.setForeground(TXT_BRIGHT);
        updTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(updTitle);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel editPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        editPanel.setOpaque(false);
        editPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JTextField txtTelefono = createStyledField();
        JPanel telGroup = new JPanel();
        telGroup.setLayout(new BoxLayout(telGroup, BoxLayout.Y_AXIS));
        telGroup.setOpaque(false);
        JLabel telLabel = new JLabel("Tel\u00e9fono \u270E");
        telLabel.setFont(FONT_BOLD_11);
        telLabel.setForeground(TXT);
        telLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        telGroup.add(telLabel);
        telGroup.add(Box.createRigidArea(new Dimension(0, 6)));
        txtTelefono.setAlignmentX(Component.LEFT_ALIGNMENT);
        telGroup.add(txtTelefono);
        editPanel.add(telGroup);

        final JTextField txtFicha = createStyledField();
        JPanel fichaGroup = new JPanel();
        fichaGroup.setLayout(new BoxLayout(fichaGroup, BoxLayout.Y_AXIS));
        fichaGroup.setOpaque(false);
        JLabel fichaLabel = new JLabel("N\u00famero de Ficha \u270E");
        fichaLabel.setFont(FONT_BOLD_11);
        fichaLabel.setForeground(TXT);
        fichaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fichaGroup.add(fichaLabel);
        fichaGroup.add(Box.createRigidArea(new Dimension(0, 6)));
        txtFicha.setAlignmentX(Component.LEFT_ALIGNMENT);
        fichaGroup.add(txtFicha);
        editPanel.add(fichaGroup);

        card.add(editPanel);

        // Separator
        card.add(Box.createRigidArea(new Dimension(0, 24)));
        card.add(createSep());
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Empresa Asignada ──
        JLabel empTitle = new JLabel("Informaci\u00f3n de Empresa Asignada");
        empTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        empTitle.setForeground(TXT_BRIGHT);
        empTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(empTitle);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        final JLabel empInfo = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x14, 0x1a, 0x14));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(0x22, 0x33, 0x22));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        empInfo.setOpaque(false);
        empInfo.setFont(FONT_PLAIN_12);
        empInfo.setForeground(TXT_DIM);
        empInfo.setBorder(new EmptyBorder(12, 16, 12, 16));
        empInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        empInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(empInfo);

        // Separator
        card.add(Box.createRigidArea(new Dimension(0, 24)));
        card.add(createSep());
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Guardar button ──
        int btnR = 12;
        JButton btnGuardar = new JButton("Guardar Cambios") {
            private final Color startColor = GREEN;
            private final Color endColor = GREEN.darker();
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                Color cS = getModel().isPressed() ? startColor.darker() : getModel().isRollover() ? startColor.brighter() : startColor;
                Color cE = getModel().isPressed() ? endColor.darker() : getModel().isRollover() ? startColor : endColor;
                g2.setPaint(new LinearGradientPaint(0, 0, w, 0, new float[]{0f, 1f}, new Color[]{cS, cE}));
                g2.fillRoundRect(0, 0, w, h, btnR, btnR);
                g2.setColor(new Color(255, 255, 255, 45));
                g2.drawRoundRect(0, 0, w - 1, h - 1, btnR, btnR);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnGuardar.setOpaque(false);
        btnGuardar.setContentAreaFilled(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(200, 44));
        btnGuardar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnGuardar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnGuardar.repaint(); }
            @Override public void mouseExited(MouseEvent e) { btnGuardar.repaint(); }
        });
        btnGuardar.addActionListener(e -> {
            String tel = txtTelefono.getText().trim();
            String ficha = txtFicha.getText().trim();
            new Thread(() -> {
                try {
                    int idUsr = controlador.Sesion.getIdUsuario();
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    String sqlUpd = "UPDATE usuario SET telefono = ? WHERE id_usuario = ?";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlUpd)) {
                        ps.setString(1, tel.isEmpty() ? null : tel);
                        ps.setInt(2, idUsr);
                        ps.executeUpdate();
                    }
                    if (!ficha.isEmpty()) {
                        String sqlFicha = "UPDATE aprendiz SET ficha = ? WHERE id_usuario = ?";
                        try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlFicha)) {
                            ps.setString(1, ficha);
                            ps.setInt(2, idUsr);
                            ps.executeUpdate();
                        }
                    }
                    conn.close();
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Cambios guardados correctamente.", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE));
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                }
            }).start();
        });

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnWrapper.setOpaque(false);
        btnWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnWrapper.add(btnGuardar);
        card.add(btnWrapper);

        body.add(card, BorderLayout.CENTER);

        // Load data
        final JTextField fTel = txtTelefono;
        final JTextField fFicha = txtFicha;
        new Thread(() -> {
            try {
                int idUsr = controlador.Sesion.getIdUsuario();
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sql = "SELECT u.nombres, u.apellidos, u.correo, u.telefono, u.tipo_documento, u.numero_documento, "
                           + "a.ficha, e.nombre as empresa "
                           + "FROM usuario u LEFT JOIN aprendiz a ON u.id_usuario = a.id_usuario "
                           + "LEFT JOIN empresa e ON a.id_empresa = e.id_empresa "
                           + "WHERE u.id_usuario = ?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idUsr);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String nom = rs.getString("nombres");
                            String ape = rs.getString("apellidos");
                            String correo = rs.getString("correo");
                            String tel = rs.getString("telefono");
                            String tipoDoc = rs.getString("tipo_documento");
                            String numDoc = rs.getString("numero_documento");
                            String ficha = rs.getString("ficha");
                            String empresa = rs.getString("empresa");
                            SwingUtilities.invokeLater(() -> {
                                roFields[0].setText((nom != null ? nom : "") + " " + (ape != null ? ape : ""));
                                roFields[1].setText((tipoDoc != null ? tipoDoc : "") + " " + (numDoc != null ? numDoc : ""));
                                roFields[2].setText(correo != null ? correo : "");
                                fTel.setText(tel != null ? tel : "");
                                fFicha.setText(ficha != null ? ficha : "");
                                if (empresa != null && !empresa.isEmpty()) {
                                    empInfo.setText("\u24D8  " + empresa);
                                    empInfo.setForeground(TXT);
                                } else {
                                    empInfo.setText("\u24D8  A\u00fan no se te ha asignado una empresa para tus pr\u00e1cticas. Contacta a tu instructor.");
                                }
                            });
                        }
                    }
                }
                conn.close();
            } catch (Exception ex) {
                System.err.println("[INFO] Error: " + ex.getMessage());
            }
        }).start();

        return body;
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setFont(FONT_PLAIN_12);
        field.setForeground(TXT_BRIGHT);
        field.setBackground(new Color(0x14, 0x14, 0x14));
        field.setCaretColor(TXT_BRIGHT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.setPreferredSize(new Dimension(0, 38));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return field;
    }

    private JPanel createFieldGroup(String label, boolean readOnly) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BOLD_11);
        lbl.setForeground(TXT_DIM);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(lbl);
        group.add(Box.createRigidArea(new Dimension(0, 6)));

        JTextField field = new JTextField();
        field.setFont(FONT_PLAIN_12);
        field.setForeground(TXT_DIM);
        field.setBackground(new Color(0x14, 0x14, 0x14));
        field.setEditable(!readOnly);
        field.setFocusable(!readOnly);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x1a, 0x1a, 0x1a), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.setPreferredSize(new Dimension(0, 38));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(field);

        return group;
    }

    private JSeparator createSep() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(BORDER);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sep;
    }

    // ═══════════════════════ NOTIFICACIONES ═══════════════════════
    private JPanel createNotificacionesView() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 24, 24, 24));

        JPanel header = createSectionHeader("\uD83D\uDD14", "Notificaciones");
        body.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        final JPanel[] listHolder = {new JPanel()};
        listHolder[0].setLayout(new BoxLayout(listHolder[0], BoxLayout.Y_AXIS));
        listHolder[0].setOpaque(false);

        // Loading
        JLabel loading = new JLabel("Cargando notificaciones...");
        loading.setFont(FONT_PLAIN_12);
        loading.setForeground(TXT_DIM);
        loading.setBorder(new EmptyBorder(40, 0, 0, 0));
        loading.setHorizontalAlignment(SwingConstants.CENTER);
        listHolder[0].add(loading);
        center.add(listHolder[0]);

        body.add(center, BorderLayout.CENTER);

        new Thread(() -> {
            try {
                int idUsr = controlador.Sesion.getIdUsuario();
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                try (java.sql.PreparedStatement ps = conn.prepareStatement(Notificacion.SELECT_BY_USUARIO)) {
                    ps.setInt(1, idUsr);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        java.util.List<Object[]> filas = new java.util.ArrayList<>();
                        while (rs.next()) {
                            filas.add(new Object[]{
                                rs.getString("mensaje"),
                                rs.getBoolean("leida"),
                                rs.getTimestamp("fecha")
                            });
                        }
                        SwingUtilities.invokeLater(() -> {
                            listHolder[0].removeAll();
                            if (filas.isEmpty()) {
                                JLabel empty = new JLabel("\uD83D\uDD14  No tienes notificaciones");
                                empty.setFont(FONT_PLAIN_12);
                                empty.setForeground(TXT_DIM);
                                empty.setHorizontalAlignment(SwingConstants.CENTER);
                                empty.setBorder(new EmptyBorder(40, 0, 0, 0));
                                listHolder[0].add(empty);
                            } else {
                                for (Object[] fila : filas) {
                                    String msg = (String) fila[0];
                                    boolean leida = (Boolean) fila[1];
                                    java.sql.Timestamp fecha = (java.sql.Timestamp) fila[2];
                                    listHolder[0].add(createNotificacionItem(msg, leida, fecha));
                                    listHolder[0].add(Box.createRigidArea(new Dimension(0, 4)));
                                }
                            }
                            listHolder[0].revalidate();
                            listHolder[0].repaint();
                        });
                    }
                }
                conn.close();
            } catch (Exception ex) {
                System.err.println("[NOTIF] Error: " + ex.getMessage());
            }
        }).start();

        return body;
    }

    private JPanel createNotificacionItem(String mensaje, boolean leida, java.sql.Timestamp fecha) {
        JPanel item = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(leida ? new Color(0x11, 0x11, 0x11) : new Color(0x14, 0x1a, 0x14));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(leida ? BORDER : new Color(0x22, 0x33, 0x22));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        item.setOpaque(false);
        item.setLayout(new BorderLayout(12, 0));
        item.setBorder(new EmptyBorder(12, 16, 12, 16));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel dotPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(leida ? TXT_DIM : GREEN);
                g2.fillOval(0, 0, 8, 8);
                g2.dispose();
            }
        };
        dotPanel.setOpaque(false);
        dotPanel.setPreferredSize(new Dimension(8, 8));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel msgLbl = new JLabel(mensaje);
        msgLbl.setFont(FONT_PLAIN_12);
        msgLbl.setForeground(leida ? TXT : TXT_BRIGHT);
        textPanel.add(msgLbl);

        if (fecha != null) {
            JLabel dateLbl = new JLabel(fecha.toString().substring(0, 16));
            dateLbl.setFont(FONT_PLAIN_11);
            dateLbl.setForeground(TXT_DIM);
            textPanel.add(dateLbl);
        }

        item.add(dotPanel, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);
        return item;
    }

    // ═══════════════════════ MIS EVIDENCIAS ═══════════════════════
    private JPanel createMisEvidenciasView() {
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 24, 24, 24));

        JPanel header = createSectionHeader("\uD83D\uDCC2", "Mis Evidencias");
        body.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);

        // Table
        String[] columns = {"#", "TIPO", "FECHA", "ESTADO", "OBSERVACIONES"};
        DefaultTableModel tm = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tm);
        Theme.estilizarTabla(table);
        JScrollPane scroll = Theme.styledScroll(table);
        center.add(scroll, BorderLayout.CENTER);

        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setOpaque(false);
        JLabel emptyIcon = new JLabel("\uD83D\uDCC2", SwingConstants.CENTER);
        emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        emptyIcon.setForeground(TXT_DIM);
        JLabel emptyText = new JLabel("No tienes evidencias subidas.", SwingConstants.CENTER);
        emptyText.setFont(FONT_PLAIN_12);
        emptyText.setForeground(TXT_DIM);
        emptyPanel.add(emptyIcon, BorderLayout.CENTER);
        JPanel emptyWrap = new JPanel(new BorderLayout());
        emptyWrap.setOpaque(false);
        emptyWrap.add(emptyIcon, BorderLayout.NORTH);
        emptyWrap.add(emptyText, BorderLayout.CENTER);
        emptyWrap.setBorder(new EmptyBorder(60, 0, 0, 0));

        body.add(center, BorderLayout.CENTER);

        new Thread(() -> {
            try {
                int idUsr = controlador.Sesion.getIdUsuario();
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                // Find id_aprendiz
                String sqlAp = "SELECT id_aprendiz FROM aprendiz WHERE id_usuario = ?";
                int idAprendiz = -1;
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlAp)) {
                    ps.setInt(1, idUsr);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idAprendiz = rs.getInt("id_aprendiz");
                    }
                }
                if (idAprendiz < 0) {
                    conn.close();
                    return;
                }
                String sql = "SELECT id_evidencia, tipo, fecha_entrega, estado, observaciones "
                           + "FROM evidencia WHERE id_aprendiz = ? ORDER BY fecha_entrega DESC";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idAprendiz);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        java.util.List<Object[]> filas = new java.util.ArrayList<>();
                        int num = 1;
                        while (rs.next()) {
                            filas.add(new Object[]{
                                num++,
                                rs.getString("tipo"),
                                rs.getTimestamp("fecha_entrega") != null ? rs.getTimestamp("fecha_entrega").toString().substring(0, 16) : "--",
                                rs.getString("estado"),
                                rs.getString("observaciones") != null ? rs.getString("observaciones") : ""
                            });
                        }
                        SwingUtilities.invokeLater(() -> {
                            tm.setRowCount(0);
                            for (Object[] row : filas) tm.addRow(row);
                            if (filas.isEmpty()) {
                                center.removeAll();
                                center.add(emptyWrap, BorderLayout.CENTER);
                                center.revalidate();
                                center.repaint();
                            }
                        });
                    }
                }
                conn.close();
            } catch (Exception ex) {
                System.err.println("[EVIDENCIAS] Error: " + ex.getMessage());
            }
        }).start();

        return body;
    }

    // ═══════════════════════ HELPERS ═══════════════════════
    private class GlassProgressBar extends JPanel {
        private int pct = 0;
        private Color accentColor = BLUE;
        GlassProgressBar() { setOpaque(false); }
        void setPct(int p) { this.pct = p; repaint(); }
        void setAccentColor(Color c) { this.accentColor = c; }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x22, 0x22, 0x22));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
            g2.setColor(accentColor);
            g2.fillRoundRect(0, 0, (int)(getWidth() * pct / 100.0), getHeight(), 6, 6);
            g2.dispose();
        }
    }

    private JPanel createSectionHeader(String icon, String title) {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setLayout(new BorderLayout(12, 0));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLbl.setForeground(GREEN);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(TXT_BRIGHT);
        left.add(iconLbl);
        left.add(titleLbl);
        header.add(left, BorderLayout.WEST);
        return header;
    }

    private JPanel createGlassCard() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
    }

    private JPanel createPlaceholder(String title, String icon) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG);
        JLabel lbl = new JLabel(icon + "  " + title + " \u2014 Vista en construcci\u00f3n");
        lbl.setFont(FONT_PLAIN_12);
        lbl.setForeground(TXT_DIM);
        p.add(lbl);
        return p;
    }

    private void drawMiniIcon(Graphics2D g2, Color c, int s) {
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.6f));
        int m = 1;
        // Generic checkmark
        g2.drawArc(m, m, s - 2, s - 2, 0, 360);
        g2.drawLine(s / 4, s / 2, s / 2 - 1, s * 3 / 4);
        g2.drawLine(s / 2 - 1, s * 3 / 4, s - 3, m + 2);
    }

    // ═══════════════════════ MENU ICON TYPE ═══════════════════════
    enum MenuIconType {
        GRID, UPLOAD, CHART, PERSON, BELL, FOLDER
    }

    // ═══════════════════════ SIDEBAR BUTTON ═══════════════════════
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
            setOpaque(false);
            setPreferredSize(new Dimension(220, 38));
            setMaximumSize(new Dimension(220, 38));
            setMinimumSize(new Dimension(220, 38));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { onClick.run(); }
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        void setActive(boolean a) { this.active = a; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            if (active || hover) {
                int rx = 4, ry = 2, rw = w - 8, rh = h - 4, arc = 6;
                if (active) {
                    g2.setColor(new Color(0x22, 0xc5, 0x5e, 35));
                    g2.fillRoundRect(rx, ry, rw, rh, arc, arc);
                    g2.setColor(new Color(0x22, 0xc5, 0x5e, 90));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(rx, ry, rw, rh, arc, arc);
                } else {
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fillRoundRect(rx, ry, rw, rh, arc, arc);
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(rx, ry, rw, rh, arc, arc);
                }
            }

            if (active) {
                g2.setColor(GREEN);
                g2.fillRoundRect(0, 6, 3, h - 12, 2, 2);
            }

            Color iconColor = active ? GREEN : (hover ? Color.WHITE : TXT);
            int iconX = 18, iconY = (h - 16) / 2;
            paintMenuIcon(g2, iconType, iconX, iconY, 16, iconColor);

            g2.setFont(FONT_PLAIN_12);
            g2.setColor(active ? GREEN : (hover ? Color.WHITE : TXT));
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
                case UPLOAD:
                    g2.drawLine(x + s / 2, y + m, x + s / 2, y + s - m - 4);
                    g2.drawLine(x + s / 2, y + m + 2, x + s / 2 - 4, y + m + 6);
                    g2.drawLine(x + s / 2, y + m + 2, x + s / 2 + 4, y + m + 6);
                    g2.drawLine(x + m + 2, y + s - 3, x + s - m - 2, y + s - 3);
                    break;
                case CHART:
                    g2.drawLine(x + m + 2, y + s - m - 2, x + s / 3, y + s / 2);
                    g2.drawLine(x + s / 3, y + s / 2, x + s * 2 / 3, y + m + 4);
                    g2.drawLine(x + s * 2 / 3, y + m + 4, x + s - m - 2, y + s / 3);
                    break;
                case PERSON:
                    g2.drawOval(x + s / 2 - 3, y + m, 6, 6);
                    g2.drawLine(x + s / 2, y + m + 7, x + s / 2, y + s - 3);
                    g2.drawLine(x + m, y + s / 2 + 2, x + s - 1, y + s / 2 + 2);
                    break;
                case BELL:
                    g2.drawArc(x + m + 2, y + m + 1, s - 6, s - 4, 200, 140);
                    g2.drawLine(x + m + 2, y + s / 2 + 1, x + s - m - 2, y + s / 2 + 1);
                    g2.drawArc(x + s / 2 - 3, y + s - 5, 6, 4, 0, 180);
                    break;
                case FOLDER:
                    g2.drawRoundRect(x + m + 1, y + m + 3, s - 4, s - 6, 3, 3);
                    g2.drawRoundRect(x + m + 1, y + m + 1, s - 4, 4, 3, 3);
                    break;
            }
        }
    }
}
