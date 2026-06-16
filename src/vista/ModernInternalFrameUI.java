package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;

public class ModernInternalFrameUI extends BasicInternalFrameUI {

    private static final Color BG = new Color(0x1a, 0x1a, 0x1a);
    private static final Color FG = new Color(0xe2, 0xe2, 0xe2);
    private static final Color HOVER = new Color(0x33, 0x33, 0x33);
    private static final Color CLOSE_HOVER = new Color(0xd9, 0x3d, 0x3d);
    private static final Color CLOSE_PRESSED = new Color(0xb5, 0x2c, 0x2c);

    public ModernInternalFrameUI(JInternalFrame frame) {
        super(frame);
    }

    @Override
    protected JComponent createNorthPane(JInternalFrame frame) {
        JComponent titleBar = new JComponent() {};
        titleBar.setLayout(new BorderLayout());
        titleBar.setPreferredSize(new Dimension(0, 32));
        titleBar.setBackground(BG);
        titleBar.setOpaque(true);

        JLabel title = new JLabel(frame.getTitle()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        title.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        title.setForeground(FG);
        title.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        titleBar.add(title, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttons.setBackground(BG);
        buttons.setOpaque(false);

        buttons.add(createButton("\u2713", e -> {
            try {
                frame.setIcon(true);
            } catch (java.beans.PropertyVetoException ex) {
                // ignore
            }
        }));
        buttons.add(createButton("\u25a1", e -> {
            if (frame.isMaximum()) {
                try {
                    frame.setMaximum(false);
                } catch (java.beans.PropertyVetoException ex) {
                    // ignore
                }
            } else {
                try {
                    frame.setMaximum(true);
                } catch (java.beans.PropertyVetoException ex) {
                    // ignore
                }
            }
        }));
        buttons.add(createCloseButton(e -> {
            try {
                frame.setClosed(true);
            } catch (java.beans.PropertyVetoException ex) {
                // ignore
            }
        }));

        titleBar.add(buttons, BorderLayout.EAST);
        return titleBar;
    }

    private JButton createButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(HOVER.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(HOVER);
                } else {
                    g2.setColor(BG);
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(FG);
                g2.setFont(new Font("Dialog", Font.PLAIN, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(40, 32));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }

    private JButton createCloseButton(java.awt.event.ActionListener action) {
        JButton btn = new JButton("\u00d7") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(CLOSE_PRESSED);
                } else if (getModel().isRollover()) {
                    g2.setColor(CLOSE_HOVER);
                } else {
                    g2.setColor(BG);
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(FG);
                g2.setFont(new Font("Dialog", Font.PLAIN, 18));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("\u00d7")) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString("\u00d7", x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(40, 32));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }
}