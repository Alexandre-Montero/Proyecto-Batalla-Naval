package Game.Controllers.Boards;

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
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AIEasyBoardController implements Initializable {

    private final int cellSize = 40;
    private String[][] aiBoard = new String[10][10]; // Tablero de la IA

    private Map<String, Integer> shipSizes = new HashMap<>();
    private Map<String, Integer> shipLimits = new HashMap<>();
    private Map<String, Integer> placedCount = new HashMap<>();

    @FXML
    private Canvas canvasAI;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar los tamaños de los barcos
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

        // Llamar a la IA para que coloque sus barcos de manera aleatoria
        placeRandomShips();

        // Dibujar la cuadrícula en el canvas
        drawGrid();
    }

    // Método que coloca los barcos de la IA de manera aleatoria
    private void placeRandomShips() {
        String[] shipTypes = {"Destructor", "Submarino", "Crucero", "Acorazado"};

        for (String shipType : shipTypes) {
            int shipSize = shipSizes.get(shipType);
            int placedShips = 0;

            while (placedShips < shipLimits.get(shipType)) {
                // Intentar colocar el barco de manera aleatoria
                int row = (int) (Math.random() * 10);
                int col = (int) (Math.random() * 10);
                boolean isHorizontal = Math.random() < 0.5; // Decide si el barco se coloca horizontal o verticalmente

                // Verifica si el barco puede ser colocado en la posición aleatoria
                if (canPlaceShip(row, col, shipSize, isHorizontal)) {
                    placeShip(row, col, shipType, shipSize, isHorizontal);
                    placedShips++;
                }
            }
        }
        drawGrid(); // Redibuja después de colocar los barcos
    }

    // Verifica si el barco puede ser colocado en la posición dada
    private boolean canPlaceShip(int row, int col, int size, boolean isHorizontal) {
        if (row < 0 || row >= 10 || col < 0 || col >= 10) {
            return false;
        }

        if (isHorizontal) {
            if (col + size > 10) {
                return false; // No cabe horizontalmente
            }
            for (int j = col; j < col + size; j++) {
                if (aiBoard[row][j] != null) {
                    return false; // Ya hay un barco en esa posición
                }
            }
        } else {
            if (row + size > 10) {
                return false; // No cabe verticalmente
            }
            for (int i = row; i < row + size; i++) {
                if (aiBoard[i][col] != null) {
                    return false; // Ya hay un barco en esa posición
                }
            }
        }
        return true;
    }

    // Coloca un barco en el tablero de la IA
    private void placeShip(int row, int col, String type, int size, boolean isHorizontal) {
        for (int i = 0; i < size; i++) {
            if (isHorizontal) {
                aiBoard[row][col + i] = type;
            } else {
                aiBoard[row + i][col] = type;
            }
        }

        drawGrid(); // Redibuja la cuadrícula después de colocar el barco
    }

    // Dibuja la cuadrícula y los barcos de la IA en el canvas
    private void drawGrid() {
        if (canvasAI == null) {
            return; // Asegúrate de que el canvas esté inicializado
        }
        GraphicsContext gc = canvasAI.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasAI.getWidth(), canvasAI.getHeight());  // Limpiar la pantalla para redibujar

        // Dibuja los barcos en el tablero de la IA
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                String shipType = aiBoard[row][col];
                if (shipType != null) {
                    Image img = getImageForShip(shipType);
                    if (img != null) {
                        gc.drawImage(img, col * cellSize, row * cellSize, cellSize, cellSize);
                    }
                }
            }
        }

        // Dibuja la cuadrícula
        gc.setStroke(Color.BLACK);
        for (int i = 0; i <= 10; i++) {
            gc.strokeLine(i * cellSize, 0, i * cellSize, cellSize * 10);
            gc.strokeLine(0, i * cellSize, cellSize * 10, i * cellSize);
        }
    }

    // Método que obtiene la imagen del barco según el tipo
    private Image getImageForShip(String type) {
        String imagePath = "/Images/"; // Asegúrate de que las imágenes están en este directorio
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
    public void switchToPlayerOneBoard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Boards/playeroneeasyboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
