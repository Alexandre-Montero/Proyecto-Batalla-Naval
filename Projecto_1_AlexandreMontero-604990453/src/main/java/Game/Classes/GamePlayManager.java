package Game.Classes;

import java.util.List;

public class GamePlayManager {
    private static GamePlayManager instance;

    public enum Difficulty {
        EASY,
        NORMAL,
        HARD
    }

    private GamePlayManager() {}

    public static GamePlayManager getInstance() {
        if (instance == null) {
            instance = new GamePlayManager();
        }
        return instance;
    }

    public boolean isPlayerVsPlayer() {
        return GameState.getInstance().isPvP();
    }

    public String getDifficulty() {
        return GameState.getInstance().getDifficulty();
    }

    public String getPlayerOneName() {
        return GameState.getInstance().getPlayerOne();
    }

    public String getPlayerTwoName() {
        return isPlayerVsPlayer() ? GameState.getInstance().getPlayerTwo() : "Computer";
    }

    public List<ShipPlacementManager.ShipPlacement> getPlayerShips(String playerId) {
        return ShipPlacementManager.getInstance().getPlacedShips(playerId);
    }

    public List<ShipComputerPlacementManager.ShipPlacement> getComputerShips() {
        return ShipComputerPlacementManager.getInstance().getShips();
    }

    public void clearAllShips() {
        ShipPlacementManager.getInstance().clearPlacements(getPlayerOneName());
        if (isPlayerVsPlayer()) {
            ShipPlacementManager.getInstance().clearPlacements(getPlayerTwoName());
        } else {
            ShipComputerPlacementManager.getInstance().clearShips();
        }
    }

    public void setupPvAI(String playerOne, Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                break;
            case NORMAL:
                break;
            case HARD:
                break;
        }
    }
}