package Game.Classes;

public class GameState {

    private static GameState instance;
    private String playerOne;
    private String playerTwo;
    private String difficulty;
    private boolean isPvP;

    private GameState() {
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isPvP() {
        return isPvP;
    }

    public void setPvP(boolean isPvP) {
        this.isPvP = isPvP;
    }
}
