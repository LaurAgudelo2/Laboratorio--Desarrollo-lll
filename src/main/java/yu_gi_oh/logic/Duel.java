package yu_gi_oh.logic;

import yu_gi_oh.model.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Duel {
    private List<Card> playerCards;
    private List<Card> aiCards;
    private int playerScore;
    private int aiScore;
    private BattleListener listener;
    private Random random;
    private boolean playerTurn;
    private boolean gameStarted;

    public Duel() {
        this.playerCards = new ArrayList<>();
        this.aiCards = new ArrayList<>();
        this.random = new Random();
        this.playerScore = 0;
        this.aiScore = 0;
        this.gameStarted = false;
        // El turno inicial se define aleatoriamente
        this.playerTurn = random.nextBoolean();
    }

    public void setBattleListener(BattleListener listener) {
        this.listener = listener;
    }

    public void addPlayerCard(Card card) {
        playerCards.add(card);
        checkIfReadyToStart();
    }

    public void addAiCard(Card card) {
        aiCards.add(card);
        checkIfReadyToStart();
    }

    private void checkIfReadyToStart() {
        if (playerCards.size() == 3 && aiCards.size() == 3 && listener != null) {
            listener.onCardsLoaded();
        }
    }

    public void startDuel() {
        if (playerCards.size() == 3 && aiCards.size() == 3) {
            gameStarted = true;
            if (listener != null) {
                listener.onScoreChanged(playerScore, aiScore);
            }
        }
    }

    public void playerSelectsCard(int cardIndex) {
        if (!gameStarted || playerCards.isEmpty() || aiCards.isEmpty()) return;

        Card playerCard = playerCards.get(cardIndex);
        Card aiCard = aiCards.get(random.nextInt(aiCards.size()));

        // Determinar el resultado del turno
        String winner = calculateTurnWinner(playerCard, aiCard);

        if ("Jugador".equals(winner)) {
            playerScore++;
        } else if ("Máquina".equals(winner)) {
            aiScore++;
        }

        // Notificar al listener
        if (listener != null) {
            listener.onTurn(playerCard.getName(), aiCard.getName(), winner);
            listener.onScoreChanged(playerScore, aiScore);
        }

        // Verificar si hay un ganador del duelo
        checkDuelWinner();

        // Cambiar turno
        playerTurn = !playerTurn;
    }

    private String calculateTurnWinner(Card playerCard, Card aiCard) {
        // Ambos en ataque - comparar ATK
        int playerPower = playerCard.getAtk();
        int aiPower = aiCard.getAtk();

        if (playerPower > aiPower) {
            return "Jugador";
        } else if (aiPower > playerPower) {
            return "Máquina";
        } else {
            return "Empate";
        }
    }

    private void checkDuelWinner() {
        if (playerScore >= 2 || aiScore >= 2) {
            gameStarted = false;
            if (listener != null) {
                String winner = playerScore >= 2 ? "Jugador" : "Máquina";
                listener.onDuelEnded(winner);
            }
        }
    }

    public List<Card> getPlayerCards() {
        return playerCards;
    }

    public List<Card> getAiCards() {
        return aiCards;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void resetGame() {
        playerCards.clear();
        aiCards.clear();
        playerScore = 0;
        aiScore = 0;
        gameStarted = false;
        playerTurn = random.nextBoolean();
    }
}