module Game {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens Game to javafx.fxml;
    exports Game;
    
    opens Game.Controllers to javafx.fxml;
    exports Game.Controllers;
    
    opens Game.Classes to javafx.fxml;
    exports Game.Classes;
    
    opens Game.Controllers.Boards to javafx.fxml;
    exports Game.Controllers.Boards;
}
