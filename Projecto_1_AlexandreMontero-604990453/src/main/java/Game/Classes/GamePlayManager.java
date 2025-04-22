package Game.Classes;

import java.util.List;
import java.util.Random;

public class GamePlayManager {

    private int playerShotsLeft;
    private int computerShotsLeft;
    private boolean showEnemyShips = false;
    private static GamePlayManager instance;
    private Random random = new Random();
    private int[][] playerHits; // 0: agua, 1: disparo fallido, 2: impacto
    private int[][] computerHits;
    private boolean playerTurn = true;
    private int lastHitRow = -1;
    private int lastHitCol = -1;
    private boolean lastHitWasHit = false;
    private int boardSize;

    public enum Difficulty {
        EASY,
        NORMAL,
        HARD
    }

    private GamePlayManager() {
    }

    public static GamePlayManager getInstance() {
        if (instance == null) {
            instance = new GamePlayManager();
        }
        return instance;
    }

    public void initializeGame() {
        String difficulty = getDifficulty();
        if (difficulty == null) {
            difficulty = "NORMAL";
        }

        switch (difficulty.toUpperCase()) {
            case "EASY":
                boardSize = 10;
                playerShotsLeft = Integer.MAX_VALUE; // Sin límite en fácil
                computerShotsLeft = Integer.MAX_VALUE;
                break;
            case "HARD":
                boardSize = 14;
                playerShotsLeft = 150;
                computerShotsLeft = 150;
                break;
            default:
                boardSize = 12;
                playerShotsLeft = 120;
                computerShotsLeft = 120;
        }

        playerHits = new int[boardSize][boardSize];
        computerHits = new int[boardSize][boardSize];
        playerTurn = true;
        showEnemyShips = false;
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
        GameState.getInstance().setPlayerOne(playerOne);
        GameState.getInstance().setDifficulty(difficulty.toString());
        initializeGame();
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public boolean playerShoot(int row, int col) {
        if (!isPlayerTurn() || row < 0 || row >= boardSize || col < 0 || col >= boardSize
                || computerHits[row][col] != 0 || playerShotsLeft <= 0) {
            return false;
        }

        playerShotsLeft--;
        boolean isHit = checkComputerShipHit(row, col);
        computerHits[row][col] = isHit ? 2 : 1;

        playerTurn = false;
        if (!isPlayerVsPlayer()) {
            computerShoot();
        }

        return isHit;
    }

    public boolean computerShoot() {
        if (isPlayerTurn() || computerShotsLeft <= 0) {
            return false;
        }

        computerShotsLeft--;

        int row = -1, col = -1;
        Difficulty difficulty = Difficulty.valueOf(getDifficulty().toUpperCase());

        if (difficulty == Difficulty.EASY) {
            do {
                row = random.nextInt(boardSize);
                col = random.nextInt(boardSize);
            } while (playerHits[row][col] != 0);

        } else if (difficulty == Difficulty.NORMAL) {
            if (lastHitWasHit && random.nextDouble() < 0.5) {
                int direction = random.nextInt(4);
                switch (direction) {
                    case 0:
                        row = lastHitRow - 1;
                        col = lastHitCol;
                        break;
                    case 1:
                        row = lastHitRow + 1;
                        col = lastHitCol;
                        break;
                    case 2:
                        row = lastHitRow;
                        col = lastHitCol - 1;
                        break;
                    default:
                        row = lastHitRow;
                        col = lastHitCol + 1;
                        break;
                }

                if (row < 0 || row >= boardSize || col < 0 || col >= boardSize || playerHits[row][col] != 0) {
                    do {
                        row = random.nextInt(boardSize);
                        col = random.nextInt(boardSize);
                    } while (playerHits[row][col] != 0);
                }
            } else {
                do {
                    row = random.nextInt(boardSize);
                    col = random.nextInt(boardSize);
                } while (playerHits[row][col] != 0);
            }

        } else { // HARD
            if (lastHitWasHit && random.nextDouble() < 0.8) {
                boolean found = false;
                for (int i = -1; i <= 1 && !found; i++) {
                    for (int j = -1; j <= 1 && !found; j++) {
                        if ((i == 0 || j == 0) && i != j) {
                            int newRow = lastHitRow + i;
                            int newCol = lastHitCol + j;
                            if (newRow >= 0 && newRow < boardSize && newCol >= 0 && newCol < boardSize
                                    && playerHits[newRow][newCol] == 0) {
                                row = newRow;
                                col = newCol;
                                found = true;
                            }
                        }
                    }
                }
                if (!found) {
                    do {
                        row = random.nextInt(boardSize);
                        col = random.nextInt(boardSize);
                    } while (playerHits[row][col] != 0);
                }
            } else {
                do {
                    row = (int) (random.nextGaussian() * (boardSize / 6.0) + boardSize / 2);
                    col = (int) (random.nextGaussian() * (boardSize / 6.0) + boardSize / 2);
                    row = Math.max(0, Math.min(boardSize - 1, row));
                    col = Math.max(0, Math.min(boardSize - 1, col));
                } while (playerHits[row][col] != 0);
            }
        }

        boolean isHit = checkPlayerShipHit(row, col);
        playerHits[row][col] = isHit ? 2 : 1;

        lastHitRow = row;
        lastHitCol = col;
        lastHitWasHit = isHit;

        // Cambia de turno siempre
        playerTurn = true;

        if (isGameOver()) {
            playerTurn = false;
        }

        return isHit;
    }

    private boolean checkComputerShipHit(int row, int col) {
        List<ShipComputerPlacementManager.ShipPlacement> ships = getComputerShips();
        for (ShipComputerPlacementManager.ShipPlacement ship : ships) {
            if (ship.horizontal) {
                if (row == ship.row && col >= ship.column && col < ship.column + ship.size) {
                    return true;
                }
            } else {
                if (col == ship.column && row >= ship.row && row < ship.row + ship.size) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkPlayerShipHit(int row, int col) {
        List<ShipPlacementManager.ShipPlacement> ships = getPlayerShips(getPlayerOneName());
        for (ShipPlacementManager.ShipPlacement ship : ships) {
            if (ship.horizontal) {
                if (row == ship.row && col >= ship.column && col < ship.column + ship.size) {
                    return true;
                }
            } else {
                if (col == ship.column && row >= ship.row && row < ship.row + ship.size) {
                    return true;
                }
            }
        }
        return false;
    }

    public void toggleEnemyShipsVisibility() {
        showEnemyShips = !showEnemyShips;
    }

    public boolean shouldShowEnemyShips() {
        return showEnemyShips && !isPlayerVsPlayer();
    }

    public int getPlayerShotsLeft() {
        return playerShotsLeft;
    }

    public int getComputerShotsLeft() {
        return computerShotsLeft;
    }

    public String getGameResult() {
        if (isGameOver()) {
            if (hasPlayerWon()) {
                return "¡Ganaste! Has hundido todos los barcos enemigos.";
            } else if (hasComputerWon()) {
                return "¡Perdiste! La computadora hundió todos tus barcos.";
            } else if (playerShotsLeft <= 0 && computerShotsLeft <= 0) {
                return "¡Empate! Se agotaron los disparos de ambos jugadores.";
            } else if (playerShotsLeft <= 0) {
                return "¡Perdiste! Te quedaste sin disparos.";
            } else if (computerShotsLeft <= 0) {
                return "¡Ganaste! La computadora se quedó sin disparos.";
            }
        }
        return null;
    }

    public int[][] getPlayerHits() {
        return playerHits;
    }

    public int[][] getComputerHits() {
        return computerHits;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public boolean isGameOver() {
        return isAllShipsSunk(getPlayerOneName()) || isAllShipsSunk("Computer");
    }

    public String getWinner() {
        if (isAllShipsSunk("Computer")) {
            return getPlayerOneName();
        } else if (isAllShipsSunk(getPlayerOneName())) {
            return "Computer";
        }
        return null;
    }

    private boolean isAllShipsSunk(String playerId) {
        List<? extends Object> ships = playerId.equals("Computer")
                ? getComputerShips()
                : getPlayerShips(playerId);

        int[][] hits = playerId.equals("Computer") ? computerHits : playerHits;

        for (Object obj : ships) {
            int row, col, size;
            boolean horizontal;
            if (playerId.equals("Computer")) {
                ShipComputerPlacementManager.ShipPlacement ship = (ShipComputerPlacementManager.ShipPlacement) obj;
                row = ship.row;
                col = ship.column;
                size = ship.size;
                horizontal = ship.horizontal;
            } else {
                ShipPlacementManager.ShipPlacement ship = (ShipPlacementManager.ShipPlacement) obj;
                row = ship.row;
                col = ship.column;
                size = ship.size;
                horizontal = ship.horizontal;
            }

            for (int i = 0; i < size; i++) {
                int r = horizontal ? row : row + i;
                int c = horizontal ? col + i : col;
                if (hits[r][c] != 2) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean hasPlayerWon() {
        return allShipsSunk(getComputerShips(), computerHits);
    }

    public boolean hasComputerWon() {
        return allShipsSunk(getPlayerShips(getPlayerOneName()), playerHits);
    }

    private boolean allShipsSunk(List<? extends Object> ships, int[][] hits) {
        for (Object obj : ships) {
            int row, col, size;
            boolean horizontal;

            if (obj instanceof ShipPlacementManager.ShipPlacement) {
                ShipPlacementManager.ShipPlacement ship = (ShipPlacementManager.ShipPlacement) obj;
                row = ship.row;
                col = ship.column;
                size = ship.size;
                horizontal = ship.horizontal;
            } else {
                ShipComputerPlacementManager.ShipPlacement ship = (ShipComputerPlacementManager.ShipPlacement) obj;
                row = ship.row;
                col = ship.column;
                size = ship.size;
                horizontal = ship.horizontal;
            }

            for (int i = 0; i < size; i++) {
                int r = horizontal ? row : row + i;
                int c = horizontal ? col + i : col;
                if (hits[r][c] != 2) {
                    return false;
                }
            }
        }
        return true;
    }
}
