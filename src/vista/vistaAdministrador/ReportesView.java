package vista.vistaAdministrador;

import vista.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import static vista.Theme.*;

public class ReportesView extends JPanel {

    public ReportesView() {
        setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Reportes");
        title.setFont(FONT_BOLD_20);
        title.setForeground(TXT_PRIMARY);

        JLabel subtitle = new JLabel("Visualizaci\u00f3n de reportes y estad\u00edsticas");
        subtitle.setFont(FONT_PLAIN_12);
        subtitle.setForeground(TXT_SECONDARY);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG_SIDEBAR);
        JLabel lbl = new JLabel("\uD83D\uDCCA  Reportes — Vista en construcci\u00f3n");
        lbl.setFont(FONT_PLAIN_14);
        lbl.setForeground(TXT_DISABLED);
        center.add(lbl);
        add(center, BorderLayout.CENTER);
    }
}
