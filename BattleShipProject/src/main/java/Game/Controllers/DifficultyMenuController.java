/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Game.Controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DifficultyMenuController {
    
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

    
    @FXML
    public void switchToModeMenu (ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/modemenu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    public void switchToShips(ActionEvent event) throws IOException {
        String selectedDifficulty = "";

        if (EasyMode.isSelected()) {
            selectedDifficulty = "Easy";
        } else if (NormalMode.isSelected()) {
            selectedDifficulty = "Normal";
        } else if (HardMode.isSelected()) {
            selectedDifficulty = "Hard";
        }

        if (selectedDifficulty.isEmpty()) {
            lbDifficultySelection.setText("¡Por favor, seleccione una dificultad!");
            return; 
        }

        String fxmlFile = "";

        switch (selectedDifficulty) {
            case "Easy":
                fxmlFile = "/Fxml/Boards/easyboardplacesofships.fxml";
                break;
            case "Normal":
                fxmlFile = "/Fxml/Boards/normalboardplacesofships.fxml";
                break;
            case "Hard":
                fxmlFile = "/Fxml/Boards/hardboardplacesofships.fxml";
                break;
        }

        root = FXMLLoader.load(getClass().getResource(fxmlFile));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}