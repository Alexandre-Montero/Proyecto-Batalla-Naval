/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto.batalla.naval;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    private Button btnReturnModeMenu;
    @FXML
    private Button btnBoardOfShips;
    
    @FXML
    public void switchToModeMenu (ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("ModeMenu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    public void switchToShips (ActionEvent event) throws IOException {
        Parent roo = FXMLLoader.load(getClass().getResource("BoardOfShips.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
