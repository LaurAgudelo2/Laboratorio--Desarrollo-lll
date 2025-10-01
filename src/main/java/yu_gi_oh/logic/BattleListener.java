package yu_gi_oh.logic;

public interface BattleListener {
    void onTurn(String playerCard, String aiCard, String winner);
    void onScoreChanged(int playerScore, int aiScore);
    void onDuelEnded(String winner);
    void onError(String errorMessage);
    void onCardsLoaded();
}