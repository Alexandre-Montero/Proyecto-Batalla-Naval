package Game.Controllers.Boards;

import Game.Classes.GamePlayManager;
import Game.Classes.ShipComputerPlacementManager;
import Game.Classes.ShipPlacementManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GameBoardController implements Initializable {

    @FXML
    private Canvas playerCanvas;
    @FXML
    private Canvas computerCanvas;

    @FXML
    private Label lbPlayerOne;
    @FXML
    private Label lbPlayerTwo;
    @FXML
    private Label lbDifficulty;

    @FXML
    private Label lblPlayerShots;
    @FXML
    private Label lblComputerShots;
    @FXML
    private Label lblShotsLeft;
    @FXML
    private Label lblGameMessage;

    @FXML
    private Button btnToggleShips;

    private final GamePlayManager gamePlayManager = GamePlayManager.getInstance();
    private final int cellSize = 40;
    private final int offsetX = 30;
    private final int offsetY = 20;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gamePlayManager.initializeGame();
        configureUI();

        if (gamePlayManager.isPlayerVsPlayer()) {
            initializePvPBoard();
        } else {
            initializePvAIBoard();
        }

        computerCanvas.setOnMouseClicked(this::handleComputerCanvasClick);
        updateShotsDisplay();
    }

    private void configureUI() {
        lbPlayerOne.setText(gamePlayManager.getPlayerOneName());
        lbPlayerTwo.setText(gamePlayManager.getPlayerTwoName());
        lbDifficulty.setText(gamePlayManager.getDifficulty());

        int boardSize = gamePlayManager.getBoardSize();
        playerCanvas.setWidth(boardSize * cellSize + offsetX);
        playerCanvas.setHeight(boardSize * cellSize + offsetY);
        computerCanvas.setWidth(boardSize * cellSize + offsetX);
        computerCanvas.setHeight(boardSize * cellSize + offsetY);
    }

    @FXML
    private void handleToggleEnemyShips() {
        if (!gamePlayManager.isPlayerVsPlayer()) {
            gamePlayManager.toggleEnemyShipsVisibility();
            drawComputerBoard();
        }
    }

    private void initializePvPBoard() {
        drawPlayerBoard(playerCanvas, gamePlayManager.getPlayerOneName());
        drawPlayerBoard(computerCanvas, gamePlayManager.getPlayerTwoName());
    }

    private void initializePvAIBoard() {
        drawPlayerBoard(playerCanvas, gamePlayManager.getPlayerOneName());
        drawComputerBoard();
    }

    private void drawPlayerBoard(Canvas canvas, String playerId) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawGrid(gc);
        drawCoordinates(gc);

        List<ShipPlacementManager.ShipPlacement> ships = ShipPlacementManager.getInstance().getPlacedShips(playerId);
        for (ShipPlacementManager.ShipPlacement ship : ships) {
            drawShip(gc, ship.row, ship.column, ship.type, ship.size, ship.horizontal);
        }

        int[][] hits = canvas == playerCanvas
                ? gamePlayManager.getPlayerHits()
                : gamePlayManager.getComputerHits();

        drawHits(gc, hits);
    }

    private void drawComputerBoard() {
        GraphicsContext gc = computerCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, computerCanvas.getWidth(), computerCanvas.getHeight());

        drawGrid(gc);
        drawCoordinates(gc);
        if (gamePlayManager.shouldShowEnemyShips()) {
            List<ShipComputerPlacementManager.ShipPlacement> computerShips = gamePlayManager.getComputerShips();
            for (ShipComputerPlacementManager.ShipPlacement ship : computerShips) {
                drawShip(gc, ship.row, ship.column, ship.type, ship.size, ship.horizontal);
            }
        }

        drawHits(gc, gamePlayManager.getComputerHits());
    }

    private void drawHits(GraphicsContext gc, int[][] hits) {
        for (int row = 0; row < hits.length; row++) {
            for (int col = 0; col < hits[row].length; col++) {
                if (hits[row][col] == 1) {
                    drawMiss(gc, row, col);
                } else if (hits[row][col] == 2) {
                    drawHit(gc, row, col);
                }
            }
        }
    }

    private void drawMiss(GraphicsContext gc, int row, int col) {
        try {
            Image splash = new Image(getClass().getResourceAsStream("/Images/oceanImage.png"));
            gc.drawImage(splash, offsetX + col * cellSize, offsetY + row * cellSize, cellSize, cellSize);
        } catch (Exception e) {
            gc.setFill(Color.BLUE);
            gc.fillOval(offsetX + col * cellSize + cellSize / 4,
                    offsetY + row * cellSize + cellSize / 4,
                    cellSize / 2, cellSize / 2);
        }
    }

    private void drawHit(GraphicsContext gc, int row, int col) {
        try {
            Image fire = new Image(getClass().getResourceAsStream("/Images/fireHit.jpg"));
            gc.drawImage(fire, offsetX + col * cellSize, offsetY + row * cellSize, cellSize, cellSize);
        } catch (Exception e) {
            gc.setFill(Color.RED);
            gc.fillRect(offsetX + col * cellSize, offsetY + row * cellSize, cellSize, cellSize);
        }
    }

    private void handleComputerCanvasClick(MouseEvent event) {
        if (!gamePlayManager.isPlayerTurn()) {
            return;
        }

        int col = (int) ((event.getX() - offsetX) / cellSize);
        int row = (int) ((event.getY() - offsetY) / cellSize);

        if (row >= 0 && row < gamePlayManager.getBoardSize()
                && col >= 0 && col < gamePlayManager.getBoardSize()) {

            boolean isHit = gamePlayManager.playerShoot(row, col);
            updateGameState();

            if (isHit) {
                drawHit(computerCanvas.getGraphicsContext2D(), row, col);
            } else {
                drawMiss(computerCanvas.getGraphicsContext2D(), row, col);
            }

            checkGameEnd();
        }
    }

    private void updateGameState() {
        drawComputerBoard();
        drawPlayerBoard(playerCanvas, gamePlayManager.getPlayerOneName());
        updateShotsDisplay();
    }

    private void updateShotsDisplay() {
        int playerShots = gamePlayManager.getPlayerShotsLeft();
        int computerShots = gamePlayManager.getComputerShotsLeft();

        lblPlayerShots.setText("Disparos: " + playerShots);
        lblComputerShots.setText("Disparos: " + computerShots);
        lblShotsLeft.setText("Total restantes - Jugador: " + playerShots + " | Computadora: " + computerShots);
    }

    private void checkGameEnd() {
        String result = gamePlayManager.getGameResult();
        if (result != null) {
            lblGameMessage.setText(result);
            computerCanvas.setDisable(true);
        }
    }

    private void drawShip(GraphicsContext gc, int row, int col, String type, int size, boolean horizontal) {
        Image img = getImageForShip(type);
        if (img != null) {
            if (horizontal) {
                gc.drawImage(img, offsetX + col * cellSize, offsetY + row * cellSize, size * cellSize, cellSize);
            } else {
                gc.save();
                gc.translate(offsetX + (col + 1) * cellSize, offsetY + row * cellSize);
                gc.rotate(90);
                gc.drawImage(img, 0, 0, size * cellSize, cellSize);
                gc.restore();
            }
        } else {
            gc.setFill(getShipColor(type));
            if (horizontal) {
                gc.fillRect(offsetX + col * cellSize, offsetY + row * cellSize, size * cellSize, cellSize);
            } else {
                gc.fillRect(offsetX + col * cellSize, offsetY + row * cellSize, cellSize, size * cellSize);
            }
        }
    }

    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        int boardSize = gamePlayManager.getBoardSize();
        for (int i = 0; i <= boardSize; i++) {
            gc.strokeLine(offsetX + i * cellSize, offsetY,
                    offsetX + i * cellSize, offsetY + boardSize * cellSize);
            gc.strokeLine(offsetX, offsetY + i * cellSize,
                    offsetX + boardSize * cellSize, offsetY + i * cellSize);
        }
    }

    private void drawCoordinates(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));

        int boardSize = gamePlayManager.getBoardSize();
        for (int i = 0; i < boardSize; i++) {
            gc.fillText(String.valueOf((char) ('A' + i)), offsetX + i * cellSize + cellSize / 3, offsetY - 5);
            gc.fillText(String.valueOf(i + 1), 5, offsetY + i * cellSize + cellSize / 2);
        }
    }

    private Color getShipColor(String type) {
        switch (type) {
            case "Destructor":
                return Color.LIGHTGRAY;
            case "Submarino":
                return Color.DARKGRAY;
            case "Crucero":
                return Color.GRAY;
            case "Acorazado":
                return Color.DARKSLATEGRAY;
            default:
                return Color.BLUE;
        }
    }

    private Image getImageForShip(String type) {
        String path = "/Images/";
        try {
            switch (type) {
                case "Destructor":
                    return new Image(getClass().getResourceAsStream(path + "destructor.jpg"));
                case "Submarino":
                    return new Image(getClass().getResourceAsStream(path + "submarine.png"));
                case "Crucero":
                    return new Image(getClass().getResourceAsStream(path + "cruser.jpg"));
                case "Acorazado":
                    return new Image(getClass().getResourceAsStream(path + "battleship.png"));
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen para barco: " + type);
            return null;
        }
    }
}
