package Game.Controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ProgramInfoController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Label lbGithubLink;
    @FXML
    private Label lbProgramInfo;
    @FXML
    private Button btnReturnToStartMenu;
    @FXML
    private Label lbStudent;
    @FXML
    private Label lbStudentName;
    @FXML
    private Label lbCarrer;
    @FXML
    private Label lbUniversity;
    @FXML
    private Label lbEmail;
    @FXML
    private Label lbCourse;
    @FXML
    private Label lbStudentUniversity;
    @FXML
    private Label lbStudentCareer;
    @FXML
    private Label lbStudentCourse;
    @FXML
    private Label lbStudentEmail;
    @FXML
    private Label lbGithub;
    @FXML
    private Label lbCredits;
    @FXML
    private Label lbCompanyName;

    @FXML
    public void switchToStartMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/startmenu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void openGithubLink(MouseEvent event) {
        String url = "https://github.com/Alexandre-Montero/Proyecto-Batalla-Naval.git";
        try {
            java.awt.Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openGmail(MouseEvent event) {
        String email = "mailto:alexandre.montero.espinosa@est.una.ac.cr";
        try {
            java.awt.Desktop.getDesktop().browse(new URI(email));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
