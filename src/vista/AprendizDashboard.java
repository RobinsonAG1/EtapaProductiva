package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class AprendizDashboard extends JInternalFrame {

    private static final Color BG = Color.BLACK;
    private static final Color CARD = new Color(0x0A, 0x0A, 0x0A);
    private static final Color GREEN = new Color(0x22, 0xC5, 0x5E);
    private static final Color BLUE = new Color(0x3B, 0x82, 0xF6);
    private static final Color ORANGE = new Color(0xFF, 0x99, 0x00);
    private static final Color TXT = new Color(0xAA, 0xAA, 0xAA);
    private static final Color TXT_BRIGHT = new Color(0xEE, 0xEE, 0xEE);
    private static final Color BORDER = new Color(0x1A, 0x1A, 0x1A);
    private static final float[] PROGRESS_GRAD = {0.0f, 0.4f, 1.0f};
    private static final Color[] PROGRESS_COLORS = {GREEN, BLUE};

    private CardLayout contentLayout;
    private JPanel contentStack;
    private JLabel lblEstado;

    public AprendizDashboard() {
        setTitle("Panel del Aprendiz - " + controlador.Sesion.getNombreCompleto());
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setBorder(BorderFactory.createEmptyBorder());
        setFrameIcon(null);

        ((javax.swing.plaf.basic.BasicInternalFrameUI) getUI()).setNorthPane(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        setContentPane(main);

        main.add(crearSidebar(), BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentStack = new JPanel(contentLayout);
        contentStack.setBackground(BG);

        contentStack.add(crearDashboardView(), "Dashboard");
        contentStack.add(crearSubirEvidenciasView(), "Subir Evidencias");

        main.add(contentStack, BorderLayout.CENTER);

        setFrameIcon(null);
        setBackground(BG);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(CARD);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(CARD);
        header.setBorder(new EmptyBorder(20, 16, 20, 16));
        header.setAlignmentX(LEFT_ALIGNMENT);

        JLabel iconLbl = new JLabel("SENA");
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconLbl.setForeground(GREEN);
        iconLbl.setAlignmentX(LEFT_ALIGNMENT);
        header.add(iconLbl);

        header.add(Box.createRigidArea(new Dimension(0, 4)));
        JLabel nameLbl = new JLabel(controlador.Sesion.getNombreCompleto());
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        nameLbl.setForeground(TXT_BRIGHT);
        nameLbl.setAlignmentX(LEFT_ALIGNMENT);
        header.add(nameLbl);

        JLabel roleLbl = new JLabel("Aprendiz");
        roleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleLbl.setForeground(TXT);
        roleLbl.setAlignmentX(LEFT_ALIGNMENT);
        header.add(roleLbl);

        sidebar.add(header);

        // Nav buttons
        sidebar.add(crearNavBtn("Dashboard", "📊", e -> contentLayout.show(contentStack, "Dashboard")));
        sidebar.add(crearNavBtn("Subir Evidencias", "📎", e -> contentLayout.show(contentStack, "Subir Evidencias")));

        sidebar.add(Box.createVerticalGlue());

        // Logout
        JButton logoutBtn = new JButton("CERRAR SESI\u00d3N");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(0x33, 0x33, 0x33));
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x44, 0x44, 0x44), 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setMaximumSize(new Dimension(220, 36));
        logoutBtn.setAlignmentX(LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            JFrame top = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (top instanceof MDI) ((MDI) top).cerrarSesion();
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        return sidebar;
    }

    private JButton crearNavBtn(String text, String icon, ActionListener action) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(TXT_BRIGHT);
        btn.setBackground(CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(action);
        return btn;
    }

    // ── Dashboard ─────────────────────────────────────────────
    private JPanel crearDashboardView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JLabel title = new JLabel("Panel de Seguimiento");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TXT_BRIGHT);
        title.setBorder(new EmptyBorder(16, 0, 16, 0));
        body.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Progreso Personal card
        JPanel progCard = new JPanel();
        progCard.setLayout(new BoxLayout(progCard, BoxLayout.Y_AXIS));
        progCard.setBackground(CARD);
        progCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(16, 20, 16, 20)));
        progCard.setMaximumSize(new Dimension(600, 160));
        progCard.setAlignmentX(LEFT_ALIGNMENT);

        JLabel progTitle = new JLabel("Progreso Personal");
        progTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        progTitle.setForeground(TXT_BRIGHT);
        progTitle.setAlignmentX(LEFT_ALIGNMENT);
        progCard.add(progTitle);
        progCard.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel progLabel = new JLabel("Cargando...");
        progLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progLabel.setForeground(TXT);
        progLabel.setAlignmentX(LEFT_ALIGNMENT);
        progCard.add(progLabel);

        progCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        progressBar.setForeground(GREEN);
        progressBar.setBackground(new Color(0x15, 0x15, 0x15));
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setPreferredSize(new Dimension(500, 18));
        progressBar.setAlignmentX(LEFT_ALIGNMENT);
        progCard.add(progressBar);

        lblEstado = new JLabel("Estado: verificando...");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblEstado.setForeground(TXT);
        lblEstado.setAlignmentX(LEFT_ALIGNMENT);
        lblEstado.setBorder(new EmptyBorder(8, 0, 0, 0));
        progCard.add(lblEstado);

        gbc.gridy = 0;
        gbc.weightx = 0;
        center.add(progCard, gbc);

        // Cargar progreso real
        new Thread(() -> {
            try {
                int idUsr = controlador.Sesion.getIdUsuario();
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                // Buscar id_aprendiz
                String sqlAp = "SELECT id_aprendiz FROM aprendiz WHERE id_usuario = ?";
                int idAprendiz = -1;
                String empresa = "No asignada";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlAp)) {
                    ps.setInt(1, idUsr);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idAprendiz = rs.getInt("id_aprendiz");
                    }
                }
                if (idAprendiz < 0) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        progLabel.setText("No tienes perfil de aprendiz registrado.");
                        progressBar.setValue(0);
                    });
                    conn.close();
                    return;
                }
                // Horas validadas
                String sqlH = "SELECT COALESCE(SUM(horas_validadas), 0) as horas FROM evidencia WHERE id_aprendiz = ? AND estado = 'Aprobada'";
                int horasCumplidas = 0;
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlH)) {
                    ps.setInt(1, idAprendiz);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) horasCumplidas = rs.getInt("horas");
                    }
                }
                // Horas requeridas del curso
                String sqlC = "SELECT c.horas_requeridas FROM curso_aprendiz ca JOIN curso c ON ca.id_curso = c.id_curso WHERE ca.id_aprendiz = ?";
                int horasReq = 800;
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlC)) {
                    ps.setInt(1, idAprendiz);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) horasReq = rs.getInt("horas_requeridas");
                    }
                }
                // Empresa
                String sqlE = "SELECT e.nombre FROM empresa e "
                        + "JOIN aprendiz a ON e.id_empresa = a.id_empresa WHERE a.id_aprendiz = ?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlE)) {
                    ps.setInt(1, idAprendiz);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) empresa = rs.getString("nombre");
                    }
                }

                final int hC = horasCumplidas;
                final int hR = horasReq;
                final String emp = empresa;
                conn.close();

                javax.swing.SwingUtilities.invokeLater(() -> {
                    int pct = hR > 0 ? Math.min(100, hC * 100 / hR) : 0;
                    progLabel.setText(hC + " / " + hR + " horas validadas  |  Empresa: " + emp);
                    progressBar.setValue(pct);
                    progressBar.setString(pct + "%");
                    lblEstado.setText("Estado: " + (pct >= 100 ? "Completado" : "En proceso"));
                });
            } catch (Exception ex) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    progLabel.setText("Error al cargar progreso");
                    progressBar.setValue(0);
                });
            }
        }).start();

        // Añadir espacio para luego poner las evidencias recientes
        JLabel secLabel = new JLabel("\u00daltimas evidencias:");
        secLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        secLabel.setForeground(TXT_BRIGHT);
        secLabel.setBorder(new EmptyBorder(24, 0, 8, 0));
        secLabel.setAlignmentX(LEFT_ALIGNMENT);

        gbc.gridy = 1;
        gbc.weightx = 0;
        center.add(secLabel, gbc);

        String[] evCols = {"ACTIVIDAD", "TIPO", "FECHA", "ESTADO", "RETROALIMENTACI\u00d3N"};
        DefaultTableModel evTm = new DefaultTableModel(evCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable evTable = new JTable(evTm);
        estilizarTabla(evTable);
        JScrollPane evSP = new JScrollPane(evTable);
        evSP.setBackground(BG);
        evSP.setBorder(BorderFactory.createEmptyBorder());
        evSP.getViewport().setBackground(BG);
        evSP.setPreferredSize(new Dimension(600, 200));

        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        center.add(evSP, gbc);

        // Cargar evidencias
        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sqlAE = "SELECT a.id_aprendiz FROM aprendiz a WHERE a.id_usuario = ?";
                int apId = -1;
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlAE)) {
                    ps.setInt(1, controlador.Sesion.getIdUsuario());
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) apId = rs.getInt("id_aprendiz");
                    }
                }
                if (apId > 0) {
                    String sqlEv = "SELECT actividad, tipo, fecha_entrega, estado, retroalimentacion "
                                 + "FROM evidencia WHERE id_aprendiz = ? ORDER BY fecha_entrega DESC LIMIT 20";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlEv)) {
                        ps.setInt(1, apId);
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            java.util.List<Object[]> filas = new java.util.ArrayList<>();
                            while (rs.next()) {
                                filas.add(new Object[]{
                                    rs.getString("actividad"),
                                    rs.getString("tipo"),
                                    rs.getTimestamp("fecha_entrega") != null ? rs.getTimestamp("fecha_entrega").toString() : "",
                                    rs.getString("estado"),
                                    rs.getString("retroalimentacion") != null ? rs.getString("retroalimentacion") : ""
                                });
                            }
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                for (Object[] f : filas) evTm.addRow(f);
                            });
                        }
                    }
                }
                conn.close();
            } catch (Exception ex) {
                System.err.println("[APRENDIZ] Error evidencias: " + ex.getMessage());
            }
        }).start();

        body.add(center, BorderLayout.CENTER);
        return body;
    }

    // ── Subir Evidencias ───────────────────────────────────────
    private JPanel crearSubirEvidenciasView() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(0, 24, 24, 24));

        JLabel title = new JLabel("Subir Evidencia");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TXT_BRIGHT);
        title.setBorder(new EmptyBorder(16, 0, 16, 0));
        body.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 8, 6, 8);

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        JLabel lblAct = new JLabel("Actividad*:");
        lblAct.setForeground(TXT_BRIGHT);
        lblAct.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblAct, g);

        g.gridx = 1; g.weightx = 1;
        JTextField txtActividad = new JTextField();
        txtActividad.setBackground(CARD);
        txtActividad.setForeground(TXT_BRIGHT);
        txtActividad.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(8, 8, 8, 8)));
        txtActividad.setCaretColor(TXT_BRIGHT);
        formPanel.add(txtActividad, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        JLabel lblTipo = new JLabel("Tipo*:");
        lblTipo.setForeground(TXT_BRIGHT);
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblTipo, g);

        g.gridx = 1; g.weightx = 1;
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Archivo / Documento", "Enlace (URL)", "Texto Descriptivo"});
        comboTipo.setBackground(CARD);
        comboTipo.setForeground(TXT_BRIGHT);
        comboTipo.setBorder(BorderFactory.createLineBorder(BORDER));
        formPanel.add(comboTipo, g);

        // Area de contenido dinámico según tipo
        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        JLabel lblCont = new JLabel("Contenido*:");
        lblCont.setForeground(TXT_BRIGHT);
        lblCont.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblCont, g);

        g.gridx = 1; g.weightx = 1;
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(CARD);

        // Panel archivo
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.setBackground(CARD);
        JTextField txtFile = new JTextField(30);
        txtFile.setBackground(CARD);
        txtFile.setForeground(TXT_BRIGHT);
        txtFile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(8, 8, 8, 8)));
        txtFile.setCaretColor(TXT_BRIGHT);
        txtFile.setEditable(false);
        JButton btnBrowse = new JButton("Seleccionar archivo...");
        btnBrowse.setBackground(CARD);
        btnBrowse.setForeground(TXT_BRIGHT);
        btnBrowse.setBorder(BorderFactory.createLineBorder(BORDER));
        btnBrowse.setFocusPainted(false);
        btnBrowse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Seleccionar archivo de evidencia");
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtFile.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
        filePanel.add(txtFile);
        filePanel.add(btnBrowse);
        contentPanel.add(filePanel, "ARCHIVO");

        // Panel URL
        JPanel urlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urlPanel.setBackground(CARD);
        JTextField txtUrl = new JTextField(35);
        txtUrl.setBackground(CARD);
        txtUrl.setForeground(TXT_BRIGHT);
        txtUrl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(8, 8, 8, 8)));
        txtUrl.setCaretColor(TXT_BRIGHT);
        urlPanel.add(txtUrl);
        contentPanel.add(urlPanel, "ENLACE");

        // Panel texto
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(CARD);
        JTextArea txtDesc = new JTextArea(4, 30);
        txtDesc.setBackground(CARD);
        txtDesc.setForeground(TXT_BRIGHT);
        txtDesc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(8, 8, 8, 8)));
        txtDesc.setCaretColor(TXT_BRIGHT);
        txtDesc.setLineWrap(true);
        JScrollPane txtSP = new JScrollPane(txtDesc);
        txtSP.setBorder(BorderFactory.createEmptyBorder());
        textPanel.add(txtSP, BorderLayout.CENTER);
        contentPanel.add(textPanel, "TEXTO");

        formPanel.add(contentPanel, g);

        comboTipo.addActionListener(e -> {
            CardLayout cl = (CardLayout) contentPanel.getLayout();
            switch (comboTipo.getSelectedIndex()) {
                case 0: cl.show(contentPanel, "ARCHIVO"); break;
                case 1: cl.show(contentPanel, "ENLACE"); break;
                case 2: cl.show(contentPanel, "TEXTO"); break;
            }
        });

        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        formPanel.add(new JLabel(), g); // spacer

        g.gridx = 1; g.weightx = 1;
        JButton btnEnviar = new JButton("ENVIAR EVIDENCIA");
        btnEnviar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEnviar.setForeground(Color.BLACK);
        btnEnviar.setBackground(GREEN);
        btnEnviar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREEN.darker(), 1),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)));
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnviar.setFocusPainted(false);
        btnEnviar.addActionListener(e -> {
            String actividad = txtActividad.getText().trim();
            if (actividad.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La actividad es obligatoria.");
                return;
            }
            int tipoIdx = comboTipo.getSelectedIndex();
            String[] tipos = {"Archivo", "Enlace", "Texto"};
            String tipo = tipos[tipoIdx];
            String contenido;
            if (tipoIdx == 0) contenido = txtFile.getText().trim();
            else if (tipoIdx == 1) contenido = txtUrl.getText().trim();
            else contenido = txtDesc.getText().trim();
            if (contenido.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El contenido de la evidencia es obligatorio.");
                return;
            }

            btnEnviar.setEnabled(false);
            btnEnviar.setText("ENVIANDO...");

            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    String sqlAp = "SELECT id_aprendiz FROM aprendiz WHERE id_usuario = ?";
                    int idAprendiz = -1;
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlAp)) {
                        ps.setInt(1, controlador.Sesion.getIdUsuario());
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) idAprendiz = rs.getInt("id_aprendiz");
                        }
                    }
                    if (idAprendiz < 0) {
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "No tienes perfil de aprendiz registrado.", "Error", JOptionPane.ERROR_MESSAGE);
                            btnEnviar.setEnabled(true);
                            btnEnviar.setText("ENVIAR EVIDENCIA");
                        });
                        conn.close();
                        return;
                    }

                    String sqlIns = "INSERT INTO evidencia (id_aprendiz, actividad, tipo, contenido, fecha_entrega, estado) "
                                  + "VALUES (?, ?, ?, ?, NOW(), 'Entregada')";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlIns)) {
                        ps.setInt(1, idAprendiz);
                        ps.setString(2, actividad);
                        ps.setString(3, tipo);
                        ps.setString(4, contenido);
                        ps.executeUpdate();
                    }
                    conn.close();

                    javax.swing.SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Evidencia entregada exitosamente. Queda pendiente de revisi\u00f3n por tu instructor.",
                                "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
                        txtActividad.setText("");
                        txtFile.setText("");
                        txtUrl.setText("");
                        txtDesc.setText("");
                        btnEnviar.setEnabled(true);
                        btnEnviar.setText("ENVIAR EVIDENCIA");
                    });
                } catch (Exception ex) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Error al enviar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        btnEnviar.setEnabled(true);
                        btnEnviar.setText("ENVIAR EVIDENCIA");
                    });
                }
            }).start();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(BG);
        btnPanel.add(btnEnviar);
        g.gridx = 1; g.gridy = 4; g.weightx = 1;
        formPanel.add(btnPanel, g);

        JScrollPane formSP = new JScrollPane(formPanel);
        formSP.setBackground(BG);
        formSP.setBorder(BorderFactory.createEmptyBorder());
        formSP.getViewport().setBackground(BG);
        body.add(formSP, BorderLayout.CENTER);

        return body;
    }

    private void estilizarTabla(JTable table) {
        table.setBackground(BG);
        table.setForeground(TXT);
        table.setGridColor(BORDER);
        table.setSelectionBackground(CARD);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setBackground(CARD);
                lbl.setForeground(TXT);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                lbl.setHorizontalAlignment(LEFT);
                return lbl;
            }
        });
        table.getTableHeader().setBackground(CARD);
        table.getTableHeader().setForeground(TXT);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
    }
}
