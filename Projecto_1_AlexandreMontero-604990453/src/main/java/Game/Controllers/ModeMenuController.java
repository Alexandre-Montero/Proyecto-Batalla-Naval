package Game.Controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Game.Classes.GameState;

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
    private TextField txtPlayerOne;
    @FXML
    private TextField txtPlayerTwo;
    @FXML
    private Label lbPlayerOne;
    @FXML
    private Label lbPlayerTwo;
    @FXML
    private Label lbAddPlayers;
    @FXML
    private Label lbPlayerTwoOptional;
    @FXML
    private Label lbPlayerOneUsername;
    @FXML
    private Label lbPlayerTwoUsername;
    @FXML
    private Label lbWarningPlayerOneObligatory;
    @FXML
    private Label lbNoServices;

    public void initialize() {
        String playerOne = GameState.getInstance().getPlayerOne();
        String playerTwo = GameState.getInstance().getPlayerTwo();

        if (playerOne != null && !playerOne.isEmpty()) {
            lbPlayerOneUsername.setText("Jugador 1: " + playerOne);
            txtPlayerOne.setText(playerOne);
        }

        if (playerTwo != null && !playerTwo.isEmpty()) {
            lbPlayerTwoUsername.setText("Jugador 2: " + playerTwo);
            txtPlayerTwo.setText(playerTwo);
        }
    }

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
        if (!validateAndSavePlayers()) return;

        String playerTwo = GameState.getInstance().getPlayerTwo();
        if (playerTwo == null || playerTwo.isEmpty()) {
            lbWarningPlayerTwo.setText("Falta el segundo Jugador");
            return;
        }
        
        GameState.getInstance().setPvP(true);

    }

    @FXML
    public void startPlayerVSAI(ActionEvent event) throws IOException {
        if (!validateAndSavePlayers()) return;
        loadNextScene("/Fxml/difficultymenu.fxml", event);
        
        GameState.getInstance().setPvP(false);
    }

    @FXML
    private void UpdatePlayerOneUsername(ActionEvent event) {
        String playerOne = txtPlayerOne.getText();
        GameState.getInstance().setPlayerOne(playerOne);
        lbPlayerOneUsername.setText("Jugador 1: " + playerOne);
    }

    @FXML
    private void UpdatePlayerTwoUsername(ActionEvent event) {
        String playerTwo = txtPlayerTwo.getText();
        GameState.getInstance().setPlayerTwo(playerTwo);
        lbPlayerTwoUsername.setText("Jugador 2: " + playerTwo);
    }

    private boolean validateAndSavePlayers() {
        String playerOne = txtPlayerOne.getText();
        String playerTwo = txtPlayerTwo.getText();

        if (playerOne == null || playerOne.trim().isEmpty()) {
            lbWarningPlayerOneObligatory.setText("¡Jugador 1 es obligatorio!");
            return false;
        } else {
            lbWarningPlayerOneObligatory.setText("");
        }

        GameState.getInstance().setPlayerOne(playerOne);
        GameState.getInstance().setPlayerTwo(playerTwo);

        return true;
    }

    private void loadNextScene(String fxmlPath, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}