package vista;

import controlador.LoginControlador;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private double scale;

    public Login() {
        calcScale();
        initComponents();
        setLocationRelativeTo(null);
    }

    private void calcScale() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = (int) (screen.width * 0.38);
        int h = (int) (screen.height * 0.55);
        setSize(w, h);
        scale = Math.max(0.65, w / 420.0);
    }

    private int s(int val) {
        return (int) Math.round(val * scale);
    }

    private void initComponents() {
        setTitle("Iniciar Sesión — SENA Prácticas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x14, 0x14, 0x14));
                g2.fillRect(0, 0, getWidth(), getHeight());
                int shapeY = (int) (getHeight() * 0.42);
                g2.setColor(new Color(0, 0, 0, 90));
                Polygon shape = new Polygon();
                shape.addPoint(0, shapeY + (int) ((getHeight() - shapeY) * 0.12));
                shape.addPoint(getWidth(), shapeY);
                shape.addPoint(getWidth(), getHeight());
                shape.addPoint(0, getHeight());
                g2.fillPolygon(shape);
                g2.dispose();
            }
        };

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setBorder(new EmptyBorder(s(40), 0, 0, 0));

        // Brand
        JPanel brandLine = new JPanel(new FlowLayout(FlowLayout.CENTER, s(10), 0));
        brandLine.setOpaque(false);
        JLabel iconLabel = new JLabel(new MortarboardIcon(scale));
        brandLine.add(iconLabel);
        JLabel brandTitle = new JLabel("SENA PRÁCTICAS");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, s(20)));
        brandTitle.setForeground(Color.WHITE);
        brandLine.add(brandTitle);
        stack.add(brandLine);
        stack.add(Box.createVerticalStrut(s(35)));

        // Card
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x22, 0x22, 0x22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), s(16), s(16));
                g2.dispose();
            }
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(s(340), d.height);
            }
        };
        card.setOpaque(false);
        int cardPad = s(28);
        card.setBorder(new EmptyBorder(cardPad, cardPad, cardPad, cardPad));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        JLabel accTitle = new JLabel("Acceso al Sistema");
        accTitle.setFont(new Font("Segoe UI", Font.BOLD, s(18)));
        accTitle.setForeground(Color.WHITE);
        c.gridy = 0;
        c.insets = new Insets(0, 0, s(5), 0);
        card.add(accTitle, c);

        JLabel accSub = new JLabel("Ingresa tus credenciales para continuar.");
        accSub.setFont(new Font("Segoe UI", Font.PLAIN, s(12)));
        accSub.setForeground(new Color(0x9c, 0xa3, 0xaf));
        c.gridy = 1;
        c.insets = new Insets(0, 0, s(25), 0);
        card.add(accSub, c);

        // Email
        JLabel lblCorreo = new JLabel("CORREO ELECTRÓNICO");
        lblCorreo.setFont(new Font("Segoe UI", Font.BOLD, s(10)));
        lblCorreo.setForeground(new Color(0x88, 0x88, 0x88));
        c.gridy = 2;
        c.insets = new Insets(0, 0, s(7), 0);
        card.add(lblCorreo, c);

        int inputR = s(10);
        txtCorreo = createRoundedField("usuario@soy.sena.edu.co", inputR);
        c.gridy = 3;
        c.insets = new Insets(0, 0, s(16), 0);
        card.add(txtCorreo, c);

        // Password
        JLabel lblPass = new JLabel("CONTRASEÑA");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, s(10)));
        lblPass.setForeground(new Color(0x88, 0x88, 0x88));
        c.gridy = 4;
        c.insets = new Insets(0, 0, s(7), 0);
        card.add(lblPass, c);

        txtPassword = createRoundedPasswordField(inputR);
        c.gridy = 5;
        c.insets = new Insets(0, 0, 0, 0);
        card.add(txtPassword, c);

        // Button
        int btnR = s(50);
        JButton btnIngresar = new JButton("INGRESAR  \u2192") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(0xe5, 0xe5, 0xe5));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), btnR, btnR);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btnIngresar.setOpaque(false);
        btnIngresar.setContentAreaFilled(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, s(11)));
        btnIngresar.setForeground(Color.BLACK);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setPreferredSize(new Dimension(s(284), s(44)));
        btnIngresar.addActionListener(e -> LoginControlador.login(txtCorreo, txtPassword, this));
        btnIngresar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnIngresar.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnIngresar.repaint();
            }
        });
        c.gridy = 6;
        c.insets = new Insets(s(22), 0, s(20), 0);
        card.add(btnIngresar, c);

        // Links
        JPanel linksPanel = new JPanel();
        linksPanel.setOpaque(false);
        linksPanel.setLayout(new BoxLayout(linksPanel, BoxLayout.Y_AXIS));

        JLabel olvidoLink = new JLabel("\u00bfOLVIDASTE TU CONTRASE\u00d1A?");
        olvidoLink.setFont(new Font("Segoe UI", Font.PLAIN, s(10)));
        olvidoLink.setForeground(new Color(0x88, 0x88, 0x88));
        olvidoLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        olvidoLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        olvidoLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(Login.this,
                        "Contacta al administrador para restablecer tu contrase\u00f1a.",
                        "Recuperar Contrase\u00f1a", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel registroPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, s(5), 0));
        registroPanel.setOpaque(false);
        JLabel nuevo = new JLabel("\u00bfNUEVO AQU\u00cd?");
        nuevo.setFont(new Font("Segoe UI", Font.PLAIN, s(10)));
        nuevo.setForeground(new Color(0x88, 0x88, 0x88));
        JLabel registrate = new JLabel("REG\u00cdSTRATE");
        registrate.setFont(new Font("Segoe UI", Font.BOLD, s(10)));
        registrate.setForeground(Color.WHITE);
        registrate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registrate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(Login.this,
                        "Funci\u00f3n de registro pr\u00f3ximamente.",
                        "Registro", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        registroPanel.add(nuevo);
        registroPanel.add(registrate);

        linksPanel.add(olvidoLink);
        linksPanel.add(Box.createVerticalStrut(s(12)));
        linksPanel.add(registroPanel);

        c.gridy = 7;
        c.insets = new Insets(0, 0, 0, 0);
        card.add(linksPanel, c);

        stack.add(card);
        stack.add(Box.createVerticalStrut(s(30)));

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, s(8), 0));
        footer.setOpaque(false);
        JLabel dot = new JLabel("\u25cf");
        dot.setFont(new Font("Segoe UI", Font.PLAIN, s(8)));
        dot.setForeground(new Color(0x6b, 0x72, 0x80));
        JLabel footerText = new JLabel("SISTEMA EN L\u00cdNEA  |  V 2.0.4");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, s(9)));
        footerText.setForeground(new Color(0x6b, 0x72, 0x80));
        footer.add(dot);
        footer.add(footerText);
        stack.add(footer);

        root.add(stack);
        setContentPane(root);
    }

    private JTextField createRoundedField(String placeholder, int radius) {
        JTextField field = new JTextField();
        field.setOpaque(true);
        field.setBackground(new Color(0x2c, 0x2c, 0x2c));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setSelectedTextColor(Color.BLACK);
        field.setSelectionColor(Color.WHITE);
        int padV = s(10);
        int padH = s(12);
        field.setBorder(new EmptyBorder(padV, padH, padV, padH));
        field.setFont(new Font("Segoe UI", Font.PLAIN, s(13)));
        field.setPreferredSize(new Dimension(s(284), s(40)));
        return field;
    }

    private JPasswordField createRoundedPasswordField(int radius) {
        JPasswordField field = new JPasswordField();
        field.setOpaque(true);
        field.setBackground(new Color(0x2c, 0x2c, 0x2c));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setSelectedTextColor(Color.BLACK);
        field.setSelectionColor(Color.WHITE);
        int padV = s(10);
        int padH = s(12);
        field.setBorder(new EmptyBorder(padV, padH, padV, padH));
        field.setFont(new Font("Segoe UI", Font.PLAIN, s(13)));
        field.setPreferredSize(new Dimension(s(284), s(40)));
        return field;
    }

    static class MortarboardIcon implements Icon {
        private final double scale;

        MortarboardIcon(double scale) {
            this.scale = scale;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            double s = scale;
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke((float) (1.5 * s)));
            g2.fillRoundRect((int) (2 * s), (int) (10 * s), (int) (20 * s), (int) (8 * s), (int) (3 * s), (int) (3 * s));
            g2.fillRoundRect((int) (5 * s), (int) (5 * s), (int) (14 * s), (int) (7 * s), (int) (2 * s), (int) (2 * s));
            g2.drawLine((int) (12 * s), (int) (5 * s), (int) (12 * s), (int) (1 * s));
            g2.fillOval((int) (11 * s), 0, (int) (2 * s), (int) (2 * s));
            g2.drawLine(0, (int) (17 * s), (int) (24 * s), (int) (17 * s));
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return (int) (24 * scale);
        }

        @Override
        public int getIconHeight() {
            return (int) (24 * scale);
        }
    }
}
