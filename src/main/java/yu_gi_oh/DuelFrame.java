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

    public DuelFrame() {
        // Panel principal con BorderLayout
        panel1 = new JPanel(new BorderLayout(10, 10));
        panel1.setBackground(new Color(30, 30, 30)); // fondo oscuro
        setContentPane(panel1);

        // --- Panel superior: botones principales ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        topPanel.setBackground(new Color(45, 45, 45));

        CargarCartas = new JButton("Cargar Cartas");
        IniciarDuelo = new JButton("Iniciar Duelo");
        styleButton(CargarCartas);
        styleButton(IniciarDuelo);

        topPanel.add(CargarCartas);
        topPanel.add(IniciarDuelo);

        panel1.add(topPanel, BorderLayout.NORTH);

        // --- Panel central: cartas (Maquina arriba / Jugador abajo) ---
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        centerPanel.setBackground(new Color(30, 30, 30));

        CartasMaquina = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        CartasJugador = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        CartasMaquina.setBackground(new Color(60, 20, 20));
        CartasJugador.setBackground(new Color(20, 40, 20));

        Carta1 = createCardLabel("Carta 1");
        Carta2 = createCardLabel("Carta 2");
        Carta3 = createCardLabel("Carta 3");

        CartasJugador.add(Carta1);
        CartasJugador.add(Carta2);
        CartasJugador.add(Carta3);

        centerPanel.add(CartasMaquina);
        centerPanel.add(CartasJugador);

        panel1.add(centerPanel, BorderLayout.CENTER);

        // --- Panel derecho: opciones ---
        JPanel rightPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        rightPanel.setBackground(new Color(45, 45, 45));

        Opcion1 = new JButton("Opción 1");
        Opcion2 = new JButton("Opción 2");
        Opcion3 = new JButton("Opción 3");
        styleButton(Opcion1);
        styleButton(Opcion2);
        styleButton(Opcion3);

        rightPanel.add(Opcion1);
        rightPanel.add(Opcion2);
        rightPanel.add(Opcion3);

        panel1.add(rightPanel, BorderLayout.EAST);

        // --- Panel inferior: log de batalla ---
        textArea1 = new JTextArea();
        textArea1.setEditable(false);
        textArea1.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea1.setBackground(new Color(20, 20, 20));
        textArea1.setForeground(Color.GREEN);

        LogDeBatalla = new JScrollPane(textArea1);
        LogDeBatalla.setPreferredSize(new Dimension(0, 150));
        panel1.add(LogDeBatalla, BorderLayout.SOUTH);

        // --- Configuración de la ventana ---
        setTitle("Yu-Gi-Oh! Duel Lite");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 70, 150));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private JLabel createCardLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(120, 180));
        label.setOpaque(true);
        label.setBackground(new Color(200, 200, 200));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DuelFrame().setVisible(true);
        });
    }
}
