package yu_gi_oh;

import yu_gi_oh.logic.BattleListener;
import yu_gi_oh.logic.Duel;
import yu_gi_oh.api.ApiClient;
import yu_gi_oh.model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DuelGame implements BattleListener {
    private DuelFrame gui;
    private Duel duel;
    private ApiClient apiClient;
    private Random random;
    private List<Card> playerCards;
    private List<Card> aiCards;
    private int playerScore = 0;
    private int aiScore = 0;
    private boolean gameEnded = false;

    public DuelGame(DuelFrame gui) {
        this.gui = gui;
        this.duel = new Duel();
        this.apiClient = new ApiClient();
        this.duel.setBattleListener(this);
        this.random = new Random();
        this.playerCards = new ArrayList<>();
        this.aiCards = new ArrayList<>();

        initializeGUI();
        setupListeners();
    }

    private <T> T getComponent(String fieldName, Class<T> componentClass) {
        try {
            Field field = DuelFrame.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return componentClass.cast(field.get(gui));
        } catch (Exception e) {
            System.err.println("Error accediendo a " + fieldName + ": " + e.getMessage());
            return null;
        }
    }

    private void initializeGUI() {
        setButtonStates(true, false);
        enablePlayerCards(false);

        // Configurar cartas de la máquina como ocultas
        JLabel carta1 = getComponent("Carta1", JLabel.class);
        JLabel carta2 = getComponent("Carta2", JLabel.class);
        JLabel carta3 = getComponent("Carta3", JLabel.class);

        JLabel[] aiCardLabels = {carta1, carta2, carta3};
        for (JLabel label : aiCardLabels) {
            if (label != null) {
                setupHiddenCardLabel(label);
            }
        }

        // Configurar text area
        JTextArea textArea = getComponent("textArea1", JTextArea.class);
        if (textArea != null) {
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        }

        // Ocultar botones de cartas inicialmente
        JButton opcion1 = getComponent("Opcion1", JButton.class);
        JButton opcion2 = getComponent("Opcion2", JButton.class);
        JButton opcion3 = getComponent("Opcion3", JButton.class);

        JButton[] playerButtons = {opcion1, opcion2, opcion3};
        for (JButton button : playerButtons) {
            if (button != null) {
                button.setVisible(false);
                button.setEnabled(false);
            }
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
        // Botón Cargar Cartas
        JButton cargarCartas = getComponent("CargarCartas", JButton.class);
        if (cargarCartas != null) {
            cargarCartas.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadCards();
                }
            });
        }

        // Botón Iniciar Duelo
        JButton iniciarDuelo = getComponent("IniciarDuelo", JButton.class);
        if (iniciarDuelo != null) {
            iniciarDuelo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startDuel();
                }
            });
        }

        // Botones de cartas del jugador
        JButton opcion1 = getComponent("Opcion1", JButton.class);
        JButton opcion2 = getComponent("Opcion2", JButton.class);
        JButton opcion3 = getComponent("Opcion3", JButton.class);

        JButton[] playerCardButtons = {opcion1, opcion2, opcion3};
        for (int i = 0; i < playerCardButtons.length; i++) {
            if (playerCardButtons[i] != null) {
                final int cardIndex = i;
                playerCardButtons[i].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        playerSelectsCard(cardIndex);
                    }
                });
            }
        }
    }

    private void loadCards() {
        setButtonStates(false, false);
        gameEnded = false;
        JTextArea textArea = getComponent("textArea1", JTextArea.class);
        if (textArea != null) {
            textArea.setText("");
            textArea.append("Cargando cartas...\n");
        }

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                duel.resetGame();
                playerCards.clear();
                aiCards.clear();
                playerScore = 0;
                aiScore = 0;

                // Cargar cartas del jugador
                for (int i = 0; i < 3; i++) {
                    try {
                        publish("Cargando carta del jugador " + (i + 1) + "...");
                        Card card = apiClient.getRandomMonsterCard();
                        playerCards.add(card);
                        duel.addPlayerCard(card);
                        publish("✓ " + card.getName() + " (ATK: " + card.getAtk() + ", DEF: " + card.getDef() + ")");
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        publish("✗ Error - Usando carta de respaldo");
                        Card backupCard = createBackupCard("Jugador-" + (i + 1));
                        playerCards.add(backupCard);
                        duel.addPlayerCard(backupCard);
                    }
                }

                // Cargar cartas de la máquina
                for (int i = 0; i < 3; i++) {
                    try {
                        publish("Cargando carta de la máquina " + (i + 1) + "...");
                        Card card = apiClient.getRandomMonsterCard();
                        aiCards.add(card);
                        duel.addAiCard(card);
                        publish("✓ " + card.getName() + " (ATK: " + card.getAtk() + ", DEF: " + card.getDef() + ")");
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        publish("✗ Error - Usando carta de respaldo");
                        Card backupCard = createBackupCard("Máquina-" + (i + 1));
                        aiCards.add(backupCard);
                        duel.addAiCard(backupCard);
                    }
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                JTextArea textArea = getComponent("textArea1", JTextArea.class);
                if (textArea != null) {
                    for (String message : chunks) {
                        textArea.append(message + "\n");
                    }
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                    JTextArea textArea = getComponent("textArea1", JTextArea.class);
                    if (textArea != null) {
                        textArea.append("¡Todas las cartas cargadas!\n");
                    }
                    updatePlayerCardsDisplay();
                    updateAiCardsDisplay(); // ✅ NUEVO: Mostrar cartas de máquina ocultas
                    setButtonStates(true, true);
                } catch (Exception ex) {
                    onError("Error durante la carga: " + ex.getMessage());
                    setButtonStates(true, false);
                }
            }
        }.execute();
    }

    private void startDuel() {
        if (gameEnded) {
            JOptionPane.showMessageDialog(gui,
                    "El duelo ya terminó. Presiona 'Cargar Cartas' para un nuevo juego.",
                    "Duelo Terminado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        duel.startDuel();
        setButtonStates(false, false);
        enablePlayerCards(true);
        JTextArea textArea = getComponent("textArea1", JTextArea.class);
        if (textArea != null) {
            textArea.append("=== DUELO INICIADO ===\n");
            textArea.append("Primero en llegar a 2 puntos gana!\n");
        }
        updateScoreDisplay();
    }

    private void playerSelectsCard(int cardIndex) {
        if (gameEnded) return;
        if (duel.isGameStarted() && duel.isPlayerTurn() && cardIndex < playerCards.size()) {
            Card selectedCard = playerCards.get(cardIndex);
            JTextArea textArea = getComponent("textArea1", JTextArea.class);
            if (textArea != null) {
                textArea.append("Seleccionaste: " + selectedCard.getName() + "\n");
            }
            duel.playerSelectsCard(cardIndex);
            enablePlayerCards(false);
        }
    }

    private void updatePlayerCardsDisplay() {
        JButton opcion1 = getComponent("Opcion1", JButton.class);
        JButton opcion2 = getComponent("Opcion2", JButton.class);
        JButton opcion3 = getComponent("Opcion3", JButton.class);

        JButton[] playerCardButtons = {opcion1, opcion2, opcion3};

        for (int i = 0; i < playerCardButtons.length; i++) {
            if (playerCardButtons[i] != null) {
                if (i < playerCards.size()) {
                    Card card = playerCards.get(i);
                    setupCardButton(playerCardButtons[i], card);
                } else {
                    playerCardButtons[i].setVisible(false);
                }
            }
        }
    }

    // ✅ NUEVO: Mostrar cartas de la máquina (ocultas pero con imágenes)
    private void updateAiCardsDisplay() {
        JLabel carta1 = getComponent("Carta1", JLabel.class);
        JLabel carta2 = getComponent("Carta2", JLabel.class);
        JLabel carta3 = getComponent("Carta3", JLabel.class);

        JLabel[] aiCardLabels = {carta1, carta2, carta3};

        for (int i = 0; i < aiCardLabels.length; i++) {
            if (aiCardLabels[i] != null && i < aiCards.size()) {
                setupHiddenCardWithImage(aiCardLabels[i], aiCards.get(i));
            }
        }
    }

    private void setupCardButton(JButton button, Card card) {
        button.setText("<html><center><b>" + card.getName() +
                "</b><br>ATK: " + card.getAtk() +
                "<br>DEF: " + card.getDef() + "</center></html>");
        button.setVisible(true);
        loadCardImage(card.getImageUrl(), button, true);
    }

    // ✅ NUEVO: Configurar carta oculta con imagen de fondo
    private void setupHiddenCardWithImage(JLabel label, Card card) {
        label.setText("?"); // Mantener el símbolo de oculto
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(30, 30, 30, 200)); // Fondo semi-transparente
        label.setForeground(Color.RED);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        label.setPreferredSize(new Dimension(120, 150));

        // Cargar imagen de fondo (oculta)
        loadCardImageBackground(card.getImageUrl(), label);
    }

    private void loadCardImage(String imageUrl, JButton button, boolean isPlayer) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                try {
                    ImageIcon originalIcon = new ImageIcon(new java.net.URL(imageUrl));
                    Image scaledImage = originalIcon.getImage().getScaledInstance(100, 120, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);
                } catch (Exception e) {
                    return createPlaceholderIcon();
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    button.setIcon(icon);
                    button.setVerticalTextPosition(SwingConstants.BOTTOM);
                    button.setHorizontalTextPosition(SwingConstants.CENTER);
                } catch (Exception ex) {
                    button.setIcon(createPlaceholderIcon());
                }
            }
        }.execute();
    }

    // ✅ NUEVO: Cargar imagen de fondo para cartas ocultas
    private void loadCardImageBackground(String imageUrl, JLabel label) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                try {
                    ImageIcon originalIcon = new ImageIcon(new java.net.URL(imageUrl));
                    // Hacer la imagen más oscura para indicar que está oculta
                    java.awt.image.BufferedImage darkenedImage = darkenImage(originalIcon.getImage());
                    Image scaledImage = darkenedImage.getScaledInstance(120, 150, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);
                } catch (Exception e) {
                    return createDarkPlaceholderIcon();
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    label.setIcon(icon);
                } catch (Exception ex) {
                    label.setIcon(createDarkPlaceholderIcon());
                }
            }
        }.execute();
    }

    // ✅ NUEVO: Oscurecer imagen para cartas ocultas
    private java.awt.image.BufferedImage darkenImage(Image image) {
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(
                image.getWidth(null), image.getHeight(null), java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // Aplicar filtro de oscurecimiento
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int rgb = bufferedImage.getRGB(x, y);
                Color color = new Color(rgb);
                Color darker = color.darker().darker(); // Oscurecer dos veces
                bufferedImage.setRGB(x, y, darker.getRGB());
            }
        }
        return bufferedImage;
    }

    private ImageIcon createPlaceholderIcon() {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(100, 120, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(new Color(70, 130, 180));
        g2d.fillRect(0, 0, 100, 120);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Carta", 30, 60);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createDarkPlaceholderIcon() {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(120, 150, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(new Color(30, 30, 30));
        g2d.fillRect(0, 0, 120, 150);

        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("?", 50, 80);

        g2d.dispose();
        return new ImageIcon(image);
    }

    private void updateAiCardDisplay(int cardIndex, Card card) {
        JLabel carta1 = getComponent("Carta1", JLabel.class);
        JLabel carta2 = getComponent("Carta2", JLabel.class);
        JLabel carta3 = getComponent("Carta3", JLabel.class);

        JLabel[] aiCardLabels = {carta1, carta2, carta3};
        if (cardIndex < aiCardLabels.length && aiCardLabels[cardIndex] != null) {
            JLabel label = aiCardLabels[cardIndex];
            // Mostrar carta revelada
            label.setText("<html><center><b>" + card.getName() +
                    "</b><br>ATK: " + card.getAtk() +
                    "<br>DEF: " + card.getDef() + "</center></html>");
            label.setBackground(Color.LIGHT_GRAY);
            label.setForeground(Color.BLACK);
            label.setFont(new Font("Arial", Font.PLAIN, 9));
            loadCardImage(card.getImageUrl(), new JButton(), false); // Solo cargar imagen normal
        }
    }

    private void setButtonStates(boolean loadEnabled, boolean startEnabled) {
        JButton cargarCartas = getComponent("CargarCartas", JButton.class);
        JButton iniciarDuelo = getComponent("IniciarDuelo", JButton.class);

        if (cargarCartas != null) {
            cargarCartas.setEnabled(loadEnabled);
        }
        if (iniciarDuelo != null) {
            iniciarDuelo.setEnabled(startEnabled);
        }
    }

    private void enablePlayerCards(boolean enabled) {
        if (gameEnded) enabled = false;

        JButton opcion1 = getComponent("Opcion1", JButton.class);
        JButton opcion2 = getComponent("Opcion2", JButton.class);
        JButton opcion3 = getComponent("Opcion3", JButton.class);

        JButton[] playerCardButtons = {opcion1, opcion2, opcion3};
        for (JButton button : playerCardButtons) {
            if (button != null && button.isVisible()) {
                button.setEnabled(enabled);
            }
        }
    }

    private Card createBackupCard(String name) {
        int atk = 1000 + random.nextInt(2000);
        int def = 800 + random.nextInt(1500);
        return new Card(name, atk, def,
                "https://via.placeholder.com/120x150/4A6572/FFFFFF?text=" + name.replace(" ", "+"),
                "Monster");
    }

    private void updateScoreDisplay() {
        JTextArea textArea = getComponent("textArea1", JTextArea.class);
        if (textArea != null) {
            textArea.append("Puntuación - Jugador: " + playerScore + " | Máquina: " + aiScore + "\n");

            // ✅ NUEVO: Verificar si alguien ganó
            if (playerScore >= 2 || aiScore >= 2) {
                gameEnded = true;
                String winner = playerScore >= 2 ? "Jugador" : "Máquina";
                textArea.append("\n🎉 ¡" + winner.toUpperCase() + " GANA EL DUELO! 🎉\n");
                textArea.append("Presiona 'Cargar Cartas' para un nuevo juego.\n");

                enablePlayerCards(false);
                setButtonStates(true, false);

                // Mostrar todas las cartas de la máquina al final
                revealAllAiCards();
            }
        }
    }

    // ✅ NUEVO: Revelar todas las cartas de la máquina al final
    private void revealAllAiCards() {
        JLabel carta1 = getComponent("Carta1", JLabel.class);
        JLabel carta2 = getComponent("Carta2", JLabel.class);
        JLabel carta3 = getComponent("Carta3", JLabel.class);

        JLabel[] aiCardLabels = {carta1, carta2, carta3};
        for (int i = 0; i < aiCards.size() && i < aiCardLabels.length; i++) {
            if (aiCardLabels[i] != null) {
                Card card = aiCards.get(i);
                aiCardLabels[i].setText("<html><center><b>" + card.getName() +
                        "</b><br>ATK: " + card.getAtk() +
                        "<br>DEF: " + card.getDef() + "</center></html>");
                aiCardLabels[i].setBackground(Color.LIGHT_GRAY);
                aiCardLabels[i].setForeground(Color.BLACK);
                aiCardLabels[i].setFont(new Font("Arial", Font.PLAIN, 9));
            }
        }
    }

    // Implementación de BattleListener
    @Override
    public void onTurn(String playerCard, String aiCard, String winner) {
        SwingUtilities.invokeLater(() -> {
            if (gameEnded) return;

            JLabel carta1 = getComponent("Carta1", JLabel.class);
            JLabel carta2 = getComponent("Carta2", JLabel.class);
            JLabel carta3 = getComponent("Carta3", JLabel.class);

            JLabel[] aiCardLabels = {carta1, carta2, carta3};

            // Mostrar carta de la máquina que fue usada
            for (int i = 0; i < aiCards.size(); i++) {
                if (aiCards.get(i).getName().equals(aiCard)) {
                    updateAiCardDisplay(i, aiCards.get(i));
                    break;
                }
            }

            String result = "Turno: " + playerCard + " vs " + aiCard + " -> Ganador: " + winner + "\n";
            JTextArea textArea = getComponent("textArea1", JTextArea.class);
            if (textArea != null) {
                textArea.append(result);
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }

            // Actualizar puntuación
            if ("Jugador".equals(winner)) {
                playerScore++;
            } else if ("Máquina".equals(winner)) {
                aiScore++;
            }
            updateScoreDisplay();

            // Preparar siguiente turno (solo si el juego no ha terminado)
            if (!gameEnded && duel.isGameStarted() && duel.isPlayerTurn()) {
                enablePlayerCards(true);
                if (textArea != null) {
                    textArea.append("Tu turno - Elige una carta\n");
                }

                // Resetear display de cartas de la máquina para el próximo turno
                for (JLabel label : aiCardLabels) {
                    if (label != null) {
                        setupHiddenCardWithImage(label, aiCards.get(0)); // Usar primera carta como referencia
                    }
                }
            }
        });
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore) {
        SwingUtilities.invokeLater(() -> {
            this.playerScore = playerScore;
            this.aiScore = aiScore;
            updateScoreDisplay();
        });
    }

    @Override
    public void onDuelEnded(String winner) {
        SwingUtilities.invokeLater(() -> {
            gameEnded = true;
            JTextArea textArea = getComponent("textArea1", JTextArea.class);
            if (textArea != null) {
                textArea.append("\n🎉 ¡EL DUELO HA TERMINADO! GANADOR: " + winner.toUpperCase() + " 🎉\n");
                textArea.append("Presiona 'Cargar Cartas' para un nuevo juego.\n");
            }

            JOptionPane.showMessageDialog(gui,
                    "¡" + winner + " gana el duelo!\n\n" +
                            "Puntuación final:\n" +
                            "Jugador: " + playerScore + "\n" +
                            "Máquina: " + aiScore + "\n\n" +
                            "Presiona 'Cargar Cartas' para jugar de nuevo.",
                    "Fin del Duelo",
                    JOptionPane.INFORMATION_MESSAGE);

            setButtonStates(true, false);
            enablePlayerCards(false);

            // Mostrar todas las cartas de la máquina al final
            revealAllAiCards();
        });
    }

    @Override
    public void onError(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JTextArea textArea = getComponent("textArea1", JTextArea.class);
            if (textArea != null) {
                textArea.append("❌ ERROR: " + errorMessage + "\n");
            }
            JOptionPane.showMessageDialog(gui, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            setButtonStates(true, false);
        });
    }

    @Override
    public void onCardsLoaded() {
        SwingUtilities.invokeLater(() -> {
            JTextArea textArea = getComponent("textArea1", JTextArea.class);
            if (textArea != null) {
                textArea.append("Cartas cargadas exitosamente\n");
            }
            updatePlayerCardsDisplay();
            updateAiCardsDisplay();
        });
    }
}