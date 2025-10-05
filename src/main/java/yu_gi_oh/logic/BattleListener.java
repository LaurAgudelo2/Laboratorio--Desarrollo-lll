package yu_gi_oh.logic;

import yu_gi_oh.model.Card;

public interface BattleListener {
    void onTurn(Card playerCard, Card aiCard, String winner);
    void onScoreChanged(int playerScore, int aiScore);
    void onDuelEnded(String winner);
    void onError(String errorMessage);
    void onCardsLoaded();
}