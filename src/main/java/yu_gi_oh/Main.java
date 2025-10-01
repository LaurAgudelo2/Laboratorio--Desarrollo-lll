package yu_gi_oh;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Crear GUI
            DuelFrame gui = new DuelFrame();
            gui.setVisible(true);

            // Conectar lógica del juego
            new DuelGame(gui);
        });
    }
}