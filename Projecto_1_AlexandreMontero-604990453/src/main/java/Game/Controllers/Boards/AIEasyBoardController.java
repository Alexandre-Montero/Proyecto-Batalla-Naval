package Game.Controllers.Boards;

import Game.Classes.GamePlayManager;
import Game.Classes.GameState;
import Game.Classes.ShipComputerPlacementManager;
import Game.Classes.ShipPlacementManager;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class AIEasyBoardController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Canvas canvasComputer;

    private final ShipComputerPlacementManager computerShipsManager = ShipComputerPlacementManager.getInstance();
    private final int cellSize = 40;
    private final String[][] computerBoard = new String[10][10];
    private final List<ShipUbication> placedShips = new ArrayList<>();
    private final Map<String, Integer> shipSizes = new HashMap<>();
    private final Map<String, Integer> shipLimits = new HashMap<>();
    @FXML
    private ImageView BtnShipDestruyer;
    @FXML
    private ImageView BtnShipSubmarine;
    @FXML
    private ImageView BtnShipCruise;
    @FXML
    private ImageView BtnShipArmored;
    @FXML
    private Button btnPlayerBoard;
    @FXML
    private Button btnPlay;

    private static class ShipUbication {

        int row, column, size;
        String type;
        boolean horizontal;

        ShipUbication(int row, int column, String type, int size, boolean horizontal) {
            this.row = row;
            this.column = column;
            this.type = type;
            this.size = size;
            this.horizontal = horizontal;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeSettings();
        initializeBoard();

        if (computerShipsManager.getShips().isEmpty()) {
            putShipsIA();
            for (ShipComputerPlacementManager.ShipPlacement ship : computerShipsManager.getShips()) {
                placedShips.add(new ShipUbication(ship.row, ship.column, ship.type, ship.size, ship.horizontal));
            }
        } else {
            placedShips.clear();
            for (ShipComputerPlacementManager.ShipPlacement ship : computerShipsManager.getShips()) {
                placedShips.add(new ShipUbication(ship.row, ship.column, ship.type, ship.size, ship.horizontal));
                if (computerBoard[ship.row][ship.column].equals("~")) {
                    for (int i = 0; i < ship.size; i++) {
                        if (ship.horizontal) {
                            computerBoard[ship.row][ship.column + i] = ship.type;
                        } else {
                            computerBoard[ship.row + i][ship.column] = ship.type;
                        }
                    }
                }
            }
        }

        drawGrid();
    }

    private void initializeSettings() {
        shipSizes.put("Submarino", 1);
        shipSizes.put("Destructor", 2);
        shipSizes.put("Crucero", 3);
        shipSizes.put("Acorazado", 4);

        shipLimits.put("Submarino", 5);
        shipLimits.put("Destructor", 4);
        shipLimits.put("Crucero", 3);
        shipLimits.put("Acorazado", 2);
    }

    private void initializeBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                computerBoard[i][j] = "~";
            }
        }
    }

    private void putShipsIA() {
        Random random = new Random();
        Map<String, Integer> placedByType = new HashMap<>();
        for (String type : shipLimits.keySet()) {
            placedByType.put(type, 0);
        }

        for (String type : shipLimits.keySet()) {
            int max = shipLimits.get(type);
            int size = shipSizes.get(type);

            while (placedByType.get(type) < max) {
                int row = random.nextInt(10);
                int column = random.nextInt(10);
                boolean horizontal = random.nextBoolean();

                if (canPutShips(row, column, size, horizontal)) {
                    putShip(row, column, type, size, horizontal);
                    placedByType.put(type, placedByType.get(type) + 1);
                }
            }
        }
    }

    private boolean canPutShips(int row, int column, int size, boolean horizontal) {
        if (horizontal) {
            if (column + size > 10) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!computerBoard[row][column + i].equals("~")) {
                    return false;
                }
            }
        } else {
            if (row + size > 10) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!computerBoard[row + i][column].equals("~")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void putShip(int row, int column, String type, int size, boolean horizontal) {
        // Coloca los barcos en el tablero físico (la matriz de tablero)
        if (horizontal) {
            for (int i = 0; i < size; i++) {
                computerBoard[row][column + i] = type;
            }
        } else {
            for (int i = 0; i < size; i++) {
                computerBoard[row + i][column] = type;
            }
        }

        // Guardar la información de la ubicación del barco
        placedShips.add(new ShipUbication(row, column, type, size, horizontal));

        // Agregar el barco al ShipComputerPlacementManager para gestionar las posiciones
        ShipComputerPlacementManager.ShipPlacement ship = new ShipComputerPlacementManager.ShipPlacement(row, column, type, size, horizontal);
        computerShipsManager.addShipsComputer(ship);

        // Dibuja los barcos en el tablero
        drawGrid();
    }

    private void drawGrid() {
        if (canvasComputer == null) {
            return;
        }

        GraphicsContext gc = canvasComputer.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasComputer.getWidth(), canvasComputer.getHeight());

        // Dibujar los barcos colocados
        for (ShipUbication ship : placedShips) {
            Image img = getImageForShip(ship.type);
            if (img != null) {
                if (ship.horizontal) {
                    gc.drawImage(img, ship.column * cellSize, ship.row * cellSize, ship.size * cellSize, cellSize);
                } else {
                    gc.save();
                    gc.translate((ship.column + 1) * cellSize, ship.row * cellSize);
                    gc.rotate(90);
                    gc.drawImage(img, 0, 0, ship.size * cellSize, cellSize);
                    gc.restore();
                }
            } else {
                gc.setFill(Color.GRAY);  // Si no se puede cargar la imagen, dibujar un bloque gris
                if (ship.horizontal) {
                    gc.fillRect(ship.column * cellSize, ship.row * cellSize, ship.size * cellSize, cellSize);
                } else {
                    gc.fillRect(ship.column * cellSize, ship.row * cellSize, cellSize, ship.size * cellSize);
                }
            }
        }

        // Dibujar la cuadrícula
        gc.setStroke(Color.BLACK);
        for (int i = 0; i <= 10; i++) {
            gc.strokeLine(i * cellSize, 0, i * cellSize, cellSize * 10);
            gc.strokeLine(0, i * cellSize, cellSize * 10, i * cellSize);
        }
    }

    private Image getImageForShip(String type) {
        String imagePath = "/Images/";
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
    }


    @FXML
    public void switchToGameBoard(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Boards/gameboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToPlayerOneBoard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Boards/playeroneeasyboard.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
