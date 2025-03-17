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
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class StartMenuController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    
    @FXML
    private Button btnProgramInformation;
    @FXML
    private Button btnPlay;
    @FXML
    private Label textGameName;
    
    @FXML
    public void switchToModeMenu(ActionEvent event)throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("ModeMenu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    public void switchToProgramInfo(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("ProgramInfo.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}