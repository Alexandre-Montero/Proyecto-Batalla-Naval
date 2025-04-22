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

public class GameBoardController implements Initializable {

    @FXML private Canvas playerCanvas;
    @FXML private Canvas computerCanvas;
    
    private final GamePlayManager gamePlayManager = GamePlayManager.getInstance();
    private int cellSize = 40; // Tamaño fijo de celda
    private int boardSize; // Tamaño del tablero (10, 12 o 15)
    private int offsetX = 30; // Espacio para coordenadas
    private int offsetY = 20; // Espacio para coordenadas

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar tamaño del tablero según dificultad
        configureBoardSize();
        
        // Configurar tamaño de los Canvas
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
        if (difficulty == null) difficulty = "NORMAL"; // Valor por defecto
        
        switch (difficulty.toUpperCase()) {
            case "EASY":
                boardSize = 10;
                break;
            case "HARD":
                boardSize = 15;
                break;
            default: // NORMAL
                boardSize = 12;
        }
    }

    private void initializePvPBoard() {
        // Dibujar tableros para ambos jugadores
        drawPlayerBoard(playerCanvas, gamePlayManager.getPlayerOneName());
        drawPlayerBoard(computerCanvas, gamePlayManager.getPlayerTwoName());
    }

    private void initializePvAIBoard() {
        // Dibujar tablero del jugador
        drawPlayerBoard(playerCanvas, gamePlayManager.getPlayerOneName());
        
        // Dibujar tablero de la IA
        drawComputerBoard();
    }

    private void drawPlayerBoard(Canvas canvas, String playerId) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Dibujar cuadrícula
        drawGrid(gc);
        
        // Dibujar barcos del jugador
        List<ShipPlacementManager.ShipPlacement> ships = gamePlayManager.getPlayerShips(playerId);
        for (ShipPlacementManager.ShipPlacement ship : ships) {
            drawShip(gc, ship.row, ship.column, ship.type, ship.size, ship.horizontal);
        }
        
        // Dibujar coordenadas
        drawCoordinates(gc);
    }

    private void drawComputerBoard() {
        GraphicsContext gc = computerCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, computerCanvas.getWidth(), computerCanvas.getHeight());

        // Dibujar cuadrícula
        drawGrid(gc);
        
        // Dibujar barcos de la IA
        List<ShipComputerPlacementManager.ShipPlacement> computerShips = gamePlayManager.getComputerShips();
        for (ShipComputerPlacementManager.ShipPlacement ship : computerShips) {
            drawShip(gc, ship.row, ship.column, ship.type, ship.size, ship.horizontal);
        }
        
        // Dibujar coordenadas
        drawCoordinates(gc);
    }

    private void drawShip(GraphicsContext gc, int row, int col, String type, int size, boolean horizontal) {
        Image img = getImageForShip(type);
        if (img != null) {
            if (horizontal) {
                gc.drawImage(img, 
                    offsetX + col * cellSize, 
                    offsetY + row * cellSize, 
                    size * cellSize, 
                    cellSize);
            } else {
                gc.save();
                gc.translate(
                    offsetX + (col + 1) * cellSize, 
                    offsetY + row * cellSize);
                gc.rotate(90);
                gc.drawImage(img, 0, 0, size * cellSize, cellSize);
                gc.restore();
            }
        } else {
            // Dibujar barco genérico si no hay imagen
            gc.setFill(getShipColor(type));
            if (horizontal) {
                gc.fillRect(
                    offsetX + col * cellSize, 
                    offsetY + row * cellSize, 
                    size * cellSize, 
                    cellSize);
            } else {
                gc.fillRect(
                    offsetX + col * cellSize, 
                    offsetY + row * cellSize, 
                    cellSize, 
                    size * cellSize);
            }
        }
    }

    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        
        // Dibujar líneas verticales
        for (int i = 0; i <= boardSize; i++) {
            gc.strokeLine(
                offsetX + i * cellSize, 
                offsetY, 
                offsetX + i * cellSize, 
                offsetY + boardSize * cellSize);
        }
        
        // Dibujar líneas horizontales
        for (int i = 0; i <= boardSize; i++) {
            gc.strokeLine(
                offsetX, 
                offsetY + i * cellSize, 
                offsetX + boardSize * cellSize, 
                offsetY + i * cellSize);
        }
    }

    private void drawCoordinates(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));
        
        // Dibujar letras (columnas)
        for (int i = 0; i < boardSize; i++) {
            gc.fillText(
                String.valueOf((char)('A' + i)), 
                offsetX + i * cellSize + cellSize/3, 
                offsetY - 5);
        }
        
        // Dibujar números (filas)
        for (int i = 0; i < boardSize; i++) {
            gc.fillText(
                String.valueOf(i + 1), 
                5, 
                offsetY + i * cellSize + cellSize/2);
        }
    }

    private Color getShipColor(String type) {
        switch (type) {
            case "Destructor": return Color.LIGHTGRAY;
            case "Submarino": return Color.DARKGRAY;
            case "Crucero": return Color.GRAY;
            case "Acorazado": return Color.DARKSLATEGRAY;
            default: return Color.BLUE;
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