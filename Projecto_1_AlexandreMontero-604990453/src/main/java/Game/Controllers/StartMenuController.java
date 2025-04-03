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

public class StartMenuController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button btnPlay;
    @FXML
    private Button btnProgramInfo;
    @FXML
    private Button btnConfiguration;
    @FXML
    private Label lbGameName;
    @FXML
    private TextField txtPlayerOne;
    @FXML
    private TextField txtPlayerTwo;
    @FXML
    private Label lbPlayerOneUsername;
    @FXML
    private Label lbPlayerTwoUsername;
    @FXML
    private Label lbAddPlayers;
    @FXML
    private Label lbPlayerOne;
    @FXML
    private Label lbPlayerTwo;
    @FXML
    private Label lbWarningPlayerOneObligatory;
    @FXML
    private Label lbPlayerTwoOptional;

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
    public void switchToModeMenu(ActionEvent event) throws IOException {
        String playerOne = txtPlayerOne.getText();
        String playerTwo = txtPlayerTwo.getText();

        if (playerOne.isEmpty()) {
            lbWarningPlayerOneObligatory.setText("¡Jugador 1 es obligatorio!");
            return;
        }

        GameState.getInstance().setPlayerOne(playerOne);

        if (!playerTwo.isEmpty()) {
            GameState.getInstance().setPlayerTwo(playerTwo);
        }

        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/modemenu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToProgramInfo(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/programInfo.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToConfiguration(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/configuration.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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
}
