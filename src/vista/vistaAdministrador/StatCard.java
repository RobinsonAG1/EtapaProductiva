package vista.vistaAdministrador;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import static vista.Theme.*;

public class StatCard extends JPanel {

    public StatCard(String title, String value, AdminDashboard.MenuIconType iconType, Color accent, String linkText, JLabel[] store, int index) {
        setOpaque(false);
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
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(36, 36));
        iconCircle.setMaximumSize(new Dimension(36, 36));
        iconCircle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_BOLD_9);
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
        linkLabel.setFont(FONT_BOLD_10);
        linkLabel.setForeground(GREEN);
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(StatCard.this),
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(17, 17, 17));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        g2.setColor(new Color(34, 34, 34));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.dispose();
    }

    private void paintMiniIcon(Graphics2D g2, AdminDashboard.MenuIconType type, int s, Color c) {
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
