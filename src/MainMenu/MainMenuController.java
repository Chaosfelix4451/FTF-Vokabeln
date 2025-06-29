package MainMenu;

import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Utils.UserSys.UserSys;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MainMenuController extends StageAwareController {
    public Button exitButton;
    public Button buttonSettings;
    public Button burgerMenu;

    @FXML
    private TextField userField;

    @FXML
    private VBox sideMenu;

    @FXML
    private Label statusLabel; // OPTIONAL für Rückmeldung

    @FXML
    private void initialize() {
        UserSys.loadFromJson();

        if (userField != null) {
            userField.setText(UserSys.getCurrentUser());
        }
    }

    @FXML
    public void openTrainer() {
        // Benutzer wird automatisch verwendet, der vorher über create/search gesetzt wurde
        SceneLoader.load("/Trainer/Trainer.fxml");
    }

    public void openSettings() {
        SceneLoader.load("/Settings/Settings.fxml");
    }

    public void openUserManagement() {
        SceneLoader.load("/UserManagement/UserManagement.fxml");
    }

    public void openScoreBoard() {
        SceneLoader.load("/ScoreBoard/ScoreBoard.fxml");
    }

    public String getUserInput() {
        return userField.getText().trim().toLowerCase();
    }

    public void setUserField(String username) {
        userField.setText(username);
    }

    public void searchAndSetUser() {
    }


    @FXML
    private void toggleSideMenu() {
        boolean isVisible = sideMenu.isVisible();
        sideMenu.setVisible(!isVisible);
        sideMenu.setManaged(!isVisible);
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleCreateUser() {
        //prüfung ob das feld leer ist,um laufzeitfehler vermeiden
        if (userField == null) return;
        String name = userField.getText().trim();
        if (name.isEmpty()) name = "user";

        if (!UserSys.userExists(name)) {
            UserSys.createUser(name);
            if (statusLabel != null) {
                statusLabel.setText("Benutzer '" + name + "' erstellt.");
            }
        } else {
            if (statusLabel != null) {
                statusLabel.setText("Benutzer '" + name + "' existiert bereits.");
            }
        }

        UserSys.setCurrentUser(name);
        UserSys.saveToJson();
    }

    @FXML
    private void handleSearchUser() {
        String originalInput = getUserInput();

        String input =getUserInput();
        List<String> matches = UserSys.searchUsers(input);
        if (!matches.isEmpty()) {
            setUserField(matches.getFirst());
            statusLabel.setText("Benutzer '" + matches.getFirst() + "' gefunden und ausgewählt.");
            UserSys.setCurrentUser(matches.getFirst());
            UserSys.saveToJson();
            statusLabel.setStyle("-fx-text-fill: green;");
            return;
        } else if (!UserSys.userExists(input)) {
            statusLabel.setText("Benutzer '" + originalInput + "' wurde nicht gefunden, bitte erstellen sie einen neuen.");
            statusLabel.setStyle("-fx-text-fill: red;");

        }

    }

}
