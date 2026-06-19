package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import static vista.Theme.*;

public class InstructoresView extends JPanel {

    private final JInternalFrame parent;
    private DefaultTableModel tm;
    private JLabel countLabel;

    public InstructoresView(JInternalFrame parent) {
        this.parent = parent;
        setBackground(BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 24, 24, 24));
        buildUI();
    }

    private void buildUI() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(16, 0, 16, 0));

        JButton btnNuevo = Theme.crearBoton("+ Nuevo Instructor", GREEN);
        JButton btnEditar = Theme.crearBoton("\u270E  Editar", BLUE);
        JButton btnEliminar = Theme.crearBoton("\u2716  Desactivar", ORANGE);
        countLabel = new JLabel("Cargando...");
        countLabel.setFont(FONT_PLAIN_12);
        countLabel.setForeground(TXT_SECONDARY);

        toolbar.add(btnNuevo);
        toolbar.add(Box.createRigidArea(new Dimension(8, 0)));
        toolbar.add(btnEditar);
        toolbar.add(Box.createRigidArea(new Dimension(8, 0)));
        toolbar.add(btnEliminar);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(countLabel);
        add(toolbar, BorderLayout.NORTH);

        String[] columns = {"ID", "NOMBRES", "APELLIDOS", "CORREO", "TEL\u00c9FONO", "\u00c1REA", "CURSOS", "ESTADO"};
        tm = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tm);
        Theme.estilizarTabla(table);
        table.removeColumn(table.getColumnModel().getColumn(0));
        add(Theme.styledScroll(table), BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> mostrarDialogo(null));
        btnEditar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(parent, "Selecciona un instructor.", "Editar", JOptionPane.WARNING_MESSAGE);
                return;
            }
            mostrarDialogo((Integer) tm.getValueAt(row, 0));
        });
        btnEliminar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(parent, "Selecciona un instructor.", "Desactivar", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (Integer) tm.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(parent,
                    "\u00bfDesactivar instructor " + tm.getValueAt(row, 1) + " " + tm.getValueAt(row, 2) + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.InstructorDAO iDao = new dao.InstructorDAO();
                modelo.Instructor inst = iDao.buscarPorId(id);
                if (inst != null) {
                    inst.setActivo(false);
                    iDao.actualizar(inst);
                    cargarTabla();
                }
            }
        });

        cargarTabla();
    }

    private void cargarTabla() {
        new Thread(() -> {
            try {
                java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                String sql = "SELECT i.id_instructor, u.nombres, u.apellidos, u.correo, u.telefono, "
                           + "i.area_formacion, i.activo, "
                           + "COALESCE(c_count.total, 0) as cursos "
                           + "FROM instructor i "
                           + "JOIN usuario u ON i.id_usuario = u.id_usuario "
                           + "LEFT JOIN (SELECT id_instructor, COUNT(*) as total FROM curso_instructor GROUP BY id_instructor) c_count ON i.id_instructor = c_count.id_instructor";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                     java.sql.ResultSet rs = ps.executeQuery()) {
                    java.util.List<Object[]> filas = new java.util.ArrayList<>();
                    while (rs.next()) {
                        filas.add(new Object[]{
                            rs.getInt("id_instructor"), rs.getString("nombres"), rs.getString("apellidos"),
                            rs.getString("correo"),
                            rs.getString("telefono") != null ? rs.getString("telefono") : "",
                            rs.getString("area_formacion") != null ? rs.getString("area_formacion") : "",
                            rs.getInt("cursos"), rs.getBoolean("activo") ? "Activo" : "Inactivo"
                        });
                    }
                    SwingUtilities.invokeLater(() -> {
                        tm.setRowCount(0);
                        for (Object[] row : filas) tm.addRow(row);
                        countLabel.setText("Total: " + filas.size() + " instructores");
                    });
                }
                conn.close();
            } catch (Exception ex) {
                System.err.println("[INSTRUCTORES] Error: " + ex.getMessage());
            }
        }).start();
    }

    private void mostrarDialogo(Integer idInstructor) {
        boolean esNuevo = (idInstructor == null);
        final modelo.Instructor inst;
        final modelo.Usuario usr;
        if (esNuevo) {
            inst = null; usr = null;
        } else {
            dao.InstructorDAO iDao = new dao.InstructorDAO();
            inst = iDao.buscarPorId(idInstructor);
            usr = (inst != null) ? new dao.UsuarioDAO().buscarPorId(inst.getIdUsuario()) : null;
        }

        JTextField txtNombres = new JTextField(usr != null ? usr.getNombres() : "");
        JTextField txtApellidos = new JTextField(usr != null ? usr.getApellidos() : "");
        JTextField txtCorreo = new JTextField(usr != null ? usr.getCorreo() : "");
        JTextField txtTelefono = new JTextField(usr != null ? usr.getTelefono() : "");
        JTextField txtArea = new JTextField(inst != null ? inst.getAreaFormacion() : "");

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 8, 4, 8);
        String[] labels = {"Nombres*:", "Apellidos*:", "Correo*:", "Tel\u00e9fono:", "\u00c1rea Formaci\u00f3n:"};
        if (esNuevo) labels = new String[]{"Nombres*:", "Apellidos*:", "Correo*:", "Tel\u00e9fono:", "\u00c1rea Formaci\u00f3n:", "Contrase\u00f1a*:"};
        JTextField[] campos = esNuevo ? new JTextField[]{txtNombres, txtApellidos, txtCorreo, txtTelefono, txtArea, new JTextField()} : new JTextField[]{txtNombres, txtApellidos, txtCorreo, txtTelefono, txtArea};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0; gbc.insets = new Insets(4, 8, 4, 4);
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1; gbc.insets = new Insets(4, 4, 4, 8);
            form.add(campos[i], gbc);
        }
        int result = JOptionPane.showConfirmDialog(parent, form,
                esNuevo ? "Nuevo Instructor" : "Editar Instructor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String area = txtArea.getText().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Nombres, apellidos y correo son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                if (esNuevo) {
                    String pass = ((JTextField) campos[5]).getText().trim();
                    if (pass.isEmpty()) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parent, "Contrase\u00f1a obligatoria.", "Error", JOptionPane.ERROR_MESSAGE));
                        return;
                    }
                    modelo.Usuario nuevoUsr = new modelo.Usuario();
                    nuevoUsr.setNombres(nombres);
                    nuevoUsr.setApellidos(apellidos);
                    nuevoUsr.setCorreo(correo);
                    nuevoUsr.setTelefono(telefono);
                    nuevoUsr.setPasswordHash(controlador.PasswordUtils.hash(pass));
                    nuevoUsr.setEstado(true);
                    nuevoUsr.setFechaCreacion(new java.sql.Timestamp(System.currentTimeMillis()));
                    java.sql.Connection conn = controlador.Conexion.getInstance().getConnection();
                    String sql = "INSERT INTO usuario (nombres, apellidos, correo, telefono, password_hash, estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_usuario";
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, nuevoUsr.getNombres()); ps.setString(2, nuevoUsr.getApellidos());
                        ps.setString(3, nuevoUsr.getCorreo()); ps.setString(4, nuevoUsr.getTelefono());
                        ps.setString(5, nuevoUsr.getPasswordHash()); ps.setBoolean(6, nuevoUsr.getEstado());
                        ps.setTimestamp(7, nuevoUsr.getFechaCreacion());
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            rs.next();
                            int idUsuario = rs.getInt(1);
                            String sqlRol = "INSERT INTO usuario_rol (id_usuario, id_rol) SELECT ?, id_rol FROM rol WHERE nombre = 'instructor'";
                            try (java.sql.PreparedStatement ps2 = conn.prepareStatement(sqlRol)) {
                                ps2.setInt(1, idUsuario); ps2.executeUpdate();
                            }
                            String sqlInst = "INSERT INTO instructor (id_usuario, area_formacion, activo) VALUES (?, ?, true)";
                            try (java.sql.PreparedStatement ps3 = conn.prepareStatement(sqlInst)) {
                                ps3.setInt(1, idUsuario); ps3.setString(2, area.isEmpty() ? null : area); ps3.executeUpdate();
                            }
                        }
                    }
                    conn.close();
                } else {
                    usr.setNombres(nombres); usr.setApellidos(apellidos); usr.setCorreo(correo); usr.setTelefono(telefono);
                    new dao.UsuarioDAO().actualizar(usr);
                    if (inst != null) {
                        inst.setAreaFormacion(area.isEmpty() ? null : area);
                        new dao.InstructorDAO().actualizar(inst);
                    }
                }
                SwingUtilities.invokeLater(this::cargarTabla);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
}
