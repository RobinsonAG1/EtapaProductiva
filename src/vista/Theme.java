package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class Theme {

    public static final Color BG_DARK = new Color(0x0a, 0x0a, 0x0a);
    public static final Color BG_SIDEBAR = new Color(0x14, 0x14, 0x14);
    public static final Color BG_CARD = new Color(0x11, 0x11, 0x11);
    public static final Color BG_INNER_BTN = new Color(0x1a, 0x1a, 0x1a);
    public static final Color GREEN = new Color(0x22, 0xc5, 0x5e);
    public static final Color BLUE = new Color(0x3b, 0x82, 0xf6);
    public static final Color ORANGE = new Color(0xf5, 0x9e, 0x0b);
    public static final Color PURPLE = new Color(0xa8, 0x55, 0xf7);
    public static final Color RED = new Color(0xef, 0x44, 0x44);
    public static final Color TXT_PRIMARY = new Color(0xf0, 0xf0, 0xf0);
    public static final Color TXT_SECONDARY = new Color(0x9c, 0xa3, 0xaf);
    public static final Color TXT_DIM = new Color(0x6b, 0x72, 0x80);
    public static final Color TXT_DISABLED = new Color(0x52, 0x55, 0x5b);
    public static final Color BORDER = new Color(0x22, 0x22, 0x22);
    public static final Color BANNER_BG = new Color(0x11, 0x16, 0x12);
    public static final Color BANNER_BORDER = new Color(0x16, 0x3a, 0x21);

    // Glassmorphism and Transparency styles
    public static final Color BG_GLASS = new Color(15, 15, 22, 140);
    public static final Color BORDER_GLASS = new Color(255, 255, 255, 22);
    public static final Color BG_SIDEBAR_GLASS = new Color(10, 10, 14, 180);

    public static final Font FONT_BOLD_20 = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_BOLD_18 = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BOLD_16 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BOLD_13 = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BOLD_12 = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_BOLD_11 = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_BOLD_10 = new Font("Segoe UI", Font.BOLD, 10);
    public static final Font FONT_BOLD_9 = new Font("Segoe UI", Font.BOLD, 9);
    public static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_PLAIN_12 = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_PLAIN_11 = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Paints a glassmorphic/frosted translucent panel background with a subtle border highlight.
     */
    public static void paintGlassEffect(Graphics g, JComponent c, int arc, Color bg, Color border) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Semi-transparent background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);
        
        // Translucent highlight border
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, arc, arc);
        
        g2.dispose();
    }

    /**
     * Replaces standard chunky scrollbars with thin, elegant, translucent pill scrollbars.
     */
    public static void estilizarScrollbar(JScrollPane sp) {
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Completely transparent track
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 6, 6);
                g2.dispose();
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
        });
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        sp.getVerticalScrollBar().setOpaque(false);
        
        sp.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 6, 6);
                g2.dispose();
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
        });
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
        sp.getHorizontalScrollBar().setOpaque(false);
    }

    public static void estilizarTabla(JTable table) {
        table.setBackground(new Color(15, 15, 20, 100));
        table.setOpaque(false);
        table.setForeground(TXT_SECONDARY);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(0x22, 0xc5, 0x5e, 40));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(36);
        table.setFont(FONT_PLAIN_11);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setBackground(BG_SIDEBAR);
                lbl.setForeground(TXT_SECONDARY);
                lbl.setFont(FONT_BOLD_10);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        BorderFactory.createEmptyBorder(10, 8, 10, 8)));
                lbl.setHorizontalAlignment(LEFT);
                return lbl;
            }
        });
        JTableHeader h = table.getTableHeader();
        h.setBackground(BG_SIDEBAR);
        h.setForeground(TXT_SECONDARY);
        h.setReorderingAllowed(false);
        h.setResizingAllowed(false);
    }

    public static JScrollPane scrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(new Color(0, 0, 0, 0));
        sp.setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(new Color(0, 0, 0, 0));
        sp.getViewport().setOpaque(false);
        estilizarScrollbar(sp);
        return sp;
    }

    public static JButton crearBoton(String text, Color bg) {
        return crearBoton(text, null, bg);
    }

    public static JButton crearBoton(String text, Icon icon, Color bg) {
        JButton btn = new JButton(text, icon) {
            private final Color startColor = bg;
            private final Color endColor = bg.darker();
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                
                Color cS = bg;
                Color cE = bg.darker();
                if (bg == GREEN || bg == BLUE || bg == ORANGE || bg == PURPLE || bg == RED) {
                    cS = getModel().isPressed() ? bg.darker() : getModel().isRollover() ? bg.brighter() : bg;
                    cE = getModel().isPressed() ? bg.darker().darker() : getModel().isRollover() ? bg : bg.darker();
                } else {
                    cS = getModel().isPressed() ? new Color(255, 255, 255, 30) : getModel().isRollover() ? new Color(255, 255, 255, 20) : new Color(255, 255, 255, 10);
                    cE = cS;
                }
                
                g2.setPaint(new LinearGradientPaint(0, 0, w, 0, new float[]{0f, 1f}, new Color[]{cS, cE}));
                g2.fillRoundRect(0, 0, w, h, 14, 14);
                
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD_11);
        btn.setForeground(bg == GREEN || bg == BLUE || bg == ORANGE ? Color.BLACK : Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JPanel createSummaryCard(String title, String value, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                paintGlassEffect(g, this, 16, BG_GLASS, BORDER_GLASS);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 14, 18));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_BOLD_9);
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

    public static JScrollPane styledScroll(JTable table) {
        return scrollPane(table);
    }
}
