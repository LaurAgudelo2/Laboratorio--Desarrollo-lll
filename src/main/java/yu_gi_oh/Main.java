package yu_gi_oh;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Intentar establecer el look and feel del sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "No se pudo aplicar el estilo nativo. Usando estilo predeterminado.", "Error de Estilo", JOptionPane.WARNING_MESSAGE);
            }

            // Crear y configurar la GUI
            DuelFrame gui = new DuelFrame();
            new DuelGame(gui); // Iniciar la lógica del juego
            gui.setVisible(true); // Mostrar la GUI después de inicializar DuelGame
        });
    }
}