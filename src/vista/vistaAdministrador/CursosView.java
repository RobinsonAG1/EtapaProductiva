package vista.vistaAdministrador;

import vista.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import static vista.Theme.*;

public class CursosView extends JPanel {

    public CursosView() {
        setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Cursos");
        title.setFont(FONT_BOLD_20);
        title.setForeground(TXT_PRIMARY);

        JLabel subtitle = new JLabel("Gesti\u00f3n de cursos y programas de formaci\u00f3n");
        subtitle.setFont(FONT_PLAIN_12);
        subtitle.setForeground(TXT_SECONDARY);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(new PlaceholderPanel("Cursos"), BorderLayout.CENTER);
    }

    private static class PlaceholderPanel extends JPanel {
        PlaceholderPanel(String name) {
            setBackground(BG_SIDEBAR);
            setLayout(new GridBagLayout());
            JLabel lbl = new JLabel("\u2B55  " + name + " — Vista en construcci\u00f3n");
            lbl.setFont(FONT_PLAIN_14);
            lbl.setForeground(TXT_DISABLED);
            add(lbl);
        }
    }
}
