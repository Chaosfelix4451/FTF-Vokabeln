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

import java.util.List;


public class MainMenuController extends StageAwareController {
    public Button exitButton;
    public Button buttonSettings;
    public Button burgerMenu;
    public Label mainLabel;
    public Button searchButton;
    public Button addButton;
    @FXML
    private Button confettiButton;

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

    @FXML
    public void openConnectTrainer() {
        SceneLoader.load("/ConnectTrainer/ConnectTrainer.fxml");
    }

    public void openSettings() {
        SceneLoader.load("/Settings/Settings.fxml");
    }

    public void openUserManagement() {
        SceneLoader.load("/UserManagement/UserManagement.fxml");
    }

    public void openScoreBoard() {
        ScoreBoard.ScoreBoardController.setLastSessionList(null);
        SceneLoader.load("/ScoreBoard/ScoreBoard.fxml");
    }

    public String getUserInput() {
        return userField.getText().trim().toLowerCase();
    }

    public void setUserField(String username) {
        userField.setText(username);
    }

    @FXML
    private void toggleSideMenu() {
        boolean isVisible = sideMenu.isVisible();
        sideMenu.setVisible(!isVisible);
       // sideMenu.setManaged(!isVisible);
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    /**
     * Erstellt einen neuen Benutzer, sofern dieser noch nicht existiert.
     * Gibt Rückmeldung im GUI-Label und speichert Änderungen in der JSON-Datei.
     */
    @FXML
    private void handleCreateUser() {
        String error = null;

        if (userField == null || userField.getText() == null) {
            statusLabel.setText("Fehler: Eingabefeld ist leer oder nicht initialisiert.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }else {

            String name = userField.getText().trim();
            if (name.isEmpty()) name = "user"; //Zusätzliche fehlersicherheit (nicht benötigt [redunant])

            try {
                if (!UserSys.userExists(name)) {
                    UserSys.createUser(name);
                    statusLabel.setText("Benutzer '" + name + "' wurde erstellt.");
                    statusLabel.setStyle("-fx-text-fill: green;");
                } else {
                    statusLabel.setText("Benutzer '" + name + "' existiert bereits.");
                    statusLabel.setStyle("-fx-text-fill: orange;");
                }

                UserSys.setCurrentUser(name);
                UserSys.saveToJson();

            } catch (Exception e) {
                error = e.getMessage();
                statusLabel.setText("Fehler bei Benutzererstellung: " + error);
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }


    @FXML
    private void handleSearchUser() {
        String error = null;
        String originalInput = getUserInput();

        if (userField != null && userField.getText() != null) {
            try {
                String input = getUserInput();
                UserSys.resetCurrentUser();
                UserSys.saveToJson();
                List<String> matches = UserSys.searchUsers(input);

                if (!matches.isEmpty()) {
                    setUserField(matches.getFirst());
                    statusLabel.setText("Benutzer '" + matches.getFirst() + "' gefunden und ausgewählt.");
                    UserSys.setCurrentUser(matches.getFirst());
                    UserSys.saveToJson();
                    statusLabel.setStyle("-fx-text-fill: green;");
                    return;
                } else if (!UserSys.userExists(input)) {
                    statusLabel.setText("Benutzer '" + originalInput + "' wurde nicht gefunden, bitte erstellen Sie einen neuen.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
                matches.clear();

            } catch (Exception e) {
                error = e.getMessage();
                statusLabel.setText("Ein Fehler ist aufgetreten: " + error);
                statusLabel.setStyle("-fx-text-fill: red;");
            }

        } else {
            statusLabel.setText("Fehler bei der Eingabe: Eingabefeld ist leer.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }

    }
}
