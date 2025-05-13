import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller class for the main view.
 *
 * @author Karsten Lehn
 * @version 17.5.2021
 */
public class MainController {

    // Member variable holding a reference to the respective scene (window).
    private Stage stage = null;
    // Member variable holding a reference to the next scene the controller should switch to.
    private Scene nextScene = null;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setNextScene(Scene nextScene) {
        this.nextScene = nextScene;
    }

    @FXML
    private Button button;

    /**
     * Button press handle, which changes the current scene.
     * @param event
     */
    @FXML
    private void handleButtonPressEvent(ActionEvent event) {
        stage.setScene(nextScene);
    }

    @FXML
    private VBox sideMenu;

    @FXML
    private void toggleSideMenu() {
        boolean isVisible = sideMenu.isVisible();
        sideMenu.setVisible(!isVisible);
        sideMenu.setManaged(!isVisible);
    }
}