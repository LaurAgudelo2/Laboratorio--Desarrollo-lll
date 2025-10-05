package yu_gi_oh.logic;

import yu_gi_oh.model.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Duel {
    private final List<Card> playerCards = new ArrayList<>();
    private final List<Card> aiCards = new ArrayList<>();
    private int playerScore = 0;
    private int aiScore = 0;
    private boolean playerTurn;
    private boolean gameStarted = false;
    private final Random random = new Random();
    private BattleListener listener;

    public void setBattleListener(BattleListener listener) {
        this.listener = listener;
    }

    public void addPlayerCard(Card card) {
        playerCards.add(card);
        checkIfReady();
    }

    public void addAiCard(Card card) {
        aiCards.add(card);
        checkIfReady();
    }

    private void checkIfReady() {
        if (playerCards.size() == 3 && aiCards.size() == 3 && listener != null) {
            listener.onCardsLoaded();
        }
    }

    public void startDuel() {
        if (playerCards.size() == 3 && aiCards.size() == 3) {
            playerTurn = random.nextBoolean();
            gameStarted = true;
            listener.onScoreChanged(playerScore, aiScore);
        } else {
            listener.onError("Cartas no cargadas completamente.");
        }
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public List<Card> getPlayerCards() {
        return new ArrayList<>(playerCards);
    }

    public List<Card> getAiCards() {
        return new ArrayList<>(aiCards);
    }


    public void removePlayerCard(int index) {
        if (index >= 0 && index < playerCards.size()) {
            playerCards.remove(index);
        }
    }

    public void removeAiCard(int index) {
        if (index >= 0 && index < aiCards.size()) {
            aiCards.remove(index);
        }
    }

    public Card selectAiRandomCard(int[] outIndex) {
        if (aiCards.isEmpty()) return null;
        int idx = random.nextInt(aiCards.size());
        outIndex[0] = idx;
        return aiCards.get(idx);
    }

    public String calculateWinner(Card attacker, Card defender, boolean defenderInDefense) {
        int defStat = defenderInDefense ? defender.getDef() : defender.getAtk();
        if (attacker.getAtk() > defStat) {
            return "Attacker";
        } else if (attacker.getAtk() < defStat) {
            return "Defender";
        } else {
            return "Empate";
        }
    }

    public void updateScore(String turnWinner, boolean isPlayerTurn) {
        if ("Empate".equals(turnWinner)) {
            return;
        }
        String winner = "Attacker".equals(turnWinner) ? (isPlayerTurn ? "Jugador" : "Máquina") : (isPlayerTurn ? "Máquina" : "Jugador");
        if ("Jugador".equals(winner)) {
            playerScore++;
        } else {
            aiScore++;
        }
        listener.onScoreChanged(playerScore, aiScore);
    }

    public void checkDuelEnd() {
        if (playerScore >= 2 || aiScore >= 2 || playerCards.isEmpty() || aiCards.isEmpty()) {
            gameStarted = false;
            String winner = playerScore >= 2 ? "Jugador" : (aiScore >= 2 ? "Máquina" : "Empate");
            listener.onDuelEnded(winner);
        }
    }

    public void flipTurn() {
        playerTurn = !playerTurn;
    }

    public void reset() {
        playerCards.clear();
        aiCards.clear();
        playerScore = 0;
        aiScore = 0;
        gameStarted = false;
    }
}