package MainMenu;

import Utils.UserSys.UserSys;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import Utils.SceneLoader.SceneLoader;

import Utils.StageAwareController;

import java.nio.file.Path;

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
        UserSys.loadFromJson(Path.of("Utils/UserSys/User.json"));

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
        String input = getUserInput();


        var matches = UserSys.searchUsers(input);
        if (!matches.isEmpty()) {
            setUserField(matches.get(0));
            return;
        }

        // Kein Treffer gefunden
        setUserField("");
        if (statusLabel != null) {
            statusLabel.setText("Kein Benutzer mit '" + input + "' gefunden.");
        }
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
        UserSys.saveToJson(Path.of("Utils/UserSys/User.json"));
    }

    @FXML
    private void handleSearchUser() {
        String originalInput = getUserInput();

        //Methode die autovervollständigt
        searchAndSetUser();

        String completedName = getUserInput();

        if (UserSys.userExists(completedName)) {
            UserSys.setCurrentUser(completedName);
            UserSys.saveToJson(Path.of("Utils/UserSys/User.json"));
            if (statusLabel != null) {
                statusLabel.setText("Benutzer '" + completedName + "' ausgewählt.");
                statusLabel.setStyle("-fx-text-fill: green;");
            }
        } else {
            if (statusLabel != null) {
                statusLabel.setText("Benutzer '" + originalInput + "' wurde nicht gefunden.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

}
