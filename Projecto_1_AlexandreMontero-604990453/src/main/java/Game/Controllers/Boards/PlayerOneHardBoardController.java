package Game.Controllers.Boards;

import Game.Classes.GamePlayManager;
import Game.Classes.ShipPlacementManager;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PlayerOneHardBoardController implements Initializable {

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
    @FXML
    private Button btnReturnToDifficultyMenu;
    @FXML
    private ToggleButton btnToggleOrientation; // Botón para cambiar orientación

    private final int CELL_SIZE = 40;
    private final String[][] playerBoard = new String[14][14];
    private final Map<String, Integer> shipSizes = new HashMap<>();
    private final Map<String, Integer> shipLimits = new HashMap<>();
    private final Map<String, Integer> placedCount = new HashMap<>();
    private final List<ShipPlacement> placedShips = new ArrayList<>();
    private boolean isHorizontal = true; // Orientación por defecto (horizontal)

    private static class ShipPlacement {

        int row, col, size;
        String type;
        boolean horizontal;

        ShipPlacement(int row, int col, String type, int size, boolean horizontal) {
            this.row = row;
            this.col = col;
            this.type = type;
            this.size = size;
            this.horizontal = horizontal;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar tablero
        for (int i = 0; i < 14; i++) {
            Arrays.fill(playerBoard[i], "~");
        }

        // Configurar tamaños de barcos
        shipSizes.put("Submarino", 1);
        shipSizes.put("Crucero", 3);
        shipSizes.put("Destructor", 2);
        shipSizes.put("Acorazado", 4);

        // Configurar límites de barcos
        shipLimits.put("Submarino", 4);
        shipLimits.put("Crucero", 3);
        shipLimits.put("Destructor", 3);
        shipLimits.put("Acorazado", 1);

        // Inicializar contadores
        shipLimits.keySet().forEach(type -> placedCount.put(type, 0));

        // Configurar eventos de arrastre
        setupDragEvents();

        // Configurar botón de orientación
        btnToggleOrientation.setText("Orientación: " + (isHorizontal ? "Horizontal" : "Vertical"));
        btnToggleOrientation.setOnAction(this::toggleOrientation);

        drawGrid();
    }

    private void setupDragEvents() {
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
                } else if (canPlaceShip(row, col, size, isHorizontal)) {
                    placeShip(row, col, type, size, isHorizontal);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    private void toggleOrientation(ActionEvent event) {
        isHorizontal = !isHorizontal;
        btnToggleOrientation.setText("Orientación: " + (isHorizontal ? "Horizontal" : "Vertical"));
    }

    private void startDrag(MouseEvent event, String shipType, ImageView imageView) {
        Dragboard db = imageView.startDragAndDrop(TransferMode.COPY);
        ClipboardContent content = new ClipboardContent();
        content.putString(shipType);
        db.setContent(content);
        db.setDragView(imageView.getImage());
        event.consume();
    }

    private boolean canPlaceShip(int row, int col, int size, boolean horizontal) {
        if (horizontal) {
            if (col < 0 || col + size > 14 || row < 0 || row >= 14) {
                return false;
            }
            for (int j = col; j < col + size; j++) {
                if (!playerBoard[row][j].equals("~")) {
                    return false;
                }
            }
        } else {
            if (row < 0 || row + size > 14 || col < 0 || col >= 14) {
                return false;
            }
            for (int i = row; i < row + size; i++) {
                if (!playerBoard[i][col].equals("~")) {
                    return false;
                }
            }
        }
        return true;
    }

    // En tu método placeShip del PlayerOneEasyBoardController
    private void placeShip(int row, int col, String type, int size, boolean horizontal) {
        // Actualiza el tablero lógico
        if (horizontal) {
            for (int j = col; j < col + size; j++) {
                playerBoard[row][j] = type;
            }
        } else {
            for (int i = row; i < row + size; i++) {
                playerBoard[i][col] = type;
            }
        }

        // Guarda el barco en el ShipPlacementManager
        ShipPlacementManager.getInstance().addShipPlacement(
                GamePlayManager.getInstance().getPlayerOneName(),
                row,
                col,
                type,
                size,
                horizontal
        );

        placedShips.add(new ShipPlacement(row, col, type, size, horizontal));
        placedCount.put(type, placedCount.get(type) + 1);
        drawGrid();
    }

    private void drawGrid() {
        GraphicsContext gc = canvasPlayer.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasPlayer.getWidth(), canvasPlayer.getHeight());

        // Dibujar barcos colocados
        for (ShipPlacement sp : placedShips) {
            Image img = getImageForShip(sp.type);
            if (img != null) {
                if (sp.horizontal) {
                    gc.drawImage(img, sp.col * CELL_SIZE, sp.row * CELL_SIZE,
                            sp.size * CELL_SIZE, CELL_SIZE);
                } else {
                    gc.save();
                    gc.translate((sp.col + 1) * CELL_SIZE, sp.row * CELL_SIZE);
                    gc.rotate(90);
                    gc.drawImage(img, 0, 0, sp.size * CELL_SIZE, CELL_SIZE);
                    gc.restore();
                }
            } else {
                // Dibujo alternativo si no hay imagen
                gc.setFill(getShipColor(sp.type));
                if (sp.horizontal) {
                    gc.fillRect(sp.col * CELL_SIZE, sp.row * CELL_SIZE,
                            sp.size * CELL_SIZE, CELL_SIZE);
                } else {
                    gc.fillRect(sp.col * CELL_SIZE, sp.row * CELL_SIZE,
                            CELL_SIZE, sp.size * CELL_SIZE);
                }
            }
        }

        // Dibujar cuadrícula
        gc.setStroke(Color.BLACK);
        for (int i = 0; i <= 14; i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, CELL_SIZE * 14);
            gc.strokeLine(0, i * CELL_SIZE, CELL_SIZE * 14, i * CELL_SIZE);
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void switchToAIHardBoard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Boards/aihardboard.fxml"));
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
