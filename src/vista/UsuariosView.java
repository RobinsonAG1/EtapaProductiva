package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import static vista.Theme.*;

public class UsuariosView extends JPanel {

    public UsuariosView() {
        setBackground(BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 24, 24, 24));

        JLabel countLabel = new JLabel("Cargando...");
        countLabel.setFont(FONT_PLAIN_12);
        countLabel.setForeground(TXT_SECONDARY);
        countLabel.setBorder(new EmptyBorder(16, 0, 16, 0));
        add(countLabel, BorderLayout.NORTH);

        String[] columns = {"NOMBRES", "APELLIDOS", "CORREO", "TEL\u00c9FONO", "DOCUMENTO", "ESTADO"};
        DefaultTableModel tm = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tm) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        Theme.estilizarTabla(table);
        add(Theme.styledScroll(table), BorderLayout.CENTER);

        new Thread(() -> {
            try {
                java.util.List<modelo.Usuario> lista = new dao.UsuarioDAO().listarTodos();
                SwingUtilities.invokeLater(() -> {
                    tm.setRowCount(0);
                    for (modelo.Usuario u : lista) {
                        tm.addRow(new Object[]{
                            u.getNombres(), u.getApellidos(), u.getCorreo(),
                            u.getTelefono() != null ? u.getTelefono() : "",
                            u.getNumeroDocumento() != null ? u.getNumeroDocumento() : "",
                            u.getEstado() ? "Activo" : "Inactivo"
                        });
                    }
                    countLabel.setText("Total: " + lista.size() + " usuarios");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> countLabel.setText("Error al cargar usuarios"));
            }
        }).start();
    }
}
