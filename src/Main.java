import Utils.SceneLoader.SceneLoader;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
//Test 123 String
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneLoader.setPrimaryStage(primaryStage); // einmalig registrieren
        SceneLoader.load("/MainMenu/mainMenu.fxml");
        primaryStage.setTitle("FTF-Vokabeln");

        // Fenstergröße setzen - wegen mvc verschieben?
        primaryStage.setWidth(800);  // Breite in Pixeln
        primaryStage.setHeight(600); // Höhe in Pixeln
        
        //Fenster zentrieren
        primaryStage.centerOnScreen();

        Image icon = new Image("file:media/Logo.png");
        if (icon.isError()) {
            System.out.println("Logo konnte nicht geladen werden: " + icon.getException());
        } else {
            primaryStage.getIcons().add(icon);
        }
        
        // Fenster anzeigen
        primaryStage.show();
    }

}