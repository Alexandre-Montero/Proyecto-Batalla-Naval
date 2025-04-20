package Game.Controllers.Boards;

import Game.Classes.ShipComputerPlacementManager;
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
import javafx.scene.image.ImageView;

public class AINormalBoardController implements Initializable {

    @FXML
    private Canvas canvasComputer;

    private final ShipComputerPlacementManager computerShipsManager = ShipComputerPlacementManager.getInstance();
    private final int cellSize = 40;
    private final String[][] computerBoard = new String[15][15]; // Board size increased to 15x15
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

        // Adjusted ship limits for Normal difficulty
        shipLimits.put("Submarino", 3); // 3 Submarines
        shipLimits.put("Destructor", 2); // 2 Destroyers
        shipLimits.put("Crucero", 2); // 2 Cruisers
        shipLimits.put("Acorazado", 1); // 1 Battleship (always remains)
    }

    private void initializeBoard() {
        for (int i = 0; i < 15; i++) { // Board size now 15x15
            for (int j = 0; j < 15; j++) {
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
                int row = random.nextInt(15);  // Adjusted for 15x15 board
                int column = random.nextInt(15); // Adjusted for 15x15 board
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
            if (column + size > 15) return false; // Adjusted for 15x15 board
            for (int i = 0; i < size; i++) {
                if (!computerBoard[row][column + i].equals("~")) return false;
            }
        } else {
            if (row + size > 15) return false; // Adjusted for 15x15 board
            for (int i = 0; i < size; i++) {
                if (!computerBoard[row + i][column].equals("~")) return false;
            }
        }
        return true;
    }

    private void putShip(int row, int column, String type, int size, boolean horizontal) {
        if (horizontal) {
            for (int i = 0; i < size; i++) {
                computerBoard[row][column + i] = type;
            }
        } else {
            for (int i = 0; i < size; i++) {
                computerBoard[row + i][column] = type;
            }
        }

        placedShips.add(new ShipUbication(row, column, type, size, horizontal));
        ShipComputerPlacementManager.ShipPlacement ship = new ShipComputerPlacementManager.ShipPlacement(row, column, type, size, horizontal);
        computerShipsManager.addShipsComputer(ship);
    }

    private void drawGrid() {
        if (canvasComputer == null) return;

        GraphicsContext gc = canvasComputer.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasComputer.getWidth(), canvasComputer.getHeight());

        for (ShipUbication ship : placedShips) {
            Image img = getImageForShip(ship.type);
            if (img != null) {
                if (ship.horizontal) {
                    gc.drawImage(img, ship.column * cellSize, ship.row * cellSize, ship.size * cellSize, cellSize);
                } else {
                    gc.drawImage(img, ship.column * cellSize, ship.row * cellSize, cellSize, ship.size * cellSize);
                }
            } else {
                gc.setFill(Color.GRAY);
                if (ship.horizontal) {
                    gc.fillRect(ship.column * cellSize, ship.row * cellSize, ship.size * cellSize, cellSize);
                } else {
                    gc.fillRect(ship.column * cellSize, ship.row * cellSize, cellSize, ship.size * cellSize);
                }
            }
        }

        gc.setStroke(Color.BLACK);
        for (int i = 0; i <= 15; i++) { // Adjusted for 15x15 board
            gc.strokeLine(i * cellSize, 0, i * cellSize, cellSize * 15);
            gc.strokeLine(0, i * cellSize, cellSize * 15, i * cellSize);
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

    public void switchToPlayerOneBoard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Boards/playeroneeasyboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}