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
        // Contenedor principal de la barra de título
        JPanel titleBar = new JPanel(new BorderLayout(0, 0));
        titleBar.setBackground(BG);
        titleBar.setOpaque(true);
        titleBar.setPreferredSize(new Dimension(0, 32));

        // Panel del título (IZQUIERDA)
        JLabel titleLabel = new JLabel(frame.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(FG);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(BG);
        titleBar.add(titleLabel, BorderLayout.WEST);

        // Panel de botones (CENTRO llenando desde la derecha)
        // Usamos un panel con GridLayout para que los botones ocupen todo el espacio disponible
        // pero solo en su zona derecha
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonsPanel.setBackground(BG);
        buttonsPanel.setOpaque(true);

        // Botón minimizar
        buttonsPanel.add(createMinimizeButton(frame));
        // Botón maximizar/restaurar
        buttonsPanel.add(createMaximizeButton(frame));
        // Botón cerrar
        buttonsPanel.add(createCloseButton(frame));

        // Envolver en otro panel para asegurar que el fondo se pinta correctamente
        JPanel rightWrapper = new JPanel(new BorderLayout(0, 0));
        rightWrapper.setBackground(BG);
        rightWrapper.setOpaque(true);
        rightWrapper.add(buttonsPanel, BorderLayout.EAST);

        // Este panel central llenará cualquier espacio vacío entre el título y los botones
        JPanel centerFiller = new JPanel(new BorderLayout(0, 0));
        centerFiller.setBackground(BG);
        centerFiller.setOpaque(true);
        centerFiller.add(rightWrapper, BorderLayout.EAST);

        titleBar.add(centerFiller, BorderLayout.CENTER);

        return titleBar;
    }

    private JButton createMinimizeButton(JInternalFrame frame) {
        return createControlButton("\u2713", BG, HOVER, HOVER.darker(), e -> {
            try {
                frame.setIcon(true);
            } catch (java.beans.PropertyVetoException ignored) {}
        });
    }

    private JButton createMaximizeButton(JInternalFrame frame) {
        return createControlButton("\u25a1", BG, HOVER, HOVER.darker(), e -> {
            try {
                if (frame.isMaximum()) {
                    frame.setMaximum(false);
                } else {
                    frame.setMaximum(true);
                }
            } catch (java.beans.PropertyVetoException ignored) {}
        });
    }

    private JButton createCloseButton(JInternalFrame frame) {
        return createCloseTypeButton("\u00d7", e -> {
            try {
                frame.setClosed(true);
            } catch (java.beans.PropertyVetoException ignored) {}
        });
    }

    private JButton createControlButton(String text, Color normal, Color hover, Color pressed, ActionListener action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(pressed);
                } else if (getModel().isRollover()) {
                    g2.setColor(hover);
                } else {
                    g2.setColor(normal);
                }
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(FG);
                g2.setFont(new Font("Dialog", Font.PLAIN, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() + fm.getAscent()) / 2) - 2;
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

    private JButton createCloseTypeButton(String text, ActionListener action) {
        JButton btn = new JButton(text) {
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
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() + fm.getAscent()) / 2) - 2;
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
}