package yu_gi_oh;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private final Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        // Cargar la imagen desde resources
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(getClass().getResource(imagePath));
        } catch (Exception e) {
            System.out.println("No se encontro la imagen: " + imagePath);
        }
        backgroundImage = (icon != null) ? icon.getImage() : null;
        setLayout(new BorderLayout());
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            //nos ajusta la imagen estirada al tamaño del panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
