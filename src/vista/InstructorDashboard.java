package vista;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class InstructorDashboard extends JInternalFrame {

    // ── Colores ──────────────────────────────────────────────────
    private static final Color BG         = new Color(0x0d, 0x0d, 0x0d);
    private static final Color SIDEBAR_BG = new Color(0x13, 0x13, 0x13);
    private static final Color CARD_BG    = new Color(0x1a, 0x1a, 0x1a);
    private static final Color BORDER_DK  = new Color(0x2a, 0x2a, 0x2a);
    private static final Color GREEN      = new Color(0x22, 0xc5, 0x5e);
    private static final Color BLUE       = new Color(0x3b, 0x82, 0xf6);
    private static final Color ORANGE     = new Color(0xf5, 0x9e, 0x0b);
    private static final Color RED        = new Color(0xef, 0x44, 0x44);
    private static final Color PURPLE     = new Color(0xa8, 0x55, 0xf7);
    private static final Color TXT_PRI    = new Color(0xf0, 0xf0, 0xf0);
    private static final Color TXT_SEC    = new Color(0x8a, 0x8a, 0x8a);
    private static final Color TXT_DIM    = new Color(0x55, 0x55, 0x55);
    private static final Color ACTIVE_BG  = new Color(0x26, 0x26, 0x26);
    private static final Color ROW_ALT    = new Color(0x16, 0x16, 0x16);
    private static final Color INPUT_BG   = new Color(0x1e, 0x1e, 0x1e);

    // ── Estado ────────────────────────────────────────────────────
    private CardLayout contentLayout;
    private JPanel     contentStack;
    private JButton    activeNavBtn = null;
    private int        idInstructor = -1;

    // Dashboard stats
    private JLabel lblAprendices = new JLabel("0");
    private JLabel lblPendientes = new JLabel("0");
    private JLabel lblFichas     = new JLabel("0");
    private JLabel lblProgreso   = new JLabel("0%");
    private JProgressBar progBar;

    // Fichas table
    private DefaultTableModel fichasModel;
    private JTable fichasTable;

    // Ficha detail state
    private int    detailIdCurso    = -1;
    private String detailFicha      = "";
    private String detailNombre     = "";
    private JLabel detailInfoLabel  = new JLabel("—");
    private JLabel detailAprendicesLabel = new JLabel("0");
    private JLabel detailPendientesLabel = new JLabel("0");
    private DefaultTableModel detailAprendicesModel;
    private JTable detailAprendicesTable;
    private DefaultTableModel detailEvidenciasModel;
    private JTable detailEvidenciasTable;

    // Evidencias pendientes
    private DefaultTableModel evidenciasPendientesModel;
    private JTable evidenciasPendientesTable;

    // Profile
    private JLabel lblNombre = new JLabel("—");
    private JLabel lblCorreo = new JLabel("—");
    private JLabel lblArea   = new JLabel("No especificada");
    private JLabel lblEstado;

    // Date formatter
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    // ─────────────────────────────────────────────────────────────
    public InstructorDashboard() {
        setTitle("Panel del Instructor");
        setClosable(true); setMaximizable(true);
        setIconifiable(true); setResizable(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setBorder(BorderFactory.createLineBorder(BORDER_DK));
        setUI(new ModernInternalFrameUI(this));
        setSize(1100, 660);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildCenter(),   BorderLayout.CENTER);
        setContentPane(root);

        SwingUtilities.invokeLater(this::cargarDatos);
    }

    // ══════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sb = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(SIDEBAR_BG); g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(BORDER_DK); g.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
            }
        };
        sb.setLayout(new BorderLayout());
        sb.setPreferredSize(new Dimension(195, 0));

        // ── Top: brand ──
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(20, 16, 12, 16));

        JLabel brand = new JLabel("InstructorPanel");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 14));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandSub = new JLabel("SEGUIMIENTO Y EVALUACI\u00d3N");
        brandSub.setFont(new Font("Segoe UI", Font.BOLD, 8));
        brandSub.setForeground(TXT_DIM);
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(brand); top.add(Box.createVerticalStrut(2)); top.add(brandSub);
        top.add(Box.createVerticalStrut(18));

        // ── Nav ──
        String[][] navItems = {
            {"Panel",                "\uD83D\uDD32"},
            {"Gesti\u00f3n de Fichas", "\uD83D\uDCCB"},
            {"Evidencias",           "\uD83D\uDCDD"},
            {"Reportes",             "\uD83D\uDCC8"},
        };
        String[] views = {"Panel", "MisFichas", "Evidencias", "Reportes"};

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));

        JButton[] btns = new JButton[navItems.length];
        for (int i = 0; i < navItems.length; i++) {
            final String view = views[i];
            final int idx = i;
            btns[i] = buildNavBtn(navItems[i][1] + "  " + navItems[i][0]);
            btns[i].addActionListener(e -> {
                contentLayout.show(contentStack, view);
                for (JButton b : btns) setNavActive(b, false);
                setNavActive(btns[idx], true);
            });
            nav.add(btns[i]);
        }
        setNavActive(btns[0], true);

        top.add(nav);

        // ── Bottom: user ──
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10, 12, 14, 12));

        JPanel userRow = new JPanel(new BorderLayout(8, 0));
        userRow.setOpaque(false);

        JLabel avatar = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String ini = controlador.Sesion.getNombres() != null &&
                        !controlador.Sesion.getNombres().isEmpty()
                        ? String.valueOf(controlador.Sesion.getNombres().charAt(0)).toUpperCase() : "I";
                g2.drawString(ini, (getWidth()-fm.stringWidth(ini))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(28, 28));
        avatar.setMinimumSize(new Dimension(28, 28));

        JPanel userInfo = new JPanel();
        userInfo.setOpaque(false);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));

        JLabel userName = new JLabel(controlador.Sesion.getNombres() != null
                ? controlador.Sesion.getNombres().toLowerCase() : "instructor");
        userName.setFont(new Font("Segoe UI", Font.BOLD, 11));
        userName.setForeground(TXT_PRI);

        JLabel userSub = new JLabel("Instructor SENA");
        userSub.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        userSub.setForeground(TXT_DIM);

        userInfo.add(userName); userInfo.add(userSub);

        JLabel dots = new JLabel("\u2022");
        dots.setForeground(TXT_SEC);
        dots.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dots.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                JPopupMenu pm = new JPopupMenu();
                pm.setBackground(CARD_BG);
                JMenuItem mi = new JMenuItem("Cerrar Sesi\u00f3n");
                mi.setForeground(RED);
                mi.setFont(new Font("Segoe UI", Font.BOLD, 11));
                mi.setBackground(CARD_BG);
                mi.addActionListener(ev -> {
                    java.awt.Frame top2 = (java.awt.Frame) SwingUtilities.getWindowAncestor(InstructorDashboard.this);
                    if (top2 instanceof MDI) ((MDI) top2).cerrarSesion();
                });
                pm.add(mi);
                pm.show(dots, e.getX(), e.getY());
            }
        });

        userRow.add(avatar,   BorderLayout.WEST);
        userRow.add(userInfo, BorderLayout.CENTER);
        userRow.add(dots,     BorderLayout.EAST);
        bottom.add(userRow);

        // ── Logout button ──
        JButton btnLogout = new JButton("\u23FB  CERRAR SESI\u00d3N") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(0xef, 0x44, 0x44, 30) : new Color(0xef, 0x44, 0x44, 10);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(new Color(0xef, 0x44, 0x44, 80));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnLogout.setForeground(RED);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnLogout.addActionListener(e -> {
            java.awt.Frame top2 = (java.awt.Frame) SwingUtilities.getWindowAncestor(InstructorDashboard.this);
            if (top2 instanceof MDI) ((MDI) top2).cerrarSesion();
        });

        JPanel sepLine = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BORDER_DK); g.drawLine(0, 0, getWidth(), 0);
            }
        };
        sepLine.setOpaque(false);
        sepLine.setPreferredSize(new Dimension(0, 1));

        JPanel sbContent = new JPanel(new BorderLayout());
        sbContent.setOpaque(false);
        sbContent.add(top, BorderLayout.CENTER);

        JPanel bottomFull = new JPanel(new BorderLayout());
        bottomFull.setOpaque(false);
        bottomFull.add(sepLine, BorderLayout.NORTH);
        bottomFull.add(bottom, BorderLayout.CENTER);

        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setOpaque(false);
        logoutPanel.setBorder(new EmptyBorder(4, 8, 10, 8));
        logoutPanel.add(btnLogout, BorderLayout.CENTER);

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setOpaque(false);
        bottomWrapper.add(bottomFull, BorderLayout.CENTER);
        bottomWrapper.add(logoutPanel, BorderLayout.SOUTH);

        sb.add(sbContent,  BorderLayout.CENTER);
        sb.add(bottomWrapper, BorderLayout.SOUTH);
        return sb;
    }

    private JButton buildNavBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                if (this == activeNavBtn) {
                    g.setColor(ACTIVE_BG);
                    g.fillRoundRect(4, 2, getWidth()-8, getHeight()-4, 8, 8);
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(0x20, 0x20, 0x20));
                    g.fillRoundRect(4, 2, getWidth()-8, getHeight()-4, 8, 8);
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(TXT_SEC);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(195, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        return btn;
    }

    private void setNavActive(JButton btn, boolean active) {
        if (active) {
            activeNavBtn = btn;
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else {
            btn.setForeground(TXT_SEC);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        btn.repaint();
    }

    // ══════════════════════════════════════════════════════════════
    //  CENTER (header + card stack)
    // ══════════════════════════════════════════════════════════════
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x10, 0x10, 0x10));
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_DK),
                new EmptyBorder(12, 24, 12, 24)));

        JLabel hTitle = new JLabel("Panel Central \u2014 Instructor");
        hTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        hTitle.setForeground(TXT_PRI);
        header.add(hTitle, BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentStack  = new JPanel(contentLayout);
        contentStack.setBackground(BG);

        contentStack.add(buildPanelView(),     "Panel");
        contentStack.add(buildFichasView(),     "MisFichas");
        contentStack.add(buildFichaDetalleView(), "FichaDetalle");
        contentStack.add(buildEvidenciasPendientesView(), "Evidencias");
        contentStack.add(buildSimpleView("Reportes", "Pr\u00f3ximamente"), "Reportes");

        center.add(header,       BorderLayout.NORTH);
        center.add(contentStack, BorderLayout.CENTER);
        return center;
    }

    // ══════════════════════════════════════════════════════════════
    //  1. PANEL PRINCIPAL (Dashboard)
    // ══════════════════════════════════════════════════════════════
    private JPanel buildPanelView() {
        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(22, 22, 22, 22));

        // ── Stats row ──
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setOpaque(false);

        JPanel c1 = buildStatCard("TOTAL APRENDICES", lblAprendices,
                "Matriculados en tus fichas", BLUE, "\uD83D\uDC65",
                e -> contentLayout.show(contentStack, "MisFichas"));

        JPanel c2 = buildStatCard("FICHAS ASIGNADAS", lblFichas,
                "Bajo tu seguimiento", PURPLE, "\uD83D\uDCCB",
                e -> contentLayout.show(contentStack, "MisFichas"));

        JPanel c3 = buildStatCard("EVIDENCIAS PENDIENTES", lblPendientes,
                "Pendientes de revisi\u00f3n", ORANGE, "\uD83D\uDCDD",
                e -> contentLayout.show(contentStack, "Evidencias"));

        JPanel c4 = buildProgresoCard();

        statsRow.add(c1); statsRow.add(c2);
        statsRow.add(c3); statsRow.add(c4);

        // ── Bottom row ──
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 14, 0));
        bottomRow.setOpaque(false);
        bottomRow.add(buildPerfilCard());
        bottomRow.add(buildAccionesCard());

        body.add(statsRow,  BorderLayout.NORTH);
        body.add(bottomRow, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildStatCard(String label, JLabel valueLbl, String sub,
                                  Color accent, String iconText, ActionListener click) {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 16, 18));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { click.actionPerformed(null); }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(TXT_SEC);
        top.add(lbl, BorderLayout.WEST);

        JLabel icon = new JLabel(iconText, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        icon.setForeground(accent);
        icon.setPreferredSize(new Dimension(30, 30));
        icon.setOpaque(false);
        top.add(icon, BorderLayout.EAST);

        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(TXT_PRI);
        valueLbl.setBorder(new EmptyBorder(6, 0, 2, 0));

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLbl.setForeground(TXT_SEC);

        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        mid.add(valueLbl); mid.add(subLbl);

        card.add(top, BorderLayout.NORTH);
        card.add(mid, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildProgresoCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 16, 18));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lbl = new JLabel("PROGRESO GENERAL");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(TXT_SEC);
        top.add(lbl, BorderLayout.WEST);

        JLabel icon = new JLabel("\uD83D\uDCC8", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x22, 0xc5, 0x5e, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        icon.setForeground(GREEN);
        icon.setPreferredSize(new Dimension(30, 30));
        icon.setOpaque(false);
        top.add(icon, BorderLayout.EAST);

        lblProgreso.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblProgreso.setForeground(GREEN);
        lblProgreso.setBorder(new EmptyBorder(6, 0, 4, 0));

        progBar = new JProgressBar(0, 100) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = getHeight();
                g2.setColor(new Color(0x2a, 0x2a, 0x2a));
                g2.fillRoundRect(0, 0, getWidth(), h, h, h);
                int fw = (int)((double)getValue()/getMaximum()*getWidth());
                if (fw > 0) { g2.setColor(GREEN); g2.fillRoundRect(0, 0, fw, h, h, h); }
                g2.dispose();
            }
        };
        progBar.setBorder(BorderFactory.createEmptyBorder());
        progBar.setPreferredSize(new Dimension(0, 6));
        progBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        progBar.setOpaque(false);

        JLabel sub = new JLabel("Promedio de tus aprendices");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sub.setForeground(TXT_SEC);

        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        lblProgreso.setAlignmentX(Component.LEFT_ALIGNMENT);
        progBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        mid.add(lblProgreso); mid.add(progBar);
        mid.add(Box.createVerticalStrut(8)); mid.add(sub);

        card.add(top, BorderLayout.NORTH);
        card.add(mid, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildPerfilCard() {
        JPanel card = roundCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        hdr.setOpaque(false);
        hdr.setAlignmentX(Component.LEFT_ALIGNMENT);
        hdr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel iconLbl = new JLabel("\uD83D\uDCCB") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x22, 0xc5, 0x5e, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        iconLbl.setPreferredSize(new Dimension(24, 24));
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel title = new JLabel("MI PERFIL DE INSTRUCTOR");
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(TXT_PRI);

        hdr.add(iconLbl); hdr.add(title);
        card.add(hdr);
        card.add(Box.createVerticalStrut(14));

        lblEstado = new JLabel("\u25CF  ACTIVO");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblEstado.setForeground(GREEN);
        lblEstado.setOpaque(true);
        lblEstado.setBackground(new Color(0x22, 0xc5, 0x5e, 30));
        lblEstado.setBorder(new EmptyBorder(3, 8, 3, 8));

        card.add(perfilRow("Nombre",              lblNombre));
        card.add(perfilRow("Correo Electr\u00f3nico", lblCorreo));
        card.add(perfilRow("\u00c1rea de Formaci\u00f3n", lblArea));
        card.add(perfilRowWidget("Estado Sistema", lblEstado));

        return card;
    }

    private JPanel perfilRow(String label, JLabel value) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0x22, 0x22, 0x22));
                g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TXT_SEC);
        value.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        value.setForeground(TXT_PRI);
        row.add(lbl,   BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JPanel perfilRowWidget(String label, JComponent widget) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TXT_SEC);
        row.add(lbl,    BorderLayout.WEST);
        row.add(widget, BorderLayout.EAST);
        return row;
    }

    private JPanel buildAccionesCard() {
        JPanel card = roundCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        hdr.setOpaque(false);
        hdr.setAlignmentX(Component.LEFT_ALIGNMENT);
        hdr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel iconLbl = new JLabel("\u26A1") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xa8, 0x55, 0xf7, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        iconLbl.setPreferredSize(new Dimension(24, 24));
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        iconLbl.setForeground(PURPLE);

        JLabel title = new JLabel("ACCIONES R\u00c1PIDAS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(TXT_PRI);

        hdr.add(iconLbl); hdr.add(title);
        card.add(hdr);
        card.add(Box.createVerticalStrut(14));

        card.add(buildAccionItem("\uD83D\uDCCB", GREEN,  "Ver Mis Fichas",
                "Gestionar tus fichas asignadas",
                e -> contentLayout.show(contentStack, "MisFichas")));
        card.add(Box.createVerticalStrut(10));
        card.add(buildAccionItem("\uD83D\uDCDD", ORANGE, "Revisar Evidencias",
                "Pendientes de tu aprobaci\u00f3n",
                e -> contentLayout.show(contentStack, "Evidencias")));
        card.add(Box.createVerticalStrut(10));
        card.add(buildAccionItem("\uD83D\uDCC8", BLUE,   "Ver Reportes",
                "Resumen de horas y bit\u00e1coras",
                e -> contentLayout.show(contentStack, "Reportes")));

        return card;
    }

    private JPanel buildAccionItem(String iconText, Color accent, String title,
                                    String sub, ActionListener action) {
        JPanel item = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x22, 0x22, 0x22));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(1, 6, 1, getHeight()-6);
                if (getMousePosition() != null) {
                    g2.setColor(new Color(255,255,255,6));
                    g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                }
                g2.dispose();
            }
        };
        item.setOpaque(false);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setBorder(new EmptyBorder(10, 14, 10, 12));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
            @Override public void mouseEntered(MouseEvent e)  { item.repaint(); }
            @Override public void mouseExited(MouseEvent e)   { item.repaint(); }
        });

        JLabel icon = new JLabel(iconText, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.dispose(); super.paintComponent(g);
            }
        };
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        icon.setForeground(accent);
        icon.setPreferredSize(new Dimension(32, 32));
        icon.setOpaque(false);

        JPanel txtPanel = new JPanel();
        txtPanel.setOpaque(false);
        txtPanel.setLayout(new BoxLayout(txtPanel, BoxLayout.Y_AXIS));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tLbl.setForeground(accent);
        tLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sLbl = new JLabel(sub);
        sLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sLbl.setForeground(TXT_SEC);
        sLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPanel.add(tLbl); txtPanel.add(Box.createVerticalStrut(2)); txtPanel.add(sLbl);

        item.add(icon,     BorderLayout.WEST);
        item.add(txtPanel, BorderLayout.CENTER);
        return item;
    }

    // ══════════════════════════════════════════════════════════════
    //  2. GESTIÓN DE FICHAS
    // ══════════════════════════════════════════════════════════════
    private JPanel buildFichasView() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(22, 22, 22, 22));

        // ── Title + Search ──
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);

        JLabel title = new JLabel("Mis Fichas Asignadas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TXT_PRI);
        topBar.add(title, BorderLayout.WEST);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(280, 36));

        JTextField searchField = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(TXT_DIM);
                    g2.setFont(getFont());
                    g2.drawString("Buscar ficha por nombre o n\u00famero...",
                            10, getHeight()/2 + getFont().getSize()/3);
                    g2.dispose();
                }
            }
        };
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setForeground(TXT_PRI);
        searchField.setCaretColor(TXT_PRI);
        searchField.setBackground(INPUT_BG);
        searchField.setBorder(new CompoundBorder(
                new LineBorder(BORDER_DK, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        searchField.setPreferredSize(new Dimension(280, 36));

        JLabel searchIcon = new JLabel("\uD83D\uDD0D") {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        searchIcon.setForeground(TXT_SEC);
        searchIcon.setPreferredSize(new Dimension(30, 36));
        searchIcon.setHorizontalAlignment(SwingConstants.CENTER);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchIcon, BorderLayout.EAST);
        topBar.add(searchPanel, BorderLayout.EAST);

        // ── Table ──
        String[] cols = {"Ficha", "Nombre del Programa", "Fecha Inicio", "Fecha Fin",
                         "Aprendices", "Evidencias Pendientes", "Acciones"};
        fichasModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        fichasTable = styleTable(fichasModel);

        // Search filter
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e)  { filter(); }
            private void filter() {
                String q = searchField.getText().toLowerCase().trim();
                TableRowSorter<DefaultTableModel> sorter =
                        new TableRowSorter<>(fichasModel);
                fichasTable.setRowSorter(sorter);
                if (q.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + q));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(fichasTable);
        scroll.setBorder(new LineBorder(BORDER_DK, 1, true));
        scroll.getViewport().setBackground(CARD_BG);

        body.add(topBar, BorderLayout.NORTH);
        body.add(scroll, BorderLayout.CENTER);
        return body;
    }

    // ══════════════════════════════════════════════════════════════
    //  3. DETALLE DE LA FICHA
    // ══════════════════════════════════════════════════════════════
    private JPanel buildFichaDetalleView() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(22, 22, 22, 22));

        // ── Top: Back button + info ──
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);

        JButton btnBack = new JButton("\u2190  Volver a Fichas");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setForeground(BLUE);
        btnBack.setBackground(new Color(0x1a, 0x1a, 0x1a));
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setBorder(new EmptyBorder(8, 12, 8, 12));
        btnBack.addActionListener(e -> {
            contentLayout.show(contentStack, "MisFichas");
        });

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        detailInfoLabel = new JLabel("Ficha: \u2014");
        detailInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailInfoLabel.setForeground(TXT_PRI);
        detailInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statsMini = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        statsMini.setOpaque(false);
        statsMini.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailAprendicesLabel = new JLabel("0");
        detailPendientesLabel = new JLabel("0");
        detailAprendicesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailAprendicesLabel.setForeground(GREEN);
        detailPendientesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailPendientesLabel.setForeground(ORANGE);

        JLabel lblA = new JLabel("Aprendices");
        lblA.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblA.setForeground(TXT_SEC);
        JPanel pA = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pA.setOpaque(false);
        pA.add(detailAprendicesLabel); pA.add(lblA);

        JLabel lblP = new JLabel("Evidencias Pendientes");
        lblP.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblP.setForeground(TXT_SEC);
        JPanel pP = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pP.setOpaque(false);
        pP.add(detailPendientesLabel); pP.add(lblP);

        statsMini.add(pA);
        statsMini.add(pP);

        infoPanel.add(detailInfoLabel);
        infoPanel.add(statsMini);

        topBar.add(btnBack, BorderLayout.WEST);
        topBar.add(infoPanel, BorderLayout.CENTER);

        // ── Split: apprentices top, evidence bottom ──
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setDividerLocation(280);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setOpaque(false);
        split.setTopComponent(buildDetailAprendicesPanel());
        split.setBottomComponent(buildDetailEvidenciasPanel());

        body.add(topBar, BorderLayout.NORTH);
        body.add(split, BorderLayout.CENTER);
        return body;
    }

    private JPanel createMiniStat(String value, String label, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 14));
        v.setForeground(color);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(TXT_SEC);

        p.add(v); p.add(l);

        JLabel valLbl = v;
        return p;
    }

    private JPanel buildDetailAprendicesPanel() {
        JPanel panel = roundCard();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        // ── Toolbar ──
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel title = new JLabel("\uD83D\uDC65  Aprendices y Progreso");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(TXT_PRI);
        toolbar.add(title, BorderLayout.WEST);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionBtns.setOpaque(false);
        JButton btnAdd = toolbarBtn("\u2795 A\u00f1adir", GREEN);
        JButton btnEdit = toolbarBtn("\u270F Editar", BLUE);
        JButton btnDelete = toolbarBtn("\u274C Eliminar", RED);
        btnAdd.addActionListener(e -> mostrarDialogoAgregarAprendiz());
        btnEdit.addActionListener(e -> editarAprendizSeleccionado());
        btnDelete.addActionListener(e -> eliminarAprendizSeleccionado());
        actionBtns.add(btnAdd);
        actionBtns.add(btnEdit);
        actionBtns.add(btnDelete);
        toolbar.add(actionBtns, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // ── Table ──
        String[] cols = {"ID", "Nombre", "Empresa", "Horas Cumplidas", "Horas Requeridas",
                         "Progreso (%)", "Estado"};
        detailAprendicesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        detailAprendicesTable = new JTable(detailAprendicesModel);
        detailAprendicesTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        detailAprendicesTable.setForeground(TXT_PRI);
        detailAprendicesTable.setBackground(CARD_BG);
        detailAprendicesTable.setGridColor(new Color(0x22, 0x22, 0x22));
        detailAprendicesTable.setRowHeight(38);
        detailAprendicesTable.setShowHorizontalLines(true);
        detailAprendicesTable.setShowVerticalLines(false);
        detailAprendicesTable.setIntercellSpacing(new Dimension(0, 1));
        detailAprendicesTable.setSelectionBackground(new Color(0x26, 0x36, 0x46));
        detailAprendicesTable.setSelectionForeground(Color.WHITE);
        detailAprendicesTable.setFillsViewportHeight(true);
        detailAprendicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Hide ID column
        detailAprendicesTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        detailAprendicesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        detailAprendicesTable.getColumnModel().getColumn(0).setMinWidth(0);
        {
            JTableHeader hdr = detailAprendicesTable.getTableHeader();
            hdr.setFont(new Font("Segoe UI", Font.BOLD, 10));
            hdr.setForeground(TXT_SEC);
            hdr.setBackground(new Color(0x14, 0x14, 0x14));
            hdr.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_DK));
            hdr.setPreferredSize(new Dimension(0, 36));
        }

        // Progreso column (col 5)
        detailAprendicesTable.getColumnModel().getColumn(5).setCellRenderer(
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                JPanel p = new JPanel(new BorderLayout());
                p.setOpaque(false);
                int pct = val instanceof Number ? ((Number)val).intValue() : 0;
                JProgressBar bar = new JProgressBar(0, 100) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        int h = 6;
                        g2.setColor(new Color(0x2a, 0x2a, 0x2a));
                        g2.fillRoundRect(0, (getHeight()-h)/2, getWidth(), h, h, h);
                        int fw = pct * getWidth() / 100;
                        if (fw > 0) {
                            g2.setColor(pct >= 100 ? GREEN : pct >= 50 ? ORANGE : BLUE);
                            g2.fillRoundRect(0, (getHeight()-h)/2, fw, h, h, h);
                        }
                        g2.dispose();
                    }
                };
                bar.setOpaque(false);
                bar.setPreferredSize(new Dimension(80, 14));
                bar.setValue(pct);
                JLabel lbl = new JLabel(pct + "%");
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lbl.setForeground(pct >= 100 ? GREEN : pct >= 50 ? ORANGE : BLUE);
                lbl.setPreferredSize(new Dimension(35, 14));
                p.add(bar,  BorderLayout.CENTER);
                p.add(lbl, BorderLayout.EAST);
                if (sel) p.setBackground(new Color(0x26, 0x36, 0x46));
                return p;
            }
        });

        // Estado column (col 6)
        detailAprendicesTable.getColumnModel().getColumn(6).setCellRenderer(
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                String estado = val != null ? val.toString() : "";
                l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Completado".equals(estado)) {
                    l.setForeground(GREEN);
                    l.setBackground(new Color(0x22, 0xc5, 0x5e, 20));
                } else {
                    l.setForeground(ORANGE);
                    l.setBackground(new Color(0xf5, 0x9e, 0x0b, 20));
                }
                l.setOpaque(true);
                l.setBorder(new EmptyBorder(3, 8, 3, 8));
                return l;
            }
        });

        // Alt row colors
        detailAprendicesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) setBackground(row % 2 == 0 ? CARD_BG : ROW_ALT);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                setFont(new Font("Segoe UI", Font.PLAIN, 11));
                setForeground(TXT_PRI);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(detailAprendicesTable);
        scroll.setBorder(new LineBorder(BORDER_DK, 1, true));
        scroll.getViewport().setBackground(CARD_BG);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ── Toolbar helper ──
    private JButton toolbarBtn(String text, Color color) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 40)
                        : new Color(color.getRed(), color.getGreen(), color.getBlue(), 15);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(color);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(100, 28));
        return b;
    }

    // ── Editar aprendiz ──
    private void editarAprendizSeleccionado() {
        int row = detailAprendicesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un aprendiz de la tabla.",
                    "Editar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = detailAprendicesTable.convertRowIndexToModel(row);
        int idAp = (int) detailAprendicesModel.getValueAt(modelRow, 0);
        String nombre = (String) detailAprendicesModel.getValueAt(modelRow, 1);
        int hCumplidas = (int) detailAprendicesModel.getValueAt(modelRow, 3);
        int hReq = (int) detailAprendicesModel.getValueAt(modelRow, 4);
        String estado = (String) detailAprendicesModel.getValueAt(modelRow, 6);

        JTextField txtHorasC = new JTextField(String.valueOf(hCumplidas));
        JTextField txtHorasR = new JTextField(String.valueOf(hReq));
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"En proceso", "Completado"});
        cmbEstado.setSelectedItem(estado);
        txtHorasC.setBackground(INPUT_BG); txtHorasC.setForeground(TXT_PRI); txtHorasC.setCaretColor(TXT_PRI);
        txtHorasR.setBackground(INPUT_BG); txtHorasR.setForeground(TXT_PRI); txtHorasR.setCaretColor(TXT_PRI);
        cmbEstado.setBackground(INPUT_BG); cmbEstado.setForeground(TXT_PRI);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8); gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel l1 = new JLabel("Horas Cumplidas:"); l1.setForeground(TXT_SEC); form.add(l1, gbc);
        gbc.gridx = 1; form.add(txtHorasC, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel("Horas Requeridas:"); l2.setForeground(TXT_SEC); form.add(l2, gbc);
        gbc.gridx = 1; form.add(txtHorasR, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel l3 = new JLabel("Estado:"); l3.setForeground(TXT_SEC); form.add(l3, gbc);
        gbc.gridx = 1; form.add(cmbEstado, gbc);

        int result = JOptionPane.showConfirmDialog(this, form, "Editar Aprendiz: " + nombre,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            int newHC = Integer.parseInt(txtHorasC.getText().trim());
            int newHR = Integer.parseInt(txtHorasR.getText().trim());
            String newEstado = (String) cmbEstado.getSelectedItem();
            if (newHC < 0 || newHR <= 0) throw new NumberFormatException();

            Connection conn = controlador.Conexion.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE aprendiz SET horas_cumplidas=?, horas_requeridas=?, estado_practica=? WHERE id_aprendiz=?");
            ps.setInt(1, newHC); ps.setInt(2, newHR); ps.setString(3, newEstado); ps.setInt(4, idAp);
            ps.executeUpdate(); ps.close(); conn.close();
            cargarFichaDetalle(detailIdCurso, detailFicha, detailNombre);
            cargarStats();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores inv\u00e1lidos. Las horas deben ser n\u00fameros enteros.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Eliminar aprendiz ──
    private void eliminarAprendizSeleccionado() {
        int row = detailAprendicesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un aprendiz de la tabla.",
                    "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = detailAprendicesTable.convertRowIndexToModel(row);
        int idAp = (int) detailAprendicesModel.getValueAt(modelRow, 0);
        String nombre = (String) detailAprendicesModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "\u00bfEst\u00e1 seguro de eliminar al aprendiz " + nombre + "?\n"
                + "Se eliminar\u00e1n todas sus evidencias y registros asociados.",
                "Eliminar Aprendiz", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        new Thread(() -> {
            try {
                Connection conn = controlador.Conexion.getInstance().getConnection();
                conn.setAutoCommit(false);
                try {
                    PreparedStatement ps1 = conn.prepareStatement("DELETE FROM curso_aprendiz WHERE id_aprendiz=?");
                    ps1.setInt(1, idAp); ps1.executeUpdate(); ps1.close();
                    PreparedStatement ps2 = conn.prepareStatement("DELETE FROM evidencia WHERE id_aprendiz=?");
                    ps2.setInt(1, idAp); ps2.executeUpdate(); ps2.close();
                    PreparedStatement ps3 = conn.prepareStatement("DELETE FROM aprendiz WHERE id_aprendiz=?");
                    ps3.setInt(1, idAp); ps3.executeUpdate(); ps3.close();
                    conn.commit();
                } catch (Exception ex2) { conn.rollback(); throw ex2; }
                conn.close();
                SwingUtilities.invokeLater(() -> {
                    cargarFichaDetalle(detailIdCurso, detailFicha, detailNombre);
                    cargarStats();
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    // ── Agregar aprendiz ──
    private void mostrarDialogoAgregarAprendiz() {
        String correo = JOptionPane.showInputDialog(this,
                "Ingrese el correo del aprendiz que desea agregar a la ficha:",
                "Agregar Aprendiz", JOptionPane.PLAIN_MESSAGE);
        if (correo == null || correo.trim().isEmpty()) return;

        new Thread(() -> {
            try {
                Connection conn = controlador.Conexion.getInstance().getConnection();

                // Buscar usuario con rol aprendiz
                PreparedStatement psU = conn.prepareStatement(
                        "SELECT u.id_usuario FROM usuario u "
                        + "JOIN usuario_rol ur ON u.id_usuario = ur.id_usuario "
                        + "WHERE u.correo = ? AND ur.id_rol = 1");
                psU.setString(1, correo.trim());
                ResultSet rsU = psU.executeQuery();
                if (!rsU.next()) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                            "No se encontr\u00f3 un aprendiz con ese correo.", "Error",
                            JOptionPane.ERROR_MESSAGE));
                    psU.close(); conn.close(); return;
                }
                int idUsr = rsU.getInt(1);
                psU.close();

                // Verificar si ya existe perfil aprendiz
                int idAp = -1;
                PreparedStatement psA = conn.prepareStatement(
                        "SELECT id_aprendiz FROM aprendiz WHERE id_usuario = ?");
                psA.setInt(1, idUsr);
                ResultSet rsA = psA.executeQuery();
                if (rsA.next()) {
                    idAp = rsA.getInt(1);
                } else {
                    PreparedStatement psIns = conn.prepareStatement(
                            "INSERT INTO aprendiz (id_usuario, ficha, estado_practica, horas_requeridas, horas_cumplidas) "
                            + "VALUES (?, ?, 'En proceso', 0, 0)", Statement.RETURN_GENERATED_KEYS);
                    psIns.setInt(1, idUsr);
                    psIns.setString(2, detailFicha);
                    psIns.executeUpdate();
                    ResultSet gen = psIns.getGeneratedKeys();
                    if (gen.next()) idAp = gen.getInt(1);
                    gen.close(); psIns.close();
                }
                rsA.close(); psA.close();

                if (idAp < 0) { conn.close(); return; }

                // Vincular a la ficha
                PreparedStatement psCA = conn.prepareStatement(
                        "INSERT INTO curso_aprendiz (id_curso, id_aprendiz) VALUES (?, ?) "
                        + "ON CONFLICT DO NOTHING");
                psCA.setInt(1, detailIdCurso);
                psCA.setInt(2, idAp);
                psCA.executeUpdate();
                psCA.close();
                conn.close();

                SwingUtilities.invokeLater(() -> {
                    cargarFichaDetalle(detailIdCurso, detailFicha, detailNombre);
                    cargarStats();
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Error al agregar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private JPanel buildDetailEvidenciasPanel() {
        JPanel panel = roundCard();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        // ── Toolbar ──
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel title = new JLabel("\uD83D\uDCDD  Evidencias de la Ficha");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(TXT_PRI);
        toolbar.add(title, BorderLayout.WEST);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionBtns.setOpaque(false);
        JButton btnView = toolbarBtn("\uD83D\uDCC4 Ver", BLUE);
        JButton btnApprove = toolbarBtn("\u2705 Aprobar", GREEN);
        JButton btnReject = toolbarBtn("\u274C Rechazar", RED);
        btnView.addActionListener(e -> verEvidenciaDetalle());
        btnApprove.addActionListener(e -> aprobarEvidenciaDetalle());
        btnReject.addActionListener(e -> rechazarEvidenciaDetalle());
        actionBtns.add(btnView);
        actionBtns.add(btnApprove);
        actionBtns.add(btnReject);
        toolbar.add(actionBtns, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // ── Table ──
        String[] cols = {"ID", "Aprendiz", "Tipo", "Fecha Entrega", "Estado", "Observaciones"};
        detailEvidenciasModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        detailEvidenciasTable = styleTable(detailEvidenciasModel);
        detailEvidenciasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        detailEvidenciasTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        detailEvidenciasTable.getColumnModel().getColumn(0).setMaxWidth(50);

        // Estado column renderer
        detailEvidenciasTable.getColumnModel().getColumn(4).setCellRenderer(
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                String estado = val != null ? val.toString() : "";
                l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setOpaque(true);
                l.setBorder(new EmptyBorder(3, 8, 3, 8));
                switch (estado) {
                    case "Aprobada":
                        l.setForeground(GREEN);
                        l.setBackground(new Color(0x22, 0xc5, 0x5e, 20));
                        break;
                    case "No Aprobada":
                        l.setForeground(RED);
                        l.setBackground(new Color(0xef, 0x44, 0x44, 20));
                        break;
                    default:
                        l.setForeground(ORANGE);
                        l.setBackground(new Color(0xf5, 0x9e, 0x0b, 20));
                        break;
                }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(detailEvidenciasTable);
        scroll.setBorder(new LineBorder(BORDER_DK, 1, true));
        scroll.getViewport().setBackground(CARD_BG);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ── Evidence actions ──
    private void verEvidenciaDetalle() {
        int row = detailEvidenciasTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una evidencia.", "Ver", JOptionPane.WARNING_MESSAGE); return; }
        int mRow = detailEvidenciasTable.convertRowIndexToModel(row);
        String tipo = (String) detailEvidenciasModel.getValueAt(mRow, 2);
        String nombre = (String) detailEvidenciasModel.getValueAt(mRow, 1);
        String estado = (String) detailEvidenciasModel.getValueAt(mRow, 4);
        String obs = detailEvidenciasModel.getValueAt(mRow, 5) != null ? detailEvidenciasModel.getValueAt(mRow, 5).toString() : "";

        int idEv = (int) detailEvidenciasModel.getValueAt(mRow, 0);
        String conteReal = "";
        try {
            Connection c = controlador.Conexion.getInstance().getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT contenido FROM evidencia WHERE id_evidencia=?");
            ps.setInt(1, idEv); ResultSet rs = ps.executeQuery();
            if (rs.next()) conteReal = rs.getString("contenido");
            rs.close(); ps.close(); c.close();
        } catch (Exception ex) { }

        JTextArea ta = new JTextArea("Tipo: " + tipo
                + "\nEstado: " + estado
                + "\nContenido:\n" + (conteReal != null ? conteReal : "Sin contenido")
                + "\n\nObservaciones:\n" + (obs != null && !obs.isEmpty() ? obs : "Sin observaciones"));
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setBackground(CARD_BG);
        ta.setForeground(TXT_PRI);
        ta.setCaretColor(TXT_PRI);
        ta.setBorder(new EmptyBorder(12, 12, 12, 12));
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(500, 350));
        sp.setBorder(new LineBorder(BORDER_DK));
        JOptionPane.showMessageDialog(this, sp, "Evidencia de " + nombre, JOptionPane.PLAIN_MESSAGE);
    }

    private void aprobarEvidenciaDetalle() {
        int row = detailEvidenciasTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una evidencia.", "Aprobar", JOptionPane.WARNING_MESSAGE); return; }
        int mRow = detailEvidenciasTable.convertRowIndexToModel(row);
        int idEv = (int) detailEvidenciasModel.getValueAt(mRow, 0);
        String nombre = (String) detailEvidenciasModel.getValueAt(mRow, 1);
        String obs = JOptionPane.showInputDialog(this, "Observaciones para " + nombre + " (aprobada):", "Aprobar Evidencia", JOptionPane.PLAIN_MESSAGE);
        if (obs == null) return;
        ejecutarAprobarRechazar(idEv, "Aprobada", obs);
    }

    private void rechazarEvidenciaDetalle() {
        int row = detailEvidenciasTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una evidencia.", "Rechazar", JOptionPane.WARNING_MESSAGE); return; }
        int mRow = detailEvidenciasTable.convertRowIndexToModel(row);
        int idEv = (int) detailEvidenciasModel.getValueAt(mRow, 0);
        String nombre = (String) detailEvidenciasModel.getValueAt(mRow, 1);
        String obs = JOptionPane.showInputDialog(this, "Retroalimentaci\u00f3n para " + nombre + " (rechazada):", "Rechazar Evidencia", JOptionPane.WARNING_MESSAGE);
        if (obs == null || obs.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe indicar el motivo del rechazo.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ejecutarAprobarRechazar(idEv, "No Aprobada", obs);
    }

    private void ejecutarAprobarRechazar(int idEv, String nuevoEstado, String obs) {
        new Thread(() -> {
            try {
                Connection conn = controlador.Conexion.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE evidencia SET estado=?, observaciones=? WHERE id_evidencia=?");
                ps.setString(1, nuevoEstado); ps.setString(2, obs); ps.setInt(3, idEv);
                ps.executeUpdate(); ps.close(); conn.close();
                SwingUtilities.invokeLater(() -> {
                    cargarFichaDetalle(detailIdCurso, detailFicha, detailNombre);
                    cargarEvidenciasPendientes();
                    cargarStats();
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    // ══════════════════════════════════════════════════════════════
    //  4. EVIDENCIAS PENDIENTES (all)
    // ══════════════════════════════════════════════════════════════
    private JPanel buildEvidenciasPendientesView() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(22, 22, 22, 22));

        // ── Toolbar ──
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JLabel title = new JLabel("Evidencias Pendientes de Revisi\u00f3n");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TXT_PRI);
        toolbar.add(title, BorderLayout.WEST);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionBtns.setOpaque(false);
        JButton btnView = toolbarBtn("\uD83D\uDCC4 Ver", BLUE);
        JButton btnApprove = toolbarBtn("\u2705 Aprobar", GREEN);
        JButton btnReject = toolbarBtn("\u274C Rechazar", RED);
        btnView.addActionListener(e -> verEvidenciaPendiente());
        btnApprove.addActionListener(e -> aprobarEvidenciaPendiente());
        btnReject.addActionListener(e -> rechazarEvidenciaPendiente());
        actionBtns.add(btnView);
        actionBtns.add(btnApprove);
        actionBtns.add(btnReject);
        toolbar.add(actionBtns, BorderLayout.EAST);

        body.add(toolbar, BorderLayout.NORTH);

        // ── Table ──
        String[] cols = {"ID", "Aprendiz", "Ficha", "Tipo", "Fecha Entrega", "Estado"};
        evidenciasPendientesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        evidenciasPendientesTable = styleTable(evidenciasPendientesModel);
        evidenciasPendientesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        evidenciasPendientesTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        evidenciasPendientesTable.getColumnModel().getColumn(0).setMaxWidth(50);

        evidenciasPendientesTable.getColumnModel().getColumn(5).setCellRenderer(
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                String estado = val != null ? val.toString() : "";
                l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setOpaque(true);
                l.setBorder(new EmptyBorder(3, 8, 3, 8));
                l.setForeground(ORANGE);
                l.setBackground(new Color(0xf5, 0x9e, 0x0b, 20));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(evidenciasPendientesTable);
        scroll.setBorder(new LineBorder(BORDER_DK, 1, true));
        scroll.getViewport().setBackground(CARD_BG);
        body.add(scroll, BorderLayout.CENTER);
        return body;
    }

    // ── Pending evidence actions ──
    private void verEvidenciaPendiente() {
        int row = evidenciasPendientesTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una evidencia.", "Ver", JOptionPane.WARNING_MESSAGE); return; }
        int mRow = evidenciasPendientesTable.convertRowIndexToModel(row);
        String tipo = (String) evidenciasPendientesModel.getValueAt(mRow, 3);
        String nombre = (String) evidenciasPendientesModel.getValueAt(mRow, 1);
        String estado = (String) evidenciasPendientesModel.getValueAt(mRow, 5);
        int idEv = (int) evidenciasPendientesModel.getValueAt(mRow, 0);
        String conteReal = "";
        try {
            Connection c = controlador.Conexion.getInstance().getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT contenido FROM evidencia WHERE id_evidencia=?");
            ps.setInt(1, idEv); ResultSet rs = ps.executeQuery();
            if (rs.next()) conteReal = rs.getString("contenido");
            rs.close(); ps.close(); c.close();
        } catch (Exception ex) { }

        JTextArea ta = new JTextArea("Tipo: " + tipo
                + "\nEstado: " + estado
                + "\nContenido:\n" + (conteReal != null ? conteReal : "Sin contenido"));
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setBackground(CARD_BG);
        ta.setForeground(TXT_PRI);
        ta.setCaretColor(TXT_PRI);
        ta.setBorder(new EmptyBorder(12, 12, 12, 12));
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(500, 350));
        sp.setBorder(new LineBorder(BORDER_DK));
        JOptionPane.showMessageDialog(this, sp, "Evidencia de " + nombre, JOptionPane.PLAIN_MESSAGE);
    }

    private void aprobarEvidenciaPendiente() {
        int row = evidenciasPendientesTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una evidencia.", "Aprobar", JOptionPane.WARNING_MESSAGE); return; }
        int mRow = evidenciasPendientesTable.convertRowIndexToModel(row);
        int idEv = (int) evidenciasPendientesModel.getValueAt(mRow, 0);
        String nombre = (String) evidenciasPendientesModel.getValueAt(mRow, 1);
        String obs = JOptionPane.showInputDialog(this, "Observaciones para " + nombre + " (aprobada):", "Aprobar Evidencia", JOptionPane.PLAIN_MESSAGE);
        if (obs == null) return;
        new Thread(() -> {
            try {
                Connection conn = controlador.Conexion.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE evidencia SET estado='Aprobada', observaciones=? WHERE id_evidencia=?");
                ps.setString(1, obs); ps.setInt(2, idEv);
                ps.executeUpdate(); ps.close(); conn.close();
                SwingUtilities.invokeLater(() -> { cargarEvidenciasPendientes(); cargarStats(); });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void rechazarEvidenciaPendiente() {
        int row = evidenciasPendientesTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Seleccione una evidencia.", "Rechazar", JOptionPane.WARNING_MESSAGE); return; }
        int mRow = evidenciasPendientesTable.convertRowIndexToModel(row);
        int idEv = (int) evidenciasPendientesModel.getValueAt(mRow, 0);
        String nombre = (String) evidenciasPendientesModel.getValueAt(mRow, 1);
        String obs = JOptionPane.showInputDialog(this, "Retroalimentaci\u00f3n para " + nombre + " (rechazada):", "Rechazar Evidencia", JOptionPane.WARNING_MESSAGE);
        if (obs == null || obs.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe indicar el motivo del rechazo.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new Thread(() -> {
            try {
                Connection conn = controlador.Conexion.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE evidencia SET estado='No Aprobada', observaciones=? WHERE id_evidencia=?");
                ps.setString(1, obs); ps.setInt(2, idEv);
                ps.executeUpdate(); ps.close(); conn.close();
                SwingUtilities.invokeLater(() -> { cargarEvidenciasPendientes(); cargarStats(); });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    // ══════════════════════════════════════════════════════════════
    //  APRENDIZ ACTION CELL (Editar / Eliminar)
    // ══════════════════════════════════════════════════════════════
    // ══════════════════════════════════════════════════════════════
    //  VISTA GENÉRICA
    // ══════════════════════════════════════════════════════════════
    private JPanel buildSimpleView(String name, String msg) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG);
        JLabel l = new JLabel(name + " \u2014 " + msg);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(TXT_SEC);
        p.add(l);
        return p;
    }

    // ══════════════════════════════════════════════════════════════
    //  CARGA DE DATOS
    // ══════════════════════════════════════════════════════════════
    private void cargarDatos() {
        lblNombre.setText(controlador.Sesion.getNombreCompleto());
        lblCorreo.setText(controlador.Sesion.getCorreo() != null
                ? controlador.Sesion.getCorreo() : "—");

        new Thread(() -> {
            try {
                Connection conn = controlador.Conexion.getInstance().getConnection();
                int idUsr = controlador.Sesion.getIdUsuario();
                int idInst = -1;

                // Buscar perfil instructor
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id_instructor, area_formacion, activo FROM instructor WHERE id_usuario = ?")) {
                    ps.setInt(1, idUsr);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            idInst = rs.getInt("id_instructor");
                            String area = rs.getString("area_formacion");
                            boolean activo = rs.getBoolean("activo");
                            SwingUtilities.invokeLater(() -> {
                                lblArea.setText(area != null && !area.isBlank() ? area : "No especificada");
                                if (!activo) {
                                    lblEstado.setText("\u25CF  INACTIVO");
                                    lblEstado.setForeground(RED);
                                    lblEstado.setBackground(new Color(0xef, 0x44, 0x44, 25));
                                }
                            });
                        }
                    }
                }

                if (idInst < 0) { conn.close(); return; }
                idInstructor = idInst;

                conn.close();
                cargarStats();
                cargarFichas();
                cargarEvidenciasPendientes();

            } catch (Exception ex) {
                System.err.println("[INSTRUCTOR] " + ex.getMessage());
            }
        }).start();
    }

    private void cargarStats() {
        new Thread(() -> {
            try {
                Connection conn = controlador.Conexion.getInstance().getConnection();

                // Total aprendices
                int aprendices = 0;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(DISTINCT ca.id_aprendiz) FROM curso_aprendiz ca "
                        + "JOIN curso_instructor ci ON ca.id_curso = ci.id_curso WHERE ci.id_instructor = ?")) {
                    ps.setInt(1, idInstructor);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) aprendices = rs.getInt(1); }
                }

                // Total fichas
                int fichas = 0;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(DISTINCT ci.id_curso) FROM curso_instructor ci WHERE ci.id_instructor = ?")) {
                    ps.setInt(1, idInstructor);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) fichas = rs.getInt(1); }
                }

                // Evidencias pendientes
                int pendientes = 0;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(*) FROM evidencia ev "
                        + "JOIN aprendiz a ON ev.id_aprendiz = a.id_aprendiz "
                        + "JOIN curso_aprendiz ca ON a.id_aprendiz = ca.id_aprendiz "
                        + "JOIN curso_instructor ci ON ca.id_curso = ci.id_curso "
                        + "WHERE ci.id_instructor = ? AND ev.estado = 'Entregada'")) {
                    ps.setInt(1, idInstructor);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) pendientes = rs.getInt(1); }
                }

                // Promedio progreso
                double promedio = 0;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COALESCE(AVG(sub.pct), 0) as prom FROM ("
                        + "  SELECT LEAST(100, CAST(COALESCE(SUM(ev.horas_validadas),0) AS FLOAT)"
                        + "         / NULLIF(c.horas_requeridas,0) * 100) as pct"
                        + "  FROM curso_aprendiz ca"
                        + "  JOIN curso c ON ca.id_curso = c.id_curso"
                        + "  JOIN curso_instructor ci ON c.id_curso = ci.id_curso"
                        + "  LEFT JOIN evidencia ev ON ca.id_aprendiz = ev.id_aprendiz AND ev.estado = 'Aprobada'"
                        + "  WHERE ci.id_instructor = ?"
                        + "  GROUP BY ca.id_aprendiz, ca.id_curso, c.horas_requeridas"
                        + ") sub")) {
                    ps.setInt(1, idInstructor);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) promedio = rs.getDouble("prom");
                    }
                }
                conn.close();

                final int fA = aprendices, fP = pendientes, fF = fichas;
                final double fProm = promedio;
                SwingUtilities.invokeLater(() -> {
                    lblAprendices.setText(String.valueOf(fA));
                    lblPendientes.setText(String.valueOf(fP));
                    lblFichas.setText(String.valueOf(fF));
                    int pct = (int) Math.round(fProm);
                    lblProgreso.setText(pct + "%");
                    progBar.setValue(pct);
                });

            } catch (Exception ex) {
                System.err.println("[INSTRUCTOR-STATS] " + ex.getMessage());
            }
        }).start();
    }

    private void cargarFichas() {
        new Thread(() -> {
            try {
                Connection conn = controlador.Conexion.getInstance().getConnection();
                List<Object[]> rows = new ArrayList<>();

                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT c.id_curso, c.ficha, c.nombre, c.fecha_inicio, c.fecha_fin, "
                        + "(SELECT COUNT(DISTINCT ca2.id_aprendiz) FROM curso_aprendiz ca2 "
                        + " WHERE ca2.id_curso = c.id_curso) as total_aprendices, "
                        + "(SELECT COUNT(*) FROM evidencia ev "
                        + " JOIN aprendiz a2 ON ev.id_aprendiz = a2.id_aprendiz "
                        + " JOIN curso_aprendiz ca3 ON a2.id_aprendiz = ca3.id_aprendiz "
                        + " WHERE ca3.id_curso = c.id_curso AND ev.estado = 'Entregada') as pendientes "
                        + "FROM curso c "
                        + "JOIN curso_instructor ci ON c.id_curso = ci.id_curso "
                        + "WHERE ci.id_instructor = ? "
                        + "ORDER BY c.nombre")) {
                    ps.setInt(1, idInstructor);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            rows.add(new Object[]{
                                rs.getString("ficha"),
                                rs.getString("nombre"),
                                rs.getDate("fecha_inicio") != null
                                        ? sdf.format(rs.getDate("fecha_inicio")) : "—",
                                rs.getDate("fecha_fin") != null
                                        ? sdf.format(rs.getDate("fecha_fin")) : "—",
                                rs.getInt("total_aprendices"),
                                rs.getInt("pendientes"),
                                "Ver Detalle"
                            });
                        }
                    }
                }
                conn.close();

                SwingUtilities.invokeLater(() -> {
                    fichasModel.setRowCount(0);
                    for (Object[] row : rows) {
                        fichasModel.addRow(row);
                    }
                });

            } catch (Exception ex) {
                System.err.println("[INSTRUCTOR-FICHAS] " + ex.getMessage());
            }
        }).start();
    }

    private void cargarEvidenciasPendientes() {
        new Thread(() -> {
            try {
                Connection conn = controlador.Conexion.getInstance().getConnection();
                List<Object[]> rows = new ArrayList<>();

                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT ev.id_evidencia, u.nombres || ' ' || COALESCE(u.apellidos, ''), "
                        + "a.ficha, ev.tipo, ev.fecha_entrega, ev.estado "
                        + "FROM evidencia ev "
                        + "JOIN aprendiz a ON ev.id_aprendiz = a.id_aprendiz "
                        + "JOIN usuario u ON a.id_usuario = u.id_usuario "
                        + "JOIN curso_aprendiz ca ON a.id_aprendiz = ca.id_aprendiz "
                        + "JOIN curso_instructor ci ON ca.id_curso = ci.id_curso "
                        + "WHERE ci.id_instructor = ? AND ev.estado = 'Entregada' "
                        + "ORDER BY ev.fecha_entrega DESC")) {
                    ps.setInt(1, idInstructor);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            rows.add(new Object[]{
                                rs.getInt(1), rs.getString(2), rs.getString(3),
                                rs.getString(4),
                                rs.getTimestamp(5) != null
                                        ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp(5)) : "—",
                                rs.getString(6)
                            });
                        }
                    }
                }
                conn.close();

                SwingUtilities.invokeLater(() -> {
                    evidenciasPendientesModel.setRowCount(0);
                    for (Object[] row : rows) {
                        evidenciasPendientesModel.addRow(row);
                    }
                });

            } catch (Exception ex) {
                System.err.println("[INSTRUCTOR-EVID] " + ex.getMessage());
            }
        }).start();
    }

    private void cargarFichaDetalle(int idCurso, String ficha, String nombre) {
        detailIdCurso = idCurso;
        detailFicha = ficha;
        detailNombre = nombre;

        SwingUtilities.invokeLater(() -> {
            detailInfoLabel.setText("Ficha: " + ficha + " \u2014 " + nombre);
        });

        new Thread(() -> {
            try {
                Connection conn = controlador.Conexion.getInstance().getConnection();

                // Cargar aprendices de la ficha
                List<Object[]> rows = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT a.id_aprendiz, "
                        + "u.nombres || ' ' || COALESCE(u.apellidos, ''), "
                        + "COALESCE(e.nombre, 'Sin empresa'), "
                        + "a.horas_cumplidas, a.horas_requeridas, "
                        + "CASE WHEN a.horas_requeridas > 0 "
                        + "  THEN LEAST(100, ROUND(a.horas_cumplidas::float / a.horas_requeridas * 100)) "
                        + "  ELSE 0 END as progreso, "
                        + "a.estado_practica "
                        + "FROM aprendiz a "
                        + "JOIN usuario u ON a.id_usuario = u.id_usuario "
                        + "LEFT JOIN empresa e ON a.id_empresa = e.id_empresa "
                        + "JOIN curso_aprendiz ca ON a.id_aprendiz = ca.id_aprendiz "
                        + "WHERE ca.id_curso = ? "
                        + "ORDER BY u.nombres")) {
                    ps.setInt(1, idCurso);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            rows.add(new Object[]{
                                rs.getInt(1), rs.getString(2), rs.getString(3),
                                rs.getInt(4), rs.getInt(5),
                                rs.getInt(6), rs.getString(7)
                            });
                        }
                    }
                }

                final int totalAprendices = rows.size();

                // Cargar evidencias de la ficha
                List<Object[]> evRows = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT ev.id_evidencia, u.nombres || ' ' || COALESCE(u.apellidos, ''), "
                        + "ev.tipo, ev.fecha_entrega, ev.estado, COALESCE(ev.observaciones, ''), "
                        + "COALESCE(ev.contenido, '') "
                        + "FROM evidencia ev "
                        + "JOIN aprendiz a ON ev.id_aprendiz = a.id_aprendiz "
                        + "JOIN usuario u ON a.id_usuario = u.id_usuario "
                        + "JOIN curso_aprendiz ca ON a.id_aprendiz = ca.id_aprendiz "
                        + "WHERE ca.id_curso = ? "
                        + "ORDER BY ev.fecha_entrega DESC")) {
                    ps.setInt(1, idCurso);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            evRows.add(new Object[]{
                                rs.getInt(1), rs.getString(2), rs.getString(3),
                                rs.getTimestamp(4) != null
                                        ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(rs.getTimestamp(4)) : "—",
                                rs.getString(5), rs.getString(6), rs.getString(7)
                            });
                        }
                    }
                }

                int pendientesEv = 0;
                for (Object[] r : evRows) {
                    if ("Entregada".equals(r[4])) pendientesEv++;
                }

                conn.close();

                final int fPendientes = pendientesEv;
                final List<Object[]> fRows = rows;
                final List<Object[]> fEvRows = evRows;

                SwingUtilities.invokeLater(() -> {
                    detailAprendicesLabel.setText(String.valueOf(totalAprendices));
                    detailPendientesLabel.setText(String.valueOf(fPendientes));

                    detailAprendicesModel.setRowCount(0);
                    for (Object[] r : fRows) detailAprendicesModel.addRow(r);

                    detailEvidenciasModel.setRowCount(0);
                    for (Object[] r : fEvRows) detailEvidenciasModel.addRow(r);
                });

            } catch (Exception ex) {
                System.err.println("[INSTRUCTOR-DETAIL] " + ex.getMessage());
            }
        }).start();
    }

    // ══════════════════════════════════════════════════════════════
    //  TABLE STYLING
    // ══════════════════════════════════════════════════════════════
    private JTable styleTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public boolean isCellEditable(int row, int col) {
                if (col == 6) return true;
                return false;
            }
        };
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setForeground(TXT_PRI);
        table.setBackground(CARD_BG);
        table.setGridColor(new Color(0x22, 0x22, 0x22));
        table.setRowHeight(38);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(0x26, 0x36, 0x46));
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 10));
        header.setForeground(TXT_SEC);
        header.setBackground(new Color(0x14, 0x14, 0x14));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_DK));
        header.setPreferredSize(new Dimension(0, 36));

        // Alternate row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? CARD_BG : ROW_ALT);
                }
                setBorder(new EmptyBorder(4, 8, 4, 8));
                setFont(new Font("Segoe UI", Font.PLAIN, 11));
                setForeground(TXT_PRI);
                return this;
            }
        });

        // "Ver Detalle" button column
        if (model == fichasModel) {
            table.getColumnModel().getColumn(6).setCellRenderer(
                    new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object val,
                        boolean sel, boolean focus, int row, int col) {
                    JButton b = new JButton("Ver Detalle") {
                        @Override protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                            Color c = getModel().isRollover() ? BLUE : new Color(0x3b, 0x82, 0xf6, 100);
                            g2.setColor(c);
                            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                            g2.dispose();
                            super.paintComponent(g);
                        }
                    };
                    b.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    b.setForeground(Color.WHITE);
                    b.setContentAreaFilled(false);
                    b.setBorderPainted(false);
                    b.setFocusPainted(false);
                    b.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    b.setPreferredSize(new Dimension(90, 24));
                    if (sel) b.setBackground(new Color(0x26, 0x36, 0x46));
                    return b;
                }
            });

            table.getColumnModel().getColumn(6).setCellEditor(
                    new DefaultCellEditor(new JCheckBox()) {
                private JButton btn;
                {
                    btn = new JButton("Ver Detalle");
                    btn.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    btn.setForeground(Color.WHITE);
                    btn.setBackground(BLUE);
                    btn.setBorderPainted(false);
                    btn.setFocusPainted(false);
                    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    btn.setPreferredSize(new Dimension(90, 24));
                    btn.addActionListener(e -> {
                        int row = table.getEditingRow();
                        if (row < 0) return;
                        int modelRow = table.convertRowIndexToModel(row);
                        int idCurso = -1;
                        try {
                            Connection conn = controlador.Conexion.getInstance().getConnection();
                            String ficha = model.getValueAt(modelRow, 0).toString();
                            try (PreparedStatement ps = conn.prepareStatement(
                                    "SELECT id_curso FROM curso WHERE ficha = ?")) {
                                ps.setString(1, ficha);
                                try (ResultSet rs = ps.executeQuery()) {
                                    if (rs.next()) idCurso = rs.getInt("id_curso");
                                }
                            }
                            conn.close();
                        } catch (Exception ex) { /* ignore */ }
                        String ficha = model.getValueAt(modelRow, 0).toString();
                        String nombre = model.getValueAt(modelRow, 1).toString();
                        cargarFichaDetalle(idCurso, ficha, nombre);
                        contentLayout.show(contentStack, "FichaDetalle");
                        fireEditingStopped();
                    });
                }
                @Override
                public Component getTableCellEditorComponent(JTable t, Object val,
                        boolean sel, int row, int col) {
                    return btn;
                }
                @Override
                public Object getCellEditorValue() { return "Ver Detalle"; }
            });
        }

        return table;
    }

    // ══════════════════════════════════════════════════════════════
    //  UTIL
    // ══════════════════════════════════════════════════════════════
    private JPanel roundCard() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.setColor(BORDER_DK);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }
}
