import Utils.SceneLoader.SceneLoader;
import javafx.application.Application;
import javafx.stage.Stage;

/*
7PG_T2 Vokabeltrainer
Feras Hassan
Felix Kioschis 👑
Toby Berndt

Gruppenname: FTF Vokabeltrainer
Soon:
Vokabel Kontrolle
High Score System
Vokabel Training Übersicht
Angabe der Länge von Test
Vokabel Kategorien
Design via. CSS
 */
//Test 123 String
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneLoader.setPrimaryStage(primaryStage); // einmalig registrieren
        SceneLoader.load("/MainMenu/mainMenu.fxml");
        primaryStage.setTitle("Vokabeltrainer");
    }



}
