import Utils.SceneLoader.SceneLoader;
import javafx.application.Application;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Objects;

/*
7PG_T2 Vokabeltrainer
Feras Hassan
Felix Kioschis 👑
Toby Berndt

Gruppenname: FTF Vokabeltrainer
Soon:
Vokabel Kategorien
Design via. CSS
 */
/**
 * Startpunkt der Anwendung. Diese Klasse initialisiert die erste Szene
 * und stellt das Hauptfenster bereit.
 */
public class Main extends Application {

    /**
     * Einstiegsmethode, die JavaFX startet.
     *
     * @param args übergebene Programmargumente
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Lädt das Hauptmenü und richtet das Anwendungsfenster ein.
     *
     * @param primaryStage von JavaFX bereitgestellte Hauptbühne
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] 🚀 Anwendung startet");
        SceneLoader.setPrimaryStage(primaryStage); // einmalig registrieren
        SceneLoader.load("/MainMenu/mainMenu.fxml");
        primaryStage.setTitle("FTF-Vokabeln");

        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);
        primaryStage.centerOnScreen();

        Image icon = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/Utils/media/Logo.png")),
                128, 128, true, true);
        if (icon.isError()) {
            System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] ⚠️ Logo konnte nicht geladen werden: " + icon.getException());
        } else {
            primaryStage.getIcons().add(icon);
        }

        // Fenster anzeigen
        primaryStage.show();
    }
}