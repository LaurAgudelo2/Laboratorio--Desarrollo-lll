package yu_gi_oh;

import yu_gi_oh.api.ApiClient;
import yu_gi_oh.logic.BattleListener;
import yu_gi_oh.logic.Duel;
import yu_gi_oh.model.Card;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class DuelGame implements BattleListener {
    private final DuelFrame gui;
    private final Duel duel;
    private final ApiClient apiClient;
    private final Random random = new Random();

    public DuelGame(DuelFrame gui) {
        this.gui = gui;
        this.duel = new Duel();
        this.apiClient = new ApiClient();
        duel.setBattleListener(this);
        initializeGUI();
        setupListeners();
    }

    private void initializeGUI() {
        setButtonStates(true, false);
        enablePlayerCards(false);
        gui.textArea1.setEditable(false);
        for (JLabel label : new JLabel[]{gui.Carta1, gui.Carta2, gui.Carta3}) {
            setupHiddenCardLabel(label);
        }
        for (JButton button : new JButton[]{gui.Opcion1, gui.Opcion2, gui.Opcion3}) {
            button.setVisible(false);
        }
    }

    private void setupHiddenCardLabel(JLabel label) {
        label.setText("?");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.DARK_GRAY);
        label.setForeground(Color.RED);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        label.setPreferredSize(new Dimension(120, 150));
    }

    private void setupListeners() {
        gui.CargarCartas.addActionListener(e -> loadCards());
        gui.IniciarDuelo.addActionListener(e -> startDuel());
        JButton[] playerButtons = {gui.Opcion1, gui.Opcion2, gui.Opcion3};
        for (int i = 0; i < playerButtons.length; i++) {
            int index = i;
            playerButtons[i].addActionListener(e -> playerCardSelected(index));
        }
    }

    private void loadCards() {
        setButtonStates(false, false);
        gui.textArea1.setText("Cargando cartas...\n");
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                duel.reset();
                for (int i = 0; i < 3; i++) {
                    try {
                        Card card = apiClient.fetchRandomMonsterCard();
                        duel.addPlayerCard(card);
                        publish("Carta jugador: " + card);
                    } catch (Exception ex) {
                        publish("Error cargando carta jugador: " + ex.getMessage());
                    }
                }
                for (int i = 0; i < 3; i++) {
                    try {
                        Card card = apiClient.fetchRandomMonsterCard();
                        duel.addAiCard(card);
                        publish("Carta maquina: " + card);
                    } catch (Exception ex) {
                        publish("Error cargando carta maquina: " + ex.getMessage());
                    }
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String msg : chunks) {
                    appendLog(msg);
                }
            }

            @Override
            protected void done() {
                appendLog("Cartas cargadas");
                updatePlayerCardsDisplay();
                updateAiCardsDisplay();
                setButtonStates(false, true);
            }
        }.execute();
    }

    private void startDuel() {
        duel.startDuel();
        setButtonStates(false, false);
        appendLog("=== DUELO INICIADO ===\nPrimero en 2 puntos gana");
        executeTurn();
    }

    private void executeTurn() {
        if (!duel.isGameStarted() || duel.getPlayerCards().isEmpty() || duel.getAiCards().isEmpty()) {
            duel.checkDuelEnd();
            return;
        }

        enablePlayerCards(true);
        if (duel.isPlayerTurn()) {
            appendLog("Tu turno: Elige carta para atacar");
        } else {
            int[] aiIdx = new int[1];
            Card aiAttacker = duel.selectAiRandomCard(aiIdx);
            updateAiCardDisplay(aiIdx[0], aiAttacker);
            appendLog("Maquina ataca con: " + aiAttacker);
            appendLog("Elige carta para defender");
        }
    }

    private void playerCardSelected(int index) {
        if (!duel.isGameStarted()) return;
        Card playerCard = duel.getPlayerCards().get(index);
        int[] aiIdx = new int[1];
        Card aiCard = duel.selectAiRandomCard(aiIdx);
        updateAiCardDisplay(aiIdx[0], aiCard);

        String turnWinner = duel.calculateWinner(playerCard, aiCard, false);
        onTurn(playerCard, aiCard, turnWinner);
        duel.updateScore(turnWinner, true);

        duel.removePlayerCard(index);
        duel.removeAiCard(aiIdx[0]);
        duel.checkDuelEnd();
        updatePlayerCardsDisplay();
        updateAiCardsDisplay();
        if (duel.isGameStarted()) {
            executeTurn();
        }
    }

    private String getWinnerString(String turnWinner, boolean isPlayerAttacker) {
        if ("Empate".equals(turnWinner)) return "Empate";
        return "Attacker".equals(turnWinner) ? (isPlayerAttacker ? "Jugador" : "Maquina") : (isPlayerAttacker ? "Maquina" : "Jugador");
    }

    private void updatePlayerCardsDisplay() {
        JButton[] buttons = {gui.Opcion1, gui.Opcion2, gui.Opcion3};
        List<Card> cards = duel.getPlayerCards();
        for (int i = 0; i < buttons.length; i++) {
            if (i < cards.size()) {
                Card card = cards.get(i);
                buttons[i].setText("<html><center>" + card.getName() + "<br>ATK: " + card.getAtk() + "<br>DEF: " + card.getDef() + "</center></html>");
                buttons[i].setVisible(true);
                loadImageToComponent(card.getImageUrl(), buttons[i]);
            } else {
                buttons[i].setVisible(false);
            }
        }
    }

    private void updateAiCardsDisplay() {
        JLabel[] labels = {gui.Carta1, gui.Carta2, gui.Carta3};
        List<Card> cards = duel.getAiCards();
        for (int i = 0; i < labels.length; i++) {
            if (i < cards.size()) {
                setupHiddenCardLabel(labels[i]);
                labels[i].setVisible(true);
            } else {
                labels[i].setVisible(false);
            }
        }
    }

    private void updateAiCardDisplay(int index, Card card) {
        JLabel[] labels = {gui.Carta1, gui.Carta2, gui.Carta3};
        if (index >= 0 && index < labels.length) {
            labels[index].setText("<html><center>" + card.getName() + "<br>ATK: " + card.getAtk() + "<br>DEF: " + card.getDef() + "</center></html>");
            labels[index].setBackground(Color.LIGHT_GRAY);
            labels[index].setForeground(Color.BLACK);
            labels[index].setFont(new Font("Arial", Font.PLAIN, 9));
            loadImageToComponent(card.getImageUrl(), labels[index]);
        }
    }

    private void loadImageToComponent(String url, JComponent component) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    ImageIcon icon = new ImageIcon(new URL(url));
                    Image scaled = icon.getImage().getScaledInstance(120, 150, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                } catch (Exception e) {
                    return new ImageIcon();
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (component instanceof JButton) ((JButton) component).setIcon(icon);
                    else if (component instanceof JLabel) ((JLabel) component).setIcon(icon);
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void setButtonStates(boolean loadEnabled, boolean startEnabled) {
        gui.CargarCartas.setEnabled(loadEnabled);
        gui.IniciarDuelo.setEnabled(startEnabled);
    }

    private void enablePlayerCards(boolean enabled) {
        JButton[] buttons = {gui.Opcion1, gui.Opcion2, gui.Opcion3};
        for (JButton button : buttons) {
            if (button.isVisible()) button.setEnabled(enabled);
        }
    }

    private void appendLog(String msg) {
        gui.textArea1.append(msg + "\n");
        gui.textArea1.setCaretPosition(gui.textArea1.getDocument().getLength());
    }

    @Override
    public void onTurn(Card playerCard, Card aiCard, String winner) {
        appendLog("Jugador: " + playerCard + " vs Maquina: " + aiCard + ". Ganador: " + winner);
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore) {
        appendLog("Puntaje: Jugador " + playerScore + " - Maquina " + aiScore);
    }

    @Override
    public void onDuelEnded(String winner) {
        appendLog("Duelo terminado. Ganador: " + winner);
        JOptionPane.showMessageDialog(gui, "Ganador: " + winner);
        setButtonStates(true, false);
        enablePlayerCards(false);
    }

    @Override
    public void onError(String errorMessage) {
        appendLog("Error: " + errorMessage);
        JOptionPane.showMessageDialog(gui, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onCardsLoaded() {
        appendLog("Cartas listas");
    }
}