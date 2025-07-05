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
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.List;


/**
 * Controller des Hauptmenüs. Hier können Benutzer angelegt, ausgewählt
 * und weitere Ansichten geöffnet werden.
 */
public class MainMenuController extends StageAwareController {
    public Button exitButton;
    public Button buttonSettings;
    public Button burgerMenu;
    public Label mainLabel;
    public Button searchButton;
    public Button addButton;
    @FXML
    private Button confettiButton;

    /** Separate stage for the settings window. */
    private Stage settingsStage;

    @FXML
    private TextField userField;

    @FXML
    private VBox sideMenu;

    @FXML
    private Label statusLabel; // OPTIONAL für Rückmeldung

    /**
     * Lädt die Benutzerinformationen und befüllt das Eingabefeld
     * mit dem aktuell gespeicherten Benutzer.
     */
    @FXML
    private void initialize() {
        System.out.println("MainMenu: initialize");
        UserSys.loadFromJson();

        if (userField != null) {
            userField.setText(UserSys.getCurrentUser());
        }
    }

    /**
     * Öffnet den normalen Trainer mit dem aktuell ausgewählten Benutzer.
     */
    @FXML
    public void openTrainer() {
        // Benutzer wird automatisch verwendet, der vorher über create/search gesetzt wurde
        SceneLoader.load("/Trainer/Trainer.fxml");
    }

    /**
     * Startet den Connect-Trainer für Zuordnungsaufgaben.
     */
    @FXML
    public void openConnectTrainer() {
        SceneLoader.load("/ConnectTrainer/ConnectTrainer.fxml");
    }

    /**
     * Öffnet das Einstellungsfenster in einem separaten Stage.
     * Existiert bereits ein Fenster, wird dieses nur in den Vordergrund gebracht.
     */
    public void openSettings() {
        System.out.println("MainMenu: opening settings window");
        if (settingsStage != null && settingsStage.isShowing()) {
            settingsStage.requestFocus();
            return;
        }
        settingsStage = new Stage();
        SceneLoader.load(settingsStage, "/Settings/Settings.fxml");

        Stage mainStage = this.stage;
        if (mainStage != null) {
            ChangeListener<Boolean> listener = new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> obs, Boolean oldVal, Boolean newVal) {
                    if (newVal && settingsStage.isShowing()) {
                        System.out.println("MainMenu: closing settings window");
                        settingsStage.close();
                        mainStage.focusedProperty().removeListener(this);
                    }
                }
            };
            mainStage.focusedProperty().addListener(listener);
        }
    }

    /**
     * Wechsel zur Benutzerverwaltung.
     */
    public void openUserManagement() {
        SceneLoader.load("/UserManagement/UserManagement.fxml");
    }

    /**
     * Zeigt die Statistiken und Highscores an.
     */
    public void openScoreBoard() {
        ScoreBoard.ScoreBoardController.setLastSessionList(null);
        SceneLoader.load("/ScoreBoard/ScoreBoard.fxml");
    }

    /**
     * Liefert den Text aus dem Eingabefeld bereinigt zurück.
     */
    public String getUserInput() {
        return userField.getText().trim().toLowerCase();
    }

    /**
     * Trägt einen Benutzernamen in das Eingabefeld ein.
     */
    public void setUserField(String username) {
        userField.setText(username);
    }

    /**
     * Blendet das seitliche Menü ein oder aus.
     */
    @FXML
    private void toggleSideMenu() {
        boolean isVisible = sideMenu.isVisible();
        sideMenu.setVisible(!isVisible);
        sideMenu.setManaged(!isVisible);
    }

    /** Beendet die Anwendung. */
    @FXML
    private void handleExit() {
        Platform.exit();
    }

    /**
     * Erstellt einen neuen Benutzer, falls der Name noch nicht vergeben ist.
     * Die Auswahl wird gespeichert und eine Statusmeldung ausgegeben.
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


    /**
     * Sucht nach Benutzernamen und wählt einen Treffer aus.
     */
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
