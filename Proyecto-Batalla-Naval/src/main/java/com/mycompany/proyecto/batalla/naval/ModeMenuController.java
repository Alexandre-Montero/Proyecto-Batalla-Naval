    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
import javafx.stage.Stage;

/**
 *
 * @author Alex
 */
public class ModeMenuController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private Button btnScene2Menu;
    @FXML
    private Button btnPlayerVSPlayer;
    @FXML
    private Button PlayerVSAI;

    @FXML
    public void switchToStartMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("StartMenu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
