package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
        contentStack.add(createPlaceholder("Subir evidencias", "\uD83D\uDCC1"), "Subir evidencias");
        contentStack.add(createPlaceholder("Mi progreso", "\uD83D\uDCC8"), "Mi progreso");
        contentStack.add(createPlaceholder("Mi informaci\u00f3n", "\uD83D\uDC64"), "Mi informaci\u00f3n");
        contentStack.add(createPlaceholder("Notificaciones", "\uD83D\uDD14"), "Notificaciones");
        contentStack.add(createPlaceholder("Mis evidencias", "\uD83D\uDCC2"), "Mis evidencias");

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

        JPanel pillWrapper = new JPanel(new BorderLayout());
        pillWrapper.setOpaque(false);
        pillWrapper.setBorder(new EmptyBorder(8, 10, 16, 10));
        pillWrapper.setMaximumSize(new Dimension(220, 60));
        pillWrapper.add(userPill, BorderLayout.CENTER);
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
