package Game.Controllers.Boards;

import Game.Classes.ShipPlacementManager;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PlayerOneHardBoardController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Canvas canvasPlayer;

    private final int cellSize = 40;
    private ShipPlacementManager shipManager = ShipPlacementManager.getInstance(); // Usar el singleton de ShipPlacementManager

    private String[][] playerBoard = new String[10][10];
    private Map<String, Integer> shipSizes = new HashMap<>();
    private Map<String, Integer> shipLimits = new HashMap<>();
    private Map<String, Integer> placedCount = new HashMap<>();

    @FXML
    private ImageView BtnShipDestruyer;
    @FXML
    private ImageView BtnShipSubmarine;
    @FXML
    private ImageView BtnShipCruise;
    @FXML
    private ImageView BtnShipArmored;
    @FXML
    private Button btnReturnToDifficultyMenu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                playerBoard[i][j] = "~";
            }
        }

        shipSizes.put("Submarino", 1);
        shipSizes.put("Crucero", 3);
        shipSizes.put("Destructor", 2);
        shipSizes.put("Acorazado", 4);

        shipLimits.put("Submarino", 5);
        shipLimits.put("Crucero", 4);
        shipLimits.put("Destructor", 3);
        shipLimits.put("Acorazado", 2);

        placedCount.put("Submarino", 0);
        placedCount.put("Crucero", 0);
        placedCount.put("Destructor", 0);
        placedCount.put("Acorazado", 0);

        BtnShipDestruyer.setOnDragDetected((MouseEvent event) -> {
            startDrag(event, "Destructor", BtnShipDestruyer);
        });
        BtnShipSubmarine.setOnDragDetected((MouseEvent event) -> {
            startDrag(event, "Submarino", BtnShipSubmarine);
        });
        BtnShipCruise.setOnDragDetected((MouseEvent event) -> {
            startDrag(event, "Crucero", BtnShipCruise);
        });
        BtnShipArmored.setOnDragDetected((MouseEvent event) -> {
            startDrag(event, "Acorazado", BtnShipArmored);
        });

        canvasPlayer.setOnDragOver((DragEvent event) -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        canvasPlayer.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String type = db.getString();
                int size = shipSizes.get(type);
                int col = (int) (event.getX() / cellSize);
                int row = (int) (event.getY() / cellSize);

                if (placedCount.get(type) >= shipLimits.get(type)) {
                    showAlert("Ya colocaste el límite de " + type);
                } else if (canPlaceShip(row, col, size)) {
                    // Coloca el barco en el tablero y en el ShipPlacementManager
                    placeShip(row, col, type, size);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        drawGrid();
    }

    private class ShipPlacement {

        int row, column, size;
        String type;

        ShipPlacement(int row, int column, String type, int size) {
            this.row = row;
            this.column = column;
            this.type = type;
            this.size = size;
        }
    }

    private void startDrag(MouseEvent event, String shipType, ImageView imageView) {
        Dragboard db = imageView.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();
        content.putString(shipType);
        db.setContent(content);
        db.setDragView(imageView.getImage());
        event.consume();
    }

    private boolean canPlaceShip(int row, int column, int size) {
        if (row < 0 || row >= 10 || column < 0 || column >= 10) {
            return false;
        }

        if (column + size > 10) {
            return false;
        }

        for (int j = column; j < column + size; j++) {
            if (!playerBoard[row][j].equals("~")) {
                return false;
            }
        }
        return true;
    }

    private void placeShip(int row, int column, String type, int size) {
        for (int j = column; j < column + size; j++) {
            playerBoard[row][j] = type;
        }

        // Añadir al ShipPlacementManager
        shipManager.addShipPlacement(row, column, type, size);

        placedCount.put(type, placedCount.get(type) + 1);

        drawGrid();
    }

    private void drawGrid() {
        GraphicsContext gc = canvasPlayer.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasPlayer.getWidth(), canvasPlayer.getHeight());

        // Dibuja todos los barcos colocados
        for (Game.Classes.ShipPlacementManager.ShipPlacement sp : shipManager.getPlacedShips()) {
            Image img = getImageForShip(sp.type);
            if (img != null) {
                gc.drawImage(img, sp.column * cellSize, sp.row * cellSize, sp.size * cellSize, cellSize);
            }
        }

        // Dibuja la cuadrícula
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
    public void switchToDifficultyMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/difficultymenu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}