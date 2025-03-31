package Game.Controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Game.Classes.GameState;
import javafx.scene.control.Label;


public class ModeMenuController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button PlayerVSPlayer;
    @FXML
    private Button btnPlayerVSAI;
    @FXML
    private Button btnReturnToStartMenu;
    @FXML
    private Label lbEnemySelectionTitle;
    @FXML
    private Label lbWarningPlayerTwo;

    @FXML
    public void switchToStartMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/startmenu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void startPlayerVSPlayer(ActionEvent event) throws IOException {
        String playerTwo = GameState.getInstance().getPlayerTwo();

        if (playerTwo == null || playerTwo.isEmpty()) {
            lbWarningPlayerTwo.setText("Falta el segundo Jugador");
            return;
        }

        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/difficultymenu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void startPlayerVSAI(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/difficultymenu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}