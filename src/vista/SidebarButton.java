package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static vista.Theme.*;

public class SidebarButton extends JPanel {

    private final String menuText;
    private final AdminDashboard.MenuIconType iconType;
    private final Runnable onClick;
    private boolean active;
    private boolean hover;

    public SidebarButton(String text, AdminDashboard.MenuIconType iconType, Runnable onClick) {
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
                onClick.run();
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

    public void setMenuActive(boolean a) {
        this.active = a;
        repaint();
    }

    public boolean isMenuActive() {
        return active;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        setOpaque(false);
    }

    public void setActive(boolean a) {
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

    private void paintMenuIcon(Graphics2D g2, AdminDashboard.MenuIconType type, int x, int y, int s, Color c) {
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
