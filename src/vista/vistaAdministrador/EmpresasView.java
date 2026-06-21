package vista.vistaAdministrador;

import vista.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import static vista.Theme.*;

public class EmpresasView extends JPanel {

    public EmpresasView() {
        setBackground(BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 24, 24, 24));

        JLabel countLabel = new JLabel("Cargando...");
        countLabel.setFont(FONT_PLAIN_12);
        countLabel.setForeground(TXT_SECONDARY);
        countLabel.setBorder(new EmptyBorder(16, 0, 16, 0));
        add(countLabel, BorderLayout.NORTH);

        String[] columns = {"NIT", "NOMBRE", "DIRECCI\u00d3N", "TEL\u00c9FONO", "CONTACTO", "ESTADO"};
        DefaultTableModel tm = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tm) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        Theme.estilizarTabla(table);
        add(Theme.styledScroll(table), BorderLayout.CENTER);

        new Thread(() -> {
            try {
                java.util.List<modelo.Empresa> lista = new dao.EmpresaDAO().listarTodas();
                SwingUtilities.invokeLater(() -> {
                    tm.setRowCount(0);
                    for (modelo.Empresa e : lista) {
                        tm.addRow(new Object[]{
                            e.getNit() != null ? e.getNit() : "",
                            e.getNombre(),
                            e.getDireccion() != null ? e.getDireccion() : "",
                            e.getTelefono() != null ? e.getTelefono() : "",
                            e.getContacto() != null ? e.getContacto() : "",
                            e.getActiva() ? "Activa" : "Inactiva"
                        });
                    }
                    countLabel.setText("Total: " + lista.size() + " empresas");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> countLabel.setText("Error al cargar empresas"));
            }
        }).start();
    }
}
