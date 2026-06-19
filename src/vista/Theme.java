package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

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

    public static void estilizarTabla(JTable table) {
        table.setBackground(BG_DARK);
        table.setForeground(TXT_SECONDARY);
        table.setGridColor(BORDER);
        table.setSelectionBackground(BG_CARD);
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
        sp.setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_DARK);
        return sp;
    }

    public static JButton crearBoton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD_11);
        btn.setForeground(bg == GREEN || bg == BLUE || bg == ORANGE ? Color.BLACK : Color.WHITE);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }

    public static JPanel createSummaryCard(String title, String value, Color accent) {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
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
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_DARK);
        return sp;
    }
}
