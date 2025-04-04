package Game.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DifficultyMenuController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private CheckBox EasyMode;
    @FXML
    private CheckBox NormalMode;
    @FXML
    private CheckBox HardMode;
    @FXML
    private Label lbDifficultyTitle;
    @FXML
    private Button btnBoardOfShips;
    @FXML
    private Button btnReturnModeMenu;
    @FXML
    private Label lbDifficultySelection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EasyMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                uncheckOther(EasyMode);
            }
        });
        
        NormalMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                uncheckOther(NormalMode);
            }
        });
        
        HardMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                uncheckOther(HardMode);
            }
        });
    }

    @FXML
    public void switchToModeMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/modemenu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToShips(ActionEvent event) throws IOException {
        String selectedDifficulty = "";

        if (EasyMode.isSelected()) {
            selectedDifficulty = "Easy";
        } 
        if (NormalMode.isSelected()) {
            selectedDifficulty = "Normal";
        } 
        if (HardMode.isSelected()) {
            selectedDifficulty = "Hard";
        }

        if (selectedDifficulty.isEmpty()) {
            lbDifficultySelection.setText("¡Por favor, seleccione una dificultad!");
            return;
        }

        String fxmlFile = "";

        switch (selectedDifficulty) {
            case "Easy":
                fxmlFile = "/Fxml/Boards/playeroneeasyboard.fxml";
                break;
            case "Normal":
                fxmlFile = "/Fxml/Boards/playeronenormalboard.fxml";
                break;
            case "Hard":
                fxmlFile = "/Fxml/Boards/playeronehardboard.fxml";
                break;
        }

        root = FXMLLoader.load(getClass().getResource(fxmlFile));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void uncheckOther(CheckBox selected) {
        if (selected != EasyMode) {
            EasyMode.setSelected(false);
        }
        
        if (selected != NormalMode) {
            NormalMode.setSelected(false);
        }
        
        if (selected != HardMode) {
            HardMode.setSelected(false);
        }
    }
}
