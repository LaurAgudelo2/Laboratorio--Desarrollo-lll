package yu_gi_oh;

import javax.swing.*;
import java.awt.*;

public class DuelFrame extends JFrame {
    public JButton CargarCartas;
    public JButton IniciarDuelo;
    public JButton Opcion1;
    public JButton Opcion2;
    public JButton Opcion3;
    public JLabel Carta1;
    public JLabel Carta2;
    public JLabel Carta3;
    public JTextArea textArea1;
    public JPanel panel1;
    public JPanel CartasMaquina;
    public JScrollPane LogDeBatalla;
    public JPanel CartasJugador;

    private Image backgroundImage;

    public DuelFrame() {
        setTitle("Yu-Gi-Oh Duel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // ⚙️ Crea la interfaz diseñada en el .form
        $$$setupUI$$$();

        // 🖼️ Carga la imagen de fondo desde resources
        backgroundImage = new ImageIcon(getClass().getResource("/yugioh.jpg")).getImage();

        // 🔹 Hace el panel principal transparente
        panel1.setOpaque(false);

        // 🧩 Crea el panel que dibuja el fondo
        JPanel fondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // Dibuja la imagen de fondo ajustada al tamaño del frame
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        fondo.setLayout(new BorderLayout());
        fondo.add(panel1, BorderLayout.CENTER);
        setContentPane(fondo);

        // 🎨 Hace transparentes los subpaneles y áreas de texto
        hacerComponentesTransparentes();
    }

    private void hacerComponentesTransparentes() {
        try {
            panel1.setOpaque(false);
            if (CartasJugador != null) CartasJugador.setOpaque(false);
            if (CartasMaquina != null) CartasMaquina.setOpaque(false);

            if (Carta1 != null) Carta1.setOpaque(false);
            if (Carta2 != null) Carta2.setOpaque(false);
            if (Carta3 != null) Carta3.setOpaque(false);

            if (LogDeBatalla != null) {
                LogDeBatalla.setOpaque(false);
                if (LogDeBatalla.getViewport() != null)
                    LogDeBatalla.getViewport().setOpaque(false);
                if (textArea1 != null) {
                    textArea1.setOpaque(false);
                    textArea1.setBackground(new Color(0, 0, 0, 80)); // Fondo semitransparente para mejor lectura
                    textArea1.setForeground(Color.WHITE);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error al ajustar transparencias: " + e.getMessage());
        }
    }

    private void createUIComponents() {
        // IntelliJ usa esto internamente, déjalo vacío
    }

    // ⚠️ No eliminar: lo genera automáticamente IntelliJ
    private void $$$setupUI$$$() {}
}
