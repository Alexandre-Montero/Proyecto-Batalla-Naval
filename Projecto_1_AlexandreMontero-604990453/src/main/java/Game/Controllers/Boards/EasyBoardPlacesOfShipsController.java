package Game.Controllers.Boards;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class EasyBoardPlacesOfShipsController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private GridPane GridBoard;
    @FXML
    private Button btnReturnToDifficultyMenu;
    @FXML
    private Button btnPlayGame;
    @FXML
    private Label lbSubmarines;
    @FXML
    private Label lbDestroyers;
    @FXML
    private Label lbCruises;
    @FXML
    private Label lbBattleship;
    @FXML
    private Circle submarineOne;
    @FXML
    private Circle submarineTwo;

    @FXML
    public void switchToDifficultyMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/difficultymenu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
