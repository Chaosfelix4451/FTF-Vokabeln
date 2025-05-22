import Utils.SceneLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Utils.SceneLoader;

import java.io.IOException;

/*
7PG_T2 Vokabeltrainer
Feras Hassan
Felix Kioschis 👑
Toby Bernd

Gruppenname: FTF Vokabeltrainer
Soon:
Vokabel Kontrolle
High Score System
Angabe der Länge von Test
Vokabel Kategorien

 */


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneLoader.setPrimaryStage(primaryStage); // einmalig registrieren
        SceneLoader.load("/MainMenu/mainMenu.fxml"); // Kein explizites stage mehr nötig
    }


}