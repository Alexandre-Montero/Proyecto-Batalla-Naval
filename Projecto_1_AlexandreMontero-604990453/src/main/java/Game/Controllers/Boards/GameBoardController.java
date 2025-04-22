package Game.Controllers.Boards;

import Game.Classes.GamePlayManager;
import Game.Classes.ShipComputerPlacementManager;
import Game.Classes.ShipPlacementManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Label;

public class GameBoardController implements Initializable {

    @FXML
    private Canvas playerCanvas;
    @FXML
    private Canvas computerCanvas;

    private final GamePlayManager gamePlayManager = GamePlayManager.getInstance();
    private int cellSize = 40;
    private int boardSize;
    private int offsetX = 30;
    private int offsetY = 20;
    @FXML
    private Label lbPlayerOne;
    @FXML
    private Label lbPlayerTwo;
    @FXML
    private Label lbDifficulty;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureBoardSize();

        playerCanvas.setWidth(boardSize * cellSize + offsetX);
        playerCanvas.setHeight(boardSize * cellSize + offsetY);
        computerCanvas.setWidth(boardSize * cellSize + offsetX);
        computerCanvas.setHeight(boardSize * cellSize + offsetY);

        if (gamePlayManager.isPlayerVsPlayer()) {
            initializePvPBoard();
        } else {
            initializePvAIBoard();
        }
    }

    private void configureBoardSize() {
        String difficulty = gamePlayManager.getDifficulty();
        if (difficulty == null) {
            difficulty = "NORMAL";
        }

        switch (difficulty.toUpperCase()) {
            case "EASY":
                boardSize = 10;
                break;
            case "HARD":
                boardSize = 14;
                break;
            default:
                boardSize = 12;
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

        // Obtiene los barcos directamente del ShipPlacementManager
        List<ShipPlacementManager.ShipPlacement> ships
                = ShipPlacementManager.getInstance().getPlacedShips(playerId);

        System.out.println("Dibujando barcos para: " + playerId);
        System.out.println("Número de barcos: " + ships.size());

        for (ShipPlacementManager.ShipPlacement ship : ships) {
            System.out.printf("Barco: %s en (%d,%d) tamaño %d %s%n",
                    ship.type, ship.row, ship.column, ship.size,
                    ship.horizontal ? "Horizontal" : "Vertical");

            drawShip(gc, ship.row, ship.column, ship.type, ship.size, ship.horizontal);
        }

        drawCoordinates(gc);
    }

    private void drawComputerBoard() {
        GraphicsContext gc = computerCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, computerCanvas.getWidth(), computerCanvas.getHeight());

        drawGrid(gc);

        List<ShipComputerPlacementManager.ShipPlacement> computerShips = gamePlayManager.getComputerShips();
        System.out.println("Dibujando barcos de la computadora. Cantidad: " + computerShips.size());

        for (ShipComputerPlacementManager.ShipPlacement ship : computerShips) {
            drawShip(gc, ship.row, ship.column, ship.type, ship.size, ship.horizontal);
        }

        drawCoordinates(gc);
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

        for (int i = 0; i <= boardSize; i++) {
            gc.strokeLine(offsetX + i * cellSize, offsetY, offsetX + i * cellSize, offsetY + boardSize * cellSize);
            gc.strokeLine(offsetX, offsetY + i * cellSize, offsetX + boardSize * cellSize, offsetY + i * cellSize);
        }
    }

    private void drawCoordinates(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));

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
        String imagePath = "/Images/";
        try {
            switch (type) {
                case "Destructor":
                    return new Image(getClass().getResourceAsStream(imagePath + "destructor.jpg"));
                case "Submarino":
                    return new Image(getClass().getResourceAsStream(imagePath + "submarine.png"));
                case "Crucero":
                    return new Image(getClass().getResourceAsStream(imagePath + "cruser.jpg"));
                case "Acorazado":
                    return new Image(getClass().getResourceAsStream(imagePath + "battleship.png"));
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen para barco: " + type);
            return null;
        }
    }
}