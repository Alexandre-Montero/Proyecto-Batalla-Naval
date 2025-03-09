module com.mycompany.proyecto.batalla.naval {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.mycompany.proyecto.batalla.naval to javafx.fxml;
    exports com.mycompany.proyecto.batalla.naval;
}
