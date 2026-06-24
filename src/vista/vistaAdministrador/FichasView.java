package vista.vistaAdministrador;

import vista.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import static vista.Theme.*;

public class FichasView extends JPanel {

    private final JInternalFrame parent;
    private DefaultTableModel tableModel;

    public FichasView(JInternalFrame parent) {
        this.parent = parent;
        setBackground(BG_DARK);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 24, 24, 24));
        buildUI();
    }

    private void buildUI() {
        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 14, 0));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Gesti\u00f3n de Fichas / Cursos");
        lblTitle.setFont(FONT_BOLD_18);
        lblTitle.setForeground(Color.WHITE);

        JButton btnNuevo = Theme.crearBoton("+ Nueva Ficha", GREEN);
        btnNuevo.setPreferredSize(new Dimension(130, 32));
        btnNuevo.addActionListener(e -> mostrarDialogoNuevaFicha());

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnNuevo, BorderLayout.EAST);
        add(headerPanel);

        // 2. Search
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(new EmptyBorder(0, 0, 16, 0));
        searchBar.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        searchBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtBuscar = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x11, 0x11, 0x11));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtBuscar.setOpaque(false);
        txtBuscar.setCaretColor(Color.WHITE);
        txtBuscar.setForeground(Color.WHITE);
        txtBuscar.setBackground(new Color(0x11, 0x11, 0x11));
        txtBuscar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        txtBuscar.setFont(FONT_PLAIN_12);
        txtBuscar.setText("Buscar ficha por nombre...");
        txtBuscar.setForeground(TXT_DIM);
        txtBuscar.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtBuscar.getText().equals("Buscar ficha por nombre...")) {
                    txtBuscar.setText(""); txtBuscar.setForeground(Color.WHITE);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    txtBuscar.setText("Buscar ficha por nombre..."); txtBuscar.setForeground(TXT_DIM);
                }
            }
        });

        JButton btnBuscar = Theme.crearBoton("Buscar", new SearchIcon(), Theme.BG_INNER_BTN);
        btnBuscar.setPreferredSize(new Dimension(100, 36));
        btnBuscar.addActionListener(e -> {
            String q = txtBuscar.getText().trim();
            if (q.equals("Buscar ficha por nombre...")) q = "";
            cargarFichas(q);
        });

        searchBar.add(txtBuscar, BorderLayout.CENTER);
        searchBar.add(btnBuscar, BorderLayout.EAST);
        add(searchBar);

        // 3. Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BG_SIDEBAR);
        tablePanel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        tablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tableHeaderP = new JPanel(new BorderLayout());
        tableHeaderP.setBackground(BG_SIDEBAR);
        tableHeaderP.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel tableTitle = new JLabel("FICHAS DISPONIBLES");
        tableTitle.setFont(FONT_BOLD_13);
        tableTitle.setForeground(Color.WHITE);
        tableHeaderP.add(tableTitle, BorderLayout.WEST);
        tablePanel.add(tableHeaderP, BorderLayout.NORTH);

        String[] columns = {"NOMBRE", "INSTRUCTORES", "APRENDICES", "C\u00d3DIGO", "ACCIONES", "ID_CURSO"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable t = new JTable(tableModel) {
            @Override public boolean isCellEditable(int r, int c) { return c == 4; }
        };
        t.setBackground(BG_DARK);
        t.setForeground(TXT_SECONDARY);
        t.setGridColor(BORDER);
        t.setSelectionBackground(BG_CARD);
        t.setSelectionForeground(Color.WHITE);
        t.setRowHeight(38);
        t.setFont(FONT_PLAIN_12);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.removeColumn(t.getColumnModel().getColumn(5));

        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tab, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tab, v, s, f, r, c);
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
        t.getTableHeader().setBackground(BG_SIDEBAR);
        t.getTableHeader().setForeground(TXT_SECONDARY);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setResizingAllowed(false);

        FichasTableCellRenderer renderer = new FichasTableCellRenderer();
        for (int i = 0; i < 5; i++) t.getColumnModel().getColumn(i).setCellRenderer(renderer);
        t.getColumnModel().getColumn(4).setCellEditor(new FichasTableCellEditor());

        JScrollPane sp = Theme.styledScroll(t);
        sp.setPreferredSize(new Dimension(800, 300));
        tablePanel.add(sp, BorderLayout.CENTER);
        add(tablePanel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // 4. Footer
        JPanel footerWrapper = new JPanel();
        footerWrapper.setLayout(new BoxLayout(footerWrapper, BoxLayout.Y_AXIS));
        footerWrapper.setOpaque(false);
        footerWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel greenLine = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(GREEN);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        greenLine.setPreferredSize(new Dimension(0, 2));
        greenLine.setMaximumSize(new Dimension(Short.MAX_VALUE, 2));

        JPanel footerTextPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        footerTextPanel.setOpaque(false);
        JLabel senaCopy = new JLabel("SENA Pr\u00e1cticas \u00a9 2026");
        senaCopy.setFont(FONT_PLAIN_11);
        senaCopy.setForeground(TXT_DIM);
        footerTextPanel.add(senaCopy);
        footerWrapper.add(greenLine);
        footerWrapper.add(footerTextPanel);
        add(footerWrapper);

        cargarFichas(null);
    }

    public void cargarFichas(String filtroNombre) {
        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sql = "SELECT c.id_curso, c.ficha, c.nombre, "
                           + "COALESCE(i_count.total, 0) as instructores, "
                           + "COALESCE(a_count.total, 0) as aprendices "
                           + "FROM curso c "
                           + "LEFT JOIN (SELECT id_curso, COUNT(*) as total FROM curso_instructor GROUP BY id_curso) i_count ON c.id_curso = i_count.id_curso "
                           + "LEFT JOIN (SELECT id_curso, COUNT(*) as total FROM curso_aprendiz GROUP BY id_curso) a_count ON c.id_curso = a_count.id_curso ";
                if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                    sql += "WHERE LOWER(c.nombre) LIKE ? ";
                }
                sql += "ORDER BY c.nombre";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                        ps.setString(1, "%" + filtroNombre.trim().toLowerCase() + "%");
                    }
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        java.util.List<Object[]> filas = new java.util.ArrayList<>();
                        while (rs.next()) {
                            int idCurso = rs.getInt("id_curso");
                            String ficha = rs.getString("ficha");
                            if (ficha == null || ficha.trim().isEmpty()) ficha = "FIC-" + idCurso;
                            filas.add(new Object[]{
                                rs.getString("nombre"),
                                String.valueOf(rs.getInt("instructores")),
                                String.valueOf(rs.getInt("aprendices")),
                                ficha, "", idCurso
                            });
                        }
                        SwingUtilities.invokeLater(() -> {
                            if (tableModel != null) {
                                tableModel.setRowCount(0);
                                for (Object[] row : filas) tableModel.addRow(row);
                            }
                        });
                    }
                }
                conn.close();
            } catch (Exception ex) {
                System.err.println("[FICHAS] Error: " + ex.getMessage());
            }
        }).start();
    }

    private void mostrarDialogoNuevaFicha() {
        JTextField txtNombre = new JTextField();
        JTextField txtFicha = new JTextField();
        JTextField txtInicio = new JTextField();
        JTextField txtFin = new JTextField();
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 8, 4, 8);
        String[] labels = {"Nombre Curso*:", "Ficha / C\u00f3digo*:", "Fecha Inicio:", "Fecha Fin:"};
        JTextField[] campos = {txtNombre, txtFicha, txtInicio, txtFin};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            form.add(campos[i], gbc);
        }
        int result = JOptionPane.showConfirmDialog(parent, form, "Nueva Ficha / Curso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String ficha = txtFicha.getText().trim();
            String inicio = txtInicio.getText().trim();
            String fin = txtFin.getText().trim();
            if (nombre.isEmpty() || ficha.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Nombre y Ficha son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new Thread(() -> {
                try {
                    modelo.Curso c = new modelo.Curso();
                    c.setNombre(nombre);
                    c.setFicha(ficha);
                    if (!inicio.isEmpty()) c.setFechaInicio(java.sql.Date.valueOf(inicio));
                    if (!fin.isEmpty()) c.setFechaFin(java.sql.Date.valueOf(fin));
                    new dao.CursoDAO().insertar(c);
                    SwingUtilities.invokeLater(() -> cargarFichas(null));
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                }
            }).start();
        }
    }

    private void editarFichaAccion(int idCurso) {
        new Thread(() -> {
            try {
                modelo.Curso c = new dao.CursoDAO().buscarPorId(idCurso);
                if (c == null) return;
                SwingUtilities.invokeLater(() -> {
                    JTextField txtNombre = new JTextField(c.getNombre());
                    JTextField txtFicha = new JTextField(c.getFicha());
                    JTextField txtInicio = new JTextField(c.getFechaInicio() != null ? c.getFechaInicio().toString() : "");
                    JTextField txtFin = new JTextField(c.getFechaFin() != null ? c.getFechaFin().toString() : "");
                    JPanel form = new JPanel(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.insets = new Insets(4, 8, 4, 8);
                    String[] labels = {"Nombre Curso*:", "Ficha / C\u00f3digo*:", "Fecha Inicio:", "Fecha Fin:"};
                    JTextField[] campos = {txtNombre, txtFicha, txtInicio, txtFin};
                    for (int i = 0; i < labels.length; i++) {
                        gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
                        form.add(new JLabel(labels[i]), gbc);
                        gbc.gridx = 1; gbc.weightx = 1;
                        form.add(campos[i], gbc);
                    }
                    int result = JOptionPane.showConfirmDialog(parent, form, "Editar Ficha / Curso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (result == JOptionPane.OK_OPTION) {
                        String nombre = txtNombre.getText().trim();
                        String ficha = txtFicha.getText().trim();
                        String inicio = txtInicio.getText().trim();
                        String fin = txtFin.getText().trim();
                        if (nombre.isEmpty() || ficha.isEmpty()) {
                            JOptionPane.showMessageDialog(parent, "Nombre y Ficha son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        new Thread(() -> {
                            try {
                                c.setNombre(nombre);
                                c.setFicha(ficha);
                                c.setFechaInicio(!inicio.isEmpty() ? java.sql.Date.valueOf(inicio) : null);
                                c.setFechaFin(!fin.isEmpty() ? java.sql.Date.valueOf(fin) : null);
                                new dao.CursoDAO().actualizar(c);
                                SwingUtilities.invokeLater(() -> cargarFichas(null));
                            } catch (Exception ex) {
                                SwingUtilities.invokeLater(() ->
                                    JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                            }
                        }).start();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void eliminarFichaAccion(int idCurso, String name) {
        int confirm = JOptionPane.showConfirmDialog(parent,
                "\u00bfEst\u00e1s seguro de eliminar \"" + name + "\"?\nSe borrar\u00e1n todas las asociaciones.",
                "Confirmar Eliminaci\u00f3n", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    try (java.sql.PreparedStatement ps1 = conn.prepareStatement("DELETE FROM curso_instructor WHERE id_curso = ?");
                         java.sql.PreparedStatement ps2 = conn.prepareStatement("DELETE FROM curso_aprendiz WHERE id_curso = ?")) {
                        ps1.setInt(1, idCurso); ps1.executeUpdate();
                        ps2.setInt(1, idCurso); ps2.executeUpdate();
                    }
                    new dao.CursoDAO().eliminar(idCurso);
                    conn.close();
                    SwingUtilities.invokeLater(() -> {
                        cargarFichas(null);
                        JOptionPane.showMessageDialog(parent, "Ficha eliminada correctamente.");
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                }
            }).start();
        }
    }

    private void asignarFichaAccion(int idCurso, String name) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Asignaciones - " + name, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(parent);
        dialog.getContentPane().setBackground(BG_DARK);
        dialog.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_SIDEBAR);
        tabs.setForeground(Color.WHITE);
        tabs.setFont(FONT_BOLD_12);

        // Instructors tab
        JPanel panelInst = new JPanel(new BorderLayout(8, 8));
        panelInst.setBackground(BG_DARK);
        panelInst.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel topInst = new JPanel(new BorderLayout(8, 8));
        topInst.setOpaque(false);
        JComboBox<ComboItem> comboInst = new JComboBox<>();
        comboInst.setBackground(BG_SIDEBAR);
        comboInst.setForeground(Color.WHITE);
        JButton btnAddInst = Theme.crearBoton("+ Asignar", GREEN);
        topInst.add(new JLabel("Instructor disponible:"), BorderLayout.WEST);
        topInst.add(comboInst, BorderLayout.CENTER);
        topInst.add(btnAddInst, BorderLayout.EAST);
        DefaultListModel<ComboItem> listModelInst = new DefaultListModel<>();
        JList<ComboItem> listInst = new JList<>(listModelInst);
        listInst.setBackground(BG_SIDEBAR);
        listInst.setForeground(Color.WHITE);
        listInst.setFont(FONT_PLAIN_12);
        JScrollPane scrollInst = Theme.styledScroll(new JTable());
        scrollInst.setViewportView(listInst);
        scrollInst.setBorder(BorderFactory.createLineBorder(BORDER));
        JButton btnDelInst = Theme.crearBoton("Remover Seleccionado", RED);
        panelInst.add(topInst, BorderLayout.NORTH);
        panelInst.add(scrollInst, BorderLayout.CENTER);
        panelInst.add(btnDelInst, BorderLayout.SOUTH);

        // Students tab
        JPanel panelApr = new JPanel(new BorderLayout(8, 8));
        panelApr.setBackground(BG_DARK);
        panelApr.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel topApr = new JPanel(new BorderLayout(8, 8));
        topApr.setOpaque(false);
        JComboBox<ComboItem> comboApr = new JComboBox<>();
        comboApr.setBackground(BG_SIDEBAR);
        comboApr.setForeground(Color.WHITE);
        JButton btnAddApr = Theme.crearBoton("+ Asignar", GREEN);
        topApr.add(new JLabel("Aprendiz disponible:"), BorderLayout.WEST);
        topApr.add(comboApr, BorderLayout.CENTER);
        topApr.add(btnAddApr, BorderLayout.EAST);
        DefaultListModel<ComboItem> listModelApr = new DefaultListModel<>();
        JList<ComboItem> listApr = new JList<>(listModelApr);
        listApr.setBackground(BG_SIDEBAR);
        listApr.setForeground(Color.WHITE);
        listApr.setFont(FONT_PLAIN_12);
        JScrollPane scrollApr = Theme.styledScroll(new JTable());
        scrollApr.setViewportView(listApr);
        scrollApr.setBorder(BorderFactory.createLineBorder(BORDER));
        JButton btnDelApr = Theme.crearBoton("Remover Seleccionado", RED);
        panelApr.add(topApr, BorderLayout.NORTH);
        panelApr.add(scrollApr, BorderLayout.CENTER);
        panelApr.add(btnDelApr, BorderLayout.SOUTH);

        tabs.addTab("Instructores", panelInst);
        tabs.addTab("Aprendices", panelApr);
        dialog.add(tabs, BorderLayout.CENTER);

        // Data loading
        Runnable cargarInstructores = () -> loadInstructorAssignments(idCurso, comboInst, listModelInst);
        Runnable cargarAprendices = () -> loadAprendizAssignments(idCurso, comboApr, listModelApr);

        btnAddInst.addActionListener(ev -> {
            ComboItem sel = (ComboItem) comboInst.getSelectedItem();
            if (sel == null) return;
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    try (java.sql.PreparedStatement ps = conn.prepareStatement("INSERT INTO curso_instructor (id_curso, id_instructor) VALUES (?, ?)")) {
                        ps.setInt(1, idCurso); ps.setInt(2, sel.id); ps.executeUpdate();
                    }
                    conn.close();
                    cargarInstructores.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }).start();
        });
        btnDelInst.addActionListener(ev -> {
            ComboItem sel = listInst.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(dialog, "Seleccione un instructor."); return; }
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    try (java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM curso_instructor WHERE id_curso = ? AND id_instructor = ?")) {
                        ps.setInt(1, idCurso); ps.setInt(2, sel.id); ps.executeUpdate();
                    }
                    conn.close();
                    cargarInstructores.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }).start();
        });
        btnAddApr.addActionListener(ev -> {
            ComboItem sel = (ComboItem) comboApr.getSelectedItem();
            if (sel == null) return;
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    try (java.sql.PreparedStatement ps = conn.prepareStatement("INSERT INTO curso_aprendiz (id_curso, id_aprendiz, estado) VALUES (?, ?, ?)")) {
                        ps.setInt(1, idCurso); ps.setInt(2, sel.id); ps.setString(3, "Activo"); ps.executeUpdate();
                    }
                    conn.close();
                    cargarAprendices.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }).start();
        });
        btnDelApr.addActionListener(ev -> {
            ComboItem sel = listApr.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(dialog, "Seleccione un aprendiz."); return; }
            new Thread(() -> {
                try {
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    try (java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM curso_aprendiz WHERE id_curso = ? AND id_aprendiz = ?")) {
                        ps.setInt(1, idCurso); ps.setInt(2, sel.id); ps.executeUpdate();
                    }
                    conn.close();
                    cargarAprendices.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            }).start();
        });

        cargarInstructores.run();
        cargarAprendices.run();
        dialog.setVisible(true);
    }

    private void loadInstructorAssignments(int idCurso, JComboBox<ComboItem> combo, DefaultListModel<ComboItem> listModel) {
        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sqlA = "SELECT i.id_instructor, u.nombres || ' ' || u.apellidos as nombre FROM instructor i JOIN usuario u ON i.id_usuario = u.id_usuario JOIN curso_instructor ci ON i.id_instructor = ci.id_instructor WHERE ci.id_curso = ?";
                java.util.List<ComboItem> assigned = new java.util.ArrayList<>();
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlA)) {
                    ps.setInt(1, idCurso);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) assigned.add(new ComboItem(rs.getInt("id_instructor"), rs.getString("nombre")));
                    }
                }
                String sqlU = "SELECT i.id_instructor, u.nombres || ' ' || u.apellidos as nombre FROM instructor i JOIN usuario u ON i.id_usuario = u.id_usuario WHERE i.activo = true AND i.id_instructor NOT IN (SELECT id_instructor FROM curso_instructor WHERE id_curso = ?)";
                java.util.List<ComboItem> unassigned = new java.util.ArrayList<>();
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlU)) {
                    ps.setInt(1, idCurso);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) unassigned.add(new ComboItem(rs.getInt("id_instructor"), rs.getString("nombre")));
                    }
                }
                conn.close();
                SwingUtilities.invokeLater(() -> {
                    listModel.clear();
                    for (ComboItem item : assigned) listModel.addElement(item);
                    combo.removeAllItems();
                    for (ComboItem item : unassigned) combo.addItem(item);
                });
            } catch (Exception ex) { ex.printStackTrace(); }
        }).start();
    }

    private void loadAprendizAssignments(int idCurso, JComboBox<ComboItem> combo, DefaultListModel<ComboItem> listModel) {
        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sqlA = "SELECT a.id_aprendiz, u.nombres || ' ' || u.apellidos as nombre FROM aprendiz a JOIN usuario u ON a.id_usuario = u.id_usuario JOIN curso_aprendiz ca ON a.id_aprendiz = ca.id_aprendiz WHERE ca.id_curso = ?";
                java.util.List<ComboItem> assigned = new java.util.ArrayList<>();
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlA)) {
                    ps.setInt(1, idCurso);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) assigned.add(new ComboItem(rs.getInt("id_aprendiz"), rs.getString("nombre")));
                    }
                }
                String sqlU = "SELECT a.id_aprendiz, u.nombres || ' ' || u.apellidos as nombre FROM aprendiz a JOIN usuario u ON a.id_usuario = u.id_usuario WHERE a.id_aprendiz NOT IN (SELECT id_aprendiz FROM curso_aprendiz WHERE id_curso = ?)";
                java.util.List<ComboItem> unassigned = new java.util.ArrayList<>();
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sqlU)) {
                    ps.setInt(1, idCurso);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) unassigned.add(new ComboItem(rs.getInt("id_aprendiz"), rs.getString("nombre")));
                    }
                }
                conn.close();
                SwingUtilities.invokeLater(() -> {
                    listModel.clear();
                    for (ComboItem item : assigned) listModel.addElement(item);
                    combo.removeAllItems();
                    for (ComboItem item : unassigned) combo.addItem(item);
                });
            } catch (Exception ex) { ex.printStackTrace(); }
        }).start();
    }

    static class ComboItem {
        int id;
        String name;
        ComboItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    static class SearchIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawOval(x + 2, y + 2, 7, 7);
            g2.drawLine(x + 8, y + 8, x + 13, y + 13);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 16; }
    }



    class ActionIconButton extends JButton {
        private Color color;
        private int iconType;
        ActionIconButton(int iconType, Color color) {
            this.iconType = iconType;
            this.color = color;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setPreferredSize(new Dimension(28, 28));
            setMinimumSize(new Dimension(28, 28));
            setMaximumSize(new Dimension(28, 28));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            Color c = color;
            if (getModel().isPressed()) {
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 35));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
                g2.setColor(c.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 20));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
                g2.setColor(c.brighter());
            } else {
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 10));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
                g2.setColor(c);
            }
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
            g2.setColor(c);
            g2.setStroke(new BasicStroke(1.5f));
            int size = 12;
            int cx = (w - size) / 2;
            int cy = (h - size) / 2;
            switch (iconType) {
                case 0: g2.drawArc(cx, cy + 2, size, size - 4, 0, 180); g2.drawArc(cx, cy + 2, size, size - 4, 0, -180); g2.fillOval(cx + size/2 - 2, cy + size/2 - 2, 4, 4); break;
                case 1: g2.drawRect(cx + 2, cy + 2, 4, size - 4); g2.drawLine(cx + 2, cy + 2, cx + 5, cy + 5); g2.drawLine(cx, cy + size, cx + 2, cy + size - 2); g2.drawLine(cx, cy + size, cx + 2, cy + size); break;
                case 2: g2.drawOval(cx + 1, cy, 6, 6); g2.drawArc(cx - 3, cy + 7, 14, 8, 0, 180); g2.setStroke(new BasicStroke(1.2f)); g2.drawLine(cx + 9, cy + 4, cx + 13, cy + 4); g2.drawLine(cx + 11, cy + 2, cx + 11, cy + 6); break;
                case 3: g2.drawRect(cx + 2, cy + 3, size - 4, size - 3); g2.drawLine(cx, cy + 3, cx + size, cy + 3); g2.drawLine(cx + 3, cy, cx + size - 3, cy); g2.drawLine(cx + 4, cy + 5, cx + 4, cy + size - 2); g2.drawLine(cx + size - 4, cy + 5, cx + size - 4, cy + size - 2); break;
            }
            g2.dispose();
        }
    }

    class FichasTableCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String text = value != null ? value.toString() : "";
            JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            cellPanel.setOpaque(false);
            if (isSelected) {
                cellPanel.setOpaque(true);
                cellPanel.setBackground(table.getSelectionBackground());
            }
            switch (column) {
                case 0:
                    JLabel nameLabel = new JLabel(text);
                    nameLabel.setFont(FONT_BOLD_12);
                    nameLabel.setForeground(BLUE);
                    nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    cellPanel.add(nameLabel);
                    return cellPanel;
                case 1: case 2:
                    JPanel countCapsule = new JPanel(new BorderLayout()) {
                        @Override protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(0x22, 0xc5, 0x5e, 10));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                            g2.setColor(GREEN);
                            g2.setStroke(new BasicStroke(1.0f));
                            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                            g2.dispose();
                        }
                    };
                    countCapsule.setOpaque(false);
                    countCapsule.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
                    JLabel countLabel = new JLabel(text);
                    countLabel.setFont(FONT_BOLD_11);
                    countLabel.setForeground(GREEN);
                    countCapsule.add(countLabel, BorderLayout.CENTER);
                    cellPanel.add(countCapsule);
                    return cellPanel;
                case 3:
                    JLabel codeLabel = new JLabel(text);
                    codeLabel.setFont(FONT_PLAIN_11);
                    codeLabel.setForeground(TXT_SECONDARY);
                    cellPanel.add(codeLabel);
                    return cellPanel;
                case 4:
                    JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
                    actionsPanel.setOpaque(false);
                    actionsPanel.add(new ActionIconButton(0, BLUE));
                    actionsPanel.add(new ActionIconButton(1, TXT_SECONDARY));
                    actionsPanel.add(new ActionIconButton(2, GREEN));
                    actionsPanel.add(new ActionIconButton(3, RED));
                    return actionsPanel;
                default:
                    return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
    }

    class FichasTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private final JPanel panel;
        private final ActionIconButton btnVer, btnEditar, btnAsignar, btnEliminar;
        private JTable table;
        private int currentRow;

        FichasTableCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            panel.setOpaque(true);
            panel.setBackground(BG_CARD);
            btnVer = new ActionIconButton(0, BLUE);
            btnEditar = new ActionIconButton(1, TXT_SECONDARY);
            btnAsignar = new ActionIconButton(2, GREEN);
            btnEliminar = new ActionIconButton(3, RED);
            btnVer.addActionListener(this);
            btnEditar.addActionListener(this);
            btnAsignar.addActionListener(this);
            btnEliminar.addActionListener(this);
            panel.add(btnVer); panel.add(btnEditar); panel.add(btnAsignar); panel.add(btnEliminar);
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int row, int col) {
            this.table = t; this.currentRow = row; panel.setBackground(t.getSelectionBackground()); return panel;
        }
        @Override public Object getCellEditorValue() { return ""; }
        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            fireEditingStopped();
            int idCurso = (Integer) table.getModel().getValueAt(currentRow, 5);
            String name = table.getModel().getValueAt(currentRow, 0).toString();
            if (src == btnVer) {
                System.out.println("[ACCIONES] Ver Ficha ID: " + idCurso + " - " + name);
            } else if (src == btnEditar) {
                editarFichaAccion(idCurso);
            } else if (src == btnAsignar) {
                asignarFichaAccion(idCurso, name);
            } else if (src == btnEliminar) {
                eliminarFichaAccion(idCurso, name);
            }
        }
    }
}
