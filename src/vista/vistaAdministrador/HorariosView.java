package vista.vistaAdministrador;

import vista.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import static vista.Theme.*;

public class HorariosView extends JPanel {

    public HorariosView() {
        setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Horarios");
        title.setFont(FONT_BOLD_20);
        title.setForeground(TXT_PRIMARY);

        JLabel subtitle = new JLabel("Asignaci\u00f3n de horarios a fichas y cursos");
        subtitle.setFont(FONT_PLAIN_12);
        subtitle.setForeground(TXT_SECONDARY);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG_SIDEBAR);
        JLabel lbl = new JLabel("\u23F0  Horarios — Vista en construcci\u00f3n");
        lbl.setFont(FONT_PLAIN_14);
        lbl.setForeground(TXT_DISABLED);
        center.add(lbl);
        add(center, BorderLayout.CENTER);
    }
}
