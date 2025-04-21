package Game.Controllers.Boards;

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
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PlayerOneNormalBoardController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Canvas canvasPlayer;
    @FXML
    private ImageView BtnShipDestruyer;
    @FXML
    private ImageView BtnShipSubmarine;
    @FXML
    private ImageView BtnShipCruise;
    @FXML
    private ImageView BtnShipArmored;
    @FXML
    private Button btnAIBoard;

    private final int CELL_SIZE = 40;
    private final String[][] playerBoard = new String[12][12];
    private final Map<String, Integer> shipSizes = new HashMap<>();
    private final Map<String, Integer> shipLimits = new HashMap<>();
    private final Map<String, Integer> placedCount = new HashMap<>();
    private final List<ShipPlacement> placedShips = new ArrayList<>();
    @FXML
    private Button btnReturnToDifficultyMenu;

    private static class ShipPlacement {

        int row, col, size;
        String type;

        ShipPlacement(int row, int col, String type, int size) {
            this.row = row;
            this.col = col;
            this.type = type;
            this.size = size;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (int i = 0; i < 12; i++) {
            Arrays.fill(playerBoard[i], "~");
        }

        shipSizes.put("Submarino", 1);
        shipSizes.put("Crucero", 3);
        shipSizes.put("Destructor", 2);
        shipSizes.put("Acorazado", 4);

        shipLimits.put("Submarino", 4);
        shipLimits.put("Crucero", 3);
        shipLimits.put("Destructor", 2);
        shipLimits.put("Acorazado", 1);

        shipLimits.keySet().forEach(type -> placedCount.put(type, 0));

        BtnShipDestruyer.setOnDragDetected(e -> startDrag(e, "Destructor", BtnShipDestruyer));
        BtnShipSubmarine.setOnDragDetected(e -> startDrag(e, "Submarino", BtnShipSubmarine));
        BtnShipCruise.setOnDragDetected(e -> startDrag(e, "Crucero", BtnShipCruise));
        BtnShipArmored.setOnDragDetected(e -> startDrag(e, "Acorazado", BtnShipArmored));

        canvasPlayer.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        canvasPlayer.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String type = db.getString();
                int size = shipSizes.get(type);
                int col = (int) (event.getX() / CELL_SIZE);
                int row = (int) (event.getY() / CELL_SIZE);

                if (placedCount.get(type) >= shipLimits.get(type)) {
                    showAlert("Ya colocaste el límite de " + type);
                } else if (canPlaceShip(row, col, size)) {
                    placeShip(row, col, type, size);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        drawGrid();
    }

    private void startDrag(MouseEvent event, String shipType, ImageView imageView) {
        Dragboard db = imageView.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();
        content.putString(shipType);
        db.setContent(content);
        db.setDragView(imageView.getImage());
        event.consume();
    }

    private boolean canPlaceShip(int row, int col, int size) {
        if (row < 0 || row >= 12 || col < 0 || col + size > 12) {
            return false;
        }
        for (int j = col; j < col + size; j++) {
            if (!playerBoard[row][j].equals("~")) {
                return false;
            }
        }
        return true;
    }

    private void placeShip(int row, int col, String type, int size) {
        for (int j = col; j < col + size; j++) {
            playerBoard[row][j] = type;
        }
        placedShips.add(new ShipPlacement(row, col, type, size));
        placedCount.put(type, placedCount.get(type) + 1);
        drawGrid();
    }

    private void drawGrid() {
        GraphicsContext gc = canvasPlayer.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasPlayer.getWidth(), canvasPlayer.getHeight());

        for (ShipPlacement sp : placedShips) {
            Image img = getImageForShip(sp.type);
            if (img != null) {
                gc.drawImage(img, sp.col * CELL_SIZE, sp.row * CELL_SIZE, sp.size * CELL_SIZE, CELL_SIZE);
            }
        }

        gc.setStroke(Color.BLACK);
        for (int i = 0; i <= 12; i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, CELL_SIZE * 12);
            gc.strokeLine(0, i * CELL_SIZE, CELL_SIZE * 12, i * CELL_SIZE);
        }
    }

    private Image getImageForShip(String type) {
        String path = "/Images/";
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
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void switchToAINormalBoard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Boards/ainormalboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToDifficultyMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/difficultymenu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
