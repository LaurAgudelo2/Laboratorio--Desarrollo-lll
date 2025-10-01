package yu_gi_oh;

import javax.swing.*;

public class DuelFrame extends JFrame {
    private JButton iniciarButton;
    private JButton seleccionarCartasButton;
    private JPanel panel1;
    private JTextArea textArea1;
    private JButton CargarCartas;
    private JButton IniciarDuelo;
    private JButton Opcion1;
    private JButton Opcion2;
    private JButton Opcion3;
    private JPanel CartasMaquina;
    private JLabel Carta1;
    private JLabel Carta2;
    private JLabel Carta3;
    private JScrollPane LogDeBatalla;
    private JPanel CartasJugador;

    public DuelFrame() {
        setContentPane(panel1);
        setTitle("Yu-Gi-Oh! Duel Lite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
}